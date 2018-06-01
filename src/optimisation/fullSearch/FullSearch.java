package optimisation.fullSearch;

import java.util.List;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import simulatedSimilarities.OptimizationApp;

public class FullSearch {
	
	private OptimizationApp optimizationApp;
	TIntObjectMap<TDoubleArrayList> resultMatrix;
	
	public FullSearch(OptimizationApp optimizationApp) {
		this.optimizationApp = optimizationApp;
		this.resultMatrix = optimizationApp.getResultMatrix();
	}
	
	public void optimize() {

		int listSize = resultMatrix.size();
		int i = 1;
		while ( i < listSize) {
			TDoubleArrayList line = resultMatrix.get(i);
			int numberColumn = isThereABetterSolution(i, line);
			if(numberColumn != -1) {
				i = numberColumn;
				continue;
			}
			i++;
		}
	}

	private int isThereABetterSolution( int i, TDoubleArrayList line) {
		
		int lineLength = line.size() - 1;
		double chosenSim = line.get(lineLength);
		double maxValue = 0;
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
