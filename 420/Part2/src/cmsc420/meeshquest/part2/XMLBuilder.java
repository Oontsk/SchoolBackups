package cmsc420.meeshquest.part2;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.xml.XmlUtility;

public class XMLBuilder {
    private static String[] validCommands = {
    		"createCity", "clearAll", "listCities", "printTreap", "mapRoad",   
    		"mapCity", "printPMQuadtree", "saveMap", "rangeCities",
    		"rangeRoads", "nearestCity", "nearestIsolatedCity", "nearestRoad", 
    		"nearestCityToRoad", "shortestPath"
    };
    private static String[][] validCommandsParameters = {
           	{"name", "x", "y", "radius", "color"}, {"sortBy"}, {"start", "end"}, {"name"},
           	{"x", "y", "radius"}, {"x", "y", "radius", "saveMap"}, {"x", "y"},
           	{"start", "end", "saveMap"}, {"start", "end", "saveHTML"},
           	{"start", "end", "saveMap", "saveHTML"}
    };
    
    public static Element handleCommandNode(Document results, Element cn, Mediator mediator) {
        String comName = cn.getNodeName();

        for (int i = 0; i < validCommands.length; ++i) {
            if (validCommands[i].equals(comName)) {
                return statusNode(results, cn, mediator, i);
            }
        }

        return results.createElement("fatalError"); //unrecognized command
    }

    private static Element statusNode(Document results, Element cn, Mediator mediator, int comNum) {
        Element status = null, output = null, commandOutput = null;
        String[] params = new String[]{}, values = new String[]{};
        
        boolean saveHTML = false;
        String saveHTMLName = "";

        switch (comNum) {
            case 0: //createCity
                params = validCommandsParameters[0];
                values = paramValues(cn, params);
                try {
                    mediator.createCity(values);
                } catch (Meeshception e) {
                    status = errorNode(results, e);
                }
                break;
            
            case 1: //clearAll
                mediator.clearAll();
                break;
            case 2: //listCities
                params = validCommandsParameters[1];
                values = paramValues(cn, params);
                try {
                    commandOutput = mediator.listCities(results, values[0]);
                } catch (Meeshception e) {
                    status = errorNode(results, e);
                }
                break;
            case 3: //printTreap
            	try {
            		commandOutput = mediator.printTreap(results);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 4: //mapRoad
            	params = validCommandsParameters[2];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.mapRoad(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 5: //mapCity
            	params = validCommandsParameters[3];
            	values = paramValues(cn, params);
            	try {
            		mediator.mapCity(values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 6: //printPMQuadtree
            	try {
            		commandOutput = mediator.printPMQuadtree(results);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 7: //saveMap
            	params = validCommandsParameters[3];
            	values = paramValues(cn, params);
            	mediator.saveMap(values[0]);
            	break;
            case 8: //rangeCities
            	params = cn.getAttribute("saveMap").equals("") ?
            			validCommandsParameters[4] : validCommandsParameters[5];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.rangeCities(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 9: //rangeRoads
            	params = cn.getAttribute("saveMap").equals("") ?
            			validCommandsParameters[4] : validCommandsParameters[5];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.rangeRoads(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 10: //nearestCity
            	params = validCommandsParameters[6];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.nearestCity(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 11: //nearestIsolatedCity
            	params = validCommandsParameters[6];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.nearestIsolatedCity(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 12: //nearestRoad
            	params = validCommandsParameters[6];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.nearestRoad(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 13: //nearestCityToRoad
            	params = validCommandsParameters[2];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.nearestCityToRoad(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            default: //shortestPath
            	boolean saveMap = cn.getAttribute("saveMap").equals("") ? false : true;
            	String sh = cn.getAttribute("saveHTML");
            	if (!sh.equals("")) {
            		saveHTML = true;
            		saveHTMLName = sh;
            	}
            	if (saveMap) {
            		if (saveHTML) {
            			params = validCommandsParameters[9];
            		} else {
            			params = validCommandsParameters[7];
            		}
            	} else {
            		if (saveHTML) {
            			params = validCommandsParameters[8];
            		} else {
            			params = validCommandsParameters[2];
            		}
            	}
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.shortestPath(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
        }

        if (status == null) {
            status = results.createElement("success");
            output = results.createElement("output");
            if (commandOutput != null) {
                output.appendChild(commandOutput);
                if (saveHTML) {
                	org.w3c.dom.Document shortestPathDoc;
                	try {
						shortestPathDoc = XmlUtility.getDocumentBuilder().newDocument();
					} catch (ParserConfigurationException e) {
						shortestPathDoc = null;
					}
                	if (shortestPathDoc != null) {
                		org.w3c.dom.Node spNode = shortestPathDoc.importNode(status, true);
                		shortestPathDoc.appendChild(spNode);
                		try {
							XmlUtility.transform(shortestPathDoc, new File("shortestPath.xsl"), 
									new File(saveHTMLName + ".html"));
						} catch (FileNotFoundException | TransformerException e) {}
                	}                	
                }
            }
        }
        
        status.appendChild(commandNode(results, cn));
        status.appendChild(parametersNode(results, cn, params));
        if (output != null) {
        	status.appendChild(output);
        }
        
        return status;
    }

    public static Element cityNode(Document results, City tba) {
        Element city = results.createElement("city");
        appendCityAttributes(city, tba);
        return city;
    }
    
    public static Element cityUnmappedNode(Document results, City tba) {
    	Element cityUnmapped = results.createElement("cityUnmapped");
    	appendCityAttributes(cityUnmapped, tba);
    	return cityUnmapped;
    }
    
    public static Element isolatedCityNode(Document results, City tba) {
    	Element isolatedCity = results.createElement("isolatedCity");
    	appendCityAttributes(isolatedCity, tba);
    	return isolatedCity;
    }
    
    public static Element roadNode(Document results, Road rd) {
    	Element road = results.createElement("road");
    	appendRoadAttributes(road, rd.getStart(), rd.getEnd());
    	return road;
    }
    
    public static Element roadNode(Document results, City start, City end) {
    	Element road = results.createElement("road");
    	appendRoadAttributes(road, start, end);
    	return road;
    }
    
    public static Element roadCreatedNode(Document results, City start, City end) {
    	Element roadCreated = results.createElement("roadCreated");
    	appendRoadAttributes(roadCreated, start, end);
    	return roadCreated;
    }
    
    private static void appendRoadAttributes(Element road, City start, City end) {
    	road.setAttribute("start", start.getName());
    	road.setAttribute("end", end.getName());
    }

    private static void appendCityAttributes(Element city, City next) {
        city.setAttribute("name", next.getName());
        city.setAttribute("x", next.getXToString());
        city.setAttribute("y", next.getYToString());
        city.setAttribute("radius", next.getRadius());
        city.setAttribute("color", next.getColor());
    }

    public static String[] paramValues(Element in, String ... params) {
        if (params.length == 0) {
            return new String[]{};
        }
        String[] ret = new String[params.length];
        for (int i = 0; i < params.length; ++i) {
            ret[i] = in.getAttribute(params[i]);
        }
        return ret;
    }

    

    private static Element commandNode(Document results, Element in) {
        Element command = results.createElement("command");
        command.setAttribute("name", in.getNodeName());
        String id = in.getAttribute("id");
        if (!id.equals("")) {
            command.setAttribute("id", id);
        }
        return command;
    }

    private static Element parametersNode(Document results, Element in, String[] params) {
        Element parameters = results.createElement("parameters"), p;

        for (int i = 0; i < params.length; ++i) {
        	p = results.createElement(params[i]);
        	p.setAttribute("value", in.getAttribute(params[i]));
            parameters.appendChild(p);
        }
        return parameters;
    }
    
    private static Element errorNode(Document results, Meeshception e) {
        Element error = results.createElement("error");
        error.setAttribute("type", e.getMessage());
        return error;
    }
}