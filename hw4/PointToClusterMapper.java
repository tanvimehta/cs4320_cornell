import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * You can modify this class as you see fit.  You may assume that the global
 * centroids have been correctly initialized.
 */
public class PointToClusterMapper extends Mapper<Text, Text, IntWritable, Point> {
	
	public void map(Text key, Text value, Context context) 
			throws IOException, InterruptedException {
		
		float minDistance = Float.MAX_VALUE;
		Integer closestCentroidIndex = 0;
		Point currPoint = new Point(key.toString());
		
		// Get closest index of centroid
		for (int i = 0; i < KMeans.centroids.size(); i++) {
			float distToCentroid = Point.distance(KMeans.centroids.get(i), currPoint);
			
			if (minDistance > distToCentroid) {
				minDistance = distToCentroid;
				closestCentroidIndex = i;
			}
		}
		
		context.write(new IntWritable(closestCentroidIndex), currPoint);
	}
}
