package optimisation.localSearch;

import java.util.Arrays;
import java.util.Random;

import optimisation.patientmatching.PatientMatchingProblem;
import optimisation.patientmatching.Problem;

public class HillClimber {

	int[] currentSol;
	int[] bestSol;
	double currentFit;
	double bestFit;
	int maxFes;
	int fes;
	Problem problem;
	Random rand;
	
	public HillClimber(int[] startingSol, int nFes, Problem pb) {
		this.rand = new Random();
		
		// Initialise solutions
		this.currentSol = new int[startingSol.length];
		this.bestSol = new int[startingSol.length];
		System.arraycopy(startingSol, 0, this.currentSol, 0, this.currentSol.length);
		System.arraycopy(startingSol, 0, this.bestSol, 0, this.bestSol.length);
		
		// Initialise problem
		this.problem = pb;
		
		// Initialise maximum number of FEs
		this.maxFes = nFes;
		
		// Evaluate and set fitness of initial solution
		double thefitness = this.problem.evaluate(this.currentSol);
		this.bestFit = thefitness;
		this.currentFit = thefitness;
		
		// Increment fitness evaluation counter
		fes = 1;
	}

	public void evolve(){
		PermutationMutation mutation = new SwapMutation(this.rand);
		
		while(fes <= maxFes){

			// Mutate current solution and evaluate
			int[] child = mutation.mutate(this.currentSol);
			double childfit = this.problem.evaluate(child);
			// If fitness of new solution is better than current solution, then set it as current solution
			if(childfit > this.currentFit){
				System.arraycopy(child, 0, this.currentSol, 0, this.currentSol.length);
				System.arraycopy(child, 0, this.bestSol, 0, this.bestSol.length);
				this.currentFit = childfit;
				this.bestFit = childfit;
			}
			
			//System.out.println("fes: "+fes+"\tBestFitness: "+this.bestFit);
			
			// Increment fitness evaluation counter
			fes ++;
		}
	}

	public int[] getBestSolution() {
		return bestSol;
	}

	public double getBestFitness() {
		return bestFit;
	}	
	
	
	
}
