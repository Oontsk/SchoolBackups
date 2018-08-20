package cmsc420.meeshquest.part3;

import cmsc420.meeshquest.part3.City;

import cmsc420.sortedmap.Treap;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Dictionary {
    private Treap<String, City> cityNameMap = new Treap<>(Site.getSiteNameComp());
    private Treap<String, Airport> airportNameMap = new Treap<>(Site.getSiteNameComp());
    private Treap<String, Terminal> terminalNameMap = new Treap<>(Site.getSiteNameComp());
    private TreeSet<City> cityCoorSet = new TreeSet<>(Site.getSiteCoorComp());
    private TreeSet<Airport> airportCoorSet = new TreeSet<>(Site.getSiteCoorComp());
    private TreeSet<Terminal> terminalCoorSet = new TreeSet<>(Site.getSiteCoorComp());

    public boolean containsName(String a) {
        return cityNameMap.containsKey(a) ||
        		airportNameMap.containsKey(a) ||
        		terminalNameMap.containsKey(a);
    }

    public boolean containsCoor(PointGeometry2D a) {
        return cityCoorSet.contains(a) ||
        		airportCoorSet.contains(a) ||
        		terminalCoorSet.contains(a);
    }

    public void add(City a) {
        cityNameMap.put(a.getName(), a);
        cityCoorSet.add(a);
    }

    public void remove(City a) {
        cityNameMap.remove(a.getName());
        cityCoorSet.remove(a);
    }
    
    public void add(Airport a) {
    	airportNameMap.put(a.getName(), a);
    	airportCoorSet.add(a);
    }
    
    public void remove(Airport a) {
    	airportNameMap.remove(a.getName());
    	airportCoorSet.remove(a);
    }
    
    public void add(Terminal a) {
    	terminalNameMap.put(a.getName(), a);
    	terminalCoorSet.add(a);
    }
    
    public void remove(Terminal a) {
    	terminalNameMap.remove(a.getName());
    	terminalCoorSet.remove(a);
    }

    public Treap<String, City> getCityNameMap() {
        return cityNameMap;
    }

    public TreeSet<City> getCityCoorSet() {
        return cityCoorSet;
    }
    
    public Treap<String, Airport> getAirportNameMap() {
    	return airportNameMap;
    }
    
    public TreeSet<Airport> getAirportCoorSet() {
    	return airportCoorSet;
    }
    
    public Treap<String, Terminal> getTerminalNameMap() {
    	return terminalNameMap;
    }
    
    public TreeSet<Terminal> getTerminalCoorSet() {
    	return terminalCoorSet;
    }

    public boolean isEmpty() {
    	return hasNoCities() && hasNoAirports() && hasNoTerminals();
    }
    
    public boolean hasNoCities() {
        return cityNameMap.isEmpty() && cityCoorSet.isEmpty();
    }
    
    public boolean hasNoAirports() {
        return airportNameMap.isEmpty() && airportCoorSet.isEmpty();
    }
    
    public boolean hasNoTerminals() {	
        return terminalNameMap.isEmpty() && terminalCoorSet.isEmpty();
    }

    public void clear() {
        cityNameMap.clear();
        cityCoorSet.clear();
        airportNameMap.clear();
        airportCoorSet.clear();
        terminalNameMap.clear();
        terminalCoorSet.clear();
    }
    
    public City getCity(String str) {
    	return cityNameMap.get(str);
    }
    
    public Airport getAirport(String str) {
    	return airportNameMap.get(str);
    }
    
    public Terminal getTerminal(String str) {
    	return terminalNameMap.get(str);
    }
    
    public Element printTreap(Document results) {
    	return cityNameMap.print(results);
    }
}
