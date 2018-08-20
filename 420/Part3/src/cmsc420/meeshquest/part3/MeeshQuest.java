package cmsc420.meeshquest.part3;

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
        		String localTestName = //put local test names here; empty string or null indicates System.in
        			//"part3.mst3.input.xml"
        			//"p3sampletests/part3.public.mst.input.xml"
        			""
    			;
        		
        		Document doc;
        		if (localTestName == null || localTestName.equals("")) {
        			doc = XmlUtility.validateNoNamespace(System.in);
        		} else {
        			File in = new File(localTestName);
        			doc = XmlUtility.validateNoNamespace(in);
        		}
        		
       	 		Element commandNode = doc.getDocumentElement(),
       	 			root = results.createElement("results");
       	 		
				results.appendChild(root);

       	 		int localDim = Integer.parseInt(commandNode.getAttribute("localSpatialWidth")),
       	 				remoteDim = Integer.parseInt(commandNode.getAttribute("remoteSpatialWidth")),
       	 				pmOrder = Integer.parseInt(commandNode.getAttribute("pmOrder"));
				Mediator mediator = new Mediator(remoteDim, localDim, pmOrder);
       	 		
        		final NodeList nl = commandNode.getChildNodes();
        		for (int i = 0; i < nl.getLength(); i++) {
        			if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        				commandNode = (Element) nl.item(i);
        				root.appendChild(XMLBuilder.handleCommandNode(results, commandNode, mediator));
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
