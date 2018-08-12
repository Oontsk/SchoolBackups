package cmsc420.meeshquest.part2;

import java.awt.geom.Rectangle2D;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public class RangeCircle extends Circle2D.Float {

	private PointGeometry2D center;
	
	public RangeCircle(String x, String y, String radius) {
		super(new PointGeometry2D(x, y), java.lang.Float.parseFloat(radius));
		this.center = new PointGeometry2D(x, y);
	}
	
	@Override
	public PointGeometry2D getCenter() {
		return this.center;
	}
	
	public boolean intersectsRectangle(Rectangle2D.Float rect) {
		return Inclusive2DIntersectionVerifier.intersects(rect, this);
	}
}
