/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import optimisation.algorithm.HillClimber;

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
import jcolibri.exception.ExecutionException;   
import patientMatching.CsvConnector;
import patientMatching.PatientSim;
import optimisation.patientmatching.PatientMatchingProblem;
import optimisation.patientmatching.Problem;
import optimisation.utils.CSVWriter;
import optimisation.utils.Generator;
import optimisation.utils.PatientGenerator;
import optimisation.utils.TimeMeasurer;
import optimisation.utils.Utils;

/**
 *
 * @author ss6035
 */
public class PatientSimMain {
    
    private static final int NUMBER_TRIAL_PATIENT = 70;
	private static final int NUMBER_CONTROL_PATIENT = 33000;

	public static void main(String[] args) {
		String dirPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/";
    	String cbPath = dirPath + "Controls.csv"; 
        String qPath = dirPath + "Cases.csv";
        String outPath = dirPath + "Output.csv";
        String outSim = dirPath + "outSim.csv";
        PatientSim app = new PatientSim(cbPath, outPath);
        List<String> controls = new ArrayList();
        
        
		try{
            app.configure();
            CBRCaseBase caseBase = app.preCycle();
            Collection<CBRCase> cases = caseBase.getCases();
            
            
            Map<String, CBRCase> casesMap = new HashMap();
            for(CBRCase c:cases){
                casesMap.put((String)c.getID(), c);
            }

            CsvConnector conn = new CsvConnector(qPath);
            List<CBRCase> qCases = (List)conn.retrieveAllCases();
            Map<String, CBRCase> queriesMap = new HashMap<String, CBRCase>();
            for(CBRCase c:qCases){
                queriesMap.put((String)c.getID(), c);
            }
            app.setqCases(qCases);
            for(CBRCase c:qCases){
                app.cycle(c);
            }
        
            double total_sim = app.getTotal_sim();
            double ave_sim = total_sim/qCases.size();
            System.out.println("\nAve Sim: "+ave_sim);
            app.postCycle();
            
            controls = app.getRetrievedCases();
            int numberOfTrialPatients = qCases.size();	
			int[] startingSolution = new int[numberOfTrialPatients];
			for(int i=0; i<numberOfTrialPatients; i++){ // Currently set with dummy indices for testing purposes
				startingSolution[i] = Integer.parseInt(controls.get(i));
			}
			
			Problem problem = new PatientMatchingProblem(app);
			
			System.out.println("Initial Solution: "+Utils.tableToString(startingSolution,","));
			double evaluateSolution = problem.evaluate(startingSolution);
			System.out.println("Initial Solution Fitness: "+ evaluateSolution);
			
	
            int numberOfFitnessEvaluations = 1000;
			HillClimber localsearch = new HillClimber(startingSolution, numberOfFitnessEvaluations, problem);
			localsearch.evolve();
			
			System.out.println("Best Solution after Local Search: "+Utils.tableToString(localsearch.getBestSolution(),","));
			double localSearchSolution = localsearch.getBestFitness();
			System.out.println("Best Fitness: "+localSearchSolution);
			
			
        }catch(ExecutionException e){
            System.err.println(e.getMessage());
        }

//        }
		double total_sim = app.getTotal_sim();
        System.out.println("\nSimilarities sum : " + total_sim);
        double best_total_sim = app.getBest_total_sim();
		System.out.println("Best Similarities sum : " + best_total_sim);
		System.out.println("Difference : " + String.valueOf(best_total_sim - total_sim));

    }

	private static void writeResults(String statPath, double evaluateSolution, double localSearchSolution) {
		CSVWriter csvWriter = new CSVWriter(statPath);
		StringBuilder sb = computeAndDisplayARecapOfTheTests(statPath, evaluateSolution, localSearchSolution);		
		
		try {
			csvWriter.addLignesToFile(sb);
		} catch (IOException e) {
			System.err.println("Can't write the folder\n" + e.getMessage());
		}
	}

	private static void generatePatients(String cbPath, String qPath) {
		try {
        	Generator generator = new PatientGenerator(cbPath);
        	generator.makeData(NUMBER_CONTROL_PATIENT);
        	generator.setFilepath(qPath);
        	generator.makeData(NUMBER_TRIAL_PATIENT);        	
        } catch (FileNotFoundException e) {
        	System.err.println(e.getMessage() + '\n');
        }
	}

	private static StringBuilder createRecapLigne(double evaluateSolution, double localSearchSolution, double averageDif,
			double currentDif) {
		StringBuilder sb = new StringBuilder();
		sb.append("Starting solution :");
		sb.append(',');
		sb.append(evaluateSolution);
		sb.append(',');
		sb.append("Local search solution :");
		sb.append(',');
		sb.append(localSearchSolution);
		sb.append(',');
		sb.append("Difference :");
		sb.append(',');
		sb.append(currentDif);
		sb.append(',');
		sb.append("Average difference :");
		sb.append(',');
		sb.append(averageDif);
		sb.append(',');
		sb.append("Number of control patients");
		sb.append(',');
		sb.append(NUMBER_CONTROL_PATIENT);
		sb.append('\n');
		return sb;
	}
	
	public static StringBuilder computeAndDisplayARecapOfTheTests(String statPath, double evaluateSolution, double localSearchSolution) {
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
		return createRecapLigne(evaluateSolution, localSearchSolution, averageDif, currentDif);
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
