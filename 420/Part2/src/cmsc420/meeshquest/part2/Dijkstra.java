package cmsc420.meeshquest.part2;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.sortedmap.Treap;

public class Dijkstra {
	
	private City start, end;
	private Treap<City, Double> dist = new Treap<City, Double>(City.getCityCompareByNameComp());
	private TreeMap<City, City> pred = new TreeMap<City, City>(City.getCityCompareByNameComp());
	private PriorityQueue<City> pq = new PriorityQueue<City>(new Comparator<City>() {
		public int compare(City a, City b) {
			Double d1 = dist.get(a), d2 = dist.get(b);
			double ch = d1 - d2;
			if (ch < 0) {
				return -1;
			} else if (ch > 0) {
				return 1;
			} else {
				return a.getName().compareTo(b.getName());
			}
		}
	});
	
	public Dijkstra(City start, City end, Dictionary dictionary) {
		this.start = start;
		this.end = end;
		for (Map.Entry<String, City> entry : dictionary.getCityNameMap().entrySet()) {
			City city = entry.getValue();
			if (city != start) {
				dist.put(city, Double.MAX_VALUE);
			} else {
				dist.put(city, 0.);
			}
			pred.put(city, null);
			pq.add(city);
		}
	}
	
	public void findShortestPath() {
		
		while (!pq.isEmpty()) {
			City curMin = pq.poll();
			if (curMin == end) {
				return;
			}
			
			for (City neighbor : curMin.getSpMapNeighbors()) {
				double newDist = dist.get(curMin) + Utilities.distanceBetweenGeos(curMin, neighbor);
				if (newDist < dist.get(neighbor)) {
					dist.put(neighbor, newDist);
					pred.put(neighbor, curMin);
					pq.remove(neighbor);
					pq.add(neighbor);
				}
			}
			
			boolean allInfinite = true;
			for (City city : pq) {
				if (dist.get(city) != Double.MAX_VALUE) {
					allInfinite = false;
					break;
				}
			}
			if (allInfinite) {
				return; //can't go anywhere from here
			}
		}
	}
	
	public Element shortestPath(Document results, CanvasPlus canvas) {
		Element path = results.createElement("path");
		double length = 0;
		int hops = 0;
		
		Stack<Element> res = new Stack<Element>();
		City cur = end, predecessor = pred.get(cur), predsPred = null;
		
		while (predecessor != null) {
			length += Utilities.distanceBetweenGeos(cur, predecessor);
			++hops;
			
			Element road = XMLBuilder.roadNode(results, predecessor, cur);
			res.push(road);
			
			if (canvas != null) {
				canvas.addLine(start.x, start.y, end.x, end.y, Color.BLUE);
			}
			
			predsPred = pred.get(predecessor);
			if (predsPred != null) {
				res.push(directionNode(results, predsPred, predecessor, cur));
			}
			cur = predecessor;
			predecessor = pred.get(predecessor);
		}
		if (start == cur) {
			DecimalFormat df = new DecimalFormat("0.000");
			path.setAttribute("length", df.format(length));
			path.setAttribute("hops", String.valueOf(hops));
			while (!res.isEmpty()) {
				path.appendChild(res.pop());
			}
			return path;
		}
		return null;
	}
	
	private static Element directionNode(Document results, City fst, City snd, City trd) {
    	Element direction;
    	Arc2D.Double arc = new Arc2D.Double();
    	arc.setArcByTangent(fst, snd, trd, 1);
    	double degree = arc.getAngleExtent();
    	if (degree < -45) {
    		direction = results.createElement("left");
    	} else if (degree >= 45) {
    		direction = results.createElement("right");
    	} else {
    		direction = results.createElement("straight");
    	}
    	return direction;
    }
}
