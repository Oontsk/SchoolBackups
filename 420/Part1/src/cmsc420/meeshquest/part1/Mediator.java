package cmsc420.meeshquest.part1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

public class Mediator {
    private Dictionary dictionary = new Dictionary();
    private PRQT prqt;
    
    public Mediator(int dim) {
    	prqt = new PRQT(dim);
    }

    public void createCity(String[] values) throws Exception {
        City tba = new City(values[0], values[1], values[2], values[3], values[4]);
        if (dictionary.containsCoor(tba)) {
            throw new Exception("duplicateCityCoordinates");
        }
        if (dictionary.containsName(tba.getName())) {
            throw new Exception("duplicateCityName");
        }
        dictionary.add(tba);
    }

    public Element listCities(Document results, String[] values) throws Exception {
        if (dictionary.isEmpty()) {
            throw new Exception("noCitiesToList");
        }
        Element cityList = results.createElement("cityList");
        if (values[0].equals("name")) {
            for (Map.Entry<String, City> each : dictionary.getCityNameMap().entrySet()) {
                cityList.appendChild(XMLBuilder.cityNode(results, each.getValue()));
            }
        }
        if (values[0].equals("coordinate")) {
            for (City each : dictionary.getCityCoorSet()) {
                cityList.appendChild(XMLBuilder.cityNode(results, each));
            }
        }
        return cityList;
    }

    public void clearAll() {
        dictionary.clear();
        prqt.clear();
    }
    
    public Element deleteCity(Document results, String[] values) throws Exception {
    	if (!dictionary.containsName(values[0])) {
    		throw new Exception("cityDoesNotExist");
    	}
    	Element cityUnmapped = null;
    	City del = dictionary.get(values[0]);
    	if (del.isMapped()) {
    		cityUnmapped = XMLBuilder.cityUnmappedNode(results, del);
    		prqt.remove(del);
    	}
    	//remove from dictionary
    	dictionary.remove(del);
    	return cityUnmapped;
    }
    
    public void mapCity(String[] values) throws Exception {
    	if (!dictionary.containsName(values[0])) {
    		throw new Exception("nameNotInDictionary");
    	}
    	City toMap = dictionary.get(values[0]);
    	if (toMap.isMapped()) {
    		throw new Exception("cityAlreadyMapped");
    	}
    	if (!prqt.intersectsSpatialMap(toMap)) {
    		throw new Exception("cityOutOfBounds");
    	}
    	prqt.add(toMap);
    	toMap.map();
    }
    
    public void unmapCity(String[] values) throws Exception {
    	if (!dictionary.containsName(values[0])) {
    		throw new Exception("nameNotInDictionary");
    	}
    	City toUnmap = dictionary.get(values[0]);
    	if (!toUnmap.isMapped()) {
    		throw new Exception("cityNotMapped");
    	}
    	prqt.remove(toUnmap);
    	toUnmap.unmap();
    }
    
    public Element printPRQuadtree(Document results) throws Exception {
    	if (prqt.isEmpty()) {
    		throw new Exception("mapIsEmpty");
    	}
    	return prqt.print(results);
    }
    
    public void saveMap(String[] values) {
    	CanvasPlus canvas = prqt.draw();
    	try {
    		canvas.save(values[0]);
    	} catch (IOException e) {} //shouldn't happen
    	canvas.draw();
    	canvas.dispose();
    }
    
    public Element rangeCities(Document results, String[] values) throws Exception {
    	RangeCircle circ = new RangeCircle(values[0], values[1], values[2]);
    	TreeSet<City> inRange = prqt.citiesInRange(circ);
    	
    	if (inRange.isEmpty()) {
    		throw new Exception("noCitiesExistInRange");
    	}
    	
    	Element cityList = results.createElement("cityList");
    	for (City cur : inRange) {
    		cityList.appendChild(XMLBuilder.cityNode(results, cur));
    	}
    	
    	if (values.length == 4) { //saveMap
    		CanvasPlus canvas = prqt.draw();
    		canvas.addCircle(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), Color.BLUE, false);
    		try {
    			canvas.save(values[3]);
    		} catch (IOException e) {}
    		canvas.draw();
    		canvas.dispose();
    	}

    	return cityList;
    }
    
    public Element nearestCity(Document results, String[] values) throws Exception {
    	if (prqt.isEmpty()) {
    		throw new Exception("mapIsEmpty");
    	}
    	PointGeometry2D point = new PointGeometry2D(values[0], values[1]);
    	City nearest = prqt.nearestCity(point);
    	return XMLBuilder.cityNode(results, nearest);
    }
}
