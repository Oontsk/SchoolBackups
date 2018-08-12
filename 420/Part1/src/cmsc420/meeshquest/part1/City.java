package cmsc420.meeshquest.part1;

import java.util.Comparator;

import cmsc420.geom.Geometry2D;

public class City extends PointGeometry2D implements Geometry2D {
	private static final long serialVersionUID = 1L;
	private String name, color, radius;
	private boolean mapped = false;

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
    
    public boolean isMapped() {
    	return mapped == true;
    }
    
    public void map() {
    	mapped = true;
    }
    
    public void unmap() {
    	mapped = false;
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
    
}