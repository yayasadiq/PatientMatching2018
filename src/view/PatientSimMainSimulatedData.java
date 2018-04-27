package view;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.exception.ExecutionException;
import optimisation.algorithm.HillClimber;
import optimisation.patientmatching.OptimizationProblem;
import optimisation.patientmatching.PatientMatchingProblem;
import optimisation.patientmatching.Problem;
import optimisation.utils.Generator;
import optimisation.utils.PatientGenerator;
import optimisation.utils.TimeMeasurer;
import optimisation.utils.Utils;
import patientMatching.CsvConnector;
import patientMatching.PatientSim;
import simulatedSimilarities.OptimizationApp;

public class PatientSimMainSimulatedData {
	
	private static final int NBR_TRIAL_PATIENT = 1000;
	private static final int NUMBER_CONTROL_PATIENT = 1500;
	private static final int NUMBER_TRIAL_PATIENT = 70;

	public static void main(String[] args) {

		String parentRep = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/";
		String cbPath = parentRep + "Controls.csv"; 
		String qPath = parentRep + "Cases.csv";
		String outPath = parentRep + "Output.csv";
		String simulatedSim = parentRep + "SimulatedSim.csv";
		String outSim = parentRep + "outSim.csv";
		
		OptimizationApp app = new OptimizationApp(outSim, simulatedSim);
		TimeMeasurer timeMeasurer = new TimeMeasurer();
		timeMeasurer.startTimer("Total time");
		
		timeMeasurer.startTimer("Configure");
		try {
			app.configure();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		timeMeasurer.stopTimer();
		
		timeMeasurer.startTimer("Cycle");
		app.cycle();
		timeMeasurer.stopTimer();
		Problem problem = new OptimizationProblem(app);
		int[] startingSol = new int[app.getNumberSolutions()];
		int solLength = startingSol.length;
		for (int i = 0; i < solLength; i++) {
			//put in the solution the position of the controlPatientsId in app
			startingSol[i] = i;
		}
		System.out.println("Initial Solution: "+Utils.tableToString(startingSol,","));
		double evaluateSolution = problem.evaluate(startingSol);
		System.out.println("Initial Solution Fitness: "+ evaluateSolution);
		
		timeMeasurer.startTimer("IncreaseDiff");
		app.increaseDiff(1000000);
		timeMeasurer.stopTimer();
		
		timeMeasurer.startTimer("Merge Matrix");
		app.mergeMatrixAndData();
		timeMeasurer.stopTimer();

//		timeMeasurer.startTimer("display Matrix");
//		app.displayMatrix();
//		timeMeasurer.stopTimer();
		
		timeMeasurer.startTimer("Local Search");
		
		
		int nFes = 100000;
		
		HillClimber localsearch = new HillClimber(startingSol , nFes , problem);
		localsearch.evolve();
		
		System.out.println("\nBest Solution after Local Search: "+Utils.tableToString(localsearch.getBestSolution(),","));
		double localSearchSolution = localsearch.getBestFitness();
		System.out.println("Best Fitness: "+localSearchSolution);
		timeMeasurer.stopTimer();
		timeMeasurer.stopTimer();
		timeMeasurer.displayTimes();
	}
}
