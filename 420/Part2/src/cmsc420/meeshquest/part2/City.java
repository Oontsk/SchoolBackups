package cmsc420.meeshquest.part2;

import java.util.Comparator;
import java.util.TreeSet;

import cmsc420.geom.Geometry2D;

public class City extends PointGeometry2D implements Geometry2D {
	private static final long serialVersionUID = 1L;
	private static final int UNKNOWN = 0, ISOLATED = 1, NONISOLATED = 2;
	private int isoStatus = UNKNOWN;
	private String name, color, radius;
	private boolean mapped = false;
	private TreeSet<City> neighbors = new TreeSet<City>(cityCompareByNameComp),
			spMapNeighbors = new TreeSet<City>(cityCompareByNameComp);

    public City(String name, String x, String y, String radius, String color) {
        super(x, y);
        this.name = name;
        this.radius = radius;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public String getRadius() {
        return this.radius;
    }
    
    public boolean isIsolated() {
    	return isoStatus == ISOLATED;
    }
    
    public boolean isNonIsolated() {
    	return isoStatus == NONISOLATED;
    }
    
    public boolean isMapped() {
    	return mapped == true;
    }
    
    public void mapIsolated() {
    	mapped = true;
    	isoStatus = ISOLATED;
    }
    
    public void mapNonIsolated() {
    	mapped = true;
    	isoStatus = NONISOLATED;
    }
    
    public void unmap() {
    	mapped = false;
    	isoStatus = UNKNOWN;
    }

    private static Comparator<String> cityNameComp = new Comparator<String>() {
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    };

    public static Comparator<String> getCityNameComp() {
        return cityNameComp;
    }

    private static Comparator<City> cityCompareByNameComp = new Comparator<City>() {
        public int compare(City a, City b) {
            return cityNameComp.compare(a.getName(), b.getName());
        }
    };

    public static Comparator<City> getCityCompareByNameComp() {
        return cityCompareByNameComp;
    }

    public boolean equals(Object o) {
    	if (this == o) {
    		return true;
    	}
    	if (o == null || !(o instanceof City)) {
    		return false;
    	}
    	City other = (City) o;
    	return super.equals(other) && name.equals(other.name) &&
    			radius.equals(other.radius) && color.equals(other.color) && 
    			mapped == other.mapped;
    }
    
    public int getType() {
    	return Geometry2D.POINT;
    }
    
    public TreeSet<City> getNeighbors() {
    	return neighbors;
    }
    
    public TreeSet<City> getSpMapNeighbors() {
    	return spMapNeighbors;
    }
    
    public String toString() {
    	return "(" + getXToString() + "," + getYToString() + ")";
    }
    
}