import java.util.Vector;
import java.lang.Math;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

/** Take XML file, parse it into DOM Documents and compare each element. */
public class GeneralXMLDiff
{
	final static int SAME = 0;
	final static int ERROR_ITEM_NUM = 1;
	final static int ERROR_ITEM_NAME = 2;
	final static int ERROR_COMMAND_NAME = 3;
	final static int ERROR_COMMAND_OUTPUT = 4;
	final static int ERROR_BTREE_ATTR = 5;
	final static int ERROR_ROOT_NODE = 6;
	final static int ERROR_LEAF_NODE = 7;
	final static int ERROR_INTERNAL_NODE = 8;
	final static int ERROR_LEAF_LEVEL = 9;
	final static int ERROR_MULTIPLE_ROOT = 10;
	final static int ERROR_UNKNOW_NODE = 11;
	final static int ERROR_KEY_MID_ORDER = 12;
	final static int ERROR_TOTAL_KEY = 13;

	/** error Messages. */
	final static String[] errorMessage = {
	"Congratulations! Your output is correct!",
	"Error: The number of item for this output is incorrect!",
	"Error: Incorrect tag name or another tag expected at some point in the XML.",
	"Error: Wrong command!",
	"Error: The output of the command is incorrect!",
	"Error: Attribute value of BTree is incorrect!",
	"Error: Root node of BTree should has at least two non-empty child subtrees.",
	"Error: There is incorrect leaf node in the BTree!", 
	"Error: There is incorrect internal node in the BTree!",
	"Error: Leaf node should all be at the same level!",
	"Error: There are more than one root node in the BTree!",
	"Error: There are nodes other than interal and leaf in the BTree!",
	"Error: The keys are not in correct order in the BTree!",
	"Error: The total number of keys in the BTree is incorrect!"
	};

	/** The command which output needs to be traversed. */
	final static String printBTreeTag = new String("printBTree");
	final static String cmdTag = new String("command");
	final static String nameTag = new String("name");
    final static String btreeTag = new String("btree");
    final static String cardinalityTag = new String("cardinality");
    final static String heightTag = new String("height");
    final static String leafOrderTag = new String("leafOrder");
    final static String leafTag = new String("leaf");
    final static String entryTag = new String("entry");
    final static String internalTag = new String("internal");

    private int cardinality, height, leafOrder, bTreeIndex = 0, cmdIndex = 0;
    // for traverse the entries.
    private int currentIndex = 0;
    private Vector<Element> entryVector = new Vector<Element>();
	private String extraErrorInfo = new String();

	/** An error node in actual XML. */
	private static Element errorElement = null;

    /**
     * Does an XML diff of two input files specified on the command line and outputs the results to stdout.
     */
    public static void main(String[] args)
    {
        InputStream file1 = null ;
        InputStream file2 = null ;

        if (args.length != 1 && args.length != 2)
        {
            System.err.println("GeneralXMLDiff -- Compare two XML files structurally\n") ;
            System.err.println("\tUsage:\tjava GeneralXMLDiff file1.xml [file2.xml]\n") ;
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
			StringBuffer nodeInfo = new StringBuffer();

			GeneralXMLDiff diff = new GeneralXMLDiff();
			System.out.println("----------------------------------------");
            System.out.println(errorMessage[diff.generalXMLDiff(file1, file2)]);
			System.out.println("----------------------------------------");
			diff.printNodeToString(nodeInfo, errorElement, 1);
            System.out.println(nodeInfo.toString()) ;
			System.out.println("----------------------------------------\n" + diff.extraErrorInfo + "\n");
            System.exit(1) ;
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
    public int generalXMLDiff(InputStream expected, InputStream actual) 
        throws ParserConfigurationException, SAXException, IOException
    {
        Document expectedDoc = XmlUtility.parse(expected) ;
        Document actualDoc = XmlUtility.parse(actual) ;
        
        strip(expectedDoc) ;
        strip(actualDoc) ;
        actualDoc.normalizeDocument() ;
        expectedDoc.normalizeDocument() ;
        
		Element actualDocElement = actualDoc.getDocumentElement(); 
		Element expectedDocElement = expectedDoc.getDocumentElement();

		if(!actualDocElement.getTagName().equals(expectedDocElement.getTagName())){
			extraErrorInfo += "The first tag should be <results>!";
			return ERROR_ITEM_NAME;
		}

		NodeList actualChildNodes = actualDocElement.getChildNodes();
		NodeList expectedChildNodes = expectedDocElement.getChildNodes();
		int actualChildNum = actualChildNodes.getLength();
		int expectedChildNum = expectedChildNodes.getLength();
		int result = 0;

		if(actualChildNum != expectedChildNum){
			extraErrorInfo += "There are " + expectedChildNum + " input commands. You have " + actualChildNum + " command outputs."; 
			result = ERROR_ITEM_NUM;
		}
		else{
			Element oneActualElement, oneExpectedElement;
			for(int i=0; i<expectedChildNum; i++){
				oneActualElement = (Element)actualChildNodes.item(i);
				oneExpectedElement = (Element)expectedChildNodes.item(i);

				cmdIndex++;
				extraErrorInfo = "Cmd: " + cmdIndex + "\n";
				result = compareElement(oneExpectedElement, oneActualElement);
				if(result != SAME){
					break;
				}
			}
		}

		if(result == 0){
			errorElement = null;
		}
		return result;
        //return !expectedDoc.isEqualNode(actualDoc) ;
    }

    /** Recursively compares two DOM Node. 
     * 
     * @param elementOne the expected Node 
     * @param elementTwo the actual Node 
	 * @return int comparison result
     */ 
    private int compareElement (Element elementOne, Element elementTwo)
	{
		errorElement = elementTwo;

		//compare name
		if(!elementOne.getTagName().equals(elementTwo.getTagName())){
			extraErrorInfo += "Expected Tag: " + elementOne.getTagName() + ". Your Tag : " + elementTwo.getTagName() + ".";
			return ERROR_ITEM_NAME;
		}

		NodeList cmdListOne, cmdListTwo;
		cmdListOne = elementOne.getElementsByTagName(cmdTag);
		cmdListTwo = elementTwo.getElementsByTagName(cmdTag);

		if(cmdListOne.getLength() != cmdListTwo.getLength()){
			extraErrorInfo += "The number of <command ...> should be 1 in one command output! Please check whether the tag name is wrong or you have multiple command output packed in."; 
			return ERROR_ITEM_NUM;
		}

		// should have only one command element
		Element cmdElementOne = (Element)cmdListOne.item(0);
		Element cmdElementTwo = (Element)cmdListTwo.item(0);

		String cmdNameOne = cmdElementOne.getAttribute(nameTag);
		String cmdNameTwo = cmdElementTwo.getAttribute(nameTag);

		if(!cmdNameOne.equals(cmdNameTwo)){
			extraErrorInfo += "The " + cmdIndex + " command should be " + cmdNameOne + ". Yours is " + cmdNameTwo + ".";
			return ERROR_ITEM_NAME;
		}

		if(!cmdNameOne.equals(printBTreeTag)){
			// other command
			if(elementOne.isEqualNode(elementTwo)){
				return SAME;
			}
			else{
				extraErrorInfo += "Somewhere in this output is incorrect."; 
				return ERROR_COMMAND_OUTPUT;
			}
		}
		else{
			bTreeIndex++;
			errorElement = cmdElementTwo;
			return printBTreeDiff(elementOne, elementTwo);
		}
	}
    
    /** Recursively prints the internal representation of a DOM Node to an output string.
     * 
     * @param node The node at which to start printing
	 * @indent indentation
	 * @return the string contains information of the node
     */ 
    public void printNodeToString (StringBuffer s, Node node, int indent)
    {
        if (node == null)
            return;
        
        s.append(node.getNodeName());
		
		if(node.hasAttributes()){
			NamedNodeMap nodeMap = node.getAttributes();
			for(int i=0; i<nodeMap.getLength(); i++){
				s.append(" ");
				s.append(nodeMap.item(i).getNodeName());
				s.append(" = ");
				s.append(nodeMap.item(i).getNodeValue());
			}
		}

		s.append("\n");
        for (int i = 0 ; i < indent ; i++)
            s.append("\t");
        
        NodeList children = node.getChildNodes() ;
        for (int i = 0 ; i < children.getLength() ; i++)
            printNodeToString(s, children.item(i), indent + 1);
		return;
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
    
    /** 
     * Check whether the printBTree output is correct.
     *
     * @param nodeOne expected node.
     * @param nodeTwo actual node.
     * @return error message index.
     */
    private int printBTreeDiff(Element nodeOne, Element nodeTwo)
    {
        // check number of children
        NodeList nodeOneChildNodes = nodeOne.getChildNodes();
        NodeList nodeTwoChildNodes = nodeTwo.getChildNodes();

        if(nodeOneChildNodes.getLength() != nodeTwoChildNodes.getLength()){
			extraErrorInfo += "There should be <command ...>, <parameters/> and <output> in this output. No more and no less."; 
            return ERROR_ITEM_NUM;
        }

        Element nodeOneChild, nodeTwoChild;
        for(int i=0; i<nodeOneChildNodes.getLength(); i++){
            nodeOneChild = (Element)nodeOneChildNodes.item(i);
            nodeTwoChild = (Element)nodeTwoChildNodes.item(i);

            if(!nodeOneChild.getTagName().equals(nodeTwoChild.getTagName())){
				extraErrorInfo += "In " + bTreeIndex + " BTree, this tag <" + nodeTwoChild.getTagName() + "> is incorrect. Expected tag : <" + nodeOneChild.getTagName() + ">.";
                return ERROR_ITEM_NAME;
            }

            if(!nodeOneChild.getTagName().equals("output")){
                if(!nodeOneChild.isEqualNode(nodeTwoChild)){
					extraErrorInfo += "In " + bTreeIndex + " BTree, something is wrong other than the output.";
                    return ERROR_COMMAND_OUTPUT;
                }
            }
            else{
                // compare output of printBTree command
                int result = comparePrintBTreeOutput(nodeOneChild, nodeTwoChild)
;
                if(result > 0){
                    return result;
                }
            }
        }

		return SAME;
    }

    /**
     * Check whether the output part of printBTree command is correct.
     *
     * @param firstNode expected node.
     * @param secondNode actual node.
     * @return error message index.
     */
    private int comparePrintBTreeOutput(Element nodeOne, Element nodeTwo)
    {

        // check number of children
        NodeList nodeOneBTree = nodeOne.getElementsByTagName(btreeTag
);
        NodeList nodeTwoBTree = nodeTwo.getElementsByTagName(btreeTag
);

        // here firstNodeList.getLength() must be 1. only one BTree
        if(nodeOneBTree.getLength() != nodeTwoBTree.getLength()){
			extraErrorInfo += "There should be one <btree ...> in this output. No more and no less."; 
            return ERROR_ITEM_NUM;
        }

        // should have only one BTree
        Element firstBTree = (Element)nodeOneBTree.item(0);
        Element secondBTree = (Element)nodeTwoBTree.item(0);

        // check whether the attributes of BTree are valid
        int firstCardinality = Integer.parseInt(firstBTree.getAttribute(cardinalityTag));
        int secondCardinality = Integer.parseInt(secondBTree.getAttribute(cardinalityTag));
        int firstHeight = Integer.parseInt(firstBTree.getAttribute(heightTag));
        int secondHeight = Integer.parseInt(secondBTree.getAttribute(heightTag));
        int firstLeafOrder = Integer.parseInt(firstBTree.getAttribute(leafOrderTag));
        int secondLeafOrder = Integer.parseInt(secondBTree.getAttribute(leafOrderTag));

        if(firstCardinality != secondCardinality ||
            firstHeight != secondHeight ||
            firstLeafOrder != secondLeafOrder){
				extraErrorInfo += "Something is wrong with attributes in the " + bTreeIndex + "BTree printed!";
                return ERROR_BTREE_ATTR;
        }
        cardinality = firstCardinality;
        height = firstHeight;
        leafOrder = firstLeafOrder;

        // check whether it is a valid BTree
		entryVector.removeAllElements();
		currentIndex = 0;
        extractEntry(firstBTree, entryVector);
		int result = compareBTree(entryVector, (Element)(secondBTree.getChildNodes().item(0)), 0);

		if(result == SAME && currentIndex == entryVector.size()){
			return result;
		}
		else{
			extraErrorInfo += "The total number of keys in " + bTreeIndex + " BTree should be " + cardinality; 
            return ERROR_TOTAL_KEY;
        }
    }

    /** Recursively extracts entries from a BTree.
     *
     * @param node BTree node.
     * @return Vector contains all entries.
     */
    public void extractEntry(Node bTreeNode, Vector<Element> entryVector)
    {
        Element bTreeElement;
        NodeList bTreeNodeList = bTreeNode.getChildNodes();

        for(int i=0; i<bTreeNodeList.getLength(); i++){
            bTreeElement = (Element)bTreeNodeList.item(i);

            if(bTreeElement.getTagName().equals(entryTag)){
                // entry
				//StringBuffer tmp = new StringBuffer();
				//printNodeToString(tmp, bTreeElement, 1);
				//System.out.println(tmp + "\n------");
                entryVector.add(bTreeElement);
            }
            else{
                // internal or leaf
                extractEntry(bTreeElement, entryVector);
            }
        }
    }

    /** Check whether a node has correct number of keys and children.
     *
     * @param node a BTree node.
     * @param minChildrenNum minimum number of children allowed.
     * @param maxChildrenNum maxmum number of children allowed.
     * @param minKeyNum minimum number of keys allowed.
     * @param maxKeyNum maxmum number of keys allowed.
     * @return true for valid node, false otherwise.
     */
    private boolean checkBTreeNode(Element node, int minChildrenNum, int maxChildrenNum, int minKeyNum, int maxKeyNum)
    {
        Element element;
        int internalNum = 0;
        int entryNum = 0;
        boolean isLeaf = false;

        if(node.getTagName().equals(leafTag)){
            isLeaf = true;
        }

        NodeList nodeList = node.getChildNodes();
        for(int i=0; i<nodeList.getLength(); i++){
            element = (Element)nodeList.item(i);
            if(!isLeaf){
                // internal, may contains internal, leaf and entry
                if(i % 2 == 0){
                    if(!element.getTagName().equals(internalTag) &&
                        !element.getTagName().equals(leafTag)){
						extraErrorInfo += "For this node, child nodes and keys should be alternately listed.\n";
                        return false;
                    }
                    internalNum++;
                }
                else{
                    // entry
                    if(!element.getTagName().equals(entryTag)){
						extraErrorInfo += "For this node, child nodes and keys should be alternately listed.\n";
                        return false;
                    }
                    entryNum++;

                }
            }
            else{
                // leaf, only contains entry
                if(!element.getTagName().equals(entryTag)){
					extraErrorInfo += "A leaf node can only contain keys.\n";
                    return false;
                }
                entryNum++;
            }
        }

        if(!(internalNum >= minChildrenNum &&
            internalNum <= maxChildrenNum &&
            entryNum >= minKeyNum &&
            entryNum <= maxKeyNum)){
            return false;
        }

        return true;
    }

    /**
     * Recursively check whether the BTree is correct.
     *
     * @param expectedEntrys Vector of expected entrys.
     * @param secondNode root node of actual BTree.
     * @return error message index.
     */
    private int compareBTree(Vector<Element> treeEntry, Element bTreeNode, int level)
    {
        int result, currentLevel;
        NodeList childNodeList;

        currentLevel = level;

        if(currentLevel == 0){
            // check whether root now has right number of key and subtree
			if(bTreeNode.getElementsByTagName(leafTag).getLength() == 0 &&
				bTreeNode.getElementsByTagName(internalTag).getLength() == 0){
				// leaf as root, only has entries	
            	if(!checkBTreeNode(bTreeNode, 0, 0, 1, leafOrder)){
					extraErrorInfo += "The error is in " + bTreeIndex + " BTree. If the root is a leaf, it should have at least 1 key, at most 4 keys and no children.";
                	return ERROR_ROOT_NODE;
            	}
			}
			else{
            	if(!checkBTreeNode(bTreeNode, 2, 4, 1, 3)){
					extraErrorInfo += "The error is in " + bTreeIndex + " BTree. If the root is an internal node, it should have 2-4 children and 1-3 keys.";
                	return ERROR_ROOT_NODE;
            	}
			}

            childNodeList = bTreeNode.getChildNodes();
            for(int i=0; i<childNodeList.getLength(); i++){
                result = compareBTree(entryVector, (Element)(childNodeList.item(i)), currentLevel + 1);
                if(result != 0){
                    return result;
                }
            }

        }
        else{
            // check internal node or leaf node
            if(bTreeNode.getTagName().equals(internalTag)){
                if(!checkBTreeNode(bTreeNode, 2, 4, 1, 3)){
					extraErrorInfo += "The error is in " + bTreeIndex + " BTree. Internal node should have 2-4 children and 1-3 keys.";
                    return ERROR_INTERNAL_NODE;
                }

                childNodeList = bTreeNode.getChildNodes();
                for(int i=0; i<childNodeList.getLength(); i++){
                    result = compareBTree(treeEntry, (Element)(childNodeList.item(i)), currentLevel + 1);
                    if(result != 0){
                        return result;
                    }
                }
            }
            else if(bTreeNode.getTagName().equals(leafTag)){
                if(!checkBTreeNode(bTreeNode, 0, 0, (int)(Math.ceil((leafOrder-1)/2)), leafOrder)){
					extraErrorInfo += "The error is in " + bTreeIndex + " BTree. Leaf node should have ceil((leafOrder-1)/2) to leafOrder keys.";
                    return ERROR_LEAF_NODE;
                }

                // check level
                if(currentLevel != height - 1){
					errorElement = bTreeNode;
					extraErrorInfo += "The error is in " + bTreeIndex + " BTree. This leaf is not at level " + height + " .";
                    return ERROR_LEAF_LEVEL;
                }

                childNodeList = bTreeNode.getChildNodes();
                for(int i=0; i<childNodeList.getLength(); i++){
                    // check each entry
                    if(currentIndex < entryVector.size()){
                        if(!childNodeList.item(i).isEqualNode((Node)entryVector.elementAt(currentIndex))){
							System.out.println("ERROR_KEY_MID_ORDER_1");
                            return ERROR_KEY_MID_ORDER;
                        }
                        currentIndex++;
                    }
                    else{
						extraErrorInfo += "The total number of keys in " + bTreeIndex + " BTree should be " + cardinality; 
                        return ERROR_TOTAL_KEY;
                    }
                }
            }
            else if(bTreeNode.getTagName().equals(entryTag)){
                if(currentIndex < entryVector.size()){
                    if(!bTreeNode.isEqualNode((Node)entryVector.elementAt(currentIndex))){
						errorElement = bTreeNode;
						extraErrorInfo += "Either the entry itself is incorrect, or it is in wrong position in the " + bTreeIndex + " BTree.";
                        return ERROR_KEY_MID_ORDER;
                    }
                    currentIndex++;
                }
                else{
					extraErrorInfo += "The total number of keys in " + bTreeIndex + " BTree should be " + cardinality; 
                    return ERROR_TOTAL_KEY;
                }
            }
            else{
				errorElement = bTreeNode;
				extraErrorInfo += "In " + bTreeIndex + " BTree, there is unknown tag!";
                return ERROR_UNKNOW_NODE;
            }
        }

		return SAME;
    }
}
