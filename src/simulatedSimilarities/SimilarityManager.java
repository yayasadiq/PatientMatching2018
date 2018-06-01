package simulatedSimilarities;

import java.util.List;
import java.util.Random;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;

public class SimilarityManager {	
	
	private double simSum;
	private double maxSimSum;
	private int nbrOfSwaps;
	
	private TIntObjectMap<TDoubleArrayList> resultMatrix;
	private TIntArrayList trialPatientsId;
	
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

	private double getOptimalSwap(TDoubleArrayList tDoubleArrayList) {
		int nbrOfSims = tDoubleArrayList.size() - 1; // the last one belong to the diagonal
		double chosenSim = tDoubleArrayList.get(nbrOfSims);
		double max = chosenSim;
		double currentSim;
		for (int i = 0; i < nbrOfSims; i++) {
			currentSim = tDoubleArrayList.get(i);
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
				incrementSwapNumber(trialSize, rand);
			}
		} else {
			throw new IllegalArgumentException("The number of swaps asked is more than the number of possible swaps (" + totalMaxSwap +")");
		}
		app.setResultMatrix(resultMatrix);
		computeSimilaritiesSum();
		
	}

	private void incrementSwapNumber(int trialSize, Random rand) {
		int indiceLine = rand.nextInt(trialSize) + 1;
		TDoubleArrayList line = resultMatrix.get(indiceLine);
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

	private int reduceNumberOfSwaps(int totalMaxSwap) {
		int nbrOfTrials = resultMatrix.size();
		for (int i = 0; i < nbrOfTrials; i++) {
			TDoubleArrayList line = resultMatrix.get(i);
			if (line.get(line.size()-1)>=1.0) {
				totalMaxSwap -= line.size() - 1;
			}
		} 	
		
		return totalMaxSwap;
	}

	private void ameliorateSim(Random rand, int indiceLine, TDoubleArrayList line, int indiceColumn, double chosenSim) {
		double amelioratedSim = chosenSim + (1-chosenSim) * rand.nextDouble();
		line.set(indiceColumn, amelioratedSim);
		resultMatrix.put(indiceLine, line);
	}
	
	public void setResultMatrix(TIntObjectMap<TDoubleArrayList> resultMatrix2) {
		this.resultMatrix = resultMatrix2;
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
