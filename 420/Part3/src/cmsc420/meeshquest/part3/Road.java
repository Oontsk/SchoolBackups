package cmsc420.meeshquest.part3;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public class Road extends Line2D.Float implements Geometry2D {
	private static final long serialVersionUID = 1L;

	private Site start, end;
	
	public Road(Site start, Site end) {
		super(start, end);
		
		if (start.getName().compareTo(end.getName()) < 0) {
			this.start = start;
			this.end = end;
		} else {
			this.start = end;
			this.end = start;
		}
	}
	
	public Site getStart() {
		return start;
	}
	
	public Site getEnd() {
		return end;
	}
	
	public boolean startsOrEndsAt(Site a) {
		return a == start || a == end;
	}
	
	public int getType() {
		return Geometry2D.SEGMENT;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof Road)) {
			return false;
		}
		Road other = (Road) o;
		return super.equals(o) && start.equals(other.start)
				&& end.equals(other.end);	
	}
	
	public boolean intersectsPoint(PointGeometry2D point) {
		return Inclusive2DIntersectionVerifier.intersects(point, this);
	}
	
	public boolean intersectsRoad(Road road) {
		return Inclusive2DIntersectionVerifier.intersects(road, this);
	}
	
	public boolean intersectsRectangle(Rectangle2D.Float rect) {
		return Inclusive2DIntersectionVerifier.intersects(this, rect);
	}
	
	public boolean intersectsRangeCircle(RangeCircle circ) {
		double dist = super.ptSegDist(circ.getCenter());
		return dist <= circ.getRadius() ? true : false;
	}
	
	private static Comparator<Road> roadNameComp = new Comparator<Road>() {
		public int compare(Road a, Road b) {
			int res = b.start.getName().compareTo(a.start.getName());
			return res != 0 ? res : 
				b.end.getName().compareTo(a.end.getName());
		}
	};
	
	public static Comparator<Road> getRoadNameComp() {
		return roadNameComp;
	}
	
	public String toString() {
		return "Start: " + start.getName() + ", End: " + end.getName();
	}
}
