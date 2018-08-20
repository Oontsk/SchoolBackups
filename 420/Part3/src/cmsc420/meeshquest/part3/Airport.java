package cmsc420.meeshquest.part3;

import java.util.TreeSet;

public class Airport extends Site {
	private static final long serialVersionUID = 1L;
	private TreeSet<Terminal> connectedTerms = new TreeSet<Terminal>(siteCompareByNameComp);
	private static TreeSet<Airport> allAirports = new TreeSet<Airport>(siteCompareByNameComp);
	
	protected Airport(String name, String localX, String localY, String remX, String remY) {
		super(name, localX, localY, remX, remY);
	}
	
	public TreeSet<Terminal> getConnectedTerms() {
		return connectedTerms;
	}
	
	public static TreeSet<Airport> getAllAirports() {
		return allAirports;
	}
	
	public boolean equals(Object ot) {
		if (this == ot) {
			return true;
		}
		if (ot == null || !(ot instanceof Airport)) {
			return false;
		}
		Airport o = (Airport) ot;
		return super.equals(ot) && connectedTerms.equals(o.connectedTerms);
	}
	
	public TreeSet<Site> getMSTNeighbors() {
		TreeSet<Site> neighbors = new TreeSet<Site>(Site.getSiteCompareByNameComp());
		neighbors.addAll(connectedTerms);
		for (Airport otherAirport : allAirports) {
			if (!otherAirport.getRemCoor().equals(this.getRemCoor())) {
				neighbors.add(otherAirport);
			}
		}
		return neighbors;
	}
}