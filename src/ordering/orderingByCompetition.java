package ordering;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import simulatedSimilarities.OptimizationConnector;
import utils.timer.TimeMeasurer;

public class orderingByCompetition implements OrderingMethod {
	private TIntArrayList trialPatientsId;
	private TIntArrayList controlPatientsId;
	private TIntArrayList trialHigherSim;
	
	private TIntObjectMap<TIntDoubleHashMap> trialControlAssociation;
	
	public orderingByCompetition(OptimizationConnector optimizationConnector) {
		this.trialControlAssociation = optimizationConnector.getTrialControlAssociation();
		this.controlPatientsId = optimizationConnector.getControlPatientsId();
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
		this.trialHigherSim = new TIntArrayList();
	}

	@Override
	public void runSort() {
		TimeMeasurer timeMeasurer = new TimeMeasurer();
		timeMeasurer.startTimer("Sort");
		int nbrOfTrials = trialPatientsId.size();
		for (int i = 0; i < nbrOfTrials; i++) {
			int trialId = trialPatientsId.get(i);
			trialHigherSim.add(getMaxId(trialControlAssociation.get(trialId)));
		}
		actualise(nbrOfTrials);
		coutingSort(trialHigherSim.max(), nbrOfTrials);
		timeMeasurer.stopTimer();
	}
	
	private void swap(int leftPointer, int rightPointer) {
		 int tempId = trialPatientsId.get(leftPointer);
		 trialPatientsId.set(leftPointer, trialPatientsId.get(rightPointer));
		 trialPatientsId.set(rightPointer, tempId);
		 int tempSim = trialHigherSim.get(leftPointer);
		 trialHigherSim.set(leftPointer, trialHigherSim.get(rightPointer));
		 trialHigherSim.set(rightPointer, tempSim);
	}
	
	private void coutingSort(int max, int nbrOfTrials){
        
        int[] count = new int[max];
        int[] output = new int[nbrOfTrials];

        for (int i=0; i< max; ++i)
            count[i] = 0;
 
        for (int i=0; i< nbrOfTrials; ++i)
            ++count[trialHigherSim.get(i)];
 

        for (int i=1; i<= max - 1; ++i)
            count[i] += count[i-1];
 
        for (int i = 0; i< nbrOfTrials; ++i) {
            output[count[trialHigherSim.get(i)]-1] = trialPatientsId.get(i);
            --count[trialHigherSim.get(i)];
        }
        
        for (int i = nbrOfTrials - 1; i > 0; i--)
        	trialPatientsId.set(i, output[i]);
 
    }
	
	private void actualise(int nbrOfTrials) {
		TIntIntHashMap nbrOfControlAppears = new TIntIntHashMap();
		for (int i = 0; i < nbrOfTrials; i++) {
			int controlId = trialHigherSim.get(i);
			nbrOfControlAppears.put(controlId, nbrOfControlAppears.get(controlId)+1);
		}
		for (int i = 0; i < nbrOfTrials; i++) {
			trialHigherSim.set(i, nbrOfControlAppears.get(trialHigherSim.get(i)));
		}
		
	}

	private int getMaxId(TIntDoubleHashMap tIntDoubleHashMap) {
		int nbrOfControls = controlPatientsId.size();
		double maxValue = 0;
		int controlId = 0;
		for (int i = 0; i < nbrOfControls; i++) {
			if(tIntDoubleHashMap.get(controlPatientsId.get(i)) > maxValue) {
				controlId = controlPatientsId.get(i);
				maxValue = tIntDoubleHashMap.get(controlId);				
			}
		}
		return controlId;
	}

}
