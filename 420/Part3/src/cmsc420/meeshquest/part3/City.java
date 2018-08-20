package cmsc420.meeshquest.part3;

import java.util.TreeSet;

public class City extends Site {
	private static final long serialVersionUID = 1L;
	private String color, radius;
	private boolean mapped = false;
	private TreeSet<City> neighbors = new TreeSet<City>(siteCompareByNameComp),
			spMapNeighbors = new TreeSet<City>(siteCompareByNameComp);
	private TreeSet<Terminal> connectedTerminals = new TreeSet<Terminal>(siteCompareByNameComp);
	
    public City(String name, String localX, String localY, String remX, String remY, String radius, String color) {
        super(name, localX, localY, remX, remY);
        this.radius = radius;
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

    public String getRadius() {
        return this.radius;
    }
    
    public boolean isMapped() {
    	return mapped == true;
    }
    
    public void map() {
    	mapped = true;
    }
    
    public void unmap() {
    	mapped = false;
    }

    public boolean equals(Object o) {
    	if (this == o) {
    		return true;
    	}
    	if (o == null || !(o instanceof City)) {
    		return false;
    	}
    	City other = (City) o;
    	return super.equals(o) && radius.equals(other.radius) &&
    			color.equals(other.color) && mapped == other.mapped;
    }
    
    public TreeSet<City> getNeighbors() {
    	return neighbors;
    }
    
    public TreeSet<City> getSpMapNeighbors() {
    	return spMapNeighbors;
    }
    
    public TreeSet<Terminal> getConnectedTerminals() {
    	return connectedTerminals;
    }
    
    public String toString() {
    	return "(" + getXToString() + "," + getYToString() + ")";
    }
    
    public TreeSet<Site> getMSTNeighbors() {
    	TreeSet<Site> neighbors = new TreeSet<Site>(Site.getSiteCompareByNameComp());
    	neighbors.addAll(spMapNeighbors);
    	neighbors.addAll(connectedTerminals);
    	return neighbors;
    }
    
}