package function;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import visitor.IVisitor;

public class ObfuscationData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		try {
			List<Vector<String>> v = (List<Vector<String>>) c;
			policyContent = new DefaultMutableTreeNode("Obfuscation");
			int nbCol = ((Vector<String>)v.get(0)).size();
			
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			obfuscation = document.createElement("obfuscation");
		
			nbRowsToInject = (int)(v.size()*((int)this.obfuscationData.getValue()/100.0));
		
			for (int j = 0; j < nbRowsToInject; j++) {
				Vector<String> newRow = new Vector<String>();
				Element xmlNewRow = document.createElement("newRow");
				int nbRows = v.size();
				int rowInsert = (int) (Math.random()*nbRows);
				
				policyContent.add(new DefaultMutableTreeNode(rowInsert));
				xmlNewRow.setAttribute("numLine", Integer.toString(rowInsert));
				
				for (int i = 0; i < nbCol; i++) {
					Element fieldValue = document.createElement("field");
					fieldValue.setAttribute("numCol", Integer.toString(i));
					String newValue = ((Vector<String>)v.get((int) (Math.random()*nbRows))).elementAt(i);
					fieldValue.setAttribute("value", newValue);
					newRow.add(newValue);
					xmlNewRow.appendChild(fieldValue);
				}
				
				v.add(rowInsert, newRow);
				
				obfuscation.appendChild(xmlNewRow);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		
		panel.add(new JSpinner(obfuscationData));
		
		return panel;
	}

	@Override
	public void init() {
		obfuscationData = new SpinnerNumberModel();
		obfuscationData.setValue(0);
		obfuscationData.setStepSize(1);
		obfuscationData.setMinimum(0);
	}

	@Override
	public String name() {
		return "Obfuscation";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		return policyContent;
	}
	

	@Override
	public boolean isAll() {
		return true;
	}
	
	@Override
	public Node asXML() {
		return obfuscation;
	}

	private DefaultMutableTreeNode policyContent;
	private SpinnerNumberModel obfuscationData;
	private int nbRowsToInject;
	private Element obfuscation;
}
