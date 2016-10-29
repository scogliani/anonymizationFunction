package function;

import java.awt.GridLayout;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import visitor.IVisitor;

public class HashData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		try {
			List<Vector<String>> v = (List<Vector<String>>) c;
			hashFct = (String)hashFunction.getSelectedItem();
			MessageDigest msgDigest = MessageDigest.getInstance(hashFct);
			
			for (int i = 0; i < v.size(); i++) {
	
				String[] value = new String[2];
				value[1] = ((Vector<String>)v.get(i)).elementAt(numCol).toString();
				
				msgDigest.reset();
				msgDigest.update(value[1].getBytes());
				
				byte[] array = msgDigest.digest();
				
				StringBuffer sb = new StringBuffer();
		        for (int j = 0; j < array.length; ++j) {
		          sb.append(Integer.toHexString((array[j] & 0xFF) | 0x100).substring(1,3));
		        }
		        
		        value[0] = sb.toString();
						        
		        ((Vector<String>)v.get(i)).set(numCol, sb.toString());
			}
			
			
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		
		panel.add(new JComboBox<String>(hashFunction));
		
		return panel;
	}

	@Override
	public void init() {
		Security.addProvider(new BouncyCastleProvider());
		
		Set<String> messageDigest = Security.getAlgorithms("MessageDigest");
		
		hashFunction = new DefaultComboBoxModel<String>();
		for (String str : messageDigest) {
			hashFunction.addElement(str);
		}
	}

	@Override
	public String name() {
		return "Hachage";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		return new DefaultMutableTreeNode("Hachage : " + hashFct);
	}
	

	@Override
	public boolean isAll() {
		return false;
	}
	
	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
			Element hashData = document.createElement("hashData");
			hashData.setAttribute("algorithm", hashFct);
		
			return hashData;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}

	private String hashFct;
	private DefaultComboBoxModel<String> hashFunction;
}
