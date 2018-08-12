package cmsc420.meeshquest.part1;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class City extends Point2D.Float {
	private static final long serialVersionUID = 1L;
	private String name, color, radius;

    public City(String name, String x, String y, String radius, String color) {
        super(java.lang.Float.parseFloat(x), java.lang.Float.parseFloat(y));
        this.name = name;
        this.radius = radius;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public String getXToString() {
        return String.valueOf((int) x);
    }

    public String getYToString() {
        return String.valueOf((int) y);
    }

    public String getColor() {
        return this.color;
    }

    public String getRadius() {
        return this.radius;
    }

    private static Comparator<String> cityNameComp = new Comparator<String>() {
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    };

    public static Comparator<String> getCityNameComp() {
        return cityNameComp;
    }

    private static Comparator<Point2D.Float> cityCoorComp = new Comparator<Point2D.Float>() {
        public int compare(Point2D.Float a, Point2D.Float b) {
            int res = (int) (a.y - b.y);
            return res != 0 ? res : (int) (a.x - b.x);
        }
    };

    public static Comparator<Point2D.Float> getCityCoorComp() {
        return cityCoorComp;
    }

    private static Comparator<City> cityCompareByNameComp = new Comparator<City>() {
        public int compare(City a, City b) {
            return cityNameComp.compare(a.getName(), b.getName());
        }
    };

    public static Comparator<City> getCityCompareByNameComp() {
        return cityCompareByNameComp;
    }

}
