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

public class DeleteData implements IVisitor {
	@Override
	public void init() {}
	
	@Override
	public String name() {
		return "Suppression de la donnee";
	}
	
	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		List<Vector<String>> v = (List<Vector<String>>) c;
		for (int i = 0; i < v.size(); i++) {
			((Vector<String>)v.get(i)).set(numCol, "");
		}
	}
	
	@Override
	public DefaultMutableTreeNode policyContent()  {
		return new DefaultMutableTreeNode("Delete data");
	}
	
	@Override
	public JPanel configuration() {
		return null;
	}
	

	@Override
	public boolean isAll() {
		return false;
	}

	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element deleteData = document.createElement("deleteData");
			
			return deleteData;
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
