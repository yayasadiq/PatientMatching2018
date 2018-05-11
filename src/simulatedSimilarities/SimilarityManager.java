package simulatedSimilarities;

import java.util.List;
import java.util.Random;

public class SimilarityManager {	
	
	private double simSum;
	private double maxSimSum;
	private int nbrOfSwaps;
	
	private List<List<Double>> resultMatrix;
	private List<String> trialPatientsId;
	
	private OptimizationApp app;
	
	public SimilarityManager(OptimizationApp app) {
		super();
		
		this.trialPatientsId = app.getTrialPatientIds();
		
		this.app = app;
		this.resultMatrix = app.getResultMatrix();
		this.computeSimilaritiesSum();
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

	public int getNumberOfSwapsOnALine(List<Double> list) {
		int nbrOfSwap = 0;
		double chosenSim = list.get(list.size() - 1);
		for (Double sim : list) {
			if (chosenSim < sim) {
				nbrOfSwap ++;
			}
		}
		return nbrOfSwap;
	}

	public void increaseDiffAuto(int nbrOfSwapsNeeded) {
		int trialSize = resultMatrix.size() - 1;
		computeSimilaritiesSum();
		//In nbrOfSwaps we could have some similarities that are removed in reduceTotalSwap
		int totalMaxSwap = (trialSize * (trialSize + 1))/2 ;
		totalMaxSwap = reduceNumberOfSwaps(totalMaxSwap);
		if (totalMaxSwap >= nbrOfSwapsNeeded) {
			Random rand = new Random();
			while (nbrOfSwaps < nbrOfSwapsNeeded) {
				int indiceLine = rand.nextInt(trialSize) + 1;
				List<Double> line = resultMatrix.get(indiceLine);
				int lineSize = line.size() - 1;
				double chosenSim = line.get(lineSize);
				if (chosenSim < 1) {
					int indiceColumn = rand.nextInt(lineSize);
					if (chosenSim > line.get(indiceColumn)) {
						ameliorateSim(rand, indiceLine, line, indiceColumn, chosenSim);
						nbrOfSwaps ++;
					}
				}
			}
		} else {
			throw new IllegalArgumentException("The number of swaps asked is more than the number of possible swaps (" + totalMaxSwap +")");
		}
		app.setResultMatrix(resultMatrix);
		computeSimilaritiesSum();
		
	}

	private int reduceNumberOfSwaps(int totalMaxSwap) {
		for ( List<Double> line : resultMatrix) {
			if (line.get(line.size()-1)>=1.0) {
				totalMaxSwap -= line.size() - 1;
			}
		}
		return totalMaxSwap;
	}

	private void ameliorateSim(Random rand, int indiceLine, List<Double> line, int indiceColumn, double chosenSim) {
		double amelioratedSim = chosenSim + (1-chosenSim) * rand.nextDouble();
		line.set(indiceColumn, amelioratedSim);
		resultMatrix.set(indiceLine, line);
	}
	
	public void setResultMatrix(List<List<Double>> resultMatrix) {
		this.resultMatrix = resultMatrix;
	}

	private void changeNextAmeliorableSim(int indiceLine, int indiceColumn, Random rand) {
		int nbrOfLines = resultMatrix.size();
		int i = indiceLine;
		int j = 0;
		double chosenSim = 0;
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
			chosenSim = line.get(lineSize);
			if (chosenSim < 1) {
				j = indiceColumn;
				int initialColumnPointPlusSize = lineSize + indiceColumn;
				while (j < initialColumnPointPlusSize && !hasFound) {
					currentColumn = j % lineSize;
					if (line.get(currentColumn) < chosenSim) {
						hasFound = true;
					} 
					j++;															
				}
			}
			i++;
		}
		if (hasFound) {
			ameliorateSim(rand, currentLine , line, currentColumn, chosenSim);
		} else {
			throw new NullPointerException("There is no more similities available");
		}
	}	

	public double getSimSum() {
		return simSum;
	}

	public double getMaxSimSum() {
		return maxSimSum;
	}

	public int getNbrOfSwaps() {
		return nbrOfSwaps;
	}
}
