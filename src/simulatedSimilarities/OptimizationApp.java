package simulatedSimilarities;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import utils.IOhelpers.InputManager;

public class OptimizationApp{
	
	private OptimizationConnector optimizationConnector;

	private List<String> resultControlId;
	private List<String> trialPatientsId;
	private List<List<Double>> resultMatrix;
	
	private Map<String, Map<String, Double>> trialControlAssociation;

	public OptimizationApp(OptimizationConnector optimizationConnector) {
		this.optimizationConnector = optimizationConnector;
		this.trialControlAssociation = optimizationConnector.getTrialControlAssociation();
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
		
		this.resultControlId = new ArrayList<>();
	}
	
	public void cycle() {
		for (String trialId : trialPatientsId) {
			this.resultControlId.add(findIndexOfMax(trialControlAssociation.get(trialId)));
		}
		this.resultMatrix = makeMatrix(resultControlId);
	}
	
	private String findIndexOfMax(Map<String, Double> controlMap) {
		double max = 0;
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
	
	public List<List<Double>> makeMatrix(List<String> controlId) {
		List<List<Double>> triangularMatrix = new ArrayList<>();
		int counter = 1;
		for (String trialId : trialPatientsId) {			
			List<Double> sims = new ArrayList<>();
			for(int i = 0; i < counter; i++) {
				double sim = trialControlAssociation.get(trialId).get(controlId.get(i));
				sims.add(sim);
			}
			triangularMatrix.add(sims);
			counter ++;
		}
		return triangularMatrix;
	}
	
	public List<List<Double>> makeMatrix(int[] indexControl) {
		List<String> controlIds = new ArrayList<>();
		int lengthSolutions = indexControl.length;
		for (int i = 0; i < lengthSolutions; i++) {
			controlIds.add(resultControlId.get(indexControl[i]));
		}
		return makeMatrix(controlIds);
	}
	
	public double evaluate(int i, int j, double val, Double chosenSim) {
		Double newVal = trialControlAssociation.get(trialPatientsId.get(j)).get(resultControlId.get(i));
		Double otherCurDiagSim = trialControlAssociation.get(trialPatientsId.get(j)).get(resultControlId.get(j));
		double newSims = val + newVal;
		double oldSims = chosenSim + otherCurDiagSim;
		if( newSims > oldSims) {
			return newSims;
		} else {
			return -1;
		}
	}
	
	public void makeChanges(Integer x, Integer y) {
		String tempId;
		
		tempId = resultControlId.get(x);
		resultControlId.set(x, resultControlId.get(y));
		resultControlId.set(y, tempId);
		int nbrOfTrial = trialPatientsId.size();
		for (int i = x; i < nbrOfTrial; i++) {
			List<Double> line = resultMatrix.get(i);
			Double newSim = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(x));
			line.set(x, newSim);
			resultMatrix.set(i, line);
		}
		for (int i = y; i < nbrOfTrial; i++) {
			List<Double> line = resultMatrix.get(i);
			Double newSim = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(y));
			line.set(y, newSim);
			resultMatrix.set(i, line);
		}
		
	}
	
	public double[] getSimilarities(int[] solutions) {
		double[] similarities = new double[solutions.length];
		int nbrOfTrialPatients = trialPatientsId.size();
		for (int i = 0; i < nbrOfTrialPatients; i++) {
			similarities[i] = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(solutions[i]));
		}
		return similarities;
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
	}
	

}
