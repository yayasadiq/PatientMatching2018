package optimisation.fullSearch;

import java.util.List;
import java.util.Set;

import simulatedSimilarities.OptimizationConnector;
import utils.generator.Tuple;

public class FullSearch {
	
	private OptimizationConnector optimizationConnector;
	public FullSearch(OptimizationConnector optimizationConnector) {
		this.optimizationConnector = optimizationConnector;
	}
	
	public void optimize() {

		List<List<Double>> resultMatrix = optimizationConnector.getResultMatrix();
		int listSize = resultMatrix.size();
		int i = 1;
		while ( i < listSize) {
			List<Double> line = resultMatrix.get(i);
			int numberColumn = isThereABetterSolution( i, line);
			if(numberColumn != -1) {
				resultMatrix = optimizationConnector.getResultMatrix();
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
				double evaluateValue = optimizationConnector.evaluate(i,j, val, chosenSim);
				if(evaluateValue > maxValue) {
					bestTuple = j;
					maxValue = evaluateValue;
				}
			}				
		}
		if (bestTuple != -1) { 
			optimizationConnector.makeChanges(i, bestTuple);
			return bestTuple;
		}
		return -1;
	}
}
