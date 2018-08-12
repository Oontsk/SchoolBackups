package cmsc420.meeshquest.part2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

public class Mediator {
    private Dictionary dictionary = new Dictionary();
    private PM3 pm3;
    
    public Mediator(int dim) {
    	pm3 = new PM3(dim);
    }

    public void createCity(String[] values) throws Meeshception {
        City tba = new City(values[0], values[1], values[2], values[3], values[4]);
        if (dictionary.containsCoor(tba)) {
            throw new Meeshception("duplicateCityCoordinates");
        }
        if (dictionary.containsName(tba.getName())) {
            throw new Meeshception("duplicateCityName");
        }
        dictionary.add(tba);
    }
    
    public void clearAll() {
        dictionary.clear();
        pm3.clear();
    }
    
    public Element listCities(Document results, String sortBy) throws Meeshception {
        if (dictionary.isEmpty()) {
            throw new Meeshception("noCitiesToList");
        }
        Element cityList = results.createElement("cityList");
        if (sortBy.equals("name")) {
            for (Map.Entry<String, City> each : dictionary.getCityNameMap().entrySet()) {
                cityList.appendChild(XMLBuilder.cityNode(results, each.getValue()));
            }
        } else { //sortBy.equals("coordinate")) {
            for (City each : dictionary.getCityCoorSet()) {
                cityList.appendChild(XMLBuilder.cityNode(results, each));
            }
        }
        return cityList;
    }

    public Element printTreap(Document results) throws Meeshception {
    	if (dictionary.isEmpty()) {
    		throw new Meeshception("emptyTree");
    	}
    	return dictionary.printTreap(results);
    }
    
    public Element mapRoad(Document results, String[] values) throws Meeshception {
    	City start = dictionary.get(values[0]), end = dictionary.get(values[1]);
    	if (start == null) {
    		throw new Meeshception("startPointDoesNotExist");
    	}
    	if (end == null) {
    		throw new Meeshception("endPointDoesNotExist");
    	}
    	if (start == end) {
    		throw new Meeshception("startEqualsEnd");
    	}
    	if (start.isIsolated() || end.isIsolated()) {
    		throw new Meeshception("startOrEndIsIsolated");
    	}
    	if (start.getNeighbors().contains(end)) {
    		throw new Meeshception("roadAlreadyMapped");
    	}
    	Road road = new Road(start, end);
    	if (!pm3.encloses(road)) {
    		throw new Meeshception("roadOutOfBounds");
    	}
    	//map the endpoints first if need to, followed by road
    	if (!start.isMapped() && pm3.encloses(start)) {
    		pm3.add(start);
    		start.mapNonIsolated();
    	}
    	if (!end.isMapped() && pm3.encloses(end)) {
    		pm3.add(end);
    		end.mapNonIsolated();
    	}
    	
    	//update appropriate adjacency lists
    	start.getNeighbors().add(end);
    	end.getNeighbors().add(start);
    	if (pm3.encloses(start) && pm3.encloses(end)) {
    		start.getSpMapNeighbors().add(end);
    		end.getSpMapNeighbors().add(start);
    	}
    	
    	pm3.add(road);
    	
    	return XMLBuilder.roadCreatedNode(results, start, end);
    }
    
    public void mapCity(String[] values) throws Meeshception {
    	City toMap = dictionary.get(values[0]);
    	if (toMap == null) {
    		throw new Meeshception("nameNotInDictionary");
    	}
    	if (toMap.isMapped()) {
    		throw new Meeshception("cityAlreadyMapped");
    	}
    	if (!pm3.encloses(toMap)) {
    		throw new Meeshception("cityOutOfBounds");
    	}
    	pm3.add(toMap);
    	toMap.mapIsolated();
    }
    
    public Element printPMQuadtree(Document results) throws Meeshception {
    	if (pm3.isEmpty()) {
    		throw new Meeshception("mapIsEmpty");
    	}
    	return pm3.print(results);
    }
    
    public CanvasPlus saveMap(String fileName) {
    	CanvasPlus canvas = pm3.draw();
    	drawSaveDisposeCanvas(fileName, canvas);
    	return canvas;
    }
    
    private void drawSaveDisposeCanvas(String fileName, CanvasPlus canvas) {
    	try {
    		canvas.save(fileName);
    	} catch (IOException e) {}
    	canvas.draw();
    	canvas.dispose();
    }
    
    public Element rangeCities(Document results, String[] values) throws Meeshception {
    	RangeCircle circ = new RangeCircle(values[0], values[1], values[2]);
    	TreeSet<City> inRange = pm3.citiesInRange(circ);
    	
    	if (inRange.isEmpty()) {
    		throw new Meeshception("noCitiesExistInRange");
    	}
    	
    	Element cityList = results.createElement("cityList");
    	for (City cur : inRange) {
    		cityList.appendChild(XMLBuilder.cityNode(results, cur));
    	}
    	
    	if (values.length == 4) { //saveMap
    		CanvasPlus canvas = pm3.draw();
    		canvas.addCircle(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), Color.BLUE, false);
    		drawSaveDisposeCanvas(values[3], canvas);
    	}
    	return cityList;
    }
    
    public Element rangeRoads(Document results, String[] values) throws Meeshception {
    	RangeCircle circ = new RangeCircle(values[0], values[1], values[2]);
    	TreeSet<Road> inRange = pm3.roadsInRange(circ);
    	
    	if (inRange.isEmpty()) {
    		throw new Meeshception("noRoadsExistInRange");
    	}
    	
    	Element roadList = results.createElement("roadList");
    	for (Road cur : inRange) {
    		roadList.appendChild(XMLBuilder.roadNode(results, cur));
    	}
    	
    	if (values.length == 4) {
    		CanvasPlus canvas = pm3.draw();
    		canvas.addCircle(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), Color.BLUE, false);
    		drawSaveDisposeCanvas(values[3], canvas);
    	}
    	return roadList;
    }
    
    public Element nearestCity(Document results, String[] values) throws Meeshception {
    	PointGeometry2D point = new PointGeometry2D(values[0], values[1]);
    	City nearest = pm3.nearestNonIsolatedCity(point);
    	if (nearest == null) {
    		throw new Meeshception("cityNotFound");
    	}
    	return XMLBuilder.cityNode(results, nearest);
    }
    
    public Element nearestIsolatedCity(Document results, String[] values) throws Meeshception {
    	PointGeometry2D point = new PointGeometry2D(values[0], values[1]);
    	City nearest = pm3.nearestIsolatedCity(point);
    	if (nearest == null) {
    		throw new Meeshception("cityNotFound");
    	}
    	return XMLBuilder.isolatedCityNode(results, nearest);

    }
    
    public Element nearestRoad(Document results, String[] values) throws Meeshception {
    	PointGeometry2D point = new PointGeometry2D(values[0], values[1]);
    	Road nearest = pm3.nearestRoad(point);
    	if (nearest == null) {
    		throw new Meeshception("roadNotFound");
    	}
    	return XMLBuilder.roadNode(results, nearest);
    }
    
    public Element nearestCityToRoad(Document results, String[] values) throws Meeshception {
    	City start = dictionary.get(values[0]), end = dictionary.get(values[1]);
    	if (start == null || end == null || !start.getNeighbors().contains(end)) {
    		throw new Meeshception("roadIsNotMapped");
    	}
    	Road road = new Road(start, end);
    	City nearest = pm3.nearestCity(road);
    	if (nearest == null) {
    		throw new Meeshception("noOtherCitiesMapped");
    	}
    	return XMLBuilder.cityNode(results, nearest);
    }
    
    public Element shortestPath(Document results, String[] values) throws Meeshception {
    	City start = dictionary.get(values[0]), end = dictionary.get(values[1]);
    	if (start == null || !start.isMapped()) {
    		throw new Meeshception("nonExistentStart");
    	}
    	if (end == null || !end.isMapped()) {
    		throw new Meeshception("nonExistentEnd");
    	}
    	boolean saveMap = false;

    	if (values.length > 2 && values[2].equals("saveMap")) {
    		saveMap = true;
    	}
    	//tis time to Dijkstra
    	Dijkstra dijkstra = new Dijkstra(start, end, dictionary);
    	dijkstra.findShortestPath();

    	CanvasPlus canvas = null;
    	if (saveMap) {
    		canvas = pm3.draw();
    		canvas.removePoint(start.getName(), start.x, start.y, Color.BLACK);
    		canvas.addPoint("Start", start.x, start.y, Color.GREEN);
    		canvas.removePoint(end.getName(), end.x, end.y, Color.BLACK);
    		canvas.addPoint("End", end.x, end.y, Color.RED);
    	}

    	Element sp = dijkstra.shortestPath(results, canvas);
    	if (sp == null) {
    		throw new Meeshception("noPathExists");
    	}
    	
    	if (saveMap) {
    		drawSaveDisposeCanvas(values[2], canvas);
    	}
    	return sp;
    }
    
    
}
