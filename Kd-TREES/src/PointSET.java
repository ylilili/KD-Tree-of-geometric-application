import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {

	private TreeSet<Point2D> setUnitSquare = new TreeSet<>();
	
	// construct an empty set of points
	public PointSET() {}
	
	// is the set empty? 
	public boolean isEmpty(){
		return setUnitSquare.isEmpty();
	} 
	
	// number of points in the set 
	public int size() {
		return setUnitSquare.size();
	}
	
	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p){
		validate_Point(p);
		if(!contains(p))
			setUnitSquare.add(p);
	}
	
	// does the set contain point p?
	public boolean contains(Point2D p){
		validate_Point(p);
		return setUnitSquare.contains(p);
	}
	
	// draw all points to standard draw 
	public void draw() {
		for(Point2D point: setUnitSquare)
			point.draw();
	}
	
	// all points that are inside the rectangle (or on the boundary) 
	public Iterable<Point2D> range(RectHV rect){
		validate_Rect(rect);
		Deque<Point2D> setRange = new ArrayDeque<>();
		for(Point2D point: setUnitSquare) {
			if(rect.contains(point))
				setRange.add(point);
		}
		return setRange;
	}
	
	// a nearest neighbor in the set to point p; null if the set is empty
	public Point2D nearest(Point2D p) {
		validate_Point(p);
		if(setUnitSquare == null || size() == 0)
			return null;
		Point2D nearestPoint = setUnitSquare.first();
		for(Point2D point: setUnitSquare) {
			if(p.distanceSquaredTo(nearestPoint) > p.distanceSquaredTo(point))
				nearestPoint = point;
		}
		return nearestPoint;
//		Object[] temp = setUnitSquare.toArray();
//		Arrays.sort(temp, p.distanceToOrder());
//		return temp[0];
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
		PointSET set = new PointSET();
		Point2D p1 = new Point2D(0.0, 0.0);
		set.insert(p1);
		RectHV rect1 = new RectHV(0.0, 0.0, 0.0, 1.0);
		System.out.println(set.range(rect1));
		set.insert(p1);
		Point2D p2 = new Point2D(0.0, 1.0);
		System.out.println(set.nearest(p2));
		RectHV rect2 = new RectHV(0.0, 1.0, 1.0, 1.0);
		System.out.println(set.range(rect2));
	}

}
