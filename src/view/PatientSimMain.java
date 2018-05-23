/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.Connector;
import jcolibri.exception.ExecutionException;   
import model.CsvConnector;
import model.MockPatientConnector;
import patientMatching.PatientSim;
import utils.IOhelpers.CSVWriter;
import utils.generator.Generator;
import utils.generator.MockPatientGenerator;
import utils.generator.PatientGenerator;
import utils.timer.TimeMeasurer;
import optimisation.localSearch.HillClimber;
import optimisation.patientmatching.PatientMatchingProblem;
import optimisation.patientmatching.Problem;
import optimisation.utils.Utils;

/**
 *
 * @author ss6035
 */
public class PatientSimMain {
    
    private static final int NUMBER_TRIAL_PATIENT = 500;
	private static int NUMBER_CONTROL_PATIENT = 1000;
	private static final String dirPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/";
	private static final String cbPath = dirPath + "ControlsModified.csv"; 
	private static final String qPath = dirPath + "CasesModified.csv";
	private static final String outPath = dirPath + "Output.csv";
	private static final String outSim = dirPath + "outSim.csv";
	private static final String statPath = dirPath + "statistics/GeneratedDataStatistics.csv";
	private static TimeMeasurer timeMeasurer = new TimeMeasurer();

	public static void main(String[] args) {
		
		while (NUMBER_CONTROL_PATIENT < 12000) {
			CSVWriter csvWriter = new CSVWriter(outSim);
			timeMeasurer.startTimer("Total :");
			generatePatients();
	        PatientSim app = new PatientSim(cbPath, outPath);
	        List<String> controls = new ArrayList<>();
	        
			try{
	            app.configure();
	            CBRCaseBase caseBase = app.preCycle();
	            Collection<CBRCase> cases = caseBase.getCases();
	            Map<String, CBRCase> casesMap = new HashMap();
	            for(CBRCase c:cases){
	                casesMap.put((String)c.getID(), c);
	            }
	            Connector conn = new MockPatientConnector(qPath);
	            List<CBRCase> qCases = (List)conn.retrieveAllCases();
	            Map<String, CBRCase> queriesMap = new HashMap();
	            for(CBRCase c:qCases){
	                queriesMap.put((String)c.getID(), c);
	            }
	           
	            try {
	            	app.writeControls(csvWriter);
	            	csvWriter.createCSVWithContent();
	            	app.setqCases(qCases);
	                for(CBRCase c:qCases){
	                    app.writeSimilarities(c, csvWriter);
	                    System.out.println("Done !");
	                }
	            	csvWriter.saveData();
	            } catch (IOException e) {
					e.printStackTrace();
				}
	            app.postCycle();
	            csvWriter = new CSVWriter(statPath);
	            Tests.main(args, NUMBER_CONTROL_PATIENT, NUMBER_TRIAL_PATIENT, csvWriter);
				timeMeasurer.stopTimer();
				csvWriter.writeCell(timeMeasurer.getLastTime());
				csvWriter.newLine();
				timeMeasurer.displayTimes();
				try {
					csvWriter.saveData();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }catch(ExecutionException e){
	            System.err.println(e.getMessage());
	        }
			NUMBER_CONTROL_PATIENT += 1000;
		}

    }

	

	private static void generatePatients() {
		try {
        	Generator generator = new MockPatientGenerator(cbPath);
        	generator.makeData(NUMBER_CONTROL_PATIENT);
        	generator.setFilepath(qPath);
        	generator.makeData(NUMBER_TRIAL_PATIENT);        	
        } catch (IOException e) {
        	System.err.println(e.getMessage() + '\n');
        }
	}

	private static void createRecapLigne(double evaluateSolution, double localSearchSolution, double averageDif,
			double currentDif) {
		CSVWriter csvWriter = new CSVWriter(outSim);
		csvWriter.writeCell("Starting solution :");
		csvWriter.writeCell(String.valueOf(evaluateSolution));
		csvWriter.writeCell("Local search solution :");
		csvWriter.writeCell(String.valueOf(localSearchSolution));
		csvWriter.writeCell("Difference :");
		csvWriter.writeCell(String.valueOf(currentDif));
		csvWriter.writeCell("Average difference :");
		csvWriter.writeCell(String.valueOf(averageDif));
		csvWriter.writeCell("Number of control patients");
		csvWriter.writeCell(String.valueOf(NUMBER_CONTROL_PATIENT));
		csvWriter.newLine();
		try {
			csvWriter.saveData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void computeAndDisplayARecapOfTheTests(String statPath, double evaluateSolution, double localSearchSolution) {
		CsvConnector connStat = new CsvConnector(statPath);
		List<String[]> datas = new ArrayList<>();
		try {				
			datas = connStat.parse();
			
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage() + '\n');
		}

		int nbrTests = datas.size() + 1; 
		double averageDif = 0.0;
		double averageEvalSol = 0.0;
		double averageLocalSol = 0.0;
		//add current values to array and make the averages
		String[] stringArray = {null, String.valueOf(evaluateSolution), null, String.valueOf(localSearchSolution)};
		datas.add(stringArray);
		for (String[] strings : datas) {
			double evalSol = Double.parseDouble(strings[1]);
			double localSol = Double.parseDouble(strings[3]);
			averageDif = averageDif + Math.abs(evalSol - localSol);
			averageEvalSol = averageEvalSol + evalSol;
			averageLocalSol = averageLocalSol + localSol;					
		}
		double currentDif = Math.abs(evaluateSolution - localSearchSolution);
		
		averageDif = averageDif / nbrTests;
		averageEvalSol = averageEvalSol / nbrTests;
		averageLocalSol = averageLocalSol / nbrTests;
		
		System.out.println("Current difference : " + currentDif);
		System.out.println("Number of tests done : " + nbrTests);
		System.out.println("Average of the difference : " + averageDif);
		System.out.println("Average of the starting solution : " + averageEvalSol);
		System.out.println("Average of the local search : " + averageLocalSol);
		createRecapLigne(evaluateSolution, localSearchSolution, averageDif, currentDif);
	}
	
    
//    public static void optimise(){
//        int numberOfTrialPatients = 40;	
//		int[] startingSolution = new int[numberOfTrialPatients];
//		for(int i=0; i<numberOfTrialPatients; i++){ // Currently set with dummy indices for testing purposes
//			startingSolution[i] = i;
//		}
//		
//		PatientMatchingProblem problem = new PatientMatchingProblem(app);
//		
//		System.out.println("Initial Solution: "+Utils.tableToString(startingSolution,","));
//		System.out.println("Initial Solution Fitness: "+problem.evaluate(startingSolution));
//		
//		int numberOfFitnessEvaluations = 1000;
//		HillClimber localsearch = new HillClimber(startingSolution, numberOfFitnessEvaluations, problem);
//		localsearch.evolve();
//		
//		System.out.println("Best Solution after Local Search: "+Utils.tableToString(localsearch.getBestSolution(),","));
//		System.out.println("Best Fitness: "+localsearch.getBestFitness());
//    }
    
}
