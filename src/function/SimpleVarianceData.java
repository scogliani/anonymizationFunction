package function;

import java.awt.GridLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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

public class SimpleVarianceData implements IVisitor {

	@Override
	public void anonymization(Collection<Vector<String>> c, int numCol) {
		variance = (Integer)simpleVarianceValueModel.getValue();
		List<Vector<String>> v = (List<Vector<String>>) c;
		
		for (int i = 0; i < v.size(); i++) {
			String element = ((Vector<String>)v.get(i)).elementAt(numCol);

			SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd" );
			f.setLenient(false);
			String simpleVariance = "";
			Boolean error = false;
			Date d = null;
			try {
				d = f.parse(element);
			} catch (ParseException e) {
				error = true;
			}
			if (! error) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				int day = cal.get(Calendar.DAY_OF_MONTH);
				cal.add(Calendar.DATE, (int) Math.floor(day*(variance/100.0 + 1)));
				Date date = cal.getTime();
				simpleVariance = f.format(date);
				
				
			} else {
				simpleVariance = (new Double((Double.parseDouble(element) * (variance/100.0 + 1)))).toString();
			}
			
			
			((Vector<String>)v.get(i)).set(numCol, simpleVariance);
		}
	}

	@Override
	public JPanel configuration() {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		
		panel.add(new JSpinner(simpleVarianceValueModel));
		
		return panel;
	}

	@Override
	public void init() {
		simpleVarianceValueModel = new SpinnerNumberModel();
		simpleVarianceValueModel.setValue(0);
		simpleVarianceValueModel.setStepSize(1);
	}

	@Override
	public String name() {
		return "Variance simple";
	}

	@Override
	public DefaultMutableTreeNode policyContent() {
		return new DefaultMutableTreeNode("Variance : " + variance + "%");
	}
	

	@Override
	public boolean isAll() {
		return false;
	}
	
	@Override
	public Node asXML() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element simpleVarianceData = document.createElement("simpleVarianceData");
			
			Element variance = document.createElement("variance");
			variance.setAttribute("value", Integer.toString(this.variance));
			
			simpleVarianceData.appendChild(variance);
			
			return simpleVarianceData;
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private int variance;
	private SpinnerNumberModel simpleVarianceValueModel;
}
