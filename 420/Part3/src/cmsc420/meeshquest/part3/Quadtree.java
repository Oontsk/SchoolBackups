package cmsc420.meeshquest.part3;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Geometry2D;

public abstract class Quadtree {
	
	protected abstract class Node extends Rectangle2D.Float implements Geometry2D {
		private static final long serialVersionUID = 1L;
		
		protected Node(float x, float y, float dim) {
			super(x, y, dim, dim);
		}
		
		protected Node(Node o) {
			this(o.x, o.y, o.width);
		}
		
		public int getType() {
			return Geometry2D.RECTANGLE;
		}
		
		public abstract boolean contains(Geometry2D a);
		public abstract Node add(Geometry2D a) throws RoadIntersectsAnotherRoadException, ViolatesPMRulesException;
		public abstract Node remove(Geometry2D a);
		public abstract Element print(Document results);
		public abstract double distNearestCity(PointGeometry2D point);
		public abstract void draw(CanvasPlus canvas);
	}
	
	protected abstract class White extends Node {
		private static final long serialVersionUID = 1L;
		
		protected White(float x, float y, float dim) {
			super(x, y, dim);
		}
		
		protected White(Node o) {
			super(o);
		}
		
		public boolean contains(Geometry2D a) {
			return false;
		}
		
		public Node remove(Geometry2D a) {
			return this;
		}
		
		public Element print(Document results) {
			return results.createElement("white");
		}
		
		public double distNearestCity(PointGeometry2D point) {
			return java.lang.Double.MAX_VALUE - 1;
		}

		public void draw(CanvasPlus canvas) {}
		
	}
	
	protected abstract class Gray extends Node {
		private static final long serialVersionUID = 1L;
		protected Node[] kids = new Node[4];
		protected TreeSet<Geometry2D> enclosedGeos = 
				new TreeSet<Geometry2D>(geoComp);
		
		protected Gray(Node o) {
			super(o);	
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
		
		public double distNearestCity(PointGeometry2D point) {
			return Utilities.distanceBetweenGeos(point, this);
		}
		
		public void draw(CanvasPlus canvas) {
			canvas.addCross(kids[1].x, kids[1].y, kids[1].width, Color.GRAY);
			for (int i = 0; i < 4; ++i) {
				kids[i].draw(canvas);
			}
		}
	}
	
	protected abstract class Black extends Node {
		private static final long serialVersionUID = 1L;
		protected TreeSet<Geometry2D> geos = new TreeSet<Geometry2D>(geoComp);
		
		public Black(Node o, Geometry2D geo) {
			super(o);
			geos.add(geo);
		}
		
		public Black(Node o, TreeSet<Geometry2D> geos) {
			super(o);
			this.geos.addAll(geos);
		}
		
		public PointGeometry2D getPointGeometry2D() {
			try {
				return (PointGeometry2D) geos.first();
			} catch (ClassCastException | NoSuchElementException e) {
				return null;
			}
		}
		
		public City getCity() {
			try {
				return (City) geos.first();
			} catch (ClassCastException | NoSuchElementException e) {
				return null;
			}
		}
		
		public Site getSite() {
			try {
				return (Site) geos.first();
			} catch (ClassCastException | NoSuchElementException e) {
				return null;
			}
		}
		
		public TreeSet<Road> getRoads() {
			TreeSet<Road> roads = new TreeSet<Road>(Road.getRoadNameComp());
			
			for (Geometry2D cur : geos) {
				if (cur.getType() == Geometry2D.SEGMENT) {
					roads.add((Road) cur);
				}
			}
			return roads;
		}
		
		public boolean contains(Geometry2D a) {
			return geos.contains(a);
		}

		
		public Element print(Document results) {
			Element black = results.createElement("black");
			black.setAttribute("cardinality", String.valueOf(geos.size()));
			Site site = getSite();
			if (site != null) {
				if (site instanceof City) {
					black.appendChild(XMLBuilder.cityNode(results, (City) site));
				} else if (site instanceof Terminal)  {
					black.appendChild(XMLBuilder.terminalNode(results, (Terminal) site));
				} else { //Airport
					black.appendChild(XMLBuilder.airportNode(results, (Airport) site));
				}
			}
			for (Road road : getRoads()) {
				black.appendChild(XMLBuilder.roadNode(results, road));
			}
			return black;
		}
		
		public double distNearestCity(PointGeometry2D point) {
			City city = getCity();
			return city != null ? Utilities.distanceBetweenGeos(city, point) : java.lang.Double.MAX_VALUE;
		}
		
		public void draw(CanvasPlus canvas) {
			Site site = getSite();
			if (site != null) {
				canvas.addPoint(site.getName(), site.x, site.y, Color.BLACK);
			}
			for (Road road : getRoads()) {
				canvas.addLine(road.getStart().x, road.getStart().y,
						road.getEnd().x, road.getEnd().y, Color.BLACK);
			}
		}
		
		public abstract boolean isValidBlackNode();
	}
	
	protected Node root;
	
	public boolean contains(Geometry2D a) {
		return root.contains(a);
	}
	
	public void add(Geometry2D a) throws RoadIntersectsAnotherRoadException, ViolatesPMRulesException {
		if (a.getType() == Geometry2D.SEGMENT) {
			Road b = (Road) a;
			for (Geometry2D geo : enclosedGeos()) {
				if (geo.getType() == Geometry2D.SEGMENT) {
					Road c = (Road) geo;
					if (Utilities.geosIntersect(b, c, false) &&
							!b.startsOrEndsAt(c.getStart()) &&
							!b.startsOrEndsAt(c.getEnd())) {
						throw new RoadIntersectsAnotherRoadException();
					}
				}
			}
		}
		root = root.add(a);
	}
	
	public void remove(Geometry2D a) {
		root = root.remove(a);
	}
	
	public boolean isEmpty() {
		return root instanceof White;
	}
	
	public CanvasPlus draw() {
		CanvasPlus canvas = new CanvasPlus("MeeshQuest");
		canvas.setFrameSize((int) root.width, (int) root.width);
		canvas.addRectangle(0, 0, root.width, root.width, Color.WHITE, true);
		canvas.addRectangle(0, 0, root.width, root.width, Color.BLACK, false);
		root.draw(canvas);
		return canvas;
	} 
	
	public TreeSet<City> citiesInRange(RangeCircle circ) {
		TreeSet<City> res = new TreeSet<City>(Site.getSiteCompareByNameComp());
		citiesInRangeHelper(circ, root, res);
		return res;
	}
	
	private void citiesInRangeHelper(RangeCircle circ, Node cur, TreeSet<City> inRange) {
		if (cur instanceof Gray) {
			Gray qur = (Gray) cur;
			for (int i = 0; i < 4; ++i) {
				Node kid = qur.kids[i];
				if (Utilities.geosIntersect(kid, circ, false)) {
					citiesInRangeHelper(circ, kid, inRange);
				}
			}
		} else if (cur instanceof Black) {
			Black qur = (Black) cur;
			City city = qur.getCity();
			if (city != null && Utilities.geosIntersect(city, circ, false)) {
				inRange.add(city);
			}
		}
	}
	
	public City nearestCity(PointGeometry2D point) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>(new Comparator<Node>() {
			public int compare(Node a, Node b) {
				double d1 = a.distNearestCity(point), 
						d2 = b.distNearestCity(point), 
						res = d1 - d2;
				if (res < 0) {
					return -1;
				} else if (res > 0) {
					return 1;
				} else {
					if (d1 >= java.lang.Double.MAX_VALUE - 1) {
						return 0; //edge case where inaccurately equidistant
					} else if (a instanceof Gray) {
						return -1;
					} else if (a instanceof White) {
						return 1;
					} else if (b instanceof Gray) {
						return 1;
					} else if (b instanceof White) {
						return -1;
					} else { //both are black and have a city, choose by greater asciibetically
						Black aBlack = (Black) a, bBlack = (Black) b;
						City cityA = aBlack.getCity(), cityB = bBlack.getCity();
						return Site.getSiteCompareByNameComp().compare(cityA, cityB);
					}
				}
			}
		});
		
		Node cur = root;
		do {
			if (cur instanceof Gray) {
				Gray qur = (Gray) cur;
				for (int i = 0; i < 4; ++i) {
					pq.add(qur.kids[i]);
				}
			} else if (cur instanceof Black) {
				Black qur = (Black) cur;
				City city = qur.getCity();
				if (city != null) {
					return city;
				}
			} else { //white node
				break;
			}
			cur = pq.poll();
		} while (!pq.isEmpty());
		return null;
	}
	
	private TreeSet<Geometry2D> enclosedGeos() {
		if (root instanceof White) {
			return new TreeSet<Geometry2D>(geoComp);
		} else if (root instanceof Black) {
			return ((Black) root).geos;
		} else {
			return ((Gray) root).enclosedGeos;
		}
	}
	
	public TreeSet<City> enclosedCities() {
		TreeSet<City> res = new TreeSet<City>(Site.getSiteCompareByNameComp());
		if (root instanceof White) {
			return res;
		} else if (root instanceof Black) {
			City city = ((Black) root).getCity();
			if (city != null) {
				res.add(city);
			}
			return res;
		} else { //gray
			TreeSet<Geometry2D> graysGeos = ((Gray) root).enclosedGeos;
			for (Geometry2D geo : graysGeos) {
				if (geo.getType() == Geometry2D.POINT) {
					City city;
					try {
						city = (City) geo;
					} catch (ClassCastException e) {
						city = null;
					}
					if (city != null) {
						res.add(city);
					}
				}
				if (geo.getType() == Geometry2D.SEGMENT) {
					break;
				}
			}
			return res;
		}
	}
		
	public abstract boolean encloses(Geometry2D a);
	public abstract void clear();
	public abstract Element print(Document results);
	
	private static Comparator<Geometry2D> geoComp = new Comparator<Geometry2D>() {
		public int compare(Geometry2D a, Geometry2D b) {
			if (a.equals(b)) {
				return 0;
			} else if (a.getType() == Geometry2D.POINT) {
				if (b.getType() == Geometry2D.POINT) {
					return PointGeometry2D.getPointCoorComp().compare((PointGeometry2D) a, (PointGeometry2D) b);
				} else {
					return -1;
				}
			} else { //a is segment
				if (b.getType() == Geometry2D.POINT) {
					return 1;
				} else {
					return Road.getRoadNameComp().compare((Road) a, (Road) b);
				}
			}
		}
	};
	
	public static Comparator<Geometry2D> getGeoComp() {
		return geoComp;
	}
}
