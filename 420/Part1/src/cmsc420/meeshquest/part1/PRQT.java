package cmsc420.meeshquest.part1;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Shape2DDistanceCalculator;

public class PRQT {
	
	private abstract class PRQTNode extends Rectangle2D.Float implements Geometry2D {
		private static final long serialVersionUID = 1L;
		protected PRQTNode(float x, float y, float dim) {
			super(x, y, dim, dim);
		}
		
		protected PRQTNode(PRQTNode o) {
			this(o.x, o.y, o.width);
		}
		
		public int getType() {
			return Geometry2D.RECTANGLE;
		}
		
		public abstract boolean contains(City a);
		public abstract PRQTNode add(City a);
		public abstract PRQTNode remove(City a);
		public abstract Element print(Document results);
		public abstract double distToPoint(PointGeometry2D point);
		public abstract void draw(CanvasPlus canvas);
	}
	
	private class PRQTWhite extends PRQTNode {
		private static final long serialVersionUID = 1L;

		private PRQTWhite(float x, float y, float dim) {
			super(x, y, dim);
		}
		
		private PRQTWhite(PRQTNode o) {
			super(o);
		}
		
		public boolean contains(City a) {
			return false;
		}
		
		public PRQTNode add(City a) {
			return new PRQTBlack(this, a);
		}
		
		public PRQTNode remove(City a) {
			return this;
		}
		
		public Element print(Document results) {
			return results.createElement("white");
		}
		
		public double distToPoint(PointGeometry2D point) {
			return java.lang.Double.MAX_VALUE - 1;
		}
		
		public void draw(CanvasPlus canvas) {}
	}
	
	private class PRQTGray extends PRQTNode {
		private static final long serialVersionUID = 1L;
		private PRQTNode[] kids = new PRQTNode[4];
		
		public PRQTGray(PRQTNode o) {
			super(o);
			
			float newDim = (int) o.width >> 1;
			kids[0] = new PRQTWhite(o.x, o.y + newDim, newDim);
			kids[1] = new PRQTWhite(o.x + newDim, o.y + newDim, newDim);
			kids[2] = new PRQTWhite(o.x, o.y, newDim);
			kids[3] = new PRQTWhite(o.x + newDim, o.y, newDim);
		}
		
		public boolean contains(City a) {
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], true)) {
					return kids[i].contains(a);
				}
			}
			return false;
		}
		
		public PRQTNode add(City a) {
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], true)) {
					kids[i] = kids[i].add(a);
					return this; //optimization in PRQT since point geometry can only occupy one quadrant
				}
			}
			return this;
		}
		
		public PRQTNode remove(City a) {
			int whiteCount = 0, blackCount = 0;
			City blacksGeo = null;
			for (int i = 0; i < 4; ++i) {
				if (Utilities.geosIntersect(a, kids[i], true)) {
					kids[i] = kids[i].remove(a);
				}
				if (kids[i] instanceof PRQTWhite) {
					++whiteCount;
				} else if (kids[i] instanceof PRQTBlack) {
					++blackCount;
					blacksGeo = ((PRQTBlack) kids[i]).city;
				}
			}
			if (whiteCount == 3 && blackCount == 1) {
				return new PRQTBlack(this, blacksGeo);
			}
			return this;
		}
		
		public Element print(Document results) {
			Element gray = results.createElement("gray");
			gray.setAttribute("x", String.valueOf((int) kids[1].x));
			gray.setAttribute("y", String.valueOf((int) kids[1].y));
			
			for (int i = 0; i < 4; ++i) {
				gray.appendChild(kids[i].print(results));
			}
			return gray;
		}
		public double distToPoint(PointGeometry2D point) {
			return Shape2DDistanceCalculator.distance(point, this);
		}
		
		public void draw(CanvasPlus canvas) {
			canvas.addCross(kids[1].x, kids[1].y, kids[1].width, Color.BLACK);
			for (int i = 0; i < 4; ++i) {
				kids[i].draw(canvas);
			}
		}
	}
	
	private class PRQTBlack extends PRQTNode {
		private static final long serialVersionUID = 1L;
		private City city;
		
		public PRQTBlack(PRQTNode o, City city) {
			super(o);
			this.city = city;
		}
		
		public boolean contains(City a) {
			return a == city;
		}
		
		public PRQTNode add(City a) {
			PRQTGray gray = new PRQTGray(this);
			gray.add(a);
			gray.add(city);
			return gray;
		}
		
		public PRQTNode remove(City a) {
			return a == city ? new PRQTWhite(this) : this;
		}
		
		public Element print(Document results) {
			Element black = results.createElement("black");
			black.setAttribute("name", city.getName());
			black.setAttribute("x", city.getXToString());
			black.setAttribute("y", city.getYToString());
			return black;
		}
		
		public double distToPoint(PointGeometry2D point) {
			return city != null ? Utilities.distanceBetweenGeos(point, city) : java.lang.Double.MAX_VALUE;
		}
		
		public void draw(CanvasPlus canvas) {
			if (city != null) {
				canvas.addPoint(city.getName(), city.x, city.y, Color.BLACK);
			}
		}

	}

	private PRQTNode root;
	
	public PRQT(int dim) {
		root = new PRQTWhite(0, 0, (float) dim);
	}
	
	public boolean contains(City a) {
		return root.contains(a);
	}
	
	public void add(City a) {
		root = root.add(a);
	}
	
	public void remove(City a) {
		root = root.remove(a);
	}
	
	public Element print(Document results) {
		Element quadtree = results.createElement("quadtree");
		quadtree.appendChild(root.print(results));
		return quadtree;
	}
	
	public boolean isEmpty() {
		return root instanceof PRQTWhite;
	}
	
	public void clear() {
		root = new PRQTWhite(0, 0, root.width);
	}
	
	public boolean intersectsSpatialMap(PointGeometry2D point) {
		return Utilities.geosIntersect(point, root, true);
	}
	
	public TreeSet<City> citiesInRange(RangeCircle circ) {
		TreeSet<City> res = new TreeSet<City>(City.getCityCompareByNameComp());
		citiesInRangeHelper(circ, root, res);
		return res;
	}
	
	private void citiesInRangeHelper(RangeCircle circ, PRQTNode cur, TreeSet<City> inRange) {
		if (cur instanceof PRQTGray) {
			PRQTGray qur = (PRQTGray) cur;
			for (int i = 0; i < 4; ++i) {
				PRQTNode kid = qur.kids[i];
				if (Utilities.geosIntersect(kid, circ, false)) {
					citiesInRangeHelper(circ, kid, inRange);
				}
			}
		} else if (cur instanceof PRQTBlack) {
			PRQTBlack qur = (PRQTBlack) cur;
			City city = qur.city;
			if (city != null && Utilities.geosIntersect(city, circ, false)) {
				inRange.add(city);
			}
		}
	}
	
	public City nearestCity(PointGeometry2D point) {
		PriorityQueue<PRQTNode> pq = new PriorityQueue<PRQTNode>(new Comparator<PRQTNode>() {
			public int compare(PRQTNode a, PRQTNode b) {
				double d1 = a.distToPoint(point), d2 = b.distToPoint(point), res = d1 - d2;
				if (res < 0) {
					return -1;
				} else if (res > 0) {
					return 1;
				} else {
					if (d1 >= java.lang.Double.MAX_VALUE - 1 || d2 >= java.lang.Double.MAX_VALUE - 1) {
						return 0; //edge case where inaccurately equidistant
					} else if (a instanceof PRQTGray) {
						return -1;
					} else if (a instanceof PRQTWhite) {
						return 1;
					} else if (b instanceof PRQTGray) {
						return 1;
					} else if (b instanceof PRQTWhite) {
						return -1;
					} else { //both are black, choose by greater asciibetically
						PRQTBlack aBlack = (PRQTBlack) a, bBlack = (PRQTBlack) b;
						City cityA = aBlack.city, cityB = bBlack.city;
						return cityA != null && cityB != null ? City.getCityCompareByNameComp().compare(cityA, cityB) : 0;
					}
				}
			}
		});
		
		PRQTNode cur = root;
		do {
			if (cur instanceof PRQTGray) {
				PRQTGray qur = (PRQTGray) cur;
				for (int i = 0; i < 4; ++i) {
					pq.add(qur.kids[i]);
				}
			} else if (cur instanceof PRQTBlack) {
				PRQTBlack qur = (PRQTBlack) cur;
				City city = qur.city;
				if (city != null) {
					return city;
				}
			} else { //cur instanceof PRQTWhite
				break;
			}
			cur = pq.poll();
		} while (!pq.isEmpty());
		
		return null;
	}
	
	public CanvasPlus draw() {
		CanvasPlus canvas = new CanvasPlus("MeeshQuest");
    	canvas.setFrameSize((int) root.width, (int) root.width);
    	canvas.addRectangle(0, 0, root.width, root.width, Color.WHITE, true);
    	canvas.addRectangle(0, 0, root.width, root.width, Color.BLACK, false);
    	root.draw(canvas);
    	return canvas;
	}
	
}
