package cmsc420.meeshquest.part1;

import cmsc420.meeshquest.part1.City;

import java.util.TreeMap;
import java.util.TreeSet;

public class Dictionary {
    private TreeMap<String, City> cityNameMap =
    		new TreeMap<String, City>(City.getCityNameComp());
    private TreeSet<City> cityCoorSet = new TreeSet<>(City.getPointCoorComp());

    public boolean containsName(String a) {
        return cityNameMap.containsKey(a);
    }

    public boolean containsCoor(City a) {
        return cityCoorSet.contains(a);
    }

    public void add(City a) {
        cityNameMap.put(a.getName(), a);
        cityCoorSet.add(a);
    }

    public void remove(City a) {
        cityNameMap.remove(a.getName());
        cityCoorSet.remove(a);
    }

    public TreeMap<String, City> getCityNameMap() {
        return cityNameMap;
    }

    public TreeSet<City> getCityCoorSet() {
        return cityCoorSet;
    }

    public boolean isEmpty() {
        return cityNameMap.isEmpty() && cityCoorSet.isEmpty();
    }

    public void clear() {
        cityNameMap.clear();
        cityCoorSet.clear();
    }
    
    public City get(String str) {
    	return cityNameMap.get(str);
    }
}
