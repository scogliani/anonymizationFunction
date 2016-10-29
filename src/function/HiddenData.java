package function;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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

public class HiddenData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		List<Vector<String>> v = (List<Vector<String>>) c;
		int begin  = (Integer) this.begin.getValue();
		int end    = (Integer) this.end.getValue();
		char chara = (Character)this.character.getSelectedItem();
		
		for (int i = 0; i < v.size(); i++) {
			StringBuffer element = new StringBuffer(((Vector<String>)v.get(i)).elementAt(numCol));
			
			for (int j = begin; j <= end; j++) {
				element.setCharAt(j, chara);
			}
			
			((Vector<String>)v.get(i)).set(numCol, element.toString());
		}
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(3, 2));
		
		panel.add(new JLabel("Debut"));
		panel.add(new JSpinner(begin));
		panel.add(new JLabel("Fin"));
		panel.add(new JSpinner(end));
		panel.add(new JLabel("Caractere"));
		panel.add(new JComboBox<Character>(character));
		
		return panel;
	}

	@Override
	public void init() {
		begin      = new SpinnerNumberModel();
		begin.setValue(0);
		begin.setMinimum(0);
		begin.setStepSize(1);
		
		end        = new SpinnerNumberModel();
		end.setValue(0);
		end.setMinimum(0);
		end.setStepSize(1);
		
		character  = new DefaultComboBoxModel<Character>();
		character.addElement('x');
		character.addElement('0');
	}

	@Override
	public String name() {
		return "Masquage des donnees";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		DefaultMutableTreeNode mask = new DefaultMutableTreeNode("Masquage");
		
		mask.add(new DefaultMutableTreeNode("Begin: " + begin.getValue()));
		mask.add(new DefaultMutableTreeNode("End: " + end.getValue()));
		mask.add(new DefaultMutableTreeNode("Character: " + character.getSelectedItem()));
		
		return mask;
	}
	

	@Override
	public boolean isAll() {
		return false;
	}
	
	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			Element hiddenData = document.createElement("hiddenData");
			
			Element hidden = document.createElement("hidden");
			
			hidden.setAttribute("begin", Integer.toString((Integer)begin.getValue()));
			hidden.setAttribute("end", Integer.toString((Integer)end.getValue()));
			hidden.setAttribute("character", Character.toString((Character)character.getSelectedItem()));
			
			hiddenData.appendChild(hidden);
		
			return hiddenData;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}

	private SpinnerNumberModel begin;
	private SpinnerNumberModel end;
	private DefaultComboBoxModel<Character> character;
}
