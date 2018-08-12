package cmsc420.meeshquest.part1;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public class PointGeometry2D extends Point2D.Float implements Geometry2D {
	private static final long serialVersionUID = 1L;
	
	protected PointGeometry2D(String x, String y) {
		super(java.lang.Float.parseFloat(x), java.lang.Float.parseFloat(y));
	}
	
	public String getXToString() {
        return String.valueOf((int) x);
    }

    public String getYToString() {
        return String.valueOf((int) y);
    }
    
    private static Comparator<Point2D.Float> pointCoorComp = new Comparator<Point2D.Float>() {
        public int compare(Point2D.Float a, Point2D.Float b) {
            int res = (int) (a.y - b.y);
            return res != 0 ? res : (int) (a.x - b.x);
        }
    };

    public static Comparator<Point2D.Float> getPointCoorComp() {
        return pointCoorComp;
    }
	
	public int getType() {
		return Geometry2D.POINT;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof PointGeometry2D)) {
			return false;
		}
		PointGeometry2D other = (PointGeometry2D) o;
		return super.equals(other);
	}
	
	public boolean intersectsPRQTRectangle(Rectangle2D.Float rect) {
		if (Inclusive2DIntersectionVerifier.intersects(this, rect) &&
				this.x != rect.x + rect.width && this.y != rect.y + rect.height) {
			return true;
		}
		return false;
	}
	
	public boolean intersectsRectangle(Rectangle2D.Float rect) {
		return Inclusive2DIntersectionVerifier.intersects(this, rect);
	}
	
	public boolean intersectsRangeCircle(RangeCircle circ) {
		return Inclusive2DIntersectionVerifier.intersects(this, circ);
	}
	
	/*public boolean intersectsRoad(Road road) {
		return Inclusive2DIntersectionVerifier.intersects(this, road);
	}*/
}