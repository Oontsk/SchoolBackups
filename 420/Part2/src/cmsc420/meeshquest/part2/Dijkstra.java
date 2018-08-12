package cmsc420.meeshquest.part2;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;

import cmsc420.sortedmap.Treap;

public class Dijkstra {
	
	private City start, end;
	private Treap<City, Double> dist = new Treap<City, Double>(City.getCityCompareByNameComp());
	private TreeMap<City, City> pred = new TreeMap<City, City>(City.getCityCompareByNameComp());
	private PriorityQueue<City> pq = new PriorityQueue<City>(new Comparator<City>() {
		public int compare(City a, City b) {
			if (a == null && b == null) {
				return 0;
			} else if (a == null) {
				return 1;
			} else if (b == null) {
				return -1;
			} else {
				Double d1 = dist.get(a), d2 = dist.get(b);
				if (d1 == null && d2 == null) {
					return 0;
				} else if (d1 == null) {
					return 1;
				} else if (d2 == null) {
					return -1;
				} else {
					double ch = d1 - d2;
					if (ch < 0) {
						return -1;
					} else if (ch > 0) {
						return 1;
					} else {
						return a.getName().compareTo(b.getName());
					}
				}
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
	
	public Stack<City> shortestPath() {
		Stack<City> res = new Stack<City>();
		City cur = end;
		
		while (pred.get(cur) != null) {
			res.push(cur);
			cur = pred.get(cur);
		}
		if (start == cur) {
			res.push(start);
			return res;
		}
		return null; //error case
	}
	
}
