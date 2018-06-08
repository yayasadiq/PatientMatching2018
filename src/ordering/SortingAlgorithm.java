package ordering;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

public class SortingAlgorithm {
	private TIntArrayList trialPatientsId;
	private TDoubleArrayList trialHigherSim;
	
	public void quickAscendentSort( int left, int right, int indexPivot) {
		if (left < right) {
			int leftPointer = left;
			int rightPointer = right;
			double pivot = trialHigherSim.get(indexPivot);
			while(true) {
				while(trialHigherSim.get(leftPointer) < pivot)
					leftPointer ++;
				while(trialHigherSim.get(rightPointer) > pivot && rightPointer > 0)
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
	private void swap(int leftPointer, int rightPointer) {
		 int tempId = trialPatientsId.get(leftPointer);
		 trialPatientsId.set(leftPointer, trialPatientsId.get(rightPointer));
		 trialPatientsId.set(rightPointer, tempId);
		 double tempSim = trialHigherSim.get(leftPointer);
		 trialHigherSim.set(leftPointer, trialHigherSim.get(rightPointer));
		 trialHigherSim.set(rightPointer, tempSim);
	}
}
