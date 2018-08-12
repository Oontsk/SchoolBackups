package cmsc420.meeshquest.part2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.geom.Geometry2D;

public class PRQT extends Quadtree {

	private class PRQTWhite extends White {
		private static final long serialVersionUID = 1L;
		
		private PRQTWhite(float x, float y, float dim) {
			super(x, y, dim);
		}
		
		private PRQTWhite(Node o) {
			super(o);
		}
		
		public Node add(Geometry2D a) {
			return new PRQTBlack(this, a);
		}
	}
	
	private class PRQTGray extends Gray {
		private static final long serialVersionUID = 1L;
		
		private PRQTGray(Node o) {
			super(o);
			
			float newDim = (int) width >> 1;
			kids[0] = new PRQTWhite(x, y + newDim, newDim);
			kids[1] = new PRQTWhite(x + newDim, y + newDim, newDim);
			kids[2] = new PRQTWhite(x, y, newDim);
			kids[3] = new PRQTWhite(x + newDim, y, newDim);
		}
		
		public boolean contains(Geometry2D a) {
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], true)) {
					return kids[i].contains(a);
				}
			}
			return false;
		}
		
		public Node add(Geometry2D a) {
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], true)) {
					kids[i] = kids[i].add(a);
					return this; //optimization in PRQT since point geometry can only occupy one quadrant
				}
			}
			return this;
		}
		
		public Node remove(Geometry2D a) {
			int whiteCount = 0, blackCount = 0;
			Geometry2D blacksGeo = null;
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], true)) {
					kids[i] = kids[i].remove(a);
				}
				if (kids[i] instanceof PRQTWhite) {
					++whiteCount;
				} else if (kids[i] instanceof PRQTBlack) {
					++blackCount;
					blacksGeo = ((PRQTBlack) kids[i]).getCity();
				}
			}
			if (whiteCount == 3 && blackCount == 1) {
				return new PRQTBlack(this, blacksGeo);
			}
			return this;
		}
	}
	
	private class PRQTBlack extends Black {
		private static final long serialVersionUID = 1L;
		
		private PRQTBlack(Node o, Geometry2D a) {
			super(o, a);
		}
		
		public Node add(Geometry2D a) {
			geos.add(a);
			PRQTGray gray = new PRQTGray(this);
			for (Geometry2D cur : geos) {
				gray.add(cur);
			}
			return gray;
		}
		
		public Node remove(Geometry2D a) {
			geos.remove(a);
			return geos.isEmpty() ? new PRQTWhite(this) : this;
		}
		
		public boolean isValidBlackNode() {
			return geos.size() > 1 ? false : true;
		}
	}
	
	public PRQT(int dim) {
		root = new PRQTWhite(0, 0, dim);
	}
	
	public Element print(Document results) {
		return null; //never have to print PRQT
	}
	
	public boolean encloses(Geometry2D a) {
		return Utilities.geosIntersect(a, root, true);
	}
	
	public void clear() {
		root = new PRQTWhite(0, 0, root.width);
	}
	
}
