package cmsc420.meeshquest.part3;

import java.awt.geom.Rectangle2D;

import cmsc420.geom.Geometry2D;
import cmsc420.geom.Shape2DDistanceCalculator;

public class Utilities {
	public static boolean geosIntersect(Geometry2D a, Geometry2D b, boolean enforcePRQTBounds) {
		if (a == null || b == null) {
			return false;
		}
		
		switch (a.getType()) {
			case Geometry2D.POINT:
				switch (b.getType()) {
					case Geometry2D.POINT:
						return ((PointGeometry2D) a).equals(b);
					case Geometry2D.SEGMENT:
						return ((PointGeometry2D) a).intersectsRoad((Road) b);
					case Geometry2D.RECTANGLE:
						return enforcePRQTBounds ? 
								((PointGeometry2D) a).intersectsPRQTRectangle((Rectangle2D.Float) b) :
								((PointGeometry2D) a).intersectsRectangle((Rectangle2D.Float) b);
					default: //circle
						return ((PointGeometry2D) a).intersectsRangeCircle((RangeCircle) b);
				}
			case Geometry2D.SEGMENT:
				switch (b.getType()) {
					case Geometry2D.POINT:
						return ((PointGeometry2D) b).intersectsRoad((Road) a);
					case Geometry2D.SEGMENT:
						return ((Road) a).intersectsRoad((Road) b);
					case Geometry2D.RECTANGLE:
						return ((Road) a).intersectsRectangle((Rectangle2D.Float) b);
					default: //circle
						return ((Road) a).intersectsRangeCircle((RangeCircle) b);
				}
			case Geometry2D.RECTANGLE:
				switch (b.getType()) {
					case Geometry2D.POINT:
						return enforcePRQTBounds ? ((PointGeometry2D) b).intersectsPRQTRectangle((Rectangle2D.Float) a) :
							((PointGeometry2D) b).intersectsRectangle((Rectangle2D.Float) a);
					case Geometry2D.SEGMENT:
						return ((Road) b).intersectsRectangle((Rectangle2D.Float) a);
					/*case Geometry2D.RECTANGLE:
						never have to compare whether rectangle intersects another rectangle */
					default: //circle
						return ((RangeCircle) b).intersectsRectangle((Rectangle2D.Float) a);
				}
			default: //circle
				switch (b.getType())	 {
					case Geometry2D.POINT:
						return ((PointGeometry2D) b).intersectsRangeCircle((RangeCircle) a);
					case Geometry2D.SEGMENT:
						return ((Road) b).intersectsRangeCircle((RangeCircle) a);
					/*case Geometry2D.Circle
					 	never have to compare whether circle intersects another circle*/
					default: // rectangle
						return ((RangeCircle) a).intersectsRectangle((Rectangle2D.Float) b);
				}
		}
	}
	public static double distanceBetweenGeos(Geometry2D a, Geometry2D b) {
		if (a == null || b == null) {
			return -1; //indicates error
		}
		
		//a distance between a geometry and a circle is never needed
		
		switch (a.getType()) {
			case Geometry2D.POINT:
				switch (b.getType()) {
					case Geometry2D.POINT:
						return ((PointGeometry2D) a).distance((PointGeometry2D) b);
					case Geometry2D.SEGMENT:
						return ((Road) b).ptSegDist((PointGeometry2D) a);
					default: //Geometry2D.RECTANGLE
						return Shape2DDistanceCalculator.distance((PointGeometry2D) a, (Rectangle2D.Float) b);
				}
			case Geometry2D.SEGMENT:
				switch (b.getType()) {
					case Geometry2D.POINT:
						return ((Road) a).ptSegDist((PointGeometry2D) b);
					/*case Geometry2D.SEGMENT:
						never need road distance from road*/
					default: //rectangle
						return Shape2DDistanceCalculator.distance((Road) a, (Rectangle2D.Float) b);
				}
			//recall, never finding distances between circles
			default: // Geometry2D.RECTANGLE
				switch (b.getType()) {
					case Geometry2D.POINT:
						return Shape2DDistanceCalculator.distance((PointGeometry2D) b, (Rectangle2D.Float) a);
					default: //road
						return Shape2DDistanceCalculator.distance((Road) b, (Rectangle2D.Float) a);
				}
		}
	}
}
