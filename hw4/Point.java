import java.io.*; // DataInput/DataOuput
import java.util.ArrayList;
import java.util.Collections;
import org.apache.hadoop.io.*; // Writable

/**
 * A Point is some ordered list of floats.
 * 
 * A Point implements WritableComparable so that Hadoop can serialize
 * and send Point objects across machines.
 *
 * NOTE: This implementation is NOT complete.  As mentioned above, you need
 * to implement WritableComparable at minimum.  Modify this class as you see fit.
 */
public class Point implements WritableComparable<Point> {

    private ArrayList<Float> pointDims = new ArrayList<Float>();
    private int dim;
    
	public Point() {
		this.dim = KMeans.dimension;
		
		for(int i=0; i<dim; i++) 
			pointDims.add(new Float(0.0));
	}
    
    /**
     * Construct a Point with the given dimensions [dim]. The coordinates should all be 0.
     * For example:
     * Constructing a Point(2) should create a point (x_0 = 0, x_1 = 0)
     */
    public Point(int dim)
    {
    	this.dim = dim;
        for(int i = 0; i < dim; i++) {
        	pointDims.add(new Float(0.0f));
        }
    }

    /**
     * Construct a point from a properly formatted string (i.e. line from a test file)
     * @param str A string with coordinates that are space-delimited.
     * For example: 
     * Given the formatted string str="1 3 4 5"
     * Produce a Point {x_0 = 1, x_1 = 3, x_2 = 4, x_3 = 5}
     */
    public Point(String str)
    {
    	String[] values = str.split(" ");
        dim = values.length;
        
		for (String value: values) {
			pointDims.add(Float.parseFloat(value));
		}
    }

    /**
     * Copy constructor
     */
    public Point(Point other)
    {
        dim = other.getDimension();
        pointDims.addAll(other.getPointDims());
    }

    /**
     * @return The point dims of the point.
     */
    public ArrayList<Float> getPointDims() {
    	return pointDims;
    }
    
    /**
     * @return The dimension of the point.  For example, the point [x=0, y=1] has
     * a dimension of 2.
     */
    public int getDimension()
    {
        return dim;
    }

    /**
     * Converts a point to a string.  Note that this must be formatted EXACTLY
     * for the autograder to be able to read your answer.
     * Example:
     * Given a point with coordinates {x=1, y=1, z=3}
     * Return the string "1 1 3"
     */
    public String toString() {
        String result = "";
        for (Float value: pointDims) {
        	result += value + " ";
        }
        return result.substring(0, result.length()-1);
    }

    /**
     * One of the WritableComparable methods you need to implement.
     * See the Hadoop documentation for more details.
     * You should order the points "lexicographically" in the order of the coordinates.
     * Comparing two points of different dimensions results in undefined behavior.
     */
    public int compareTo(Point o)
    {   
        if (dim != o.getDimension()) {
        	System.err.println("Points have different dimensions.");
        	System.exit(1);
        }

		for (int i = 0; i < dim; i++) {
			float diff = pointDims.get(i) - o.pointDims.get(i);
			if (diff > 0.000001 ) {
				return 1;
			} else if (diff < 0.000001) {
				return -1;
			}
		}
        return 0;
    }

    /**
     * @return The L2 distance between two points.
     */
    public static final float distance(Point x, Point y)
    {
		if (x.getDimension() != y.getDimension()) {
			System.err.println("Points have different dimensions.");
			System.exit(1);
		}
		
		double squaredDistance = 0.0f;
		ArrayList<Float> pointDims_x = x.getPointDims();
		ArrayList<Float> pointDims_y = y.getPointDims();
	
		for (int i = 0; i < x.getDimension(); i++) {
			double diff = Math.abs(pointDims_x.get(i) - pointDims_y.get(i));
			squaredDistance += Math.pow(diff, 2);
		}

        return (float)Math.sqrt(squaredDistance);
    }

    /**
     * @return A new point equal to [x]+[y]
     */
    public static final Point addPoints(Point x, Point y) {
        if (x.getDimension() != y.getDimension()) {
        	System.err.println("Points have different dimensions.");
        	System.exit(1);
        }

        Point result = new Point(x.getDimension());
		ArrayList<Float> pointDims_x = x.getPointDims();
		ArrayList<Float> pointDims_y = y.getPointDims();
        
		for (int i = 0; i < x.getDimension(); i++) {
			result.pointDims.set(i, new Float(pointDims_x.get(i) + pointDims_y.get(i)));
		}
		
		return result;
    }

    /**
     * @return A new point equal to [c][x]
     */
    public static final Point multiplyScalar(Point x, float c)
    {
        Point result = new Point(x.getDimension());
        ArrayList<Float> dims = new ArrayList<Float>();
        
        ArrayList<Float> pointDims_x = x.getPointDims();
		for (int i = 0; i < x.getDimension(); i++) {
			result.pointDims.set(i, new Float(pointDims_x.get(i)*c));
		}
		
		return result;
    }

    public void readFields (DataInput in) throws IOException {
		dim = in.readInt();
		
		for (int i=0; i<dim; i++)
			pointDims.set(i,in.readFloat());
	}

	public void write(DataOutput out) throws IOException {   //must implement Writable
		
		out.writeInt(dim);
		
		for (int i=0; i<dim; i++)
			out.writeFloat(pointDims.get(i));
	}
    
}
