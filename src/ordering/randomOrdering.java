package ordering;

import java.util.Random;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import simulatedSimilarities.OptimizationConnector;

public class randomOrdering implements OrderingMethod{
	private TIntArrayList trialPatientsId;
	private final static int SHUFFLING_NUMBER = 2000;
	
	public randomOrdering(OptimizationConnector optimizationConnector) {
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
	}

	@Override
	public void runSort() {
		Random random = new Random();
		int nbrOfTrial = trialPatientsId.size();
		for (int i = 0; i < SHUFFLING_NUMBER; i++) {
			int firstIndex = random.nextInt(nbrOfTrial);
			int secondIndex = random.nextInt(nbrOfTrial);
			swap(firstIndex, secondIndex);
		}
	}
	
	private void swap(int leftPointer, int rightPointer) {
		 int tempId = trialPatientsId.get(leftPointer);
		 trialPatientsId.set(leftPointer, trialPatientsId.get(rightPointer));
		 trialPatientsId.set(rightPointer, tempId);
	}

}
