package model;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CaseComponent;

public class MockPatient implements CaseComponent {
	private String id;
	private double[] attributs;
	public static final int NUMBER_OF_ATTRIBUTS = 30;
	
	@Override
    public Attribute getIdAttribute() {
        return new Attribute("id", this.getClass());
    }
	
	public MockPatient() {
		this.attributs = new double[NUMBER_OF_ATTRIBUTS];
	}
	
	public double[] getAttributs() {
		return attributs;
	}

	public void setAttributs(double[] attributs) {
		this.attributs = attributs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
