package cmsc420.meeshquest.part3;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.sortedmap.Treap;

public class Prim {
	
	private City start;
	private Treap<Site, Double> dist = new Treap<Site, Double>(Site.getSiteCompareByNameComp());
	private TreeMap<Site, Site> pred = new TreeMap<Site, Site>(Site.getSiteCompareByNameComp());
	private PriorityQueue<Site> outside = new PriorityQueue<Site>(new Comparator<Site>() {
		public int compare(Site a, Site b) {
			Double d1 = dist.get(a), d2 = dist.get(b);
			double ch = d1 - d2;
			if (ch < 0) {
				return -1;
			} else if (ch > 0) {
				return 1;
			} else {
				return Site.getSiteCompareByNameComp().compare(a, b);			
			}
		}
	});
	
	public Prim(City start, Dictionary dictionary) {
		this.start = start;
		for (City city : dictionary.getCityCoorSet()) {
			if (!city.equals(start)) {
				dist.put(city, Double.MAX_VALUE);
			} else {
				dist.put(city, 0.);
			}
			pred.put(city, null);
			outside.add(city);
		}
		for (Terminal terminal : dictionary.getTerminalCoorSet()) {
			dist.put(terminal, Double.MAX_VALUE);
			pred.put(terminal, null);
			outside.add(terminal);
		}
		for (Airport airport : dictionary.getAirportCoorSet()) {
			dist.put(airport, Double.MAX_VALUE);
			pred.put(airport, null);
			outside.add(airport);
		}
	}
	
	public void findMinimumSpanningTree() {	
		while (!outside.isEmpty()) {
			Site min = outside.poll();
			TreeSet<Site> mSTNeighbors = min.getMSTNeighbors();
			for (Site neighbor : mSTNeighbors) {
				if (outside.contains(neighbor)) {
					double newDist;
					if (min instanceof Airport && neighbor instanceof Airport) {
						newDist = Utilities.distanceBetweenGeos(min.getRemCoor(), neighbor.getRemCoor());
					} else {
						newDist = Utilities.distanceBetweenGeos(min, neighbor);
					}
					if (newDist == dist.get(neighbor) && Site.getSiteCompareByNameComp().compare(min, pred.get(neighbor)) < 0) {
						pred.put(neighbor, min);
					} else if (newDist < dist.get(neighbor)) {
						dist.put(neighbor, newDist);
						pred.put(neighbor, min);
						outside.remove(neighbor);
						outside.add(neighbor);
					}
				}
			}
		}
	}
	
	public Element mSTNode(Document results) {
		Element mst = results.createElement("mst"),
			node = results.createElement("node");
		node.setAttribute("name", start.getName());
		mst.appendChild(node);
		
		double distanceSpanned = mSTNodeHelper(results, start, node);
		
		DecimalFormat df = new DecimalFormat("0.000");
		mst.setAttribute("distanceSpanned", df.format(distanceSpanned));
		return mst;
	}
	
	private double mSTNodeHelper(Document results, Site curPred, Element curNode) {
		double d = 0;
		TreeSet<Site> childrenInOrder = new TreeSet<Site>(Site.getSiteCompareByNameComp());
		for (Map.Entry<Site, Site> entry : pred.entrySet())  {
			Site next = entry.getKey(), pred = entry.getValue();
			if (curPred == pred) { //next's predecessor is the current guy; therefore,
				//next is a child node in the tree
				childrenInOrder.add(next);
			}
		}
		for (Site child : childrenInOrder) {
			d += dist.get(child);
			Element node = results.createElement("node");
			node.setAttribute("name", child.getName());
			d += mSTNodeHelper(results, child, node);
			curNode.appendChild(node);
		}
		return d;
	}
}
