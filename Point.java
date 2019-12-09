package voronoi;

/**
 *
 * @author VIVEK
 */
// a point in 2D that is sorted by the y coordinate
public class Point implements Comparable <Point>{
	
	double x;
	double y;
	
	public Point (double x0, double y0) {//defining the constructor
		x = x0;
		y = y0;
	}
	
	public int compareTo (Point other) {//comparing and sorting according to y coordinate
		if (this.y == other.y) {
			if (this.x == other.x) return 0;//same point
			else if (this.x > other.x) return 1;//greater than
			else return -1;//lesser than
		}
		else if (this.y > other.y) {//if y coordinate is greater
			return 1;
		}
		else {//if y coordinate is lesser
			return -1;
		}
	}
	
	public String toString(){ //converts to string
		return "(" + x + ", " + y + ")";
	}

}
