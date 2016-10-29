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

public class SubArbitraryData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		List<Vector<String>> v = (List<Vector<String>>) c;
		for (int i = 0; i < v.size(); i++) {
			int val = ((int) (Math.random()*dico.length-1));
			((Vector<String>)v.get(i)).set(numCol, dico[val]);
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
		return "Substitution Arbitraire";
	}

	/** Ajoute le contenu du dictionnaire au policy*/
	public DefaultMutableTreeNode policyContent() {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Substitution arbitraire");
		for (int i=0; i<dico.length; i++) {
			node.add(new DefaultMutableTreeNode(dico[i]));
		}
		
		return node;
	}

	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element subArbitraryData = document.createElement("subArbitraryData");
			
			for (int i=0; i<dico.length; i++) {
				Element el = document.createElement("substitution");
				el.setAttribute("value", dico[i]);
				
				subArbitraryData.appendChild(el);
			}
			
			return subArbitraryData;
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/** Le dictionnaire qui va servir pour la substitution*/
	private String[] dico = {"Jaune","Bleu","Vert","Blanc","Noir","Rouge"};
}
