package optimisation.utils;

import java.io.FileNotFoundException;

public abstract class Generator {
	protected String filepath;
	protected CSVWriter csvWriter;
	
	public Generator(String filepath) {
		this.filepath = filepath;
		this.csvWriter = new CSVWriter(filepath);
	}
	
	public abstract void makeData(int nbrLignes) throws FileNotFoundException;
	
	public void setFilepath(String filepath) {
		this.filepath = filepath;
		this.csvWriter.setFilepath(filepath);
	}
}