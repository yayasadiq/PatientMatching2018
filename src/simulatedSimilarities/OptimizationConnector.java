package simulatedSimilarities;

import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.CsvConnector;
import optimisation.utils.CSVWriter;

public class OptimizationConnector {

	
	private CsvConnector connector;
	
	private String[] controlPatientsId;
	private String[] trialPatientsId;
	private String[] resultControlId;
	
	private Map<String, Map<String, Double>> trialControlAssociation;
	
	private String outPath;

	private List<List<Double>> resultMatrix;
	
	public OptimizationConnector(String inPath,String outPath) {
		this.connector = new CsvConnector(inPath);
	
		this.resultMatrix = new ArrayList<>();
		
		this.trialControlAssociation = new HashMap<>();
		this.outPath = outPath;
	}

	

	public void configure() throws FileNotFoundException {
		List<String[]> data = connector.parse();
		String[] curLine = data.get(0);
		int lineLength = curLine.length;
		this.controlPatientsId = new String[lineLength - 1];
		for (int i = 1; i < lineLength - 1; i++) {
			this.controlPatientsId[i - 1] = curLine[i];
		}
		
		int nbrOfLines = data.size();
		this.trialPatientsId = new String[nbrOfLines - 1];
		for (int i = 1; i < nbrOfLines; i++) {
			curLine = data.get(i);
			Map<String, Double> sims = new HashMap<>();
			for (int j = 1; j < lineLength - 1; j++) {
				sims.put(controlPatientsId[j - 1], Double.valueOf(curLine[j]));
			}
			
			String patientId = curLine[0];
			
			trialPatientsId[i - 1] = patientId;
			trialControlAssociation.put(patientId, sims);
		}
	}

	public void cycle() {
		int nbrOfTrial = trialPatientsId.length;
		resultControlId = new String[nbrOfTrial];
		for (int i = 0; i < nbrOfTrial ; i++ ) {
			this.resultControlId[i] = findIndexOfMax(trialControlAssociation.get(trialPatientsId[i]));
		}
		writeMatrix();
	}

	private void writeMatrix() {
		CSVWriter csvWriter = new CSVWriter(outPath);
		csvWriter.writeCell("Trial/Control");
		for (String string : resultControlId) {
			csvWriter.writeCell(string);
		}
		NumberFormat nf = NumberFormat.getInstance( new java.util.Locale( "USA" ));
		csvWriter.newLine();
		int counter = 1;
		for (String trialId : trialPatientsId) {			
			csvWriter.writeCell(trialId);
			List<Double> sims = new ArrayList<>();
			for(int i = 0; i < counter; i++) {
				double sim = trialControlAssociation.get(trialId).get(resultControlId[i]);
				sims.add(sim);
				csvWriter.writeCell(nf.format(sim));
			}
			resultMatrix.add(sims);
			csvWriter.newLine();
			counter ++;
		}
		csvWriter.endFile();
	}

	private String findIndexOfMax(Map<String, Double> controlMap) {
		double max = Integer.MIN_VALUE;
		String maxPos = "";
		for (Map.Entry<String, Double> control_sim : controlMap.entrySet()) {
			double sim = control_sim.getValue();
			if (sim > max && !contains(resultControlId, control_sim.getKey())) {
				max = sim;
				maxPos = control_sim.getKey();
			}
		}
		return maxPos;
	}
	
	private boolean contains(String[] resultControlId2, String key) {
		int nbrOfPatient = resultControlId2.length;
		int i = 0;
		String curId = resultControlId2[i];
		while (curId != null && i < nbrOfPatient) {
			if (curId.equals(key)) {
				return true;
			} 
			i++;
			curId = resultControlId2[i];
		} 
		return false;
	}



	public void mergeMatrixAndData() {
		for (int i = 0; i < resultMatrix.size(); i++) {
			List<Double> line = resultMatrix.get(i);
			String trialId = trialPatientsId[i];
			Map<String, Double> control_sim = trialControlAssociation.get(trialId);
			for (int j = 0; j < line.size(); j++) {
				control_sim.put(controlPatientsId[j], line.get(j));
			}
			trialControlAssociation.put(trialId, control_sim);
		}
	}
	
	public int getNumberSolutions() {
		return resultControlId.length;
	}

	public List<List<Double>> getResultMatrix() {
		return this.resultMatrix;
	}
	
	public void displayMatrix() {
		for (List<Double> sims : resultMatrix) {
			System.out.println(sims);
		}
	}

	public CsvConnector getConnector() {
		return connector;
	}



	public void setConnector(CsvConnector connector) {
		this.connector = connector;
	}



	public String[] getControlPatientsId() {
		return controlPatientsId;
	}



	public void setControlPatientsId(String[] controlPatientsId) {
		this.controlPatientsId = controlPatientsId;
	}



	public String[] getTrialPatientsId() {
		return trialPatientsId;
	}



	public void setTrialPatientsId(String[] trialPatientsId) {
		this.trialPatientsId = trialPatientsId;
	}



	public String[] getResultControlId() {
		return resultControlId;
	}



	public void setResultControlId(String[] resultControlId) {
		this.resultControlId = resultControlId;
	}



	public Map<String, Map<String, Double>> getTrialControlAssociation() {
		return trialControlAssociation;
	}



	public void setTrialControlAssociation(Map<String, Map<String, Double>> trialControlAssociation) {
		this.trialControlAssociation = trialControlAssociation;
	}



	public void setResultMatrix(List<List<Double>> resultMatrix) {
		this.resultMatrix = resultMatrix;
	}
}
