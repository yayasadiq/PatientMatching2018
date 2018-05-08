package simulatedSimilarities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.CsvConnector;
import utils.IOhelpers.CSVWriter;

public class OptimizationConnector {

	
	private CsvConnector connector;
	
	private List<String> controlPatientsId;
	private List<String> trialPatientsId;
	
	private Map<String, Map<String, Double>> trialControlAssociation;
	
	private String outPath;
	
	public OptimizationConnector(String inPath,String outPath) {
		this.connector = new CsvConnector(inPath);
		
		this.controlPatientsId = new ArrayList<>();
		this.trialPatientsId = new ArrayList<>();
		
		this.trialControlAssociation = new HashMap<>();
		this.outPath = outPath;
	}

	

	public void configure() throws FileNotFoundException {
		List<String[]> data = connector.parse();
		String[] curLine = data.get(0);
		int lineLength = curLine.length - 1;
		for (int i = 1; i < lineLength; i++) {
			this.controlPatientsId.add(curLine[i]);
		}
		for (int i = 1; i < data.size(); i++) {
			curLine = data.get(i);
			Map<String, Double> sims = new HashMap<>();
			for (int j = 1; j < lineLength; j++) {
				sims.put(controlPatientsId.get(j - 1), Double.valueOf(curLine[j]));
			}
			
			String patientId = curLine[0];
			
			trialPatientsId.add(patientId);
			trialControlAssociation.put(patientId, sims);
		}
	}	
	
	public void writeMatrix(String filePath, List<String> resultControlId) {
		CSVWriter csvWriter = new CSVWriter(filePath);
		csvWriter.writeCell("Trial/Control");
		for (String string : resultControlId) {
			csvWriter.writeCell(string);
		}
		NumberFormat nf = NumberFormat.getInstance(new java.util.Locale( "USA" ));
		csvWriter.newLine();
		for (String trialId : trialPatientsId) {
			csvWriter.writeCell(trialId);
			for(String controlId : resultControlId) {
				double sim = trialControlAssociation.get(trialId).get(controlId);
				csvWriter.writeCell(nf.format(sim));
			}
			csvWriter.newLine();
		}
		try {
			csvWriter.createCSVWithContent();;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mergeMatrixAndData(List<List<Double>> resultMatrix, List<String> resultControlId) {
		for (int i = 0; i < resultMatrix.size(); i++) {
			List<Double> line = resultMatrix.get(i);
			String trialId = trialPatientsId.get(i);
			Map<String, Double> control_sim = trialControlAssociation.get(trialId);
			for (int j = 0; j < line.size(); j++) {
				control_sim.put(resultControlId.get(j), line.get(j));
			}
			trialControlAssociation.put(trialId, control_sim);
		}
	}
	
	public List<String> getControlPatientsId() {
		return controlPatientsId;
	}

	public List<String> getTrialPatientsId() {
		return trialPatientsId;
	}


	public Map<String, Map<String, Double>> getTrialControlAssociation() {
		return trialControlAssociation;
	}


}
