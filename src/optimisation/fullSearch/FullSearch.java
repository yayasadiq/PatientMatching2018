package optimisation.fullSearch;

import java.util.List;
import simulatedSimilarities.OptimizationApp;
import simulatedSimilarities.OptimizationConnector;

public class FullSearch {
	
	private OptimizationApp optimizationApp;
	public FullSearch(OptimizationApp optimizationApp) {
		this.optimizationApp = optimizationApp;
	}
	
	public void optimize() {

		List<List<Double>> resultMatrix = optimizationApp.getResultMatrix();
		int listSize = resultMatrix.size();
		int i = 1;
		while ( i < listSize) {
			List<Double> line = resultMatrix.get(i);
			int numberColumn = isThereABetterSolution( i, line);
			if(numberColumn != -1) {
				resultMatrix = optimizationApp.getResultMatrix();
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
