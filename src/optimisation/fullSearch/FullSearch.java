package optimisation.fullSearch;

import java.util.List;
import simulatedSimilarities.OptimizationApp;

public class FullSearch {
	
	private OptimizationApp optimizationApp;
	List<List<Double>> resultMatrix;
	
	public FullSearch(OptimizationApp optimizationApp) {
		this.optimizationApp = optimizationApp;
		this.resultMatrix = optimizationApp.getResultMatrix();
	}
	
	public void optimize() {

		int listSize = resultMatrix.size();
		int i = 1;
		while ( i < listSize) {
			List<Double> line = resultMatrix.get(i);
			int numberColumn = isThereABetterSolution(i, line);
			if(numberColumn != -1) {
				i = numberColumn;
				continue;
			}
			i++;
		}
	}

	private int isThereABetterSolution( int i, List<Double> line) {
		
		int lineLength = line.size() - 1;
		Double chosenSim = line.get(lineLength);
		double maxValue = chosenSim;
		int bestTuple = -1;
		for (int j = 0; j < lineLength; j++) {
			double val = line.get(j);
			if (val > chosenSim) {
				double evaluateValue = optimizationApp.evaluate(i,j, val, chosenSim);
				if(evaluateValue > maxValue) {
					bestTuple = j;
					maxValue = evaluateValue;
				}
			}				
		}
		
		if (bestTuple != -1) { 
			optimizationApp.makeChanges(i, bestTuple);
			return bestTuple;
		}
		return -1;
	}
}
