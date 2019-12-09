package voronoi;

/**
 *
 * @author VIVEK
 */
public class Edge {

	Point start, end, point_left, point_right, direction; // edge would be a vector normal to left and right points
	
	Edge neighbor; // the same edge that would be pointing in the opposite direction
	
	double slope,yint;
	
	public Edge (Point firstp, Point leftp, Point rightp) {//defining the constructor
		start = firstp;
		point_left = leftp;
		point_right = rightp;
		direction = new Point(rightp.y - leftp.y, - (rightp.x - leftp.x));//computing it normal to the left and right points
		end = null;		
		slope = (rightp.x - leftp.x)/(leftp.y - rightp.y);//computing the slope
		Point mid = new Point ((rightp.x + leftp.x)/2, (leftp.y+rightp.y)/2);//computing the mid point
		yint = mid.y - slope*mid.x;
	}
	
	public String toString() {//converting to string
		return start + ", " + end;
	}
}
