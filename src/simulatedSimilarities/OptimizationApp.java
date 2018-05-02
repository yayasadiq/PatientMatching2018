package simulatedSimilarities;


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
	
	private double simSum;
	private double maxSimSum;
	private int nbrOfSwaps;

	public OptimizationApp(OptimizationConnector optimizationConnector) {
		this.optimizationConnector = optimizationConnector;
		this.trialControlAssociation = optimizationConnector.getTrialControlAssociation();

		optimizationConnector.getControlPatientsId();
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
		this.resultControlId = optimizationConnector.getResultControlId();
		this.resultMatrix = optimizationConnector.getResultMatrix();
	}
	
	public void computeSimilaritiesSum() {
		 this.simSum = 0;
		 this.maxSimSum = 0;
		 this.nbrOfSwaps = 0;
		 int nbrOfTrial = trialPatientsId.size();
		 for (int i = 0; i < nbrOfTrial; i++) {
			 simSum += resultMatrix.get(i).get(i);
			 maxSimSum += getOptimalSwap(resultMatrix.get(i));
		 }
		 System.out.println("simSum : " + simSum);
		 System.out.println("maxSimSum : " + maxSimSum);
		 System.out.println("Difference : " + String.valueOf(maxSimSum - simSum));
		 System.out.println("Number of swap : " + nbrOfSwaps);
		 double swapRatio = ((double) nbrOfSwaps / (double) ((nbrOfTrial * resultControlId.size() /2)) * 100);
		 System.out.println("Frequence of swap : " + swapRatio + "%");
	}

	private double getOptimalSwap(List<Double> list) {
		int nbrOfSims = list.size() - 1; // the last one belong to the diagonal
		double chosenSim = list.get(nbrOfSims);
		double max = chosenSim;
		double currentSim;
		for (int i = 0; i < nbrOfSims; i++) {
			currentSim = list.get(i);
			if (currentSim > chosenSim) {
				nbrOfSwaps ++;
				if (currentSim > max) {
					max = currentSim;
				}
			}
			
		}
		return max;
	}
	private int getNumberOfSwaps(List<Double> list) {
		int nbrOfSwap = 0;
		double choosenSim = list.get(list.size() - 1);
		for (Double sim : list) {
			if (choosenSim < sim) {
				nbrOfSwap ++;
			}
		}
		return nbrOfSwap;
	}

	public void increaseDiffAuto(int nbrOfSwapsNeeded) {
		int trialSize = trialPatientsId.size() - 1;
		computeSimilaritiesSum();
		//In nbrOfSwaps we could have some similarities that are removed in reduceTotalSwap
		int totalMaxSwap = - nbrOfSwaps + (trialSize * (trialSize + 1))/2 ;
		totalMaxSwap = reduceTotalSwap(totalMaxSwap);
		if (totalMaxSwap > nbrOfSwapsNeeded) {
			Random rand = new Random();
			while (nbrOfSwaps < nbrOfSwapsNeeded) {
				int indiceLine = rand.nextInt(trialSize) + 1;
				List<Double> line = resultMatrix.get(indiceLine);
				int lineSize = line.size() - 1;
				int indiceColumn = rand.nextInt(lineSize);
				double choosenSim = line.get(lineSize);
				if (choosenSim < 1) {
					if (choosenSim < line.get(indiceColumn)) {
						changeNextAmeliorableSim( indiceLine,  indiceColumn,  rand);
					} else {
						ameliorateSim(rand, indiceLine, line, indiceColumn, choosenSim);
					}
					nbrOfSwaps++;
				}
			}
		} else {
			throw new IllegalArgumentException("The number of swaps asked is more than the number of possible swaps (" + totalMaxSwap +")");
		}
		computeSimilaritiesSum();
	}

	private int reduceTotalSwap(int totalMaxSwap) {
		for ( List<Double> line : resultMatrix) {
			if (line.get(line.size()-1)>=1.0) {
				totalMaxSwap -= line.size() - 1;
			}
		}
		return totalMaxSwap;
	}

	private void ameliorateSim(Random rand, int indiceLine, List<Double> line, int indiceColumn, double choosenSim) {
		double amelioratedSim = choosenSim + (1-choosenSim) * rand.nextDouble();
		line.set(indiceColumn, amelioratedSim);
		resultMatrix.set(indiceLine, line);
	}
	
	private void changeNextAmeliorableSim(int indiceLine, int indiceColumn, Random rand) {
		int nbrOfLines = resultMatrix.size();
		int i = indiceLine;
		int j = 0;
		double choosenSim = 0;
		int lineSize = 0;
		List<Double> line = null;
		int currentColumn = 0;
		boolean hasFound = false;
		int initialPointPlusSize = indiceLine + nbrOfLines;
		int currentLine = (i % (nbrOfLines - 1))+ 1;
		while (i < initialPointPlusSize && !hasFound) {
			currentLine = (i % (nbrOfLines - 1))+ 1;
			line = resultMatrix.get(currentLine);
			lineSize = line.size() - 1;
			choosenSim = line.get(lineSize);
			if (choosenSim < 1) {
				j = indiceColumn;
				int initialColumnPointPlusSize = lineSize + indiceColumn;
				while (j < initialColumnPointPlusSize && !hasFound) {
					currentColumn = j % lineSize;
					if (line.get(currentColumn) < choosenSim) {
						hasFound = true;
					} 
					j++;															
				}
			}
			i++;
		}
		if (hasFound) {
			ameliorateSim(rand, currentLine , line, currentColumn, choosenSim);
		} else {
			throw new NullPointerException("There is no more similities available");
		}
	}

	public void increaseDiffManually() {
		int nbrOflines = resultMatrix.size();
		for (int i = 0; i < nbrOflines; i++) {
			List<Double> line = resultMatrix.get(i);
			int numberOfSwaps = getNumberOfSwaps(line);
			int maxNumberOfPossibleSwaps = line.size() - 1;
			double choosenSim = line.get(maxNumberOfPossibleSwaps);
			System.out.println("This line contains " + numberOfSwaps + " possible swaps on " + maxNumberOfPossibleSwaps);
			System.out.println("The choosen similarity is : " + choosenSim);
			System.out.println("Do you want to augment this value ? y/n");
			char enteredChar = InputManager.enterChar();
			
			if (enteredChar != 'y') {
				continue;
			}
			System.out.println("You can pass to the next line thanks to 'b'");
			
			for (int j = 1; j <= maxNumberOfPossibleSwaps; j++) {
				System.out.println("This line contains actually " + numberOfSwaps + " possible swaps on " + maxNumberOfPossibleSwaps);
				System.out.println("Current value is : " + line.get(j) + ".\nDo you want to augment this value ("+ j + "/" + maxNumberOfPossibleSwaps +") ? y/n ");
				enteredChar = InputManager.enterChar();
				if (enteredChar == 'b') {
					break;
				} else if (enteredChar != 'y') {
					continue;
				}
				Random rand = new Random();
				line.set(j, choosenSim + (1-choosenSim) * rand .nextDouble());
				numberOfSwaps ++;
				System.out.println("Ajouté");
			}
			resultMatrix.set(i, line);
			computeSimilaritiesSum();
		}
		
	}
	public double[] getSimilarities(int[] solutions) {
		int solLength = solutions.length;
		double[] similarities = new double[solLength];

		for (int i = 0; i < solLength; i++) {
			similarities[i] = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(solutions[i]));
		}
		return similarities;
	}

	public void mergeMatrixAndData() {
		optimizationConnector.setResultMatrix(resultMatrix);
		optimizationConnector.mergeMatrixAndData();
	}


}
