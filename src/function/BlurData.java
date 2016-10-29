package function;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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

public class BlurData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		try {
			List<Vector<String>> v = (List<Vector<String>>) c;
			value = (Integer)blurData.getValue();
			
			for (int i = 0; i < v.size(); i++) {
				int val = (int)Math.ceil(Double.parseDouble(((Vector<String>)v.get(i)).elementAt(numCol)));
				
				String str1 = new String(Integer.toString((int)Math.ceil(val) - (value/2)));
				String str2 = new String(Integer.toString((int)Math.ceil(val) + (value/2)));
				((Vector<String>)v.get(i)).set(numCol, "[" + str1 + "-" + str2 + "]");
			}
			
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Erreur floutage par appauvrissement", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		
		panel.add(new JSpinner(blurData));
		
		return panel;
	}

	@Override
	public void init() {
		blurData = new SpinnerNumberModel();
		blurData.setValue(0);
		blurData.setStepSize(1);
		blurData.setMinimum(0);
	}

	@Override
	public String name() {
		return "Floutage par appauvrissement";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		return new DefaultMutableTreeNode("Floutage par appauvrissement : " + value);
	}
	
	@Override
	public boolean isAll() {
		return false;
	}
	
	@Override
	public Node asXML() {

		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			Element blurData = document.createElement("blurData");
			
			Element blur = document.createElement("blur");
			blur.setAttribute("value", Integer.toString(value));
			
			blurData.appendChild(blur);
		
			return blurData;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}

	private int value;
	private SpinnerNumberModel blurData;
}
