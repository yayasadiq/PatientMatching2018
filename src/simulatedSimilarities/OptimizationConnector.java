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
	
	private List<String> controlPatientsId;
	private List<String> trialPatientsId;
	private List<String> resultControlId;
	
	private Map<String, Map<String, Double>> trialControlAssociation;
	
	private String outPath;

	private List<List<Double>> resultMatrix;
	
	public OptimizationConnector(String inPath,String outPath) {
		this.connector = new CsvConnector(inPath);
		
		this.controlPatientsId = new ArrayList<>();
		this.trialPatientsId = new ArrayList<>();
		this.resultControlId = new ArrayList<>();
		this.resultMatrix = new ArrayList<>();
		
		this.trialControlAssociation = new HashMap<>();
		this.outPath = outPath;
	}

	

	public void configure() throws FileNotFoundException {
		List<String[]> data = connector.parse();
		String[] curLine = data.get(0);
		for (int i = 1; i < curLine.length - 1; i++) {
			this.controlPatientsId.add(curLine[i]);
		}
		for (int i = 1; i < data.size(); i++) {
			curLine = data.get(i);
			Map<String, Double> sims = new HashMap<>();
			for (int j = 1; j < curLine.length - 1; j++) {
				sims.put(controlPatientsId.get(j - 1), Double.valueOf(curLine[j]));
			}
			
			String patientId = curLine[0];
			
			trialPatientsId.add(patientId);
			trialControlAssociation.put(patientId, sims);
		}
	}

	public void cycle() {
		for (String trialId : trialPatientsId) {
			this.resultControlId.add(findIndexOfMax(trialControlAssociation.get(trialId)));
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
				double sim = trialControlAssociation.get(trialId).get(resultControlId.get(i));
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
			if (sim > max && !resultControlId.contains(control_sim.getKey())) {
				max = sim;
				maxPos = control_sim.getKey();
			}
		}
		return maxPos;
	}
	
	public void mergeMatrixAndData() {
		for (int i = 0; i < resultMatrix.size(); i++) {
			List<Double> line = resultMatrix.get(i);
			String trialId = trialPatientsId.get(i);
			Map<String, Double> control_sim = trialControlAssociation.get(trialId);
			for (int j = 0; j < line.size(); j++) {
				control_sim.put(controlPatientsId.get(j), line.get(j));
			}
			trialControlAssociation.put(trialId, control_sim);
		}
	}
	
	public int getNumberSolutions() {
		return resultControlId.size();
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



	public List<String> getControlPatientsId() {
		return controlPatientsId;
	}



	public void setControlPatientsId(List<String> controlPatientsId) {
		this.controlPatientsId = controlPatientsId;
	}



	public List<String> getTrialPatientsId() {
		return trialPatientsId;
	}



	public void setTrialPatientsId(List<String> trialPatientsId) {
		this.trialPatientsId = trialPatientsId;
	}



	public List<String> getResultControlId() {
		return resultControlId;
	}



	public void setResultControlId(List<String> resultControlId) {
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
