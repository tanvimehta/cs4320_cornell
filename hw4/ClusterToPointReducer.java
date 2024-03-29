import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;

/** 
 * You can modify this class as you see fit, as long as you correctly update the
 * global centroids.
 */
public class ClusterToPointReducer extends Reducer<IntWritable, Point, Text, Text> {
	
	protected void reduce(IntWritable key, Iterable<Point> values, Context context) 
			throws IOException, InterruptedException {
		
		int counter = 0;
		Point newCentroid = null;
		
		// Add all points and maintain counter for the mean
		for (Point p: values) {
			if (counter == 0) {
				 newCentroid = new Point(KMeans.centroids.get(0).getDimension());
			}
			counter++;
			newCentroid = Point.addPoints(newCentroid, p);
		}
		
		// If centroid has 0 nighbours, centroid should remain but value should not change
		if (counter == 0 || newCentroid == null) {
			return;
		}
		
		float scalar = 1.0f/(float)counter;
		
		// Take the mean of all points by dividing sum of all points by counter
		newCentroid = Point.multiplyScalar(newCentroid, scalar);
				
		KMeans.centroids.set(key.get(), newCentroid);
	}
}
