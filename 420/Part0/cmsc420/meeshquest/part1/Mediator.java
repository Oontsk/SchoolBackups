package cmsc420.meeshquest.part1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public class Mediator {
    private Dictionary dictionary = new Dictionary();

    public Mediator(int dim) {
        //will be used for quadtree dimension later
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
    }
}
