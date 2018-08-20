package cmsc420.meeshquest.part3;

import java.util.Comparator;
import java.util.TreeSet;

public abstract class Site extends PointGeometry2D {
	private static final long serialVersionUID = 1L;
	private PointGeometry2D remCoor;
	private String name;
	
	protected Site(String name, String localX, String localY, String remX, String remY) {
		super(localX, localY);
		this.remCoor = new PointGeometry2D(remX, remY); 
		this.name = name;
	}
	
	public PointGeometry2D getRemCoor() {
		return remCoor;
	}
	
	public String getName() {
		return name;
	}
	
	protected static Comparator<String> siteNameComp = new Comparator<String>() {
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    };

    public static Comparator<String> getSiteNameComp() {
        return siteNameComp;
    }

    protected static Comparator<Site> siteCompareByNameComp = new Comparator<Site>() {
        public int compare(Site a, Site b) {
        	if (a == null || b == null) {
        		return 0;
        	}
            return siteNameComp.compare(a.getName(), b.getName());
        }
    };

    public static Comparator<Site> getSiteCompareByNameComp() {
        return siteCompareByNameComp;
    }
    
    protected static Comparator<Site> siteCoorComp = new Comparator<Site>()  {
    	public int compare(Site a, Site b) {
    		int res = PointGeometry2D.getPointCoorComp().compare(a.remCoor, b.remCoor);
    		return res != 0 ? res : PointGeometry2D.getPointCoorComp().compare(a, b);
    	}
    };
    
    public static Comparator<Site> getSiteCoorComp() {
    	return siteCoorComp;
    }
    
    public boolean equals(Object ot) {
    	if (this == ot) {
    		return true;
    	}
    	if (ot == null || !(ot instanceof Site)) {
    		return false;
    	}
    	Site o = (Site) ot;
    	return super.equals(ot) && remCoor.equals(o.remCoor) &&
    			name.equals(o.name);
    }
    
    public abstract TreeSet<Site> getMSTNeighbors();
}
