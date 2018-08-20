package cmsc420.meeshquest.part3;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

public class Mediator {
    private Dictionary dictionary = new Dictionary();
    private PRofPMs pRofPMs;
    
    public Mediator(int remoteDim, int localDim, int pmOrder) {
    	pRofPMs = new PRofPMs(remoteDim, localDim, pmOrder);
    }

    public void createCity(String[] values) throws Meeshception {
        City tba = new City(values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
        if (dictionary.containsCoor(tba)) {
            throw new Meeshception("duplicateCityCoordinates");
        }
        if (dictionary.containsName(tba.getName())) {
            throw new Meeshception("duplicateCityName");
        }
        dictionary.add(tba);
    }
    
    public Element[] deleteCity(Document results, String[] values) throws Meeshception {
    	City city = dictionary.getCity(values[0]);
    	if (city == null) {
    		throw new Meeshception("cityDoesNotExist");
    	}
    	PointGeometry2D remCoor = city.getRemCoor();
    	Element[] res;
    	int resSize = city.getNeighbors().size(), i = 0;
    	if (city.isMapped()) {
    		res = new Element[1 + resSize];
    		res[i++] = XMLBuilder.cityUnmappedNode(results, city);
    		pRofPMs.remove(remCoor, city);
    		
    		/*for (Terminal terminal : city.getConnectedTerminals()) {
    			Airport airport = terminal.getConnectedAirport();
    			airport.getConnectedTerms().remove(terminal);
    			if (airport.getConnectedTerms().isEmpty()) {
    				pRofPMs.remove(remCoor, airport);
    				dictionary.remove(airport);
    			}
    			pRofPMs.remove(remCoor, terminal);
    			dictionary.remove(terminal);
    			Road termRoad = new Road(city, terminal);
    			pRofPMs.remove(remCoor, termRoad);
    		}*/
    		
    	} else {
    		res = new Element[resSize];
    	}

    	for (City neighbor : city.getNeighbors()) {
    		neighbor.getNeighbors().remove(city);
    		if (pRofPMs.encloses(remCoor, neighbor) &&
    				pRofPMs.encloses(remCoor, city)) {
    			neighbor.getSpMapNeighbors().remove(city);
    		}
    		if (neighbor.getNeighbors().isEmpty()) {
    			/*for (Terminal terminal : neighbor.getConnectedTerminals()) {
    				Airport airport = terminal.getConnectedAirport();
    				airport.getConnectedTerms().remove(terminal);
    				if (airport.getConnectedTerms().isEmpty()) {
    					pRofPMs.remove(remCoor, airport);
    					dictionary.remove(airport);
    				}
    				pRofPMs.remove(remCoor, terminal);
    				dictionary.remove(terminal);
    				Road termRoad = new Road(neighbor, terminal);
    				pRofPMs.remove(remCoor, termRoad);
    			}*/
    			pRofPMs.remove(remCoor, neighbor);
    			if (neighbor.isMapped()) {
    				neighbor.unmap();
    			}
    		}
    		Road road = new Road(city, neighbor);
    		res[i++] = XMLBuilder.roadUnmappedNode(results, road);
    		pRofPMs.remove(remCoor, road);
    	}
    	
    	dictionary.remove(city);
    	return res;
    }
    
    public void clearAll() {
        dictionary.clear();
        pRofPMs.clear();
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
    	City start = dictionary.getCity(values[0]), end = dictionary.getCity(values[1]);
    	if (start == null) {
    		throw new Meeshception("startPointDoesNotExist");
    	}
    	if (end == null) {
    		throw new Meeshception("endPointDoesNotExist");
    	}
    	if (start == end) {
    		throw new Meeshception("startEqualsEnd");
    	}
    	if (!start.getRemCoor().equals(end.getRemCoor())) {
    		throw new Meeshception("roadNotInOneMetropole");
    	}
    	Road road = new Road(start, end);
    	if (!pRofPMs.encloses(start.getRemCoor(), road)) {
    		throw new Meeshception("roadOutOfBounds");
    	}
       	if (start.getNeighbors().contains(end)) {
    		throw new Meeshception("roadAlreadyMapped");
    	}
       	PointGeometry2D remCoor = start.getRemCoor();
       	try {
           	pRofPMs.add(remCoor, road);
       	} catch (RoadIntersectsAnotherRoadException e) {
       		pRofPMs.remove(remCoor, road);
       		throw new Meeshception("roadIntersectsAnotherRoad");
       	} catch (ViolatesPMRulesException e) {
       		pRofPMs.remove(remCoor, road);
       		throw new Meeshception("roadViolatesPMRules");
       	}
       	//map endpoints if need to
       	boolean startWasNotMapped = false;
    	if (!start.isMapped() && pRofPMs.encloses(remCoor, start)) {
    		try {
    			pRofPMs.add(remCoor, start);
    		} catch (RoadIntersectsAnotherRoadException |
    				ViolatesPMRulesException e) {
    			//adding vertex never throws RoadIntersectsAnotherRoad
    			pRofPMs.remove(remCoor, road);
    			pRofPMs.remove(remCoor, start);
    			throw new Meeshception("roadViolatesPMRules");
    		}
    		startWasNotMapped = true;
    		start.map();
    	}
    	if (!end.isMapped() && pRofPMs.encloses(remCoor, end)) {
    		try {
    			pRofPMs.add(remCoor, end);
    		} catch (RoadIntersectsAnotherRoadException |
    				ViolatesPMRulesException e) {
    			//adding vertex never throws RoadIntersectsAnotherRoad
    			pRofPMs.remove(remCoor, road);
    			pRofPMs.remove(remCoor, end);
    			if (startWasNotMapped) {
    				pRofPMs.remove(remCoor, start);
    				start.unmap();
    			}
    			throw new Meeshception("roadViolatesPMRules");
    		}
    		end.map();
    	}
    	
    	//update appropriate adjacency lists
    	start.getNeighbors().add(end);
    	end.getNeighbors().add(start);
    	if (pRofPMs.encloses(remCoor, start) && 
    			pRofPMs.encloses(remCoor, end)) {
    		start.getSpMapNeighbors().add(end);
    		end.getSpMapNeighbors().add(start);
    	}
    	
    	return XMLBuilder.roadCreatedNode(results, start, end);
    }
    
    public void mapAirport(String[] values) throws Meeshception {
    	Airport airport = new Airport(values[0], values[1], values[2], values[3], values[4]);
    	if (dictionary.containsName(airport.getName())) {
    		throw new Meeshception("duplicateAirportName");
    	}
    	if (dictionary.containsCoor(airport)) {
    		throw new Meeshception("duplicateAirportCoordinates");
    	}
    	if (!pRofPMs.encloses(airport.getRemCoor(), airport)) {
    		throw new Meeshception("airportOutOfBounds");
    	}
    	Terminal terminal = new Terminal(values[5], values[6], values[7], values[3], values[4]);
    	if (dictionary.containsName(terminal.getName())) {
    		throw new Meeshception("duplicateTerminalName");
    	}
    	if (dictionary.containsCoor(terminal)) {
    		throw new Meeshception("duplicateTerminalCoordinates");
    	}
    	if (!pRofPMs.encloses(terminal.getRemCoor(), terminal)) {
    		throw new Meeshception("terminalOutOfBounds");
    	}
    	City city = dictionary.getCity(values[8]);
    	if (city == null) {
    		throw new Meeshception("connectingCityDoesNotExist");
    	}
    	if (!city.getRemCoor().equals(airport.getRemCoor())) {
    		throw new Meeshception("connectingCityNotInSameMetropole");
    	}
    	PointGeometry2D remCoor = airport.getRemCoor();
    	//try to insert airport
    	try {
    		pRofPMs.add(remCoor, airport);
    	} catch (RoadIntersectsAnotherRoadException |
    			ViolatesPMRulesException e) {
    		pRofPMs.remove(remCoor, airport);
    		throw new Meeshception("airportViolatesPMRules");
    	}

    	if (!city.isMapped()) {
    		pRofPMs.remove(remCoor, airport); //retroactively
    		throw new Meeshception("connectingCityNotMapped");
    	}
    	//try to insert terminal
    	try {
    		pRofPMs.add(remCoor, terminal);
    	} catch (RoadIntersectsAnotherRoadException |
    			ViolatesPMRulesException e) {
    		pRofPMs.remove(remCoor, airport);
    		pRofPMs.remove(remCoor, terminal);
    		throw new Meeshception("terminalViolatesPMRules");
    	}
    	//try to insert road between terminal and connecting city
    	Road road = new Road(city, terminal);
    	try {
    		pRofPMs.add(remCoor, road);
    	} catch (ViolatesPMRulesException e) {
    		pRofPMs.remove(remCoor, airport);
    		pRofPMs.remove(remCoor, terminal);
    		pRofPMs.remove(remCoor, road);
    		throw new Meeshception("terminalViolatesPMRules");
    	} catch (RoadIntersectsAnotherRoadException e) {
    		pRofPMs.remove(remCoor, airport);
    		pRofPMs.remove(remCoor, terminal);
    		pRofPMs.remove(remCoor, road);
    		throw new Meeshception("roadIntersectsAnotherRoad");
    	}    	
    	//update fields of all sites
    	city.getConnectedTerminals().add(terminal);
    	airport.getConnectedTerms().add(terminal);
    	terminal.setConnectedAirport(airport);
    	terminal.setConnectedCity(city);
    	Airport.getAllAirports().add(airport);
    	
    	//update dictionary
    	dictionary.add(airport);
    	dictionary.add(terminal);
    }
    
    public void mapTerminal(String[] values) throws Meeshception {
    	Terminal terminal = new Terminal(values[0], values[1], values[2], values[3], values[4]);
    	if (dictionary.containsName(terminal.getName())) {
    		throw new Meeshception("duplicateTerminalName");
    	}
    	if (dictionary.containsCoor(terminal)) {
    		throw new Meeshception("duplicateTerminalCoordinate");
    	}
    	if (!pRofPMs.encloses(terminal.getRemCoor(), terminal)) {
    		throw new Meeshception("terminalOutOfBounds");
    	}
    	Airport airport = dictionary.getAirport(values[6]);
    	if (airport == null) {
    		throw new Meeshception("airportDoesNotExist");
    	}
    	if (!airport.getRemCoor().equals(terminal.getRemCoor())) {
    		throw new Meeshception("airportNotInSameMetropole");
    	}
    	City city = dictionary.getCity(values[5]);
    	if (city == null) {
    		throw new Meeshception("connectingCityDoesNotExist");
    	}
    	if (!city.getRemCoor().equals(terminal.getRemCoor())) {
    		throw new Meeshception("connectingCityNotInSameMetropole");
    	}
    	if (!city.isMapped()) {
    		throw new Meeshception("connectingCityNotMapped");
    	}
    	PointGeometry2D remCoor = terminal.getRemCoor();
    	//now try to insert the terminal
    	try {
    		pRofPMs.add(remCoor, terminal);
    	} catch (RoadIntersectsAnotherRoadException | 
    			ViolatesPMRulesException e) {
    		pRofPMs.remove(remCoor, terminal);
    		throw new Meeshception("terminalViolatesPMRules");
    	}
    	//map road from terminal to connecting city
    	Road road = new Road(city, terminal);
    	try {
    		pRofPMs.add(remCoor, road);
    	} catch (ViolatesPMRulesException e) {
    		pRofPMs.remove(remCoor, terminal);
    		pRofPMs.remove(remCoor, road);
    		throw new Meeshception("terminalViolatesPMRules");
    	} catch (RoadIntersectsAnotherRoadException e) {
    		pRofPMs.remove(remCoor, terminal);
    		pRofPMs.remove(remCoor, road);
    		throw new Meeshception("roadIntersectsAnotherRoad");
    	}
    	//update fields of sites
    	terminal.setConnectedAirport(airport);
    	terminal.setConnectedCity(city);
    	airport.getConnectedTerms().add(terminal);
    	city.getConnectedTerminals().add(terminal);
    	
    	//update dictionary
    	dictionary.add(terminal);
    }
    
    public Element unmapRoad(Document results, String[] values) throws Meeshception {
    	City start = dictionary.getCity(values[0]), end = dictionary.getCity(values[1]);
    	if (start == null) {
    		throw new Meeshception("startPointDoesNotExist");
    	}
    	if (end == null) {
    		throw new Meeshception("endPointDoesNotExist");
    	}
    	if (start == end) {
    		throw new Meeshception("startEqualsEnd");
    	}
    	if (!start.getNeighbors().contains(end)) {
    		throw new Meeshception("roadNotMapped");
    	}
    	PointGeometry2D remCoor = start.getRemCoor();
    	Road road = new Road(start, end);
    	pRofPMs.remove(remCoor, road);
    	
    	//update city fields
    	start.getNeighbors().remove(end);
    	end.getNeighbors().remove(start);
    	if (pRofPMs.encloses(remCoor, start) &&
    			pRofPMs.encloses(remCoor, end)) {
    		start.getSpMapNeighbors().remove(end);
    		end.getSpMapNeighbors().remove(start);
    	}
    	
    	//remove any cities that are now isolated
    	if (start.getNeighbors().isEmpty() && start.isMapped()) {
    		/*check if removing this city would cause any
    		 * airport or terminals to be removed as well*/
    		/*for (Terminal terminal : start.getConnectedTerminals()) {
    			Airport airport = terminal.getConnectedAirport();
    			airport.getConnectedTerms().remove(terminal);
    			if (airport.getConnectedTerms().isEmpty()) {
    				pRofPMs.remove(remCoor, airport);
    				dictionary.remove(airport);
    				Airport.getAllAirports().remove(airport);
    			}
    	
    			pRofPMs.remove(remCoor, terminal);
    			dictionary.remove(terminal);
    			Road termRoad = new Road(start, terminal);
    			pRofPMs.remove(remCoor, termRoad);
    		}*/
    		pRofPMs.remove(remCoor, start);
    		if (start.getConnectedTerminals().isEmpty()) {
    			start.unmap();
    		}
    	}
    	if (end.getNeighbors().isEmpty() && end.isMapped()) {
    		/*check if removing this city would cause any
    		 * airport or terminals to be removed as well*/
    		/*for (Terminal terminal : end.getConnectedTerminals()) {
    			Airport airport = terminal.getConnectedAirport();
    			airport.getConnectedTerms().remove(terminal);
    			if (airport.getConnectedTerms().isEmpty()) {
    				pRofPMs.remove(remCoor, airport);
    				dictionary.remove(airport);
    				Airport.getAllAirports().remove(airport);
    			}
    			
    			pRofPMs.remove(remCoor, terminal);
    			dictionary.remove(terminal);
    			Road termRoad = new Road(end, terminal);
    			pRofPMs.remove(remCoor, termRoad);
    		}*/
    		pRofPMs.remove(remCoor, end);
    		if (end.getConnectedTerminals().isEmpty()) {
    			end.unmap();
    		}
    	}
    	
    	return XMLBuilder.roadDeletedNode(results, start, end);
    }
    
    public Element[] unmapAirport(Document results, String[] values) throws Meeshception {
    	Airport airport = dictionary.getAirport(values[0]);
    	if (airport == null) {
    		throw new Meeshception("airportDoesNotExist");
    	}
    	PointGeometry2D remCoor = airport.getRemCoor();
    	pRofPMs.remove(remCoor, airport);
    	dictionary.remove(airport);
    	Airport.getAllAirports().remove(airport);
    	
    	Element[] termsUnmapped = new Element[airport.getConnectedTerms().size()];
    	int i = 0;
    	for (Terminal terminal : airport.getConnectedTerms()) {
    		City city = terminal.getConnectedCity();
    		city.getConnectedTerminals().remove(terminal);
    		pRofPMs.remove(remCoor, terminal);
    		Road road = new Road(city, terminal);
    		pRofPMs.remove(remCoor, road);
    		dictionary.remove(terminal);
    		termsUnmapped[i++] = XMLBuilder.terminalUnmappedNode(results, terminal); 
    	}

    	return termsUnmapped;
    }
    
    public Element unmapTerminal(Document results, String[] values) throws Meeshception {
    	Terminal terminal = dictionary.getTerminal(values[0]);
    	if (terminal == null) {
    		throw new Meeshception("terminalDoesNotExist");
    	}
    	//update connected city and airport first
    	City city = terminal.getConnectedCity();
    	city.getConnectedTerminals().remove(terminal);
    	Airport airport = terminal.getConnectedAirport();
    	airport.getConnectedTerms().remove(terminal);
    	
    	PointGeometry2D remCoor = terminal.getRemCoor();
    	Element airportUnmapped = null;
    	//check to see if have to unmap airport
    	if (airport.getConnectedTerms().isEmpty()) {
    		airportUnmapped = XMLBuilder.airportUnmappedNode(results, airport);
    		pRofPMs.remove(remCoor, airport);
    		dictionary.remove(airport);
    		Airport.getAllAirports().remove(airport);
    	}
    	
    	//unmap the terminal and the road
    	pRofPMs.remove(remCoor, terminal);
    	dictionary.remove(terminal);
    	Road road = new Road(city, terminal);
    	pRofPMs.remove(remCoor, road);
    	
    	return airportUnmapped;
    }
    
    public Element printPMQuadtree(Document results, String[] values) throws Meeshception {
    	return pRofPMs.print(results, values[0], values[1]);
    }
    
    public CanvasPlus saveMap(String[] values) throws Meeshception {
    	CanvasPlus canvas = pRofPMs.draw(values[0], values[1]);
    	drawSaveDisposeCanvas(values[2], canvas);
    	return canvas;
    }
    
    private void drawSaveDisposeCanvas(String fileName, CanvasPlus canvas) {
    	try {
    		canvas.save(fileName);
    	} catch (IOException e) {}
    	canvas.draw();
    	canvas.dispose();
    }
    
    public Element globalRangeCities(Document results, String[] values) throws Meeshception {
    	RangeCircle circ = new RangeCircle(values[0], values[1], values[2]);
    	TreeSet<City> inRange = pRofPMs.citiesInRange(circ);
    	
    	if (inRange.isEmpty()) {
    		throw new Meeshception("noCitiesExistInRange");
    	}
    	
    	Element cityList = results.createElement("cityList");
    	for (City cur : inRange) {
    		cityList.appendChild(XMLBuilder.cityNode(results, cur));
    	}
    	return cityList;
    }
    
    public Element nearestCity(Document results, String[] values) throws Meeshception {
    	PointGeometry2D localCoor = new PointGeometry2D(values[0], values[1]),
    			remCoor = new PointGeometry2D(values[2], values[3]);
    	City nearest = pRofPMs.nearestCity(remCoor, localCoor);
    	if (nearest == null) {
    		throw new Meeshception("cityNotFound");
    	}
    	return XMLBuilder.cityNode(results, nearest);
    }
    
    public Element mst(Document results, String[] values) throws Meeshception {
    	City city = dictionary.getCity(values[0]);
    	if (city == null) {
    		throw new Meeshception("cityDoesNotExist");
    	}
    	if (!city.isMapped()) {
    		throw new Meeshception("cityNotMapped");
    	} 
    	Prim prim = new Prim(city, dictionary);
    	prim.findMinimumSpanningTree();
    	return prim.mSTNode(results);
    }
}
