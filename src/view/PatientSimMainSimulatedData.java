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
	
	
	private static int NUMBER_CONTROL_PATIENT =30000;
	static int NUMBER_TRIAL_PATIENT = 70;

	public static void main(String[] args) {
		int NBR_SWAP_REQUIRED = 1500;
		String parentRep = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/";
		String pathResultMatrix = parentRep + "SimulatedSim.csv";
		String outSimGenerated = parentRep + "outSimGenerated.csv";
		String inPathOriginal = parentRep + "outSim.csv";
		//generateFile(outSimGenerated);
		String inSim = inPathOriginal;
		
		System.out.println("\nMethod | Max similarities sum | Similarities Sum | Number of swaps | Time");
		
		
		OptimizationConnector optimizationConnector = new OptimizationConnector(inSim, pathResultMatrix);
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
		displayAndWriteResult("Initial method", timeMeasurer, similarityManager);
		System.out.println("\nInitial max similarities sum : " + similarityManager.getSimSum());
		timeMeasurer.startTimer("IncreaseDiff");
		similarityManager.increaseDiffAuto(NBR_SWAP_REQUIRED);
		app.writeMatrix();
		timeMeasurer.stopTimer();
		System.out.println("Current max similarities sum : " + similarityManager.getMaxSimSum() + "\n");
		timeMeasurer.startTimer("Local Search");
		int nFes = 100000;
		
		HillClimber localsearch = new HillClimber(nFes , app);
		localsearch.evolve();
		
		List<List<Double>> resultMatrix = app.makeMatrixWithIndex(localsearch.getBestSolution());
		similarityManager.setResultMatrix(resultMatrix);
		similarityManager.computeSimilaritiesSum();
		timeMeasurer.stopTimer();
		displayAndWriteResult("Local Search",timeMeasurer, similarityManager);
		timeMeasurer.startTimer("Full Search");
		FullSearch fullSearch = new FullSearch(app);
		fullSearch.optimize();
		resultMatrix = app.getResultMatrix();
		similarityManager.setResultMatrix(resultMatrix);
		similarityManager.computeSimilaritiesSum();
		timeMeasurer.stopTimer();
		displayAndWriteResult("Full Search", timeMeasurer, similarityManager);		

		timeMeasurer.stopTimer();
		timeMeasurer.displayTimes();

		
	}

	private static void generateFile(String outSimGenerated) {
		SimilarityGenerator similarityGenerator = new SimilarityGenerator(outSimGenerated);
		try {
			similarityGenerator.makeSquareMatrix(NUMBER_TRIAL_PATIENT, NUMBER_CONTROL_PATIENT);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	private static void displayAndWriteResult(String methodName, TimeMeasurer timeMeasurer, SimilarityManager simManager) {
		System.out.println(methodName + " | " + simManager.getMaxSimSum() + " | " + simManager.getSimSum() + " | " + simManager.getNbrOfSwaps() + " | " + timeMeasurer.getLastTime());
	}
	

}
