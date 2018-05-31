package simulatedSimilarities;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Table;

import utils.IOhelpers.InputManager;

public class OptimizationApp{
	
	private OptimizationConnector optimizationConnector;

	private List<String> resultControlId;
	private List<String> trialPatientsId;
	private List<List<Double>> resultMatrix;
	
	private Table<String, String, Double> trialControlAssociation;

	public OptimizationApp(OptimizationConnector optimizationConnector) {
		this.optimizationConnector = optimizationConnector;
		this.trialControlAssociation = optimizationConnector.getTrialControlAssociation();
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
		
		this.resultControlId = new ArrayList<>();
	}
	
	public void cycle() {
		for (String trialId : trialControlAssociation.rowKeySet()) {
			resultControlId.add(findIndexOfMax(trialControlAssociation.row(trialId)));
		}
		this.resultMatrix = makeMatrix(resultControlId);
		optimizationConnector.writeMatrix(null, resultControlId);
	}
	
	private int insertTrial(String trialId, String controlID, List<Double> sims) {
		double curSim = trialControlAssociation.get(trialId, controlID);
		int counter = 0;
		int nbrOfColumns = sims.size();
		while (counter < nbrOfColumns && sims.get(counter) > curSim)
			counter++;
		
		if (counter == nbrOfColumns) {
			sims.add(curSim);
			return -1;
		}
		return counter;
	}

	private String findIndexOfMax(Map<String, Double> map) {
		double max = 0;
		String maxPos = "";
		for (Map.Entry<String, Double> control_sim : map.entrySet()) {
			double sim = control_sim.getValue();
			if (sim > max && !resultControlId.contains(control_sim.getKey())) {
				max = sim;
				maxPos = control_sim.getKey();
			}
		}
		return maxPos;
	}
	
	public List<List<Double>> makeMatrix(List<String> controlId) {
		List<List<Double>> triangularMatrix = new ArrayList<>();
		int counter = 1;
		for (String trialId : trialPatientsId) {			
			List<Double> sims = new ArrayList<>();
			for(int i = 0; i < counter; i++) {
				double sim = trialControlAssociation.get(trialId, controlId.get(i));
				sims.add(sim);
			}
			triangularMatrix.add(sims);
			counter ++;
		}
		return triangularMatrix;
	}
	
	public List<List<Double>> makeMatrixWithIndex(List<Integer> indexControl) {
		List<String> controlIds = new ArrayList<>();
		int lengthSolutions = indexControl.size();
		for (int i = 0; i < lengthSolutions; i++) {
			controlIds.add(resultControlId.get(indexControl.get(i)));
		}
		return makeMatrix(controlIds);
	}
	
	public double evaluate(int i, int j, double val, Double chosenSim) {
		Double newVal = trialControlAssociation.get(trialPatientsId.get(j), resultControlId.get(i));
		Double otherCurDiagSim = trialControlAssociation.get(trialPatientsId.get(j), resultControlId.get(j));
		double newSims = val + newVal;
		double oldSims = chosenSim + otherCurDiagSim;
		return newSims - oldSims;
	}
	
	public void makeChanges(Integer x, Integer y) {
		String tempId;
		
		tempId = resultControlId.get(x);
		resultControlId.set(x, resultControlId.get(y));
		resultControlId.set(y, tempId);
		int nbrOfTrial = trialPatientsId.size();
		changeColumn(x, nbrOfTrial);
		changeColumn(y, nbrOfTrial);
		
	}

	private void changeColumn(Integer x, int nbrOfTrial) {
		for (int i = x; i < nbrOfTrial; i++) {
			List<Double> line = resultMatrix.get(i);
			Double newSim = trialControlAssociation.get(trialPatientsId.get(i), resultControlId.get(x));
			line.set(x, newSim);
			resultMatrix.set(i, line);
		}
	}

	public List<List<Double>> getResultMatrix() {
		return resultMatrix;
	}

	public List<String> getTrialPatientIds() {
		return trialPatientsId;
	}

	public int getNumberSolutions() {
		return resultControlId.size();
	}
	
	public void setResultMatrix(List<List<Double>> resultMatrix) {
		this.resultMatrix = resultMatrix;
		optimizationConnector.mergeMatrixAndData(resultMatrix, resultControlId);
		this.writeMatrix();
	}

	public double evaluate(int i, int j, int firstControlPatientId, int secondControlPatientId) {
		Double newVal = trialControlAssociation.get(trialPatientsId.get(j), resultControlId.get(firstControlPatientId));
		Double val = trialControlAssociation.get(trialPatientsId.get(i), resultControlId.get(secondControlPatientId));
		Double otherCurDiagSim = trialControlAssociation.get(trialPatientsId.get(j), resultControlId.get(secondControlPatientId));
		Double chosenSim = trialControlAssociation.get(trialPatientsId.get(i), resultControlId.get(firstControlPatientId));
		double newSims = val + newVal;
		double oldSims = chosenSim + otherCurDiagSim;
		return newSims - oldSims;
	}
	
	public void writeMatrix() {
		optimizationConnector.writeMatrix(null, resultControlId);
	}
}
