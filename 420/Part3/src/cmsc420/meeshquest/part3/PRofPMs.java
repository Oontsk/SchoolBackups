package cmsc420.meeshquest.part3;

import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Geometry2D;
import cmsc420.sortedmap.Treap;

public class PRofPMs {
	private PRQT prqt;
	private int localDim, pmOrder;
	private Treap<PointGeometry2D, PM> localPMs = new Treap<PointGeometry2D, PM>(PointGeometry2D.getPointCoorComp());
	
	public PRofPMs(int remoteDim, int localDim, int pmOrder) {
		prqt = new PRQT(remoteDim);
		this.localDim = localDim;
		this.pmOrder = pmOrder;
	}
	
	public boolean contains(PointGeometry2D remCoor, Geometry2D s) {
		if (!prqt.contains(remCoor)) {
			return false;
		}
		PM pm = localPMs.get(remCoor);
		return pm != null ? pm.contains(s) : false;
	}
	
	public void add(PointGeometry2D remCoor, Geometry2D s) throws RoadIntersectsAnotherRoadException, ViolatesPMRulesException {
		if (prqt.contains(remCoor)) {
			PM pm = localPMs.get(remCoor);
			pm.add(s);
		} else {
			prqt.add(remCoor);
			PM pm = pmOrder == 1 ? new PM1(localDim) : new PM3(localDim);
			pm.add(s);
			localPMs.put(remCoor, pm);
		}
	}
	
	public void remove(PointGeometry2D remCoor, Geometry2D s) {
		if (prqt.contains(remCoor)) {
			PM pm = localPMs.get(remCoor);
			pm.remove(s);
			if (pm.isEmpty()) {
				localPMs.remove(remCoor);
				prqt.remove(remCoor);
			}
		}
	}
	
	public boolean encloses(PointGeometry2D remCoor, Geometry2D localGeo) {
		PM dummy = pmOrder == 1 ? new PM1(localDim) : new PM3(localDim);
		return prqt.encloses(remCoor) && dummy.encloses(localGeo);
	}
	
	public boolean isEmpty() {
		return prqt.isEmpty();
	}
	
	public void clear() {
		prqt.clear();
		localPMs.clear();
	}
	
	public Element print(Document results, String remX, String remY) throws Meeshception {
		PointGeometry2D point = new PointGeometry2D(remX, remY);
		if (!prqt.encloses(point)) {
			throw new Meeshception("metropoleOutOfBounds");
		}
		PM pm = localPMs.get(point);
		if (pm == null) {
			throw new Meeshception("metropoleIsEmpty");
		}
		return pm.print(results);
	}
	
	public CanvasPlus draw(String remX, String remY) throws Meeshception {
		PointGeometry2D point = new PointGeometry2D(remX, remY);
		if (!prqt.encloses(point)) {
			throw new Meeshception("metropoleOutOfBounds");
		}
		PM pm = localPMs.get(point);
		if (pm == null) {
			throw new Meeshception("metropoleIsEmpty");
		}
		return pm.draw();
	}
	
	public TreeSet<City> citiesInRange(RangeCircle circ) {
		TreeSet<PointGeometry2D> metropolesInRange = prqt.metropolesInRange(circ);
		TreeSet<City> res = new TreeSet<City>(Site.getSiteCompareByNameComp());
		
		for (PointGeometry2D point : metropolesInRange) {
			PM pm = localPMs.get(point);
			if (pm != null) {
				res.addAll(pm.enclosedCities());
			}
		}
		return res;
	}
	
	public City nearestCity(PointGeometry2D remCoor, PointGeometry2D localCoor) {
		PM pm = localPMs.get(remCoor);
		return pm != null ? pm.nearestCity(localCoor) : null;
	}
}
