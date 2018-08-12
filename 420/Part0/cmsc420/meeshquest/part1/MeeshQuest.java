package cmsc420.meeshquest.part1;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

public class MeeshQuest {
    public static void main(String[] args) {

    	Document results;
    	try {
    		results = XmlUtility.getDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
    	    results = null;
        }
    	
        if (results != null) {
        	try {
        		String localTestName = 
        			//"sum18p1sampletsts/part1.primary.input.xml"
        			""
    			;
        		
        		Document doc;
        		if (localTestName.equals("")) {
        			doc = XmlUtility.validateNoNamespace(System.in);
        		} else {
        			doc = XmlUtility.validateNoNamespace(new File(localTestName));
        		}
        		
       	 		Element commandNode = doc.getDocumentElement();
       	 		
				Element root = results.createElement("results");
				results.appendChild(root);

       	 		int dim = Integer.parseInt(commandNode.getAttribute("spatialWidth"));
				Mediator mediator = new Mediator(dim);
       	 		
        		final NodeList nl = commandNode.getChildNodes();
        		for (int i = 0; i < nl.getLength(); i++) {
        			if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        				commandNode = (Element) nl.item(i);
        				Element res = XMLBuilder.handleCommandNode(results, commandNode, mediator);
						root.appendChild(res);
        			}
        		}
       	 	} catch (SAXException | IOException | ParserConfigurationException e) {
        		results.appendChild(results.createElement("fatalError"));
			} finally {
            	try {
					XmlUtility.print(results);
				} catch (TransformerException e) {
					e.printStackTrace();
				}
        	}
        }
    }
}
