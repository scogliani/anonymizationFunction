package function;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import visitor.IVisitor;

public class Tokenization implements IVisitor {

	public void init() {
		Security.addProvider(new BouncyCastleProvider());
				
		hashFunction = new DefaultComboBoxModel<String>();
		hashFunction.addElement("MD5");
		hashFunction.addElement("SHA-256");
		hashFunction.addElement("SHA-512");
		
		lengthSeedValueModel = new SpinnerNumberModel();
		lengthSeedValueModel.setValue(0);
		lengthSeedValueModel.setStepSize(1);
		
		seedInitialValueModelLabel = new JLabel("Valeur de la graine");
		seedInitialValueModelLabel.setVisible(false);
		
		seedInitialValueModel = new JTextField();
		seedInitialValueModel.setVisible(false);
		
		checkBoxInitialSeed = new JCheckBox();
		
		checkBoxInitialSeed.addActionListener(new ActionListener() {

		    public void actionPerformed(ActionEvent e) {
		    	JCheckBox d = (JCheckBox)e.getSource();
		    	
		    	if (!d.isSelected()) {
		    		seedInitialValueModelLabel.setVisible(false);
		    		seedInitialValueModel.setVisible(false);
		    	} else {
		    		seedInitialValueModelLabel.setVisible(true);
		    		seedInitialValueModel.setVisible(true);
		    	}
		    	
		    	seedInitialValueModel.setText("");
		    }
		});
		
		initialSeedValue = null;
	}
	
	@Override
	public String name() {
		return "Tokenization";
	}
	
	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		List<Vector<String>> v = (List<Vector<String>>) c;
		hashFct = (String)hashFunction.getSelectedItem();
		token = new ArrayList <String[]>();
		lengthSeed = (Integer)lengthSeedValueModel.getValue();
		RandomGenerator random = null;
		byte[] array = new byte[lengthSeed];
		String s = seedInitialValueModel.getText();
		
		switch (hashFct) {
		case "SHA-256":
			random = new DigestRandomGenerator(new SHA256Digest());
			break;
			
		case "SHA-512":
			random = new DigestRandomGenerator(new SHA512Digest());
			break;
			
		case "MD5":
			random = new DigestRandomGenerator(new MD5Digest());
			break;
		}
		
		if (s.trim() != null && s.trim() != "" && seedInitialValueModelLabel.isVisible()) {
			initialSeedValue = s.getBytes();
			random.addSeedMaterial(initialSeedValue);
		}
		
		for (int i = 0; i < v.size(); i++) {
			String[] value = new String[2];
			value[1] = ((Vector<String>)v.get(i)).elementAt(numCol).toString();
			
			random.nextBytes(array);
			
			StringBuffer sb = new StringBuffer();
	        for (int j = 0; j < array.length; ++j) {
	          sb.append(Integer.toHexString((array[j] & 0xFF) | 0x100).substring(1,3));
	        }
	        
	        value[0] = sb.toString();
	        
	        token.add(value);
					        
	        ((Vector<String>)v.get(i)).set(numCol, sb.toString());
		}
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Tokenization");
		
		node.add(new DefaultMutableTreeNode("Fonction de hachage : " + hashFct));
		node.add(new DefaultMutableTreeNode("Taille des jetons (en octet) : " + lengthSeed));
		
		String s = seedInitialValueModel.getText();
		if (s.trim() != null && s.trim() != "" && seedInitialValueModelLabel.isVisible()) {
			node.add(new DefaultMutableTreeNode("Valeur graine initiale : " + s));
		}
		
		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("Correpondance");
		
		node.add(node2);
	
		for (int i=0; i<token.size(); i++) {
			node2.add(new DefaultMutableTreeNode(token.get(i)[0] + " = " + token.get(i)[1]));
		}
	
		return node;
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(5, 2));
		
		panel.add(new JLabel("Fonction de hachage"));
		panel.add(new JComboBox<String>(hashFunction));
		
		panel.add(new JLabel("Taille des jetons (en octet)"));
		panel.add(new JSpinner(lengthSeedValueModel));
		
		panel.add(new JLabel("Graine"));
		panel.add(checkBoxInitialSeed);
		
		panel.add(seedInitialValueModelLabel);
		panel.add(seedInitialValueModel);
		
		return panel;
	}
	

	@Override
	public boolean isAll() {
		return false;
	}
	
	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			Element tokenization = document.createElement("tokenization");
			tokenization.setAttribute("hashFct", hashFct);
			
			for (int i = 0; i<token.size(); i++) {
				Element row = document.createElement("row");
				row.setAttribute("token", token.get(i)[0]);
				row.setAttribute("value", token.get(i)[1]);
				
				tokenization.appendChild(row);
			}
		
			return tokenization;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	private String hashFct;
	private int lengthSeed;
	private byte[] initialSeedValue;
	
	private JCheckBox checkBoxInitialSeed;
	private SpinnerNumberModel lengthSeedValueModel;
	private JLabel seedInitialValueModelLabel;
	private JTextField seedInitialValueModel;
	private ArrayList<String[]> token;
	private DefaultComboBoxModel<String> hashFunction;
}
