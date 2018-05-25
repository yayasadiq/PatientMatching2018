package optimisation.localSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import simulatedSimilarities.OptimizationApp;

public class HillClimber {

	List<Integer> currentSol = new ArrayList<>();
	int maxFes;
	int fes;
	OptimizationApp app;
	Random rand;
	
	public HillClimber(int nFes, OptimizationApp app) {
		this.rand = new Random();
		
		// Initialise solutions
		int nbrOfSolution = app.getNumberSolutions();
		for (int i = 0; i < nbrOfSolution; i++) 
			this.currentSol.add(i);
		
		// Initialise problem
		this.app = app;
		
		// Initialise maximum number of FEs
		this.maxFes = nFes;
		
		// Evaluate and set fitness of initial solution


		fes = 1;
	}
	
	public void evolve(){
		
		int NbrOfControl = currentSol.size() - 1;
		while(fes <= maxFes){
			int i = rand.nextInt(NbrOfControl) + 1;
			int j = rand.nextInt(i);
			
			double sol = app.evaluate(i, j, currentSol.get(i), currentSol.get(j));
			if (sol > 0) {
				swapColumns(i, j);
			}

			fes ++;
		}

	}

	private void swapColumns(int i, int j) {
		int temp = this.currentSol.get(i);
		this.currentSol.set(i, this.currentSol.get(j));
		this.currentSol.set(j, temp);
	}

	public List<Integer> getBestSolution() {
		return currentSol;
	}


	
	
	
}
