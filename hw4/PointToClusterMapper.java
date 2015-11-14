import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * You can modify this class as you see fit.  You may assume that the global
 * centroids have been correctly initialized.
 */
public class PointToClusterMapper extends Mapper<Text, Text, Integer, Point> {
	
	public void map(Text key, Text value, Context context) 
			throws IOException, InterruptedException {
		
		float minDistance = Float.MAX_VALUE;
		Integer closestCentroidIndex = 0;
		Point currPoint = new Point(key.toString());
		
		// Get closest index of centroid
		for (int i = 0; i < KMeans.centroids.size(); i++) {
			Point centroid = new Point(KMeans.centroids.get(i));
			float distToCentroid = Point.distance(centroid, currPoint);
			
			if (minDistance > distToCentroid) {
				minDistance = distToCentroid;
				closestCentroidIndex = i;
			}
		}
		
		context.write(closestCentroidIndex, currPoint);
	}
}
