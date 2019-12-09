package voronoi;

/**
 *
 * @author VIVEK
 */
// represents the beach line, which can either be a site that is the center of a parabola called the focus of the parabola
//or it can be a vertex that bisects the two sites
public class Parabola {
	
	public static int IS_FOCUS = 0;
	public static int IS_VERTEX = 1;
	
	int type;
	Point point; // if it is the focus of the parabola
	Edge edge; // if it is the vertex that bisects the two sites
	Event event; // a parabola with a focus can disappear in a halfedge event
	
	Parabola parent;//to construct the BST of the parabola
	Parabola left_child;
	Parabola right_child;
	
	public Parabola () {//if it is the vertex that bisects the two sites
		type = IS_VERTEX;
	}
	
	public Parabola (Point p) {//if it is the focus of the parabola
		point = p;
		type = IS_FOCUS;
	}

	public void setLeftChild (Parabola p) {//setting the left child of the parabola
		left_child = p;
		p.parent = this;
	}

	public void setRightChild (Parabola p) {//setting the right child of the parabola
		right_child = p;
		p.parent = this;
	}

	public String toString() {//returns the point in the form of string
		if (type == IS_FOCUS) {
			return "Focus at " + point;
		}
		else{
			return "Vertex/Edge beginning at " + edge.start;
		}
	}
	
	// returns the closest left site (focus of parabola) 
	public static Parabola getLeft(Parabola p) {//gets the left site
		return getLeftChild(getLeftParent(p));
	}
	
	// returns the closest right site (focus of parabola)
	public static Parabola getRight(Parabola p) {//gets the right site
		return getRightChild(getRightParent(p));
	}
	
	// returns the closest parent on the left
	public static Parabola getLeftParent(Parabola par) {//returns the closest left parent
		Parabola parent = par.parent;
		if (parent == null) return null;//if parent doesn't exist
		Parabola last = par;
		while (parent.left_child == last) {
			if(parent.parent == null) return null;//returns null
			last = parent;
			parent = parent.parent;
		}
		return parent;
	}
	
	// returns the closest parent on the right
	public static Parabola getRightParent(Parabola par) {//returns the closest right parent
		Parabola parent = par.parent;
		if (parent == null) return null;//if parent doesn't exist
		Parabola last = par;
		while (parent.right_child == last) {
			if(parent.parent == null) return null;//returns null
			last = parent;
			parent = parent.parent;
		}
		return parent;
	}
	
	// returns closest site (focus of another parabola) to the left
	public static Parabola getLeftChild(Parabola p) {
		if (p == null) return null;
		Parabola child = p.left_child;
		while(child.type == IS_VERTEX) child = child.right_child;
		return child;
	}
	
	// returns closest site (focus of another parabola) to the right
	public static Parabola getRightChild(Parabola p) {
		if (p == null) return null;
		Parabola child = p.right_child;
		while(child.type == IS_VERTEX) child = child.left_child;
		return child;	
	}
	
	

}
