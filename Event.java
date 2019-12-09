package voronoi;

/**
 *
 * @author VIVEK
 */
public class Event implements Comparable <Event>{
	
	// a site event is when the point is a site, that is an input node taken from the GUI
	public static int SITE_EVENT = 0;
	
	// a half edge event would be when the point is a vertex of the voronoi diagram/parabolas
	public static int HALFEDGE_EVENT = 1;
	
	Point p;
	int type;
	Parabola arc; // only if it is a halfedge event
	
	public Event (Point p, int type) {//setting the type of a point
		this.p = p;
		this.type = type;
		arc = null;
	}
	
	public int compareTo(Event other) {//compares two points
		return this.p.compareTo(other.p);
	}

}
