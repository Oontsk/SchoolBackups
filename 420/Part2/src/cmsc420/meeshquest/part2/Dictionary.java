package cmsc420.meeshquest.part2;

import cmsc420.meeshquest.part2.City;

import cmsc420.sortedmap.Treap;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Dictionary {
    private Treap<String, City> cityNameMap = new Treap<>(City.getCityNameComp());
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

    public Treap<String, City> getCityNameMap() {
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
    
    public Element printTreap(Document results) {
    	return cityNameMap.print(results);
    }
}
