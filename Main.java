package voronoi;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

//main class
public class Main{
	public static void main(String args[]){
		new Frame();
	}
}
class Frame extends JFrame implements ActionListener, MouseListener{
	int V;
	ArrayList<Pair> vertexMapping;
	ArrayList<ArrayList<Pair> > adjList;
	ArrayList<Pair> l;
        JButton b;
        JPanel p = new JPanel();
	public Frame(){//constructor
		V = 0;//number of vertices
		vertexMapping = new  ArrayList<Pair>();
		adjList = new ArrayList<ArrayList<Pair> >();
		l = new ArrayList<Pair>();
		setTitle("Fortune's Algorithm");
		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.addMouseListener(this);
		p.setBackground(Color.GREEN);
		setLayout(new FlowLayout());
		p.setPreferredSize( new Dimension( getWidth(), getHeight() ) );
		add(p);//adding our panel
                b = new JButton("Find Voronoi Diagram");
		b.setBounds(220,65,170,25);
		p.add(b);//adding our button
		b.addActionListener(this);
		
	}
	public void actionPerformed(ActionEvent e){		
               if(e.getSource()==b){
			findVoronoi();//calls the voronoi function when clicked
		}

	}
	public void mouseClicked(MouseEvent e) {//allowing the adding of nodes on clicking
		adjList.add(new ArrayList<Pair>());
		Integer i = new Integer(V);
		int x = e.getX();
		int y = e.getY();
		vertexMapping.add(new Pair(x,y));
		String text = i.toString();
		Graphics g= getGraphics();
		g.setColor(new Color(190,255,190));
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		g.drawString(text,x-5,y+5);
		V++;
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	public void invertEdge(int x,int y,int x1,int y1){//drawing the voronoi edges
		Graphics g= getGraphics();
		g.drawLine(x,y,x1,y1);
	}

	public void findVoronoi(){



		ArrayList<Point> points = new ArrayList<Point>();
            for(int i=0;i<vertexMapping.size();i++)
            {
                Pair p = vertexMapping.get(i);
			Double x = (double)(p.getx()/1000.0);
			Double y = (double)(p.gety()/1000.0);
                        points.add(new Point(x, y));
                        
            }
            System.out.println(points);//printing our coordinates
		VoronoiDiagram diagram = new VoronoiDiagram (points);//calling the voronoi class

		for (Edge e: diagram.edges) {//printing the edges
                       invertEdge((int)(e.start.x*1000), (int)(e.start.y*1000), (int)(e.end.x*1000), (int)(e.end.y*1000));

		}

	}
}
class Pair{//setting and getting x and y coordinates
	int x,y;
	public Pair(int x,int y){
		this.x = x;
		this.y = y;
	}
	public int getx(){
		return x;
	}
	public int gety(){
		return y;
	}
}
class MyComparator implements Comparator<Pair> {//compares two integers
    public int compare(Pair a, Pair b) {
		return (new Integer(a.getx())).compareTo(new Integer(b.getx()));
    }
}

