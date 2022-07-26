//Represents a line segment on the x-y plane, where each have an x coordinate and y coordinate

import java.io.*;
import java.util.*;
import java.awt.*;  
import javax.swing.*;

import org.decimal4j.util.DoubleRounder;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.geom.*; 

public class Tree {

	private NodePoint root;
	private int maxSplits = 1;
	private static PrintWriter pw;
	private Graphics2D g2;
	private Graphics g3;
	private int mid = 400;
	private int segLength = 8;
	public HashSet<NodePoint> intersections;
	
	public Tree(int x, int y) {
		
		root = new NodePoint(x, y);
		g2 = null;
	}
	
	public Tree(NodePoint np) {
		
		root = np;
		
		try {
			pw = new PrintWriter("test.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
//		g2 = null;
		intersections = new HashSet<NodePoint>();
	}
	
	//File output to test.txt
	public void printTree(NodePoint np, PrintWriter of) {
		
		if (np == null)
			return;
			
		String toPrint = np.toString() + " " + np.getNumChildren();
		
		for (int i = 0; i < np.getNumChildren(); i++) {
			toPrint += " " + np.getChild(i);
//			index++;
		}
		
//		System.out.println(toPrint);
//		of.printf(toPrint + "\r\n");
		
		for (NodePoint child : np.getChildren())
			printTree(child, of);
	
	}
	
	
	//Non-intersection algorithm
	public double[] detIntersection(NodePoint np1, NodePoint np2, NodePoint np3, NodePoint np4) {
		
		double x0 = np1.getX(); double x1 = np2.getX(); double x2 = np3.getX(); double x3 = np4.getX();
		double y0 = np1.getY(); double y1 = np2.getY(); double y2 = np3.getY(); double y3 = np4.getY();
		
		
		double snum = x2 * (y0 - y1) + x0 * (y1 - y2) + x1 * (-1 * y0 + y2);
		double sdenom = (x2 - x3) * (y0 - y1) - (x0 - x1) * (y2 - y3);
		
		double tnum = x3 * (y0 - y2) + x0 * (y2 - y3) + x2 * (-1 * y0 + y3);
		double tdenom = -1 * (x2 - x3) * (y0 - y1) + (x0 - x1) * (y2 - y3);
		
		if (sdenom == 0 && tdenom == 0) {
			
			
			double top = Math.max(y0, y3);
			double bottom = Math.min(y0, y3);
			
			double topOrig = Math.max(y0, y1);
			double bottomOrig = Math.min(y0, y1);
			
			double intersection = (y3 + y1) / 2;
					
			if (x0 == x1 && x1 == x2 && x2 == x3) {
			
				
//				System.out.println("\nhere: " + np1 + ", " + np2 + ", " + np3 + ", " + np4);
//				System.out.println("We here at: " + x2);
				
				if ((y2 >= bottomOrig && y2 <= topOrig) || (y3 >= bottomOrig && y3 <= topOrig))
					return new double[] {x0, intersection, Math.abs(y0 - intersection) / segLength};
				

				
			} 
			
			if (y0 == y1 && y1 == y2 && y2 == y3) {
				
				top = Math.max(x0, x3);
				bottom = Math.min(x0, x3);
				
				topOrig = Math.max(x0, x1);
				bottomOrig = Math.min(x0, x1);
				
				intersection = (x3 + x1) / 2;
				
//				System.out.println("X3: " + x3);
				if ((x2 >= bottomOrig && x2 <= topOrig) || (x3 >= bottomOrig && x3 <= topOrig))
					return new double[] {intersection, y0, Math.abs(x0 - intersection) / segLength};
				
				
 
				
				
				
				
			}
//			System.out.println("null is being returned\n");
			return null;
		}
		
		double s = snum / sdenom;
		double t = tnum / tdenom;
		
		s = rounder(s, 3);
		t = rounder(t, 3);
		
		if ((s > 0 && s <= 1) && (t > 0 && t <= 1)) {
//			System.out.println("T value: " + t);
			
			//return an array
			double[] toReturn = {rounder((x0 + (x1 - x0) * t), 3), rounder((y0 + (y1 - y0) * t), 3), t};
			return toReturn;
		}
		
		return null;
	}
	
	public double rounder(double value, int places) {
		return DoubleRounder.round(value, places);
	}
	//Checks existing segments, determining intersections if they exist and returns intersection point
	public double[] checkExisting(NodePoint initial, NodePoint test) {
		
		Queue<NodePoint> allChildren = new LinkedList<NodePoint>();
		  
		NodePoint curNode = root;
		double[] toReturn = null;
		double minT = 1.1;
		  
		allChildren.add(curNode);
		
		do {
			  
			  curNode = allChildren.remove();
			  
			
			  for (NodePoint np : curNode.getChildren()) {
				  
				  
				  if (curNode.equals(initial) || np.equals(initial))
					  continue;
				  
//				  System.out.println("CurNode: " + curNode + " np: " + np);
				  double[] values = detIntersection(initial, test, curNode, np);
				  
				  if (values != null)
					  System.out.println("So detIntersection returned a t val of: " + values[2] + " when we passed in: " + curNode + " and " + np);
//				  else System.out.println();
				  
				  if (values != null && values[2] < minT) {
					  System.out.println("New min intersection found at: " + new NodePoint(values[0], values[1]) + " with t value: " + values[2] + "\n");
					  toReturn = values;
					  minT = values[2];
				  }
				  
				  allChildren.add(np);
			  }
			  
		 } while (! allChildren.isEmpty());
		  
		if (toReturn != null) {
			System.out.println("So the closest intersection is: " + new NodePoint(toReturn[0], toReturn[1]));
		}
		
		return toReturn;
	}
	
	public HashSet<NodePoint> newCheckExisting(NodePoint initial, NodePoint test) {
		
		Queue<NodePoint> allChildren = new LinkedList<NodePoint>();
		  
		NodePoint curNode = root;
		HashSet<NodePoint> toReturn = new HashSet<NodePoint>();
		  
		allChildren.add(curNode);
		
		do {
			  
			  curNode = allChildren.remove();
			  
			
			  for (NodePoint np : curNode.getChildren()) {
				  
				  
				  if (curNode.equals(initial) || np.equals(initial))
					  continue;
				  
//				  System.out.println("CurNode: " + curNode + " np: " + np);
				  double[] values = detIntersection(initial, test, curNode, np);
				  
				  
				  if (values != null) {
					  NodePoint inter = new NodePoint(values[0], values[1]);
//					  System.out.println("Intersection found at: " + inter + " with t value: " + values[2] + "");
					  intersections.add(inter);
					  toReturn.add(inter);
				}
				  
				  allChildren.add(np);
			  }
			  
		 } while (! allChildren.isEmpty());
		
		return toReturn;
	}
	
	public NodePoint getMaxInter(HashSet<NodePoint> intersections, NodePoint initial) {
		
		double maxDist = 0; NodePoint toReturn = null;
		
		for (NodePoint inter : intersections) {
			
			double curDist = initial.distance(inter);
			if (curDist > maxDist) {
				maxDist = curDist; 
				toReturn = inter;
			}
		}
		
		return toReturn;
	}
	
	public boolean onSegment(NodePoint p, NodePoint q, NodePoint r) {
		
		double x0 = p.getX(); double x1 = q.getX(); double x = r.getX();
		double y0 = p.getY(); double y1 = q.getY(); double y = r.getY();
//		System.out.println("VALUE NEEDED: " + DoubleRounder.round(Math.abs((x1 - x0) * (y - y0) - (x - x0) * (y1 - y0)), 4));
	    if (rounder(Math.abs((x1 - x0) * (y - y0) - (x - x0) * (y1 - y0)), 2) == 0)
	    	return (x >= Math.min(x0, x1) && x <= Math.max(x0, x1) && y >= Math.min(y0, y1) && y <= Math.max(y0, y1));
	    return false;
	}
	
	
	
	public NodePoint nonIntNode(NodePoint initial, NodePoint intersection, double t) {
		
		double newX = initial.getX() + t * (intersection.getX() - initial.getX());
		double newY = initial.getY() + t * (intersection.getY() - initial.getY());
		newX = rounder(newX, 3);
		newY = rounder(newY, 3);
//		System.out.println("New X: " + newX + ", New Y: " + newY);
		return new NodePoint(newX, newY);
	}
	
	//Returns a random terminal node with a specified length whose direction is in the range [0, 2π]
	public NodePoint randomNode(NodePoint start, int length) {
		
		double startX = start.getX();
		double startY = start.getY();
		
        double theta = 2 * Math.PI * Math.random();
        
		return new NodePoint(startX + length * Math.cos(theta), startY + length * Math.sin(theta));
	}
	
	public double[] getAngles(int n) {
		
		double[] toReturn = new double[n];
		double startVal = 0;
		
		if (n % 2 == 1)
			startVal = Math.PI / 2;

		toReturn[0] = startVal;
		
		
		for (int i = 1; i < n; i++) {
			toReturn[i] = startVal + 2 * Math.PI / n;
			startVal += 2 * Math.PI / n;
		}
		
		return toReturn;
	}
	
	//Outputting plots
 	class CartesianFrame extends JFrame {
 		
		 CartesianPanel panel;
		 
		 public CartesianFrame() {
			 panel = new CartesianPanel();
			 add(panel);
		 }
		 
		 public void showUI() {
			  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			  setTitle("Output Plot");
			  setSize(800, 800);
			  setVisible(true);
		 }
 	}
 	
	class CartesianPanel extends JPanel {
		
	
		// x-axis coord constants
		public final int X_AXIS_FIRST_X_COORD = 0;
		public final int X_AXIS_SECOND_X_COORD = 800;
		public final int X_AXIS_Y_COORD = mid;
		 
		// y-axis coord constants
		public final int Y_AXIS_FIRST_Y_COORD = 0;
		public final int Y_AXIS_SECOND_Y_COORD = 800;
		public final int Y_AXIS_X_COORD = mid;
		
		private int tickLength = 3;
		
		// size of start coordinate length
		public static final int ORIGIN_COORDINATE_length = 6;
		 
		// distance of coordinate strings from axis
		public static final int AXIS_STRING_DISTANCE = 20;
		 		 
		public void paintComponent(Graphics g) {
			
			String dest = "/Users/saunakroy/Desktop/NIST/OutputPDFs/Tree.pdf";
			  
			Document document = new Document(new Rectangle(800, 800));
			PdfWriter writer = null;
			
			
			try {
				writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
				document.open();
				writer.open();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			 
		 	PdfContentByte canvas = writer.getDirectContent();
//	        PdfTemplate template = canvas.createTemplate(150, 150);

		  
//			super.paintComponent(g);
		  
			g2 = canvas.createGraphics(800, 800);
			g3 = (Graphics2D) g;
		  
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		  
		  	g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//		  	g3.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			g2.setColor(Color.LIGHT_GRAY);
			g2.setStroke(new BasicStroke(0.2f));
			
			g2.setColor(Color.LIGHT_GRAY);
			
		    // x-axis
		    g2.drawLine(X_AXIS_FIRST_X_COORD, X_AXIS_Y_COORD, X_AXIS_SECOND_X_COORD, X_AXIS_Y_COORD);
		    // y-axis
		    g2.drawLine(Y_AXIS_X_COORD, Y_AXIS_FIRST_Y_COORD, Y_AXIS_X_COORD, Y_AXIS_SECOND_Y_COORD);
		    
		    
		    g3.drawLine(X_AXIS_FIRST_X_COORD, X_AXIS_Y_COORD, X_AXIS_SECOND_X_COORD, X_AXIS_Y_COORD);
		    // y-axis
		    g3.drawLine(Y_AXIS_X_COORD, Y_AXIS_FIRST_Y_COORD, Y_AXIS_X_COORD, Y_AXIS_SECOND_Y_COORD);

		    
		    // draw origin Point
//		    g2.fillOval(424.5, 424.5, 1, 1);
		    
		    Ellipse2D.Double origin = new Ellipse2D.Double(399.5, 399.5, 1, 1);
		    g2.draw(origin);
		    g2.fill(origin);
		    
		  
		    
		    // numerate axis
		    int xCoordNumbers = 80;
		    int yCoordNumbers = 80;
		    int xLength = (mid*2) / xCoordNumbers;
		    int yLength = (mid*2) / yCoordNumbers;
		  
		    // draw x-axis numbers
		    for (int i = 0; i <= xCoordNumbers; i++) {
		    	
		    	if (i == 40) continue;
		    	
		    	if (i % 5 != 0) tickLength = 1;
		    	else tickLength = 2;
		    	
		    	g2.drawLine((i * xLength),
		   		     X_AXIS_Y_COORD - tickLength,
		   		     (i * xLength),
		   		     X_AXIS_Y_COORD + tickLength);
		    	
		    	g3.drawLine((i * xLength),
			   		     X_AXIS_Y_COORD - tickLength,
			   		     (i * xLength),
			   		     X_AXIS_Y_COORD + tickLength);
		    	
				if (i % 5 != 0) continue;

				// TODO slightly shift numbers when negative
//		    	g2.drawString(Integer.toString(i-40), 
//		   		     (i * xLength) - 3,
//		   		     X_AXIS_Y_COORD + (i * xLength));
		    }
		  
		    //draw y-axis numbers
		    for (int i = 0; i <= yCoordNumbers; i++) {
			  
				if (i == 40) continue;
				  
				if (i % 5 != 0) tickLength = 1;
				else tickLength = 2;
				  
				g2.drawLine(Y_AXIS_X_COORD - tickLength,
					 mid*2 - (i * yLength), 
				     Y_AXIS_X_COORD + tickLength,
				     mid*2 - (i * yLength));
				
				g3.drawLine(Y_AXIS_X_COORD - tickLength,
						 mid*2 - (i * yLength), 
					     Y_AXIS_X_COORD + tickLength,
					     mid*2 - (i * yLength));
				   
				if (i % 5 != 0) continue;
				  
				// TODO slightly shift numbers when negative
	
//				g2.drawString(Integer.toString(i-40), 
//				     Y_AXIS_X_COORD - (i * yLength), 
//				     mid*2 - (i * yLength));
		     }
			 
		     g2.setStroke(new BasicStroke(0.05f));
	         Queue<NodePoint> allChildren = new LinkedList<NodePoint>();
				  
			 NodePoint curNode = root;
			 allChildren.add(curNode);
			 do {
				 
				 curNode = allChildren.remove();
				 for (NodePoint np : curNode.getChildren()) {
					 drawSegment(curNode.getX(), curNode.getY(), np.getX(), np.getY(), np);
					 allChildren.add(np);
				 }
				 drawPoint(curNode);
			 } while (! allChildren.isEmpty());
			  
			 g2.dispose();
//		 	 canvas.addTemplate(template, 0, 0);
//		  	 document.newPage();		 		
			 document.close();
		 		
		 	  
//			  drawSegment(g2, 3, 4, 1, 1);
//			  drawSegment(g2, 5, 6, 7, 4);
//			  drawSegment(g2, 7, 4, 6, 1);
//			  drawSegment(g2, 6, 6, 3, 4);
			  
		}
		
		
		 
		public void drawSegment(double x1, double y1, double x2, double y2, NodePoint np) {
		     
			 g2.setColor(Color.BLACK);
			 g2.draw(new Line2D.Double(mid + 10*x1, mid - 10*y1, mid + 10*x2, mid - 10*y2));
			 
			 g3.setColor(Color.BLACK);
			 g3.drawLine((int) (mid + 10*x1),  (int) (mid - 10*y1),  (int) (mid + 10*x2),(int) (mid - 10*y2));

			 
		}
		
		public void drawPoint(NodePoint np) {
		     
			 int nodeLevel = np.getLevel();
			 g2.setColor(np.getColor());
			 
			 // TODO scale
			 
			 ArrayList<Double> sizes = new ArrayList<Double>();
			 
			 for (int i = 0; i < maxSplits-1; i++) 
				 
				 sizes.add(0.05 + i * 0.04);
			 //0.04 for end
			 sizes.add(0.04);
//			 double[] sizes = {0.65334, 0.57666, 0.5, 0.4233333, 0.34666, 0.26999, 0.1933333, 0.116666, 0.04};
			 double size = sizes.get(maxSplits-1 - nodeLevel);
			 Ellipse2D.Double shape = new Ellipse2D.Double(mid - size/2 + 10*np.getX(), mid - size/2 - 10*np.getY(), size, size);
			 
			 g2.draw(shape);
			
			 if (nodeLevel == maxSplits-1)
				 g2.fill(shape);

		}
	}
	
	public void randomTrinaryTree(NodePoint root, int numSplits, Color[] levelColors) {
		
       
        int ternaryTracker = (int) (0.5 * (Math.pow(3, numSplits) - 1)); //work up to 333 for 999 branches 1093 for 6
        
        maxSplits = numSplits;
        
        ArrayList<Integer> levels = new ArrayList<Integer>();
        int levelTracker = 1;
        
        
        //2187 is next value
        for (int i = 1; i < (int) Math.pow(3, numSplits); i *= 3) {
        	
        	for (int z = 0; z < i; z++) {
        		levels.add(levelTracker);
        		
        	}
        	levelTracker++;
        }
        
        System.out.println(levels);
        
             
        Queue<NodePoint> storage = new LinkedList<NodePoint>();
        storage.add(root);
        
        for (int i = 0; i < maxSplits; i++) {
        	
        	System.out.println("\n" + (i+1) + " Split:\n");
        	
        	//Make branches without considering intersections
        	for (int c = 0; c < Math.pow(3, i); c++) {
        		NodePoint curNode = storage.remove();
        		
        		
        		for (int z = 0; z < 3; z++) {
//	        		
        			NodePoint testNode = randomNode(curNode, segLength); 
        			System.out.println("The randomly generated node is: " + testNode + ", and the current node is: " + curNode);
	        	
        			double[] values = checkExisting(curNode, testNode);
//	        		
        			if (values != null) {
        				testNode = nonIntNode(curNode, new NodePoint(values[0], values[1]), 0.8);
        				System.out.println("Intersection occurs at: " + new NodePoint(values[0], values[1]));
        			}else
        				System.out.println("This should not intersect any previous line \n");
	        	
	        
				
					testNode.setColor(levelColors[i]);
		        	testNode.setLevel(i);
		        	curNode.addChild(testNode);
					storage.add(testNode);

        		}
        	}
        }
        
//        for (int i = 0; i < ternaryTracker; i++) {
//        	
//	        NodePoint curNode = storage.remove();
////	        curNode.setLevel(levels.get(i));
//        	System.out.println("I: " + i);
//        	
//
//	        for (int z = 0; z < 3; z++) {
////	        		
//	        	NodePoint testNode = randomNode(curNode, segLength); 
//        		System.out.println("The randomly generated node is: " + testNode + ", and the current node is: " + curNode);
//	        	
//	        	double[] values = checkExisting(curNode, testNode);
////	        		
//	        	if (values != null) {
//        			testNode = nonIntNode(curNode, new NodePoint(values[0], values[1]), 0.8);
//	        		System.out.println("Intersection occurs at: " + new NodePoint(values[0], values[1]));
//	        	}else
//	        		System.out.println("This should not intersect any previous line \n");
//	        	
//	        	testNode.setLevel(levels.get(i));
//				curNode.addChild(testNode);
//				storage.add(testNode);
//	        }
//	        
//        }
      
       
	}


    public static void main(String[] args) {
    	
    	
//    	Scanner fileIn = null;
//    	
//    	fileIn = new Scanner("test.txt");
//    	
//    	while (fileIn.hasNextLine()) {
//    		String[] line = fileIn.nextLine().split(" ");
//    		
//    		
//    	}
    	
    	Color[] levelColors = {Color.RED, Color.YELLOW, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.ORANGE, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.RED, Color.DARK_GRAY, Color.cyan, Color.PINK};
    	
    	
    	
//        NodePoint root = new NodePoint(5, 6);
//        NodePoint np2 = new NodePoint(3, 4);
//        NodePoint np3 = new NodePoint(7, 4);
//        NodePoint np4 = new NodePoint(1, 1);
//        NodePoint np5 = new NodePoint(3, 1);
//        NodePoint np6 = new NodePoint(6, 1);
    	
		NodePoint root = new NodePoint(0, 0);
//	    NodePoint np2 = new NodePoint(7, 5);
//	    NodePoint np3 = new NodePoint(6.5, 6.25);
//	    NodePoint np4 = new NodePoint(13, 8.75);
//	    NodePoint np5 = new NodePoint(8.59, 9);
//	    NodePoint np6 = new NodePoint(6, 10);
//	    NodePoint np7 = new NodePoint(4, 5);
	    
//        root.addChild(np2);
//        root.addChild(np3);
//        root.addChild(np4);
//        np3.addChild(np5);
//        np5.addChild(np7);
//        np2.addChild(np6);
//        np2.addChild(np7);
       
        Tree thisTree = new Tree(root);
//        thisTree.randomTrinaryTree(root, 7, levelColors);
        int n = 2;
        int circleRadius = 37;
        
        thisTree.maxSplits = 6;
        double newDist = 0.8;
        Queue<NodePoint> storage = new LinkedList<NodePoint>();
        Queue<NodePoint> curHeads  = new LinkedList<NodePoint>();
        double[] angleArray = thisTree.getAngles(n);

        storage.add(root);
                
        for (int i = 0; i < thisTree.maxSplits; i++) {
        	
        	System.out.println("\n" + (i+1) + " Split:\n");
        	
        	//Make branches without considering intersections
        	for (int c = 0; c < Math.pow(n, i); c++) {
        		NodePoint curNode = storage.remove();
        		
        		curHeads.add(curNode);
    	        for (int z = 0; z < n; z++) {
    	        	
    	        	double tempX = Math.cos(angleArray[z]);
    	        	double tempY = Math.sin(angleArray[z]);
    	        	
    	        	if (n % 2 == 0 && (i+1) % 2 == 0) {
    	        		tempX = Math.cos(angleArray[z] + Math.PI/n);
    	        		tempY = Math.sin(angleArray[z] + Math.PI/n);
    	        	}
    	        	
    	        	double newX = thisTree.rounder(curNode.getX() + thisTree.segLength * tempX, 3);
    	        	double newY = thisTree.rounder(curNode.getY() + thisTree.segLength * tempY, 3);
    	        	
    	        	NodePoint testNode = new NodePoint(newX, newY);
    	        	
    	        	if (testNode.distance(root) > circleRadius) {
//    	        		
    	        		double theta = Math.asin(testNode.getY() / testNode.distance(root));
    	        		
    	        		if (testNode.getX() < 0)
    	        			theta = Math.PI - theta;
    	        		
    	        		double xVal = circleRadius * Math.cos(theta);
    	        		double yVal = circleRadius * Math.sin(theta);
    	        		
    	        		testNode = new NodePoint(xVal, yVal);
//    	        		
    	        	}
//    	        	System.out.println("Current node: " + curNode + " -- Generated: " + testNode);
    	        	
	        		testNode.setColor(levelColors[i]);
    	        	testNode.setLevel(i);
    	        	curNode.addChild(testNode);
    				storage.add(testNode);

    	        }
        	}
        	
        	
        	//Checking and storing intersections for level
        	System.out.println("Heads: " + curHeads);
//        	ArrayList<NodePoint> intersections = new ArrayList<NodePoint>();
            
            
            for (NodePoint head : curHeads) {
            	
            	for (NodePoint np : head.getChildren()) {
            		
            		System.out.println("Checking1: " + head + " and " + np);

            		
            		System.out.println(thisTree.newCheckExisting(head, np) + " intersections found for this one.");
//            		double[] values = thisTree.checkExisting(head, np);
            		
//            		if (values != null) {
            			
            			
//            			NodePoint intersection = new NodePoint(values[0], values[1]);
            			
//    	        		intersections.add(intersection);
            		
//            		}
            	}
            }

            System.out.println("ALL THE INTERSECTIONS: "  + thisTree.intersections.toString());
            System.out.println(thisTree.intersections.size() + " total intersections.");
            
            
            for (NodePoint head : curHeads) {
    			
    			
    			for (NodePoint np : head.getChildren()) {
    				
    				if (thisTree.intersections.contains(np)) {
    					
//    					System.out.println(np + " is in intersections.");
    					NodePoint nonInt = thisTree.nonIntNode(head, np, newDist);
        				np.set(nonInt.getX(), nonInt.getY());
//        				System.out.println(thisTree.intersections.remove(np));
        				
    				}
    			}
    			
            }
            
            thisTree.intersections.clear();
            
            for (NodePoint head : curHeads) {
    			
    			
    			for (NodePoint np : head.getChildren()) {
    				
    				thisTree.newCheckExisting(head, np);
    			}
            }


            System.out.println("\nIntersections after removing the first nodes: " + thisTree.intersections);
            int maxInts = 0;
            
            
            do {
            	
            	maxInts = 0;
            	int size = curHeads.size();
            	for (int v = 0; v < size; v++) {
        			
        			NodePoint temp = curHeads.remove();
        			
        			for (NodePoint np : temp.getChildren()) {
            			
            			HashSet<NodePoint> inters = new HashSet<NodePoint>();
            			
            			for (NodePoint inter : thisTree.intersections) {
            				if (! inter.equals(temp) && thisTree.onSegment(temp, np, inter))
            					inters.add(inter);
            					
            			}
            		
            			if (inters.size() > maxInts)
            				maxInts = inters.size();
            			
            			NodePoint farthest = thisTree.getMaxInter(inters, temp);
            			
            			
            			if (maxInts > 0)
            				System.out.println("FROM: " + temp + " to " + np + " -- Inters: " + inters + " maxInts: " + maxInts + " farthest: " + farthest);
            			
            			
            			if (inters.size() > 0) {
            				NodePoint nonInt = thisTree.nonIntNode(temp, farthest, newDist);
            				np.set(nonInt.getX(), nonInt.getY());
            			}
            			
        			}
        			
        			
        			curHeads.add(temp);
        		}
            	
            			
            	
            	
            } while (maxInts > 0);

 
            curHeads.clear();


            thisTree.intersections.clear();
            
            
            
            
        	
        }
        
        
        
        NodePoint np3 = new NodePoint(0.0, 8);
        NodePoint np4 = new NodePoint(-7.799, 6.22);
        NodePoint np5 = new NodePoint(-6.255, 4.988);
//        
//        
//        System.out.println(thisTree.onSegment(np3, np4, np5));
       
        NodePoint np6 = new NodePoint(-12.51, 9.976);
//        System.out.println(thisTree.detIntersection(np3, np4, np5, np6));
        
        
        
        
        
        /*
        Queue<NodePoint> tempStorage = new LinkedList<NodePoint>();
        HashSet<String> intersections = new HashSet<String>();
        
        NodePoint curNode = root;
        tempStorage.add(curNode);
        
        do {
        	
        	curNode = tempStorage.remove();
        	
        	for (NodePoint np : curNode.getChildren()) {
        		tempStorage.add(np);
//        		System.out.println("Now checking: " + curNode + " and  " + np);
        		double[] values = thisTree.checkExisting(curNode, np);
        		
        		if (values != null) {
        			
	        		System.out.println(new NodePoint(values[0], values[1]));
	        		intersections.add(new NodePoint(values[0], values[1]).toString());
        		}
        		
        	}
        } while (! tempStorage.isEmpty());
        
        
        
//        System.out.println(intersections.toString());
//        
//        NodePoint fdsa = new NodePoint(6.9282, 4.0);
//        
//        System.out.println(intersections.contains(fdsa.toString()));
        Queue<NodePoint> finalIterate = new LinkedList<NodePoint>();
        curNode = root;
        finalIterate.add(curNode);

        do {
        	
        	curNode = finalIterate.remove();
        	
        	for (NodePoint np : curNode.getChildren()) {
        		finalIterate.add(np); 
        		
        		if (intersections.contains(np.toString())) {
        			
        			NodePoint newNode = thisTree.nonIntNode(curNode, np, 0.8);
        			System.out.println(newNode);
        			np.set(newNode.getX(), newNode.getY());
        		}
        	}
        } while (! finalIterate.isEmpty());
        */
        
//        NodePoint np3 = new NodePoint(2, 2);
//        NodePoint np4 = new NodePoint(0, 4);
//        NodePoint np5 = new NodePoint(2, 0);
//        NodePoint np6 = new NodePoint(2, 6);
//        NodePoint intersection = new NodePoint(1, 6);
//        
//        root.addChild(np3);
//        root.addChild(np4);
//        root.addChild(np5);
//        np4.addChild(np6);
//        
////        Tree thisTree = new Tree(root);
//
//    	double[] values = thisTree.detIntersection(np5, intersection, root, np3);
////    	double[] values = thisTree.checkExisting(np5, intersection);
//    	
//    	intersection = new NodePoint(values[0], values[1]);
//    	System.out.println("This should be the intersection: " + intersection);
//		NodePoint testNode = thisTree.nonIntNode(np5, new NodePoint(values[0], values[1]), 0.8);
//        
//		np5.addChild(testNode);
//    	System.out.println(values[0] + ", " + values[1] + " here");
    	
    	
//        for (int i = 0; i < ternaryTracker; i++) {
        	
//        	NodePoint np8 = new NodePoint(((Math.random() * 4)) + 7, 12);
//        	
//        	
//        	
//        	double[] values = thisTree.detIntersection(np3, np8, np2, np7);
////        System.out.println(values + " is where they intersect");
//        	
//        	if (values != null) {
//        		
//        		NodePoint inters = new NodePoint(values[0], values[1]);
//        		//function returns intersection point, as well as t
//        		np8 = thisTree.nonIntNode(np3, inters, values[2]);
//        		
//        		
//        		
//        		
//        		np8 = new NodePoint(10, 13);
//        	}
//        	
//        	np3.addChild(np8);
//        	np3.addChild(new NodePoint(9, 10));
//        	System.out.println(np3.getChild(0));
//        	
//        	ternaryTracker *= 3;
//        }
        
        
//        thisTree.printTree(thisTree.root, pw);
        
        //Viewing Tree
        CartesianFrame frame = thisTree.new CartesianFrame();
	    frame.showUI();
    	
//	    System.out.println(thisTree.detIntersection(np2, new NodePoint(8, 2), np6, new NodePoint(6, 4)));
//	    frame.drawSegment(3, 3, 5, 5);
       
    }
}