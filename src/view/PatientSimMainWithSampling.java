/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jcolibri.casebase.MyLinealCaseBase;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.exception.ExecutionException;   
import patientMatching.CsvConnector;
import patientMatching.PatientSim;
import optimisation.localSearch.HillClimber;
import optimisation.patientmatching.PatientMatchingProblem;
import optimisation.utils.Utils;

/**
 *
 * @author ss6035
 */
public class PatientSimMainWithSampling {
    
    public static void main(String[] args){
        String cbPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/ControlsModified.csv"; 
        String qPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/Cases.csv";
        String outPath = "/home/gat/Documents/Travail/Stage/Code_and_Data/PatientPairs/PatientMatching/Output.csv";
//        if(args.length<3){
//            System.out.println("USAGE:  java -jar PatientMatching.jar \tcontrols_file \tcases_file \toutput_file");
//            System.exit(1);
//        }else{
//            String cbPath = args[0];
//            String qPath = args[1];
//            String outPath = args[2];
        int trialSampleSize = 100;
    
        PatientSim app = new PatientSim(cbPath, outPath);
        List<String> controls = new ArrayList();
        
        try{
            List<CBRCase> allCases = new ArrayList();
            app.configure();
            MyLinealCaseBase caseBase = (MyLinealCaseBase)app.preCycle();
            List<CBRCase> cases = (List)caseBase.getCases();
            allCases.addAll(cases);
            
            Map<String, CBRCase> casesMap = new HashMap();
            for(CBRCase c:cases){
                casesMap.put((String)c.getID(), c);
            }

            CsvConnector conn = new CsvConnector(qPath);
            List<CBRCase> qCases = new ArrayList();
            allCases.addAll((List)conn.retrieveAllCases());   
            System.out.println("All cases: "+allCases.size());
            
            Collections.shuffle(allCases);                
            qCases.addAll(allCases.subList(0, trialSampleSize));
            System.out.println("trial sample size: "+qCases.size());
            allCases.removeAll(qCases);
            System.out.println("All cases: "+allCases.size());
            caseBase.forgetCases();
            caseBase.learnCases(allCases);
            
            //TODO à supprimer peut-être
            app.setCasesMap((List<CBRCase>) caseBase.getCases());
            
            Map<String, CBRCase> queriesMap = new HashMap();
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
			System.out.println("Initial Solution Fitness: "+problem.evaluate(startingSolution));
			
			int numberOfFitnessEvaluations = 1000;
			HillClimber localsearch = new HillClimber(startingSolution, numberOfFitnessEvaluations, problem);
			localsearch.evolve();
			
			System.out.println("Best Solution after Local Search: "+Utils.tableToString(localsearch.getBestSolution(),","));
			System.out.println("Best Fitness: "+localsearch.getBestFitness());

        }catch(ExecutionException e){
            System.err.println(e.getMessage());
        }
//        }
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
