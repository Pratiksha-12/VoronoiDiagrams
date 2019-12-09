package voronoi;


import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class VoronoiDiagram {
	
	List <Point> points;//points in Voronoi diagra,
	List <Edge> edges; // edges in the Voronoi diagram
	PriorityQueue<Event> events; // priority queue representing the sweep line
	Parabola root; // binary search tree representing the beach line
	double ycurrent; // current y coordinate of the sweep line
	
        double width=1;
        double height=1;
	public VoronoiDiagram (List <Point> points) {//constructing setting the values of sites and edges, called by the main function of the main class
                //System.out.println("1");
		this.points = points;
		edges = new ArrayList<Edge>();
		generateVoronoiDiagram();//calls the VD function
	}
	
	private void generateVoronoiDiagram() {
		System.out.println("2");
		events = new PriorityQueue <Event>();//our sweep line algorithm
		for (Point p : points) {
			events.add(new Event(p, Event.SITE_EVENT));//adds our points and the type of event it can be to the priority queue
                        //System.out.println("Here");
		}
                
                System.out.println("dfd>>"+events.size());
              
		
		// processing the events (sweep line)
		int count = 0;//maintains count of all the events
		while (!events.isEmpty()) {
			//System.out.println();
			Event e;
                    e = events.remove();//removes the top event from the queue
			ycurrent = e.p.y;//updates the y coordinate of the sweepline with the coordinate of the current event
			count++;//increments the count of the number of events
                       // System.out.println("Printing count "+count);
			if (e.type == Event.SITE_EVENT) {//if it is one of the points
				System.out.println(count + ". SITE_EVENT " + e.p);
				handleSite(e.p);//calls the function that deals with site events
			}
			else {
				System.out.println(count + ". HALFEDGE_EVENT " + e.p);
				handleHalfEdge(e);//calls the function that deals with half edge events
			}
		}

		ycurrent=width+height;//draws the edges to fill the panel
		endEdges(root); // closes off any dangling or leftover edges
		
		// in case we need to remove the inifinte lines
		for (Edge e: edges){
			if (e.neighbor != null) {//neighbor here is the same edge pointing in another direction
				e.start = e.neighbor.end;
				e.neighbor = null;
			}
		}
	}

	// end all unfinished edges
	private void endEdges(Parabola p) {
		if (p.type == Parabola.IS_FOCUS) {//if it is the focus of a parabola
			p = null;//set to null
			return;
		}

		double x = getXofEdge(p);//returns the current x coordinate of the parabola
		p.edge.end = new Point (x, p.edge.slope*x+p.edge.yint);//updates the end point of the edge
		edges.add(p.edge);//adds it to the list of edges
		
		endEdges(p.left_child);//passes the left and right child to the function to end all unfinished edges recursively
		endEdges(p.right_child);
		
		p = null; //finally removes the parabola
	}

	// handling site events
	private void handleSite(Point p) {//recursive function
		// base case
		if (root == null) {
			root = new Parabola(p);//if the BST is empty
			return;
		}
		
		// to find the parabola on the beach line right above p
		Parabola par = getParabolaByX(p.x);//returns parabola above this x coordinate on the beach line
		if (par.event != null) {//if the event is not null, removes it from the queue and sets it to null
			events.remove(par.event);
			par.event = null;
		}
                

		// creates a new dangling edge; bisects parabola focus and p
		Point start = new Point(p.x, getY(par.point, p.x));// find corresponding y-coordinate to x on parabola with focus p
		Edge edgeleft = new Edge(start, par.point, p);
		Edge edgeright = new Edge(start, p, par.point);
		edgeleft.neighbor = edgeright;
		edgeright.neighbor = edgeleft;
		par.edge = edgeleft;
		par.type = Parabola.IS_VERTEX;//a vertex that bisects two sites
		
		//the original parabola is replaced
		Parabola par0 = new Parabola (par.point);
		Parabola par1 = new Parabola (p);
		Parabola par2 = new Parabola (par.point);

		par.setLeftChild(par0);
		par.setRightChild(new Parabola());
		par.right_child.edge = edgeright;
		par.right_child.setLeftChild(par1);
		par.right_child.setRightChild(par2);

		checkHalfEdgeEvent(par0);//checks for Half edge event
		checkHalfEdgeEvent(par2);//checks for half edge event
	}
	
	// processes half edge event
	private void handleHalfEdge(Event e) {
		
		// find par0, par1, par2 that generate this event from left to right
		Parabola par1 = e.arc;
		Parabola xl = Parabola.getLeftParent(par1);//xl becomes the left parent of the parabola
		Parabola xr = Parabola.getRightParent(par1);//xr becomes the right parent of the parabola
		Parabola par0 = Parabola.getLeftChild(xl);//par0 becomes the left child of the parabola
		Parabola par2 = Parabola.getRightChild(xr);//par2 becomes the right child of the parabola
		
		// removes all the associated events since the points have been altered
		if (par0.event != null) {
			events.remove(par0.event);
			par0.event = null;
		}
		if (par2.event != null) {
			events.remove(par2.event);
			par2.event = null;
		}
		
		Point p = new Point(e.p.x, getY(par1.point, e.p.x));// the new vertex
	
		//the end edges
		xl.edge.end = p;
		xr.edge.end = p;
		edges.add(xl.edge);
		edges.add(xr.edge);

		// starts a new bisector (edge) from this vertex to whichever original edge is higher in yhe tree
		Parabola higher = new Parabola();
		Parabola par = par1;
		while (par != root) {
			par = par.parent;
			if (par == xl) higher = xl;
			if (par == xr) higher = xr;
		}
		higher.edge = new Edge(p, par0.point, par2.point);
		
		// deletes par1 and the parent, which is the boundary edge from the beach line
		Parabola grandparent = par1.parent.parent;
		if (par1.parent.left_child == par1) {
			if(grandparent.left_child == par1.parent) grandparent.setLeftChild(par1.parent.right_child);
			if(grandparent.right_child == par1.parent) grandparent.setRightChild(par1.parent.right_child);
		}
		else {
			if(grandparent.left_child  == par1.parent) grandparent.setLeftChild(par1.parent.left_child);
			if(grandparent.right_child == par1.parent) grandparent.setRightChild(par1.parent.left_child);
		}

		//Point op = par1.point;
		par1.parent = null;
		par1 = null;
		
		checkHalfEdgeEvent(par0);
		checkHalfEdgeEvent(par2);
	}
	
	// adds half edge event if focus a, b, c lie on the same circle, without any other point lying inside the circle
	private void checkHalfEdgeEvent(Parabola b) {

		Parabola leftp = Parabola.getLeftParent(b);//gets the left parentparabola of b
		Parabola rightp = Parabola.getRightParent(b);//gets the right parent parabola of b

		if (leftp == null || rightp == null) return;
		
		Parabola a = Parabola.getLeftChild(leftp);
		Parabola c = Parabola.getRightChild(rightp);
	
		if (a == null || c == null || a.point == c.point) return;

		if (circlearea(a.point,b.point,c.point) != 1) return;//if area between the circles is non negative
		
		
		Point start = getEdgeIntersection(leftp.edge, rightp.edge);// edges will intersect to form a vertex for a half edge event
		if (start == null) return;//ig getEdgeIntersection returns null
		
		// computing radius of the circle
		double dx = b.point.x - start.x;
		double dy = b.point.y - start.y;
		double d = Math.sqrt((dx*dx) + (dy*dy));//from the eqn x^2+y^2=r^2
		if (start.y + d < ycurrent) return; // must be after the sweep line

		Point ep = new Point(start.x, start.y + d);//otherwise halfedge event is created

		// add the halfedge event
		Event e = new Event (ep, Event.HALFEDGE_EVENT);
		e.arc = b;
		b.event = e;
		events.add(e);//adds the halfedge event to the priority queue
	}

	//computing the area of the circle
	public int circlearea(Point a, Point b, Point c) {
        double area2 = (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
        if (area2 < 0) return -1;//if the area is non existent
        else if (area2 > 0) return 1;//if the area exists
        else return  0;
    }

	// returns the intersection of the lines of the edges with a and b
	private Point getEdgeIntersection(Edge a, Edge b) {

		if (b.slope == a.slope && b.yint != a.yint) return null;//if they have the same slope but not the same yint
                //else
		double x = (b.yint - a.yint)/(a.slope - b.slope);
		double y = a.slope*x + a.yint;

		return new Point(x, y);//returns the intersecting point of the two
	} 
	
	// returns the current x coordinates of an unfinished edge
	private double getXofEdge (Parabola par) {
		

                //finds the intersection of the two parabolas
		
		Parabola left = Parabola.getLeftChild(par);
		Parabola right = Parabola.getRightChild(par);
		
		Point l = left.point;
		Point r = right.point;
		
		double dp = 2*(l.y - ycurrent);
		double a1 = 1/dp;
		double b1 = -2*l.x/dp;
		double c1 = (l.x*l.x + l.y*l.y - ycurrent*ycurrent)/dp;
		
		double dp2 = 2*(r.y - ycurrent);
		double a2 = 1/dp2;
		double b2 = -2*r.x/dp2;
		double c2 = (r.x*r.x + r.y*r.y - ycurrent*ycurrent)/dp2;
		
		double a = a1-a2;
		double b = b1-b2;
		double c = c1-c2;
		
		double disc = b*b - 4*a*c;
		double x1 = (-b + Math.sqrt(disc))/(2*a);//calculating the roots of the equation
		double x2 = (-b - Math.sqrt(disc))/(2*a);//calculating the roots of the equation
		
		double ry;
		if (l.y > r.y) ry = Math.max(x1, x2);//if the y coordinate of the left point is greater than the right
		else ry = Math.min(x1, x2);
		
		return ry;//returns the current x coordinates of an unfinished edge
	}
	
	// returns the parabola above this x coordinate in the beach line
	private Parabola getParabolaByX (double xx) {
            //xx is the parabola above this x coordinate in the beach line
		Parabola par = root;
		double x = 0;
		while (par.type == Parabola.IS_VERTEX) {//while it is a vertex that bisects 2 sites
			x = getXofEdge(par); //x is the the current x coordinates of an unfinished edge
			if (x>xx) 
                            par = par.left_child;
			else 
                            par = par.right_child;
		}
		return par;//returns the parabola above this x coordinate in the beach line
	}
	
	// finds the corresponding y coordinates to x on the parabola with focus p
	private double getY(Point p, double x) {// find the corresponding y coordinates to x on the parabola with focus p
		// determines the equation for the parabola around focus p
		double dp = 2*(p.y - ycurrent);
		double a1 = 1/dp;
		double b1 = -2*p.x/dp;
		double c1 = (p.x*p.x + p.y*p.y - ycurrent*ycurrent)/dp;
		return (a1*x*x + b1*x + c1);
	}
	

}