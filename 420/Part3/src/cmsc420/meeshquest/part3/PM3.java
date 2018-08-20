package cmsc420.meeshquest.part3;

import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.geom.Geometry2D;

public class PM3 extends PM {
	
	private class PM3White extends White {
		private static final long serialVersionUID = 1L;
		private PM3White(float x, float y, float dim) {
			super(x, y, dim);
		}
		private PM3White(Node o) {
			super(o);
		}
		public Node add(Geometry2D a) {
			return new PM3Black(this, a);
		}
	}
	
	private class PM3Black extends Black {
		private static final long serialVersionUID = 1L;
		private PM3Black(Node o, Geometry2D a) {
			super(o, a);
		}
		private PM3Black(Node o, TreeSet<Geometry2D> a) {
			super(o, a);
		}
		public Node add(Geometry2D a) throws RoadIntersectsAnotherRoadException, ViolatesPMRulesException {
			if (a.getType() == Geometry2D.SEGMENT) {
				Road b = (Road) a;
				Site site = getSite();
				if (site != null && Utilities.geosIntersect(site, b, false) &&
						!b.startsOrEndsAt(site)) {
					throw new ViolatesPMRulesException();
				}
			}
			if (a.getType() == Geometry2D.POINT) {
				Site b = (Site) a;
				for (Road road : getRoads()) {
					if (Utilities.geosIntersect(b, road, false) &&
							!road.startsOrEndsAt(b)) {
						throw new ViolatesPMRulesException();
					}
				}
			}
			geos.add(a);
			if (this.isValidBlackNode()) {
				return this;
			}
			if (this.width == 1) {
				throw new ViolatesPMRulesException();
			}
			PM3Gray gray = new PM3Gray(this);
			for (Geometry2D cur : geos) {
				gray.add(cur);
			}
			return gray;
		}
		public Node remove(Geometry2D a) {
			geos.remove(a);
			return geos.isEmpty() ? new PM3White(this) : this;
		}
		public boolean isValidBlackNode() {
			int cityCount = 0;
			for (Geometry2D cur : geos) {
				if (cur.getType() == Geometry2D.POINT) {
					++cityCount;
				}
				if (cityCount > 1) {
					return false;
				}
				if (cur.getType() == Geometry2D.SEGMENT) {
					return true;
				}
			}
			return true;
		}
	}
	
	private class PM3Gray extends PMGray {	 
		private static final long serialVersionUID = 1L;
		private PM3Gray(Node o) {
			super(o);
			
			float newDim = (int) width >> 1;
			kids[0] = new PM3White(x, y + newDim, newDim);
			kids[1] = new PM3White(x + newDim, y + newDim, newDim);
			kids[2] = new PM3White(x, y, newDim);
			kids[3] = new PM3White(x + newDim, y, newDim);
		}
		
		public Node remove(Geometry2D a) {
			enclosedGeos.remove(a);
			int whiteCount = 0, blackCount = 0, grayCount = 0;
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], false)) {
					kids[i] = kids[i].remove(a);
				}
				if (kids[i] instanceof White) {
					++whiteCount;
				} else if (kids[i] instanceof Black) {
					++blackCount;
				} else { //gray
					++grayCount;
				}
			}
			if (whiteCount == 4) {
				return new PM3White(this);
			} else if (whiteCount == 3 && blackCount == 1) {
				return new PM3Black(this, enclosedGeos);
			} else if (grayCount != 4) {
				PM3Black test = new PM3Black(this, enclosedGeos);
				return test.isValidBlackNode() ? test : this;
			} else {
				return this;
			}
		}
	}
	
	public PM3(int dim) {
		root = new PM3White(0, 0, dim);
	}
	
	public Element print(Document results) {
		Element quadtree = results.createElement("quadtree");
		quadtree.setAttribute("order", "3");
		quadtree.appendChild(root.print(results));
		return quadtree;
	}
	
	public void clear() {
		root = new PM3White(0, 0, root.width);
	}
	
}
