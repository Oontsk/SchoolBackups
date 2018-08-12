import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

/** Compares XML files structurally. */
public class XMLDiff
{

    /**
     * Does an XML diff of two input files specified on the command line and outputs the results to stdout.
     */
    public static void main(String[] args)
    {
        InputStream file1 = null ;
        InputStream file2 = null ;
        

        if (args.length != 1 && args.length != 2)
        {
            System.err.println("XMLDiff -- Compare two XML files structurally\n") ;
            System.err.println("\tUsage:\tjava XMLDiff file1.xml [file2.xml]\n") ;
            System.err.println("\tIf file2.xml is omitted, reads file2 from standard input.") ;
            System.exit(1) ;
        }
        
        // Open inputs
        try
        {
            file1 = new FileInputStream(args[0]) ;
            file2 = args.length > 1 ? new FileInputStream(args[1]) : System.in ;
        } catch (FileNotFoundException e)
        {
            System.err.println("Error opening file: " + e.getMessage()) ;
            System.exit(1) ;
        }
    
        try
        {
            if (xmlDiff (file1, file2, null))
            {
                System.out.println("Files differ.") ;
                System.exit(1) ;
            }
        } catch (ParserConfigurationException e)
        {
            System.err.println("Parser configuration exception: " + e.getMessage()) ;
            System.exit(1) ;
        } catch (SAXException e)
        {
            System.err.println("SAX exception: " + e.getMessage()) ;
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("I/O error: " + e.getMessage()) ;
            System.exit(1);
        }
    }

    /**
     * Returns true iff the two XML documents read from InputStreams expected
     * and actual are different, after normalization, according to org.w3c.dom.Document.isEqualNode().
     */
    public static boolean xmlDiff(InputStream expected, InputStream actual, String removeElementName) 
        throws ParserConfigurationException, SAXException, IOException
    {
        Document expectedDoc = XmlUtility.parse(expected) ;
        Document actualDoc = XmlUtility.parse(actual) ;
        
        strip(expectedDoc) ;
        strip(actualDoc) ;
        if (removeElementName != null && removeElementName.trim().length() > 0) {
        	removeElements(expectedDoc, removeElementName);
        	removeElements(actualDoc, removeElementName);
        }
        actualDoc.normalizeDocument() ;
        expectedDoc.normalizeDocument() ;
        
        return !expectedDoc.isEqualNode(actualDoc) ;
    }

    
    /** Recursively prints the internal representation of a DOM tree to an output stream.
     * 
     * @param out Output will be printed here
     * @param n The node at which to start printing
     * @param indent The node will be printed with this many tabs preceding it; each level
     * down the tree will be printed with one more tab. 
     */ 
    public static void printDoc (OutputStream out, Node n, int indent)
    {
        if (out == null || n == null)
            return ;
        
        String s = "" ;
        for (int i = 0 ; i < indent ; i++)
            s = s + "\t" ;
        s = s + n.getNodeName() + " : " + n.getNodeValue() ;
        
        new PrintStream(out).println(s) ;
        
        
        NodeList children = n.getChildNodes() ;
        for (int i = 0 ; i < children.getLength() ; i++)
            printDoc(out, children.item(i), indent + 1) ;
    }
    
    
    /** Returns a string containing the sequence of integer "code points" (ASCII values) of the 
     * characters in s.
     */
    public static String charCodes(String s)
    {
        if (s == null)
            return "" ;
        
        String ss = "" ;
        int len = s.length() ;
        for (int i= 0 ; i < len ; i++)
            ss = ss + s.codePointAt(i) + ", " ;

        return ss ;
    }

    /** Recursively remove text nodes containing only whitespace, and comments from a DOM node and all its children.
     * @param n The node from which whitespace children will be removed.
     */
    public static void strip (Node n)
    {
        if (n == null)
            return ;
        
        Node child = n.getFirstChild() ;
        while (child != null)
        {
            short type = child.getNodeType() ;
            Node next = child.getNextSibling() ;
            
            switch (type)
            {
                case Node.TEXT_NODE :
                    if (child.getNodeValue().trim().equals(""))
                        n.removeChild(child) ;
                    break ;
                
                case Node.COMMENT_NODE :
                    n.removeChild(child) ;
                    break ;
                    
                default:
                    strip(child) ;
            }
            
            child = next ;
        }
    }

    public static void removeElements (Node n, String elementName)
    {
        if (n == null)
            return ;
        
        Node child = n.getFirstChild() ;
        while (child != null)
        {
        	short type = child.getNodeType() ;
            Node next = child.getNextSibling() ;
            
            if (type == Node.ELEMENT_NODE && child.getNodeName().trim().equals(elementName.trim())) {
                n.removeChild(child);            	
            } else if (type != Node.TEXT_NODE && type != Node.COMMENT_NODE) {
            	removeElements(child, elementName);
            }
            
            child = next ;
        }
    }

    
}
