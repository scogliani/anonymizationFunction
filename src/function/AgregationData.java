package function;

import java.util.Collection;
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

public class AgregationData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		List<Vector<String>> v = (List<Vector<String>>) c;
		double moyenne = 0.0;
		for (int i = 0; i < c.size(); i++) {
			moyenne = moyenne + Double.parseDouble(((Vector<String>)v.get(i)).elementAt(numCol).toString());
		}
		moyenne = moyenne / c.size();
		
		for (int i=0; i < c.size() ; i++) {
			((Vector<String>)v.get(i)).set(numCol, String.valueOf(moyenne));
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
		return "Agregation";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		return new DefaultMutableTreeNode("Agregation");
	}

	@Override
	public Node asXML() {

		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			Element agregation = document.createElement("Agregation");
		
			return agregation;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}
} 