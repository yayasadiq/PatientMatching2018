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
