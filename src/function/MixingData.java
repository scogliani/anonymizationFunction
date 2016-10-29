package function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import visitor.IVisitor;

public class MixingData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		List<Vector<String>> v = (List<Vector<String>>) c;
		ArrayList<Object> copy = new ArrayList<Object>();
		
		links = new ArrayList <String[]>();
		
		int nb = v.size();
		for (int i = 0; i < nb ; i++) {
			String value = ((Vector<String>)v.get(i)).elementAt(numCol).toString();
			copy.add(value);
		}		
		
		ArrayList<Integer> newOrder = new ArrayList<Integer>();
		for (int i = 0; i < nb ; i++) {
			newOrder.add(i);
		}
		
		Collections.shuffle(newOrder);
		
		for (int i=0; i <nb ; i++) {
			String[] value = new String[2];
			value[0] = newOrder.get(i).toString();
			value[1] = copy.get(newOrder.get(i)).toString();
			
			((Vector<String>)v.get(i)).set(numCol, value[1].toString());
			links.add(value);
		}

	}

	@Override
	public JPanel configuration() {
		return null;
	}

	@Override
	public void init() {

	}

	@Override
	public boolean isAll() {
		return false;
	}

	@Override
	public String name() {
		return "Mélange de données";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Mixing Data");
		
		for (int i=0; i<links.size(); i++) {
			node.add(new DefaultMutableTreeNode(links.get(i)[0] + " = " + links.get(i)[1]));
		}
	
		return node;
	}
	
	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			Element mixingData = document.createElement("mixingData");
			
			for (int i=0; i<links.size(); i++) {
				Element row = document.createElement("row");
				row.setAttribute("numRow", links.get(i)[0]);
				row.setAttribute("value", links.get(i)[1]);
				
				mixingData.appendChild(row);
			}
		
			return mixingData;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	private ArrayList<String[]> links;

}
