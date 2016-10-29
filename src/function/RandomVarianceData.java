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

public class RandomVarianceData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		try {
			List<Vector<String>> v = (List<Vector<String>>) c;
			Integer variance = (int) (Math.random()*Integer.MAX_VALUE);
			
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			randomVariance = document.createElement("randomVariance");
			
			for (int i = 0; i < c.size(); i++) {
				Double newVal =  Double.parseDouble(((Vector<String>)v.get(i)).elementAt(numCol).toString())* (variance/100.0 + 1);
				((Vector<String>)v.get(i)).set(numCol, String.valueOf(newVal));
			}
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
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
		return "Variance aléatoire";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		return new DefaultMutableTreeNode("Random Variance");
	}

	@Override
	public Node asXML() {		
		return randomVariance;
	}
	
	private Element randomVariance;
}
