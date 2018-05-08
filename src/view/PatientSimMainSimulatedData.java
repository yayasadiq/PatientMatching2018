package view;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.MaskFormatter;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.exception.ExecutionException;
import optimisation.fullSearch.FullSearch;
import optimisation.localSearch.HillClimber;
import optimisation.patientmatching.SimilaritiesProblem;
import optimisation.patientmatching.PatientMatchingProblem;
import optimisation.patientmatching.Problem;
import optimisation.utils.Utils;
import patientMatching.CsvConnector;
import patientMatching.PatientSim;
import simulatedSimilarities.OptimizationApp;
import simulatedSimilarities.OptimizationConnector;
import simulatedSimilarities.SimilarityManager;
import utils.IOhelpers.InputManager;
import utils.generator.Generator;
import utils.generator.PatientGenerator;
import utils.timer.TimeMeasurer;

public class PatientSimMainSimulatedData {
	
	private static final int NUMBER_CONTROL_PATIENT = 1500;
	private static final int NUMBER_TRIAL_PATIENT = 70;

	public static void main(String[] args) {

		String parentRep = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/";
		String simulatedSim = parentRep + "SimulatedSim.csv";
		String outSim = parentRep + "outSim.csv";
		
		System.out.println("Method | Max similarities sum | Similarities Sum | Number of swaps | Time");
		
		
		OptimizationConnector optimizationConnector = new OptimizationConnector(outSim, simulatedSim);
		TimeMeasurer timeMeasurer = new TimeMeasurer();
		timeMeasurer.startTimer("Total time");
		
		timeMeasurer.startTimer("Configure");
		try {
			optimizationConnector.configure();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		timeMeasurer.stopTimer();
		OptimizationApp app = new OptimizationApp(optimizationConnector);
		
		timeMeasurer.startTimer("Cycle");
		app.cycle();
		timeMeasurer.stopTimer();
		
		SimilarityManager similarityManager = new SimilarityManager(app);
		Problem problem = new SimilaritiesProblem(app);
		int[] startingSol = new int[app.getNumberSolutions()];
		int solLength = startingSol.length;
		for (int i = 0; i < solLength; i++) {
			startingSol[i] = i;
		}
		displayResult("Initial method", timeMeasurer, similarityManager);
		timeMeasurer.startTimer("IncreaseDiff");
		similarityManager.increaseDiffAuto(1000);
		timeMeasurer.stopTimer();
		
		timeMeasurer.startTimer("Local Search");
		int nFes = 100000;
		
		HillClimber localsearch = new HillClimber(startingSol , nFes , problem);
		localsearch.evolve();
		
		List<List<Double>> resultMatrix = app.makeMatrix(localsearch.getBestSolution());
		similarityManager.setResultMatrix(resultMatrix);
		similarityManager.computeSimilaritiesSum();
		timeMeasurer.stopTimer();
		displayResult("Local Search",timeMeasurer, similarityManager);
		
		timeMeasurer.startTimer("Full Search");
		FullSearch fullSearch = new FullSearch(app);
		fullSearch.optimize();
		similarityManager.computeSimilaritiesSum();
		timeMeasurer.stopTimer();
		displayResult("Full Search", timeMeasurer, similarityManager);		

		timeMeasurer.stopTimer();
		timeMeasurer.displayTimes();
	}

	private static void displayResult(String methodName, TimeMeasurer timeMeasurer, SimilarityManager simManager) {
		System.out.println(methodName + " | " + simManager.getMaxSimSum() + " | " + simManager.getSimSum() + " | " + simManager.getNbrOfSwaps() + " | " + timeMeasurer.getLastTime());
	}
	
	

}
