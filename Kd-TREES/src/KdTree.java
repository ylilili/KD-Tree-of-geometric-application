import java.util.ArrayDeque;
import java.util.Deque;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

	private static final int ODD = 1;
	private static final int EVEN = 0;
	private Node root;
	private Point2D nearestPoint = null;
	
	// construct an empty set of points
	public KdTree() {}

	private class Node {
		public Point2D p;
		public RectHV rect;
		public Node left = null;
		public Node right = null;
		public int orientation;
		public double x_axis;
		public double y_axis;
		public int count;
		
		public Node(Point2D p, int oriental){
			this.p = p;
			this.orientation = oriental;
//			if(this.orientation == EVEN)
//				this.rect = new RectHV(this.p.x(), edge1, this.p.x(), edge2);
//			if(this.orientation == ODD)
//				this.rect = new RectHV(edge1, this.p.y(), edge2, this.p.y());
			this.x_axis = this.p.x();
			this.y_axis = this.p.y();
		}
	}
		
	// is the set empty? 
	public boolean isEmpty(){
		return this.root == null;
	} 
		
	// number of points in the set 
	public int size() {
		if(isEmpty())
			return 0;
//		this.count = 0;
		return size(this.root);
//		return this.count;
	}
	
	private int size(Node node) {
		if(node == null) 
			return 0;
		return node.count;
	}
		
	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p){
		validate_Point(p);
		this.root = insert(this.root, p, EVEN, 0.0, 0.0, 1.0, 1.0);
	}
	
	private Node insert(Node node, Point2D p, int oriental, double edge1, double edge2, double edge3, double edge4) {
		if(node == null) {
			Node newNode = new Node(p, oriental);
			newNode.rect = new RectHV(edge1, edge2, edge3, edge4);
			newNode.count = 1;
			return newNode;
		}
		
		if(node.orientation == EVEN) {
			if(p.x() >= node.x_axis) 
				if(!p.equals(node.p))
					node.right = insert(node.right, p, ODD, node.x_axis, node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
			if(p.x() < node.x_axis) 		
				node.left = insert(node.left, p, ODD, node.rect.xmin(), node.rect.ymin(), node.x_axis, node.rect.ymax());
		}
		
		if(node.orientation == ODD) {
			if(p.y() >= node.y_axis) 
				if(!p.equals(node.p))
					node.right = insert(node.right, p, EVEN, node.rect.xmin(), node.y_axis, node.rect.xmax(), node.rect.ymax());
			if(p.y() < node.y_axis)    
				node.left = insert(node.left, p, EVEN, node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.y_axis);
		}	
		
		node.count = 1 + size(node.left) + size(node.right);
		return node;
	}
		
	// does the set contain point p?
	public boolean contains(Point2D p){
		validate_Point(p);
		return contains(this.root, p);
	}
	
	private boolean contains(Node node, Point2D p) {
		if(node == null)
			return false;
//		if(node.p.equals(p))
//			return true;
//		return contains(node.left, p) || contains(node.right, p);
		if(node.orientation == EVEN) {
			if(p.x() < node.x_axis)
				return contains(node.left, p);
			else if(node.p.equals(p))
				return true;
			else
				return contains(node.right, p);
		} else {
			if(p.y() < node.y_axis)   
				return contains(node.left, p);
			else if(node.p.equals(p))
				return true;
			else
				return contains(node.right, p);	
		}	
	}
	// draw all points to standard draw 
	public void draw() {
		Node node = root;
		draw(node);
	}
	
	private void draw(Node node) {
		if(node == null) return;
		
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(0.01);
		node.p.draw();
		
		if(node.left != null)
			draw(node.left);
		
		if(node.right != null)
			draw(node.right);
		
		RectHV splitline = null;
		if(node.orientation == EVEN) {
			StdDraw.setPenColor(StdDraw.RED);
			splitline = new RectHV(node.x_axis, node.rect.ymin(), node.x_axis, node.rect.ymax());
		}
			
		if(node.orientation == ODD) {
			StdDraw.setPenColor(StdDraw.BLUE);
			splitline = new RectHV(node.rect.xmin(), node.y_axis, node.rect.xmax(), node.y_axis);
		} 
			
		
		StdDraw.setPenRadius();
		splitline.draw();
	}
	
		
	// all points that are inside the rectangle (or on the boundary) 
	public Iterable<Point2D> range(RectHV rect){
		validate_Rect(rect);
		Deque<Point2D> setRange = new ArrayDeque<>();
		range(rect, this.root, setRange);
		return setRange;
	}
	
	private void range(RectHV rect, Node node, Deque<Point2D> setRange) {
		if(node == null) return;
		
		if(node.rect.intersects(rect)) {
			if(rect.contains(node.p))
				setRange.add(node.p);
			range(rect, node.left, setRange);
			range(rect, node.right, setRange);
		}		
	}
		
	// a nearest neighbor in the set to point p; null if the set is empty
	public Point2D nearest(Point2D p) {
		validate_Point(p);	
		if(this.root == null)
			return null;
		this.nearestPoint = this.root.p;
		nearest(p, this.root);
		return this.nearestPoint;
	}
	
	private void nearest(Point2D p, Node node) {		
		if(node == null)
			return;
		
		if(node.rect.distanceSquaredTo(p) > nearestPoint.distanceSquaredTo(p))
			return;
		
		if(node.p.distanceSquaredTo(p) < nearestPoint.distanceSquaredTo(p))
			this.nearestPoint = node.p;
			
		if(node.orientation == EVEN) {
			if(p.x() < node.x_axis) {
				nearest(p, node.left);
				nearest(p, node.right);
			}
			if(p.x() >= node.x_axis) {
				nearest(p, node.right);
				nearest(p, node.left);
			}
		}
		
		if(node.orientation == ODD) {
			if(p.y() < node.y_axis) {
				nearest(p, node.left);
				nearest(p, node.right);
			}
			if(p.y() >= node.y_axis) {
				nearest(p, node.right);
				nearest(p, node.left);
			}
		}
	}
	
	//helper function
	private void validate_Point(Point2D p) {
		if(p == null)
			throw new IllegalArgumentException("The point is illegal.");
	}
		
	private void validate_Rect(RectHV rect) {
		if(rect == null)
			throw new IllegalArgumentException("The argument is illegal.");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KdTree tree = new KdTree();
//		Point2D p1 = new Point2D(0.206107, 0.095492);
//		p1.draw();
//		Point2D p2 = new Point2D(0.975528, 0.654508);
//		p2.draw();
//		Point2D p3 = new Point2D(0.024472, 0.345492);
//		p3.draw();
//		Point2D p4 = new Point2D(0.793893, 0.095492);
//		p4.draw();
//		Point2D p5 = new Point2D(0.793893, 0.904508);
//		p5.draw();
//		Point2D p6 = new Point2D(0.975528, 0.345492);
//		p6.draw();
//		Point2D p7 = new Point2D(0.206107, 0.904508);
//		p7.draw();
//		Point2D p8 = new Point2D(0.500000, 0.000000);
//		p8.draw();
//		Point2D p9 = new Point2D(0.024472, 0.654508);
//		p9.draw();
//		Point2D p10 = new Point2D(0.500000, 1.000000);
//		p10.draw();
//		StdDraw.setPenColor(StdDraw.BLACK);
//		StdDraw.setPenRadius(0.01);
//		Point2D p1 = new Point2D(0.372, 0.497);
////		p1.draw();
//		Point2D p2 = new Point2D(0.564, 0.413);
////		p2.draw();
//		Point2D p3 = new Point2D(0.226, 0.577);
////		p3.draw();
//		Point2D p4 = new Point2D(0.144, 0.179);
////		p4.draw();
//		Point2D p5 = new Point2D(0.083, 0.51);
////		p5.draw();
//		Point2D p6 = new Point2D(0.32, 0.708);
////		p6.draw();
//		Point2D p7 = new Point2D(0.417, 0.362);
////		p7.draw();
//		Point2D p8 = new Point2D(0.862, 0.825);
////		p8.draw();
//		Point2D p9 = new Point2D(0.785, 0.725);
////		p9.draw();
//		Point2D p10 = new Point2D(0.499, 0.208);
////		p10.draw();
		Point2D p1 = new Point2D(0.7, 0.2);
//		p1.draw();
		Point2D p2 = new Point2D(0.5, 0.4);
//		p2.draw();
		Point2D p3 = new Point2D(0.2, 0.3);
//		p3.draw();
		Point2D p4 = new Point2D(0.4, 0.7);
//		p4.draw();
		Point2D p5 = new Point2D(0.9, 0.6);
//		p5.draw();
//		Point2D p1 = new Point2D(0.7, 0.2);
//		Point2D p2 = new Point2D(0.5, 0.4);
//		System.out.println(tree.size());
		//RectHV rect1 = new RectHV(0.027, 0.269, 0.083, 0.231);
		tree.insert(p1);		
		tree.insert(p2);
		tree.insert(p3);
		tree.insert(p4);		
		tree.insert(p5);		
//		tree.insert(p6);
//		tree.insert(p7);
//		tree.insert(p8);
//		tree.insert(p9);
//		tree.insert(p10);

		StdDraw.setPenRadius(0.01);
		StdDraw.setPenColor(StdDraw.RED);
		Point2D p = new Point2D(0.156, 0.465);
		p.draw();
		tree.draw();
		System.out.println(tree.nearest(p).x());
		System.out.println(tree.nearest(p).y());
	}
/*
 * 1. to optimize insert method, forbid using contain method to check before each insert, comparing points in the process to inserting
 * 2. use instance variable instead of get method
 * 3. 
 */
}
