package function;

import java.awt.GridLayout;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import visitor.IVisitor;

public class SymmetricEncryptionData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		try {			
			List<Vector<String>> v = (List<Vector<String>>) c;
			
			String algorithm = (String)this.algorithm.getSelectedItem();
			String mode      = (String)this.mode.getSelectedItem();
			String padding   = (String)this.padding.getSelectedItem();
			
		    SecretKey keyBytes = new SecretKeySpec(key.getText().getBytes(), algorithm);
			
			Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding, "BC");
			
			cipher.init(Cipher.ENCRYPT_MODE, keyBytes);
			
			for (int i = 0; i < v.size(); i++) {
				byte[] textEncrypted = cipher.doFinal(((Vector<String>)v.get(i)).elementAt(numCol).toString().getBytes());
				
				StringBuilder sb = new StringBuilder(textEncrypted.length*2);
				
				for (byte b : textEncrypted) {
					sb.append( String.format("%x", b) );
				}
				
				 ((Vector<String>)v.get(i)).set(numCol, sb.toString());
			}	
		} catch (NoSuchProviderException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(4, 2));
		key.setText("");
		
		panel.add(new JLabel("Algorithm"));
		panel.add(new JComboBox<String>(algorithm));
		panel.add(new JLabel("Mode"));
		panel.add(new JComboBox<String>(mode));
		panel.add(new JLabel("Padding"));
		panel.add(new JComboBox<String>(padding));
		panel.add(new JLabel("Key"));
		panel.add(key);
		
		return panel;
	}

	@Override
	public void init() {
		Security.addProvider(new BouncyCastleProvider());
		
		algorithm = new DefaultComboBoxModel<String>();
		algorithm.addElement("AES");
		algorithm.addElement("DES");
		algorithm.addElement("DESede");
		
		mode = new DefaultComboBoxModel<String>();
		mode.addElement("ECB");
		mode.addElement("CBC");
		mode.addElement("OFB");
		mode.addElement("CFB");
		mode.addElement("CTR");
		
		padding = new DefaultComboBoxModel<String>();
		padding.addElement("NoPadding");
		padding.addElement("PKCS5Padding");
		padding.addElement("PKCS7Padding");
		
		key = new JTextField();
	}

	@Override
	public String name() {
		return "Chiffrement symetrique";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		DefaultMutableTreeNode crypto = new DefaultMutableTreeNode("Symmetric encryption");
		
		crypto.add(new DefaultMutableTreeNode("algorithm : " + algorithm.getSelectedItem()));
		crypto.add(new DefaultMutableTreeNode("mode : "      + mode.getSelectedItem()));
		crypto.add(new DefaultMutableTreeNode("padding : "   + padding.getSelectedItem()));
		crypto.add(new DefaultMutableTreeNode("password : "  + key.getText()));
		
		return crypto;
	}
	

	@Override
	public boolean isAll() {
		return false;
	}
	
	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			Element symmetricEncryption = document.createElement("symmetricEncryption");
			
			Element algorithmXml = document.createElement("algorithm");
			algorithmXml.setAttribute("value", (String)algorithm.getSelectedItem());
			symmetricEncryption.appendChild(algorithmXml);
			
			Element modeXml = document.createElement("mode");
			modeXml.setAttribute("value", (String)mode.getSelectedItem());
			symmetricEncryption.appendChild(modeXml);
			
			Element paddingXml = document.createElement("padding");
			paddingXml.setAttribute("value", (String)padding.getSelectedItem());
			symmetricEncryption.appendChild(paddingXml);
			
			Element passwordXml = document.createElement("password");
			passwordXml.setAttribute("value", (String)key.getText());
			symmetricEncryption.appendChild(passwordXml);
		
			return symmetricEncryption;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	private DefaultComboBoxModel<String> algorithm;
	private DefaultComboBoxModel<String> mode;
	private DefaultComboBoxModel<String> padding;
	private JTextField key;
}
