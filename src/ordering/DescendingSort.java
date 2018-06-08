package ordering;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import simulatedSimilarities.OptimizationConnector;

public class DescendingSort implements OrderingMethod{
	private TIntArrayList trialPatientsId;
	private TIntArrayList controlPatientsId;
	private TDoubleArrayList trialHigherSim;
	
	private TIntObjectMap<TIntDoubleHashMap> trialControlAssociation;
	
	public DescendingSort(OptimizationConnector optimizationConnector) {
		this.trialControlAssociation = optimizationConnector.getTrialControlAssociation();
		this.controlPatientsId = optimizationConnector.getControlPatientsId();
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
		this.trialHigherSim = new TDoubleArrayList();
	}
	
	public void runSort() {
		int nbrOfTrials = trialPatientsId.size();
		for (int i = 0; i < nbrOfTrials; i++) {
			int trialId = trialPatientsId.get(i);
			trialHigherSim.add(getMax(trialControlAssociation.get(trialId)));
		}
		quickAscendentSort(0, nbrOfTrials - 2, nbrOfTrials - 1);
	}
	
	private void quickAscendentSort( int left, int right, int indexPivot) {
		if (left < right) {
			int leftPointer = left;
			int rightPointer = right;
			double pivot = trialHigherSim.get(indexPivot);
			while(true) {
				while(trialHigherSim.get(leftPointer) > pivot)
					leftPointer ++;
				while(trialHigherSim.get(rightPointer) < pivot && rightPointer > 0)
					rightPointer --;
				
				if (leftPointer >= rightPointer) {
					break;
				} else {
					swap(leftPointer, rightPointer);
				}
			}
			swap(leftPointer, indexPivot);
			quickAscendentSort(leftPointer, right, indexPivot);
			quickAscendentSort(left, leftPointer - 2, leftPointer - 1);
		}		
	}

	private double getMax(TIntDoubleHashMap tIntDoubleHashMap) {
		int nbrOfControls = controlPatientsId.size();
		double maxValue = 0;
		for (int i = 0; i < nbrOfControls; i++) {
			if(tIntDoubleHashMap.get(controlPatientsId.get(i)) > maxValue)
				maxValue = tIntDoubleHashMap.get(controlPatientsId.get(i));
		}
		return maxValue;
	}

	private void swap(int leftPointer, int rightPointer) {
		 int tempId = trialPatientsId.get(leftPointer);
		 trialPatientsId.set(leftPointer, trialPatientsId.get(rightPointer));
		 trialPatientsId.set(rightPointer, tempId);
		 double tempSim = trialHigherSim.get(leftPointer);
		 trialHigherSim.set(leftPointer, trialHigherSim.get(rightPointer));
		 trialHigherSim.set(rightPointer, tempSim);
	}
	
}
