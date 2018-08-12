import org.w3c.dom.*;

import cmsc420.xml.XmlUtility;

import java.io.File;
import java.util.*;

public class TreapValidator {

	private  Comparator comp;

	public TreapValidator(Comparator comp, int sizes[]) throws Exception {
		this.comp = comp;
		System.out.println("Validating...");
		Document doc = XmlUtility.parse(System.in);
		if (validateOutputDocument(doc, sizes))
			System.out.println("Document contained only valid trees");
		else
			throw new RuntimeException("Error: Document is invalid"); // will
		// not
		// be
		// printed
		// -- an
		// exception
		// occurs.
	}

	public TreapValidator(Comparator comp, int sizes[], String path) throws Exception {
		this.comp = comp;
		System.out.println("Validating...");
		Document doc = XmlUtility.parse(new File(path));
		if (validateOutputDocument(doc, sizes))
			System.out.println("Document contained only valid trees");
		else
			throw new RuntimeException("Error: Document is invalid"); // will
		// not
		// be
		// printed
		// -- an
		// exception
		// occurs.
	}

	/**
	 * Validates all of the Treaps in a document that you might have received
	 * from your program.
	 * 
	 * @param outputDoc
	 *            a document resulting from the execution of the MeeshQuest
	 *            program
	 * @param comp
	 *            the comparator used to sort the keys/guides in the tree
	 * @return true if ALL trees in the document are valid. Otherwise, throws an
	 *         exception (or returns false)
	 * @throws IllegalStructureException
	 *             indicating what type of error was discovered in the B+ tree.
	 */
	public boolean validateOutputDocument(Document outputDoc, int[] sizes) throws IllegalStructureException {
		int numTree = 0;
		Element docElem = outputDoc.getDocumentElement();
		NodeList successOrFailNodes = docElem.getChildNodes();
		for (int i = 0; i < successOrFailNodes.getLength(); ++i) {
			if (successOrFailNodes.item(i).getNodeName().equals("success")) {
				Node successNode = successOrFailNodes.item(i);
				Node commandNode = successNode.getChildNodes().item(1);
				if (commandNode == null) {
					throw new IllegalStructureException(
							"output does not contain a command node");
				}

				Node commandNameNode = commandNode.getAttributes()
						.getNamedItem("name");
				if (commandNameNode == null) {
					throw new IllegalStructureException(
							"cannot determine command name");
				}
				if (commandNameNode.getNodeValue().equals("printTreap")) {
					if (successNode.getChildNodes().item(5).getNodeName()
							.equals("output")) {
						Node treapNode = successOrFailNodes.item(i)
								.getChildNodes().item(5).getChildNodes()
								.item(1);
						if (treapNode == null) {
							throw new IllegalStructureException(
									"output does not contain a treap node");
						}
						if (treapNode.getChildNodes().item(1) == null) {
							throw new IllegalStructureException(
									"treap does not contain a root");
						}

						Node rootTag = treapNode.getChildNodes().item(1);
						try {
							TNode root = parseNode(rootTag);
							int cardinality = Integer.parseInt(treapNode.getAttributes().getNamedItem("cardinality").getNodeValue());
							// TODO check size as part of validation
							if (validate(root) && size(root) == sizes[numTree] && 
									cardinality == sizes[numTree]) {
								System.out.println("Tree " + numTree + " is valid");
							} else {
								System.out.println("Tree " + numTree + " failed to validate");
								return false;
							}
							++numTree;
						} catch (NullPointerException e) {
							System.out.println("Tree " + numTree + " failed to validate");
							return false;
						}
					} else {
						System.out.println("Tree found with no output node?");
						return false;
					}
				}
			}
		}
		return numTree == sizes.length;
	}

	private TNode parseNode(Node node) {
		if (node.getNodeName().equals("emptyChild")) {
			return null;
		}
		TNode tnode = new TNode(node.getAttributes().getNamedItem("key").getNodeValue(), 
				Integer.parseInt(node.getAttributes().getNamedItem("priority").getNodeValue()));

		NodeList nl = node.getChildNodes();
		tnode.left = parseNode(nl.item(1));
		tnode.right = parseNode(nl.item(3));
		return tnode;
	}

	private int size(TNode node) {
		if (node == null) {
			return 0;
		} else {
			return size(node.left) + size(node.right) + 1;
		}
	}

	private boolean validate(TNode node) {
		if (node == null) {
			return true;
		}
		boolean ret = true;
		if (node.left != null) {
			ret = ret && comp.compare(node.left.key, node.key) < 0 && 
					node.priority > node.left.priority && 
					validate(node.left);
		}
		if (node.right != null) {
			ret = ret && comp.compare(node.right.key, node.key) >= 0 && 
					node.priority > node.right.priority && 
					validate(node.right);
		}
		return ret;
	}

	private class TNode {
		Object key;
		int priority;
		TNode left;
		TNode right;

		public TNode (Object key, int priority) {
			this.key = key;
			this.priority = priority;
		}
	}
}
