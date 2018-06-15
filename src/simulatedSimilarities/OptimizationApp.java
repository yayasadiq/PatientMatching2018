package simulatedSimilarities;


import java.util.List;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class OptimizationApp{
	
	private OptimizationConnector optimizationConnector;

	private TIntArrayList resultControlId;
	private TIntArrayList trialPatientsId;
	private TIntObjectMap<TDoubleArrayList> resultMatrix;
	
	private TIntObjectMap<TIntDoubleHashMap> trialControlAssociation;

	public OptimizationApp(OptimizationConnector optimizationConnector) {
		this.optimizationConnector = optimizationConnector;
		this.trialControlAssociation = optimizationConnector.getTrialControlAssociation();
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
		
		this.resultControlId = new TIntArrayList();
	}
	
	public void cycle() {
		int nbrOfTrials = trialPatientsId.size();
		for (int i = 0; i < nbrOfTrials; i++) {
			resultControlId.add(findIndexOfMax(trialControlAssociation.get(trialPatientsId.get(i))));
		}
		this.resultMatrix = makeMatrix(resultControlId);
		optimizationConnector.writeMatrix(null, resultControlId);
	}
	
	private int insertTrial(int trialId, int controlID, List<Double> sims) {
		double curSim = trialControlAssociation.get(trialId).get(controlID);
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

	private int findIndexOfMax(TIntDoubleHashMap tIntDoubleHashMap) {
		double max = 0;
		int maxId = -1;
		int[] controlsId = tIntDoubleHashMap.keys();
		for (int i = 0; i < controlsId.length; i++) {
			int controlId = controlsId[i];
			double sim = tIntDoubleHashMap.get(controlId);
			if (sim > max && !resultControlId.contains(controlId)) {
				max = sim;
				maxId = controlId;
			}
		}
		return maxId;
	}
	
	public TIntObjectMap<TDoubleArrayList> makeMatrix(TIntArrayList resultControlId2) {
		TIntObjectMap<TDoubleArrayList> triangularMatrix = new TIntObjectHashMap<>();
		int counter = 1;
		int nbrOfTrials = trialPatientsId.size();
		for (int i = 0; i < nbrOfTrials; i++) {			
			TDoubleArrayList sims = new TDoubleArrayList();
			for(int j = 0; j < counter; j++) {
				double sim = trialControlAssociation.
						get(trialPatientsId.get(i)).get(resultControlId2.get(j));
				sims.add(sim);
			}
			triangularMatrix.put(i, sims);
			counter ++;
		}
		return triangularMatrix;
	}
	
	public TIntObjectMap<TDoubleArrayList> makeMatrixWithIndex(List<Integer> indexControl) {
		TIntArrayList controlIds = new TIntArrayList();
		int lengthSolutions = indexControl.size();
		for (int i = 0; i < lengthSolutions; i++) {
			controlIds.add(resultControlId.get(indexControl.get(i)));
		}
		return makeMatrix(controlIds);
	}
	
	public double evaluate(int i, int j, double val, Double chosenSim) {
		Double newVal = trialControlAssociation.get(trialPatientsId.get(j)).get(resultControlId.get(i));
		Double otherCurDiagSim = trialControlAssociation.get(trialPatientsId.get(j)).get(resultControlId.get(j));
		double newSims = val + newVal;
		double oldSims = chosenSim + otherCurDiagSim;
		return newSims - oldSims;
	}
	
	public void makeChanges(Integer x, Integer y) {
		int tempId;
		
		tempId = resultControlId.get(x);
		resultControlId.set(x, resultControlId.get(y));
		resultControlId.set(y, tempId);
		int nbrOfTrial = trialPatientsId.size();
		changeColumn(x, nbrOfTrial);
		changeColumn(y, nbrOfTrial);
		
	}

	private void changeColumn(Integer x, int nbrOfTrial) {
		for (int i = x; i < nbrOfTrial; i++) {
			TDoubleArrayList line = resultMatrix.get(i);
			Double newSim = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(x));
			line.set(x, newSim);
			resultMatrix.put(i, line);
		}
	}

	public TIntObjectMap<TDoubleArrayList> getResultMatrix() {
		return resultMatrix;
	}

	public TIntArrayList getTrialPatientIds() {
		return trialPatientsId;
	}

	public int getNumberSolutions() {
		return resultControlId.size();
	}
	
	public void setResultMatrix(TIntObjectMap<TDoubleArrayList> resultMatrix) {
		this.resultMatrix = resultMatrix;
		optimizationConnector.mergeMatrixAndData(resultMatrix, resultControlId);
		this.writeMatrix();
	}

	public double evaluate(int i, int j, int firstControlPatientId, int secondControlPatientId) {
		Double newVal = trialControlAssociation.get(trialPatientsId.get(j)).get(resultControlId.get(firstControlPatientId));
		Double val = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(secondControlPatientId));
		Double otherCurDiagSim = trialControlAssociation.get(trialPatientsId.get(j)).get(resultControlId.get(secondControlPatientId));
		Double chosenSim = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(firstControlPatientId));
		double newSims = val + newVal;
		double oldSims = chosenSim + otherCurDiagSim;
		return newSims - oldSims;
	}
	
	public void writeMatrix() {
		optimizationConnector.writeMatrix(null, resultControlId);
	}
}
