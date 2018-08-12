package cmsc420.meeshquest.part2;

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
		public abstract Node add(Geometry2D a);
		public abstract Node remove(Geometry2D a);
		public abstract Element print(Document results);
		public abstract double distIsolatedCity(PointGeometry2D point);
		public abstract double distNonIsolatedCity(PointGeometry2D point);
		public abstract double distNearestRoad(PointGeometry2D point);
		public abstract double distNearestCity(Road road);
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
		
		public double distIsolatedCity(PointGeometry2D point) {
			return java.lang.Double.MAX_VALUE - 1;
		}
		
		public double distNonIsolatedCity(PointGeometry2D point) {
			return java.lang.Double.MAX_VALUE - 1;
		}
		
		public double distNearestRoad(PointGeometry2D point) {
			return java.lang.Double.MAX_VALUE - 1;
		}
		
		public double distNearestCity(Road road) {
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
		
		public double distIsolatedCity(PointGeometry2D point) {
			return Utilities.distanceBetweenGeos(point, this);
		}
		
		public double distNonIsolatedCity(PointGeometry2D point) {
			return Utilities.distanceBetweenGeos(point, this);
		}
		
		public double distNearestRoad(PointGeometry2D point) {
			return Utilities.distanceBetweenGeos(point, this);
		}
		
		public double distNearestCity(Road road) {
			return Utilities.distanceBetweenGeos(road, this);
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
		
		private Road mostRecentNearestRoad = null;
		
		public Black(Node o, Geometry2D geo) {
			super(o);
			geos.add(geo);
		}
		
		public Black(Node o, TreeSet<Geometry2D> geos) {
			super(o);
			this.geos.addAll(geos);
		}
		
		public Road getMostRecentNearestRoad() {
			return mostRecentNearestRoad;
		}
		
		public City getCity() {
			try {
				return (City) geos.first();
			} catch (ClassCastException | NoSuchElementException e) {
				return null;
			}
		}
		
		public TreeSet<Road> getRoads() {
			TreeSet<Road> roads = new TreeSet<Road>(Road.getRoadNameComp());
			City first;
			try {
				first = (City) geos.first();
			} catch (ClassCastException | NoSuchElementException e) {
				first = null;
			}
			for (Geometry2D cur : geos) {
				if (cur != first) {
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
			City city = getCity();
			if (city != null) {
				if (city.isIsolated()) {
					black.appendChild(XMLBuilder.isolatedCityNode(results, city));
				} else { //NonIsolated city
					black.appendChild(XMLBuilder.cityNode(results, city));
				}
			}
			for (Road road : getRoads()) {
				black.appendChild(XMLBuilder.roadNode(results, road));
			}
			return black;
		}
		
		public double distIsolatedCity(PointGeometry2D point) {
			City city = getCity();
			if (city != null && city.isIsolated()) {
				return Utilities.distanceBetweenGeos(city, point);
			}
			return java.lang.Double.MAX_VALUE;
		}
		
		public double distNonIsolatedCity(PointGeometry2D point) {
			City city = getCity();
			if (city != null && city.isNonIsolated()) {
				return Utilities.distanceBetweenGeos(city, point);
			}
			return java.lang.Double.MAX_VALUE;
		}
		
		public double distNearestRoad(PointGeometry2D point) {
			mostRecentNearestRoad = null;
			double curMinDist = java.lang.Double.MAX_VALUE;
			for (Road road : getRoads()) {
				double dist = Utilities.distanceBetweenGeos(point, road);
				if (dist == curMinDist && mostRecentNearestRoad != null &&
					Road.getRoadNameComp().compare(road, mostRecentNearestRoad) < 0) {
					mostRecentNearestRoad = road;
				} else if (dist < curMinDist) {
					mostRecentNearestRoad = road;
					curMinDist = dist;
				}
			}
			return curMinDist;
		}
		
		public double distNearestCity(Road road) {
			City city = getCity();
			if (city != null && !road.startsOrEndsAt(city)) {
				return Utilities.distanceBetweenGeos(city, road);
			}
			return java.lang.Double.MAX_VALUE;
		}
		
		public void draw(CanvasPlus canvas) {
			City city = getCity();
			if (city != null) {
				canvas.addPoint(city.getName(), city.x, city.y, Color.BLACK);
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
	
	public void add(Geometry2D a) {
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
		TreeSet<City> res = new TreeSet<City>(City.getCityCompareByNameComp());
		citiesInRangeHelper(circ, root, res);
		return res;
	}
	
	public TreeSet<Road> roadsInRange(RangeCircle circ) {
		TreeSet<Road> res = new TreeSet<Road>(Road.getRoadNameComp());
		roadsInRangeHelper(circ, root, res);
		return res;
	}
	
	private void roadsInRangeHelper(RangeCircle circ, Node cur, TreeSet<Road> inRange) {
		if (cur instanceof Gray) {
			Gray qur = (Gray) cur;
			for (int i = 0; i < 4; ++i) {
				Node kid = qur.kids[i];
				if (Utilities.geosIntersect(kid, circ, false)) {
					roadsInRangeHelper(circ, kid, inRange);
				}
			}
		} else if (cur instanceof Black) {
			Black qur = (Black) cur;
			for (Road road : qur.getRoads()) {
				if (Utilities.geosIntersect(road, circ, false)) {
					inRange.add(road);
				}
			}
		}
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
	
	public City nearestIsolatedCity(PointGeometry2D point) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>(new Comparator<Node>() {
			public int compare(Node a, Node b) {
				double d1 = a.distIsolatedCity(point), 
						d2 = b.distIsolatedCity(point), res = d1 - d2;
				if (res < 0) {
					return -1;
				} else if (res > 0) {
					return 1;
				} else {
					if (d1 >= java.lang.Double.MAX_VALUE - 1 || d2 >= java.lang.Double.MAX_VALUE - 1) {
						return 0; //edge case where inaccurately equidistant
					} else if (a instanceof Gray) {
						return -1;
					} else if (a instanceof White) {
						return 1;
					} else if (b instanceof Gray) {
						return 1;
					} else if (b instanceof White) {
						return -1;
					} else { //both are black, choose by greater asciibetically
						Black aBlack = (Black) a, bBlack = (Black) b;
						City cityA = aBlack.getCity(), cityB = bBlack.getCity();
						return cityA != null && cityB != null ? City.getCityCompareByNameComp().compare(cityA, cityB) : 0;
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
				if (city != null && city.isIsolated()) {
					return city;
				}
			} else {
				break;
			}
			cur = pq.poll();
		} while (!pq.isEmpty());
		return null;
	}
	
	public City nearestNonIsolatedCity(PointGeometry2D point) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>(new Comparator<Node>() {
			public int compare(Node a, Node b) {
				double d1 = a.distNonIsolatedCity(point), 
						d2 = b.distNonIsolatedCity(point), res = d1 - d2;
				if (res < 0) {
					return -1;
				} else if (res > 0) {
					return 1;
				} else {
					if (d1 >= java.lang.Double.MAX_VALUE - 1 || d2 >= java.lang.Double.MAX_VALUE - 1) {
						return 0; //edge case where inaccurately equidistant
					} else if (a instanceof Gray) {
						return -1;
					} else if (a instanceof White) {
						return 1;
					} else if (b instanceof Gray) {
						return 1;
					} else if (b instanceof White) {
						return -1;
					} else { //both are black, choose by greater asciibetically
						Black aBlack = (Black) a, bBlack = (Black) b;
						City cityA = aBlack.getCity(), cityB = bBlack.getCity();
						return cityA != null && cityB != null ? City.getCityCompareByNameComp().compare(cityA, cityB) : 0;
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
				if (city != null && city.isNonIsolated()) {
					return city;
				}
			} else {
				break;
			}
			cur = pq.poll();
		} while (!pq.isEmpty());
		return null;
	}
	
	public Road nearestRoad(PointGeometry2D point) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>(new Comparator<Node>() {
			public int compare(Node a, Node b) {
				double d1 = a.distNearestRoad(point), 
						d2 = b.distNearestRoad(point), res = d1 - d2;
				if (res < 0) {
					return -1;
				} else if (res > 0) {
					return 1;
				} else {
					if (d1 >= java.lang.Double.MAX_VALUE - 1 || d2 >= java.lang.Double.MAX_VALUE - 1) {
						return 0; //edge case where inaccurately equidistant
					} else if (a instanceof Gray) {
						return -1;
					} else if (a instanceof White) {
						return 1;
					} else if (b instanceof Gray) {
						return 1;
					} else if (b instanceof White) {
						return -1;
					} else { //both are black, choose by greater asciibetically
						Black aBlack = (Black) a, bBlack = (Black) b;
						Road roadA = aBlack.getMostRecentNearestRoad(),
								roadB = bBlack.getMostRecentNearestRoad();
						return roadA != null && roadB != null ? Road.getRoadNameComp().compare(roadA, roadB) : 0;
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
				Road nearest = qur.getMostRecentNearestRoad();
				if (nearest != null) {
					return nearest;
				}
			} else {
				break;
			}
			cur = pq.poll();
		} while (!pq.isEmpty());
		return null;
	}
	
	public City nearestCity(Road rd) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>(new Comparator<Node>() {
			public int compare(Node a, Node b) {
				double d1 = a.distNearestCity(rd), 
						d2 = b.distNearestCity(rd), res = d1 - d2;
				if (res < 0) {
					return -1;
				} else if (res > 0) {
					return 1;
				} else {
					if (d1 >= java.lang.Double.MAX_VALUE - 1 || d2 >= java.lang.Double.MAX_VALUE - 1) {
						return 0; //edge case where inaccurately equidistant
					} else if (a instanceof Gray) {
						return -1;
					} else if (a instanceof White) {
						return 1;
					} else if (b instanceof Gray) {
						return 1;
					} else if (b instanceof White) {
						return -1;
					} else { //both are black, choose by greater asciibetically
						Black aBlack = (Black) a, bBlack = (Black) b;
						City cityA = aBlack.getCity(), cityB = bBlack.getCity();
						return cityA != null && cityB != null ? City.getCityCompareByNameComp().compare(cityA, cityB) : 0;
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
				if (city != null && !rd.startsOrEndsAt(city)) {
					return city;
				}
			} else {
				break;
			}
			cur = pq.poll();
		} while (!pq.isEmpty());
		return null;
	}
	
	public abstract boolean encloses(Geometry2D a);
	public abstract void clear();
	public abstract Element print(Document results);
	
	private static Comparator<Geometry2D> geoComp = new Comparator<Geometry2D>() {
		public int compare(Geometry2D a, Geometry2D b) {
			if (a.getType() == Geometry2D.POINT) {
				if (b.getType() == Geometry2D.POINT) {
					return City.getCityCompareByNameComp().compare((City) a, (City) b);
				} else { //segment
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
