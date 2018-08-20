package cmsc420.meeshquest.part3;

import java.util.TreeSet;

public class Terminal extends Site {
	private static final long serialVersionUID = 1L;
	private City connectedCity;
	private Airport connectedAirport;

	protected Terminal(String name, String localX, String localY, String remX, String remY) {
		super(name, localX, localY, remX, remY);
	}
	
	public City getConnectedCity() {
		return connectedCity;
	}
	
	public void setConnectedCity(City setTo) { 
		connectedCity = setTo;
	}

	public Airport getConnectedAirport() {
		return connectedAirport;
	}
	
	public void setConnectedAirport(Airport setTo) {
		connectedAirport = setTo;
	}
	
	public boolean equals(Object ot) {
		if (this == ot) {
			return true;
		}
		if (ot == null || !(ot instanceof Terminal)) {
			return false;
		}
		Terminal o = (Terminal) ot;
		return super.equals(ot) && connectedCity.equals(o.connectedCity) &&
				connectedAirport.equals(o.connectedAirport);
	}
	
	public TreeSet<Site> getMSTNeighbors() {
		TreeSet<Site> neighbors = new TreeSet<Site>(Site.getSiteCompareByNameComp());
		neighbors.add(connectedAirport);
		neighbors.add(connectedCity);
		return neighbors;
	}
	
}
