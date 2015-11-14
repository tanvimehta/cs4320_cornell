import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;

import java.io.IOException;
import java.util.ArrayList;

public class UpdateJobRunner
{
	public static ArrayList<Point> C_old = new ArrayList<Point>();
	
    /**
     * Create a map-reduce job to update the current centroids.
     * @param jobId Some arbitrary number so that Hadoop can create a directory "<outputDirectory>/<jobname>_<jobId>"
     *        for storage of intermediate files.  In other words, just pass in a unique value for this
     *        parameter.
     * @param The input directory specified by the user upon executing KMeans, in which the points
     *        to find the KMeans point files are located.
     * @param The output directory for which to write job results, specified by user.
     * @precondition The global centroids variable has been set.
     */
    public static Job createUpdateJob(int jobId, String inputDirectory, String outputDirectory)
        throws IOException {
        
    	Configuration conf = new Configuration();
        conf.addResource(new Path("/hadoop/projects/hadoop-2.7.1/conf/core-site.xml"));
        conf.addResource(new Path("/hadoop/projects/hadoop-2.7.1/conf/hdfs-site.xml"));
        
    	Job init_job = new Job(conf, Integer.toString(jobId));
        init_job.setJarByClass(KMeans.class);
        init_job.setMapperClass(PointToClusterMapper.class);
        init_job.setMapOutputKeyClass(IntWritable.class);
        init_job.setMapOutputValueClass(Point.class);
        init_job.setReducerClass(ClusterToPointReducer.class);
        init_job.setOutputKeyClass(IntWritable.class);
        init_job.setOutputValueClass(Point.class);
        FileInputFormat.addInputPath(init_job, new Path(inputDirectory));
        FileOutputFormat.setOutputPath(init_job, new Path(outputDirectory+"/"+Integer.toString(jobId)));
        init_job.setInputFormatClass(KeyValueTextInputFormat.class);
        return init_job;
        
//        Job updateJob = new Job(new Configuration(), Integer.toString(jobId));
//        updateJob.setJarByClass(KMeans.class);
//        updateJob.setMapperClass(PointToClusterMapper.class);
//        updateJob.setMapOutputKeyClass(IntWritable.class);
//        updateJob.setMapOutputValueClass(Point.class);
//        updateJob.setReducerClass(ClusterToPointReducer.class);
//        updateJob.setOutputKeyClass(IntWritable.class);
//        updateJob.setOutputValueClass(Point.class);
//        FileInputFormat.addInputPath(updateJob, new Path(inputDirectory));
//        FileOutputFormat.setOutputPath(updateJob, new Path(outputDirectory + "/" + Integer.toString(jobId)));
//        updateJob.setInputFormatClass(KeyValueTextInputFormat.class);
//
//        return updateJob;
    }

    /**
     * Run the jobs until the centroids stop changing.
     * Let C_old and C_new be the set of old and new centroids respectively.
     * We consider C_new to be unchanged from C_old if for every centroid, c, in 
     * C_new, the L2-distance to the centroid c' in c_old is less than [epsilon].
     *
     * Note that you may retrieve publically accessible variables from other classes
     * by prepending the name of the class to the variable (e.g. KMeans.one).
     *
     * @param maxIterations   The maximum number of updates we should execute before
     *                        we stop the program.  You may assume maxIterations is positive.
     * @param inputDirectory  The path to the directory from which to read the files of Points
     * @param outputDirectory The path to the directory to which to put Hadoop output files
     * @return The number of iterations that were executed.
     */
    public static int runUpdateJobs(int maxIterations, String inputDirectory,
        String outputDirectory) {
    	
    	boolean centroidChangeFlag = true;
    	
    	for (Point centroid: KMeans.centroids) {
    		C_old.add(new Point(centroid));
    	}
    	
    	int iterations;
    	
    	for (iterations = 0; iterations < maxIterations; iterations++) {
    		if (!centroidChangeFlag) {
    			break;
    		}
    		
    		for(int i = 0; i < KMeans.centroids.size(); i++){
                C_old.set(i,new Point(KMeans.centroids.get(i)));
            }
    		
    		try {
    			Job currJob = createUpdateJob(iterations, inputDirectory, outputDirectory);
    			currJob.waitForCompletion(true);
    		} catch (Exception e) {
    			System.out.println("Update job failed at creation.");
    		}
    		
    		centroidChangeFlag = isChangedCentroid();
    	}
    	
    	return iterations;
    }
    
    /**
     * Check if C_old and C_new are the same. If yes, return false else true.
     */
    public static boolean isChangedCentroid() {
    	ArrayList<Point> C_new = KMeans.centroids;
    	
    	if (C_old.size() != C_new.size()) {
    		return true;
    	}
    	
    	for (int i = 0; i < C_old.size(); i++) {
    		if (C_old.get(i).compareTo(C_new.get(i)) != 0) {
    			return true;
    		}
    	}
    	
    	return false;	
    }
}
