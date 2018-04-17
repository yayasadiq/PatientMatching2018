/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import optimisation.algorithm.HillClimber;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import optimisation.utils.CSVWriter;
import optimisation.utils.Utils;

/**
 *
 * @author ss6035
 */
public class PatientSimMain {
    
    private static final int NUMBER_TRIAL_PATIENT = 70;
	private static final int NUMBER_CONTROL_PATIENT = 500;

	public static void main(String[] args) {
    	String cbPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/ControlsUpdated.csv"; 
        String qPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/CasesUpdated.csv";
        String outPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/Output.csv";
        String statPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/Statistic_var.csv";
//        if(args.length<3){
//            System.out.println("USAGE:  java -jar PatientMatching.jar \tcontrols_file \tcases_file \toutput_file");
//            System.exit(1);
//        }else{
//            String cbPath = args[0];
//            String qPath = args[1];
//            String outPath = args[2];        
    	
        try {
        	CSVWriter csvMaker = new CSVWriter(cbPath);
        	csvMaker.makeDatas(NUMBER_CONTROL_PATIENT);
        	csvMaker.setFilepath(qPath);
        	csvMaker.makeDatas(NUMBER_TRIAL_PATIENT);        	
        } catch (FileNotFoundException e) {
        	System.err.println(e.getMessage() + '\n');
        }
    	
        
        
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
            
            double ave_sim = app.getTotal_sim()/qCases.size();
            System.out.println("\nAve Sim: "+ave_sim);
            app.postCycle();
            
            controls = app.getRetrievedCases();
            
            int numberOfTrialPatients = qCases.size();	
			int[] startingSolution = new int[numberOfTrialPatients];
			for(int i=0; i<numberOfTrialPatients; i++){ // Currently set with dummy indices for testing purposes
				startingSolution[i] = Integer.parseInt(controls.get(i));
			}
			
			PatientMatchingProblem problem = new PatientMatchingProblem(app);
			
			System.out.println("Initial Solution: "+Utils.tableToString(startingSolution,","));
			double evaluateSolution = problem.evaluate(startingSolution);
			System.out.println("Initial Solution Fitness: "+ evaluateSolution);
			
			int numberOfFitnessEvaluations = 1000;
			HillClimber localsearch = new HillClimber(startingSolution, numberOfFitnessEvaluations, problem);
			localsearch.evolve();
			
			System.out.println("Best Solution after Local Search: "+Utils.tableToString(localsearch.getBestSolution(),","));
			double localSearchSolution = localsearch.getBestFitness();
			System.out.println("Best Fitness: "+localSearchSolution);
						
			CSVWriter csvWriter = new CSVWriter(statPath);
			StringBuilder sb = computeAndDisplayARecapOfTheTests(statPath, evaluateSolution, localSearchSolution);		
			
			try {
				csvWriter.addLignesToFile(sb);
			} catch (IOException e) {
				System.err.println("Can't write the folder\n" + e.getMessage());
			}
			
			
        }catch(ExecutionException e){
            System.err.println(e.getMessage());
        }
//        }
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
		
		for (String[] strings : datas) {
			double evalSol = Double.parseDouble(strings[1]);
			double localSol = Double.parseDouble(strings[3]);
			averageDif = averageDif + Math.abs(evalSol - localSol);
			averageEvalSol = averageEvalSol + evalSol;
			averageLocalSol = averageLocalSol + localSol;					
		}
		double currentDif = Math.abs(evaluateSolution - localSearchSolution);
		
		averageDif = averageDif + currentDif;
		averageDif = averageDif / nbrTests;
		
		averageEvalSol = averageEvalSol + evaluateSolution;
		averageLocalSol = averageLocalSol + localSearchSolution;
		
		averageEvalSol = averageEvalSol / nbrTests;
		averageLocalSol = averageLocalSol / nbrTests;
		
		System.out.println("Difference : " + currentDif);
		System.out.println("The number of tests done : " + nbrTests);
		System.out.println("Average of the difference : " + averageDif);
		System.out.println("Average of the starting solution : " + averageEvalSol);
		System.out.println("Average of the local search : " + averageLocalSol);
		double[] tab = {averageDif, currentDif}; 
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
