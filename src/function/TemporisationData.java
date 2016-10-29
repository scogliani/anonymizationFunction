package function;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

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

public class TemporisationData implements IVisitor {
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
		
		chooseDateText = new JTextField();
		chooseDateText.setEditable(false);
		
		jDatePicker = JDateComponentFactory.createJDatePicker(new UtilDateModel());
		
		jDatePicker.setTextEditable(false);
		jDatePicker.setShowYearButtons(true);
	}
	
	@Override
	public String name() {
		return "Temporisation";
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		dateAccess = sdf.format((Date)jDatePicker.getModel().getValue());
		
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
		
		try {
			Random r = SecureRandom.getInstance("SHA1PRNG");
			
			id = r.nextLong();
			
			writeTokens();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Temporisation");
		
		node.add(new DefaultMutableTreeNode("Identifiant : " + id));
		node.add(new DefaultMutableTreeNode("Date d'accès : " + dateAccess));
		
		return node;
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(5, 3));
		
		panel.add(new JLabel("Fonction de hachage"));
		panel.add(new JComboBox<String>(hashFunction));
		
		panel.add(new JLabel("Taille des jetons (en octet)"));
		panel.add(new JSpinner(lengthSeedValueModel));
		
		panel.add(new JLabel("Date d'accès"));
		panel.add((JComponent)jDatePicker);
		
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
		
			Element temporisation = document.createElement("temporisation");
			
			Element idXml = document.createElement("id");
			idXml.setAttribute("value", Long.toString(id));
			temporisation.appendChild(idXml);
			
			Element dateAccessXml = document.createElement("dateAccess");
			dateAccessXml.setAttribute("value", dateAccess);
			temporisation.appendChild(dateAccessXml);
		
			return temporisation;
	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	private void writeTokens() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			document.setXmlVersion("1.0");
			document.setXmlStandalone(true);
		
			Element temporisation = document.createElement("temporisation");
			temporisation.setAttribute("id", Long.toString(id));
			temporisation.setAttribute("dateAccess", dateAccess);
			
			document.appendChild(temporisation);
			
			for (int i = 0; i<token.size(); i++) {
				Element row = document.createElement("row");
				row.setAttribute("token", token.get(i)[0]);
				row.setAttribute("value", token.get(i)[1]);
				
				temporisation.appendChild(row);
			}
			
			StringWriter str = new StringWriter();
			
			Source source = new DOMSource(document);
			Result result = new StreamResult(str);
		     
		    Transformer transfo = TransformerFactory.newInstance().newTransformer();
		     	         
		    transfo.setOutputProperty(OutputKeys.METHOD, "xml");
		    transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		    transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");       
		    transfo.setOutputProperty(OutputKeys.INDENT, "yes");
		     
		    transfo.transform(source, result);
		    
		    PrintWriter writer = new PrintWriter( new FileWriter("etc/temporisation.xml"),true );
		    writer.print(str);
		    writer.close();
		    
		    str.close();
		} catch (ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private String hashFct;
	private int lengthSeed;
	private long id;
	private byte[] initialSeedValue;
	private String dateAccess;
	
	private JCheckBox checkBoxInitialSeed;
	private SpinnerNumberModel lengthSeedValueModel;
	private JLabel seedInitialValueModelLabel;
	private JTextField seedInitialValueModel;
	private ArrayList<String[]> token;
	private DefaultComboBoxModel<String> hashFunction;
	
	private JTextField chooseDateText;
	
	private JDatePicker jDatePicker;
}
