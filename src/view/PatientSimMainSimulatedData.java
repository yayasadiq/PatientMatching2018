package view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import optimisation.fullSearch.FullSearch;
import optimisation.localSearch.HillClimber;
import simulatedSimilarities.OptimizationApp;
import simulatedSimilarities.OptimizationConnector;
import simulatedSimilarities.SimilarityManager;
import utils.IOhelpers.CSVWriter;
import utils.generator.SimilarityGenerator;
import utils.timer.TimeMeasurer;

public class PatientSimMainSimulatedData {
	
	
	private final static int NUMBER_CONTROL_PATIENT = 10000;
	private static int NUMBER_TRIAL_PATIENT = 70;
	private final static int NBR_SWAP_REQUIRED = 50;

	public static void main(String[] args) {
		String parentRep = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/";
		String pathResultMatrix = parentRep + "SimulatedSim.csv";
		String outSimGenerated = parentRep + "outSimGenerated.csv";
		String inPathOriginal = parentRep + "outSim.csv";
					
		SimilarityManager similarityManager;
		//generateFile(outSimGenerated);
		String inSim = inPathOriginal;
		
		System.out.println("\nMethod | Max similarities sum | Similarities Sum | Number of swaps | Time");

		OptimizationConnector optimizationConnector = new OptimizationConnector(inSim, pathResultMatrix);
		TimeMeasurer timeMeasurer = new TimeMeasurer();
		timeMeasurer.startTimer("Total time");
		
		runConfigure(optimizationConnector, timeMeasurer);
		
		OptimizationApp app = new OptimizationApp(optimizationConnector);
		
		similarityManager = runCycle(timeMeasurer, app);
		displayAndWriteResult("Initial method", timeMeasurer, similarityManager);
		
		runLocalSearch(similarityManager, timeMeasurer, app);
		displayAndWriteResult("Local Search",timeMeasurer, similarityManager);
		
		runFullSearch(timeMeasurer, app, similarityManager);
		displayAndWriteResult("Full Search", timeMeasurer, similarityManager);
		
		
		timeMeasurer.stopTimer();
		timeMeasurer.displayTimes();	
		
	}

	private static void runConfigure(OptimizationConnector optimizationConnector, TimeMeasurer timeMeasurer) {
		timeMeasurer.startTimer("Configure");
		try {
			optimizationConnector.configure();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		timeMeasurer.stopTimer();
	}

	private static SimilarityManager runCycle(TimeMeasurer timeMeasurer, OptimizationApp app) {
		SimilarityManager similarityManager;
		timeMeasurer.startTimer("Cycle");
		app.cycle();
		timeMeasurer.stopTimer();
		similarityManager = new SimilarityManager(app);
		return similarityManager;
	}

	private static void runIncreaseDif(int NBR_SWAP_REQUIRED, SimilarityManager similarityManager,
			TimeMeasurer timeMeasurer, OptimizationApp app) {
		timeMeasurer.startTimer("IncreaseDiff");
		similarityManager.increaseDiffAuto(NBR_SWAP_REQUIRED);
		app.writeMatrix();
		timeMeasurer.stopTimer();
	}

	private static void runLocalSearch(SimilarityManager similarityManager, TimeMeasurer timeMeasurer,
			OptimizationApp app) {
		List<List<Double>> resultMatrix;
		timeMeasurer.startTimer("Local Search");
		int nFes = 100000;
		
		HillClimber localsearch = new HillClimber(nFes , app);
		localsearch.evolve();
		resultMatrix = app.makeMatrixWithIndex(localsearch.getBestSolution());
		similarityManager.setResultMatrix(resultMatrix);
		similarityManager.computeSimilaritiesSum();
		timeMeasurer.stopTimer();
	}

	private static void runFullSearch(TimeMeasurer timeMeasurer, OptimizationApp app,
			SimilarityManager similarityManager) {
		List<List<Double>> resultMatrix;
		timeMeasurer.startTimer("Full Search");
		FullSearch fullSearch = new FullSearch(app);
		fullSearch.optimize();
		resultMatrix = app.getResultMatrix();
		similarityManager.setResultMatrix(resultMatrix);
		similarityManager.computeSimilaritiesSum();
		timeMeasurer.stopTimer();
	}

	private static void generateFile(String outSimGenerated) {
		SimilarityGenerator similarityGenerator = new SimilarityGenerator(outSimGenerated);
		try {
			similarityGenerator.makeRectangularMatrix(NUMBER_TRIAL_PATIENT, NUMBER_CONTROL_PATIENT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void displayAndWriteResult(String methodName, TimeMeasurer timeMeasurer, SimilarityManager simManager) {
		System.out.println(methodName + " | " + simManager.getMaxSimSum() + " | " + simManager.getSimSum() + " | " + simManager.getNbrOfSwaps() + " | " + timeMeasurer.getLastTime());
	}
	

}
