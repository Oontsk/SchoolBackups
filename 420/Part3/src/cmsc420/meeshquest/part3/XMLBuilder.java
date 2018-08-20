package cmsc420.meeshquest.part3;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLBuilder {
    private static String[] validCommands = {
    		"createCity", "deleteCity", "clearAll", "listCities", "printTreap", "mapRoad",   
    		"mapAirport", "mapTerminal", "unmapRoad", "unmapAirport", 
    		"unmapTerminal", "printPMQuadtree", "saveMap", "globalRangeCities",
    		"nearestCity", "mst"
    };
    private static String[][] validCommandsParameters = {
           	{"name", "localX", "localY", "remoteX", "remoteY", "radius", "color"},
           	{"name"}, {"sortBy"}, {"start", "end"},
           	{"name", "localX", "localY", "remoteX", "remoteY", "terminalName", "terminalX", "terminalY", "terminalCity"},
            {"name", "localX", "localY", "remoteX", "remoteY", "cityName", "airportName"}, 	
           	{"remoteX", "remoteY"}, {"remoteX", "remoteY", "name"}, {"remoteX", "remoteY", "radius"},
           	{"localX", "localY", "remoteX", "remoteY"}, {"start"}
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
        Element[] arrayCommandOutput = null;
        String[] params = new String[]{}, values = new String[]{};

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
            case 1: //deleteCity
            	params = validCommandsParameters[1];
            	values = paramValues(cn, params);
            	try {
            		arrayCommandOutput = mediator.deleteCity(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 2: //clearAll
                mediator.clearAll();
                break;
            case 3: //listCities
                params = validCommandsParameters[2];
                values = paramValues(cn, params);
                try {
                    commandOutput = mediator.listCities(results, values[0]);
                } catch (Meeshception e) {
                    status = errorNode(results, e);
                }
                break;
            case 4: //printTreap
            	try {
            		commandOutput = mediator.printTreap(results);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 5: //mapRoad
            	params = validCommandsParameters[3];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.mapRoad(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 6: //mapAirport
            	params = validCommandsParameters[4];
            	values = paramValues(cn, params);
            	try {
            		mediator.mapAirport(values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 7: //mapTerminal
            	params = validCommandsParameters[5];
            	values = paramValues(cn, params);
            	try {
            		mediator.mapTerminal(values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 8: //unmapRoad
            	params = validCommandsParameters[3];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.unmapRoad(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 9: //unmapAirport
            	params = validCommandsParameters[1];
            	values = paramValues(cn, params);
            	try {
            		arrayCommandOutput = mediator.unmapAirport(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 10: //unmapTerminal
            	params = validCommandsParameters[1];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.unmapTerminal(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 11: //printPMQuadtree
            	params = validCommandsParameters[6];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.printPMQuadtree(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 12: //saveMap
            	params = validCommandsParameters[7];
            	values = paramValues(cn, params);
            	try {
            		mediator.saveMap(values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 13: //globalRangeCities
            	params = validCommandsParameters[8];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.globalRangeCities(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 14: //nearestCity
            	params = validCommandsParameters[9];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.nearestCity(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
            	break;
            default: //mst
            	params = validCommandsParameters[10];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.mst(results, values);
            	} catch (Meeshception e) {
            		status = errorNode(results, e);
            	}
        }

        if (status == null) {
            status = results.createElement("success");
            output = results.createElement("output");
            if (commandOutput != null) {
                output.appendChild(commandOutput);
            } else if (arrayCommandOutput != null) {
            	for (int i = 0; i < arrayCommandOutput.length; ++i) {
            		output.appendChild(arrayCommandOutput[i]);
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
    
    public static Element terminalNode(Document results, Terminal tba) {
    	Element terminal = results.createElement("terminal");
    	appendTerminalAttributes(terminal, tba);
    	return terminal;
    }
    
    public static Element airportNode(Document results, Airport tba) {
    	Element airport = results.createElement("airport");
    	appendSiteAttributes(airport, tba);
    	return airport;
    }
    
    public static Element cityUnmappedNode(Document results, City tba) {
    	Element cityUnmapped = results.createElement("cityUnmapped");
    	appendCityAttributes(cityUnmapped, tba);
    	return cityUnmapped;
    }
    
    public static Element roadNode(Document results, Road rd) {
    	Element road = results.createElement("road");
    	appendRoadAttributes(road, rd.getStart(), rd.getEnd());
    	return road;
    }
    
    public static Element roadNode(Document results, Site start, Site end) {
    	Element road = results.createElement("road");
    	appendRoadAttributes(road, start, end);
    	return road;
    }
    
    public static Element roadUnmappedNode(Document results, Road rd) {
    	Element roadUnmapped = results.createElement("roadUnmapped");
    	appendRoadAttributes(roadUnmapped, rd.getStart(), rd.getEnd());
    	return roadUnmapped;
    }

    public static Element roadDeletedNode(Document results, City start, City end) {
    	Element roadDeleted = results.createElement("roadDeleted");
    	appendRoadAttributes(roadDeleted, start, end);
    	return roadDeleted;
    }
    
    
    public static Element roadCreatedNode(Document results, Site start, Site end) {
    	Element roadCreated = results.createElement("roadCreated");
    	appendRoadAttributes(roadCreated, start, end);
    	return roadCreated;
    }
    
    private static void appendRoadAttributes(Element road, Site start, Site end) {
    	road.setAttribute("start", start.getName());
    	road.setAttribute("end", end.getName());
    }

    private static void appendCityAttributes(Element city, City next) {
    	appendSiteAttributes(city, next);
        city.setAttribute("radius", next.getRadius());
        city.setAttribute("color", next.getColor());
    }
    
    private static void appendTerminalAttributes(Element term, Terminal next) {
    	appendSiteAttributes(term, next);
    	term.setAttribute("airportName", next.getConnectedAirport().getName());
    	term.setAttribute("cityName", next.getConnectedCity().getName());
    }
    
    private static void appendSiteAttributes(Element site, Site next) {
    	site.setAttribute("name", next.getName());
        site.setAttribute("localX", next.getXToString());
        site.setAttribute("localY", next.getYToString());
        site.setAttribute("remoteX", next.getRemCoor().getXToString());
        site.setAttribute("remoteY", next.getRemCoor().getYToString());
    }
    
    public static Element terminalUnmappedNode(Document results, Terminal term) {
    	Element terminalUnmapped = results.createElement("terminalUnmapped");
    	appendTerminalAttributes(terminalUnmapped, term);
    	return terminalUnmapped;
    }
    
    public static Element airportUnmappedNode(Document results, Airport air) {
    	Element airportUnmapped = results.createElement("airportUnmapped");
    	appendSiteAttributes(airportUnmapped, air);
    	return airportUnmapped;
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