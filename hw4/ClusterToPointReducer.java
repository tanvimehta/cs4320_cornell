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
public class ClusterToPointReducer extends Reducer<Integer, ArrayList<Point>, Text, Text> {
	
	protected void reduce(Integer key, ArrayList<Point> values, Context context) 
			throws IOException, InterruptedException {
		
		int counter = 0;
		Point newCentroid = new Point(KMeans.centroids.get(0).getDimension());
		
		// Add all points and maintain counter for the mean
		for (Point p: values) {
			counter++;
			newCentroid = Point.addPoints(newCentroid, p);
		}
		
		// Take the mean of all points by dividing sum of all points by counter
		newCentroid = Point.multiplyScalar(newCentroid, (1.0f/(float)counter));
		
		KMeans.centroids.set(key, newCentroid);
	}
}
