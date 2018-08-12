package cmsc420.meeshquest.part1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLBuilder {
    private static String[] validCommands = {
    		"createCity", "listCities", "clearAll", 
    		"deleteCity", "mapCity", "unmapCity", "printPRQuadtree", 
    		"saveMap", "rangeCities", "nearestCity"
    };
    private static String[][] validCommandsParameters = {
           	{"name", "x", "y", "radius", "color"}, {"sortBy"}, {"name"},
           	{"x", "y", "radius"}, {"x", "y", "radius", "saveMap"}, {"x", "y"}
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

        switch (comNum) {
            case 0: //createCity
                params = validCommandsParameters[0];
                values = paramValues(cn, params);
                try {
                    mediator.createCity(values);
                } catch (Exception e) {
                    status = errorNode(results, e);
                }
                break;
            case 1: //listCities
                params = validCommandsParameters[1];
                values = paramValues(cn, params);
                try {
                    commandOutput = mediator.listCities(results, values);
                } catch (Exception e) {
                    status = errorNode(results, e);
                }
                break;
            case 2: //clearAll
                mediator.clearAll();
                break;
            case 3: //deleteCity
            	params = validCommandsParameters[2];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.deleteCity(results, values);
            	} catch (Exception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 4: //mapCity
            	params = validCommandsParameters[2];
            	values = paramValues(cn, params);
            	try {
            		mediator.mapCity(values);
            	} catch (Exception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 5: //unmapCity
            	params = validCommandsParameters[2];
            	values = paramValues(cn, params);
            	try {
            		mediator.unmapCity(values);
            	} catch (Exception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 6: //printPRQuadtree
            	try {
            		commandOutput = mediator.printPRQuadtree(results);
            	} catch (Exception e) {
            		status = errorNode(results, e);
            	}
            	break;
            case 7: //saveMap
            	params = validCommandsParameters[2];
            	values = paramValues(cn, params);
            	mediator.saveMap(values);
            	break;
            case 8: //rangeCities
            	params = cn.getAttribute("saveMap").equals("") ?
            			validCommandsParameters[3] : validCommandsParameters[4];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.rangeCities(results, values);
            	} catch (Exception e) {
            		status = errorNode(results, e);
            	}
            	break;
            default: //nearestCity
            	params = validCommandsParameters[5];
            	values = paramValues(cn, params);
            	try {
            		commandOutput = mediator.nearestCity(results, values);
            	} catch (Exception e) {
            		status = errorNode(results, e);
            	}
        }

        if (status == null) {
            status = results.createElement("success");
            output = results.createElement("output");
            if (commandOutput != null) {
                output.appendChild(commandOutput);
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

    private static Element errorNode(Document results, Exception e) {
        Element error = results.createElement("error");
        error.setAttribute("type", e.getMessage());
        return error;
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
        Element parameters = results.createElement("parameters");
        for (int i = 0; i < params.length; ++i) {
            parameters.appendChild(parameterNode(results, in, params[i]));
        }
        return parameters;
    }

    private static Element parameterNode(Document results, Element in, String param) {
        Element parameter = results.createElement(param);
        parameter.setAttribute("value", in.getAttribute(param));
        return parameter;
    }
}
