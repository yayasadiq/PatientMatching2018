/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package patientMatching;

import extensions.ApproxEqual;
import extensions.DTW;
import extensions.Cosine;
import extensions.Euclidean;
import extensions.Jaccard;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcolibri.casebase.MyLinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.Connector;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.FilterBasedRetrieval.FilterBasedRetrievalMethod;
import jcolibri.method.retrieve.FilterBasedRetrieval.FilterConfig;
import jcolibri.method.retrieve.FilterBasedRetrieval.predicates.Equal;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
//import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.selection.SelectCases;
import optimisation.utils.CSVWriter;
import patientMatching.CsvConnector;
import patientMatching.PatientDescription;

/**
 *
 * @author ss6035
 */
public class PatientSim implements StandardCBRApplication{
    
    private Connector connector;
    private CBRCaseBase casebase;
    private NNConfig nnConfig;
    private FilterConfig fConfig;
    private String cbPath;   
    private BufferedWriter out;
    private String outPath;
    private List<String> retrievedCases = new ArrayList();
    private double total_sim = 0;
    private List<Double> similarities;
    private Map<Integer,CBRCase> casesMap;
    private List<CBRCase> qCases;
    private double best_total_sim = 0;

    public PatientSim(String cbPath, String outPath) {
        this.cbPath = cbPath; 
        this.outPath = outPath;
    }    

    @Override
    public void configure() throws ExecutionException{
        casesMap = new HashMap();
        connector  = new CsvConnector(cbPath);
        casebase = new MyLinealCaseBase();
        
        fConfig = new FilterConfig();
        fConfig.addPredicate(new Attribute("age", PatientDescription.class), new ApproxEqual());
        
        nnConfig = new NNConfig();
        nnConfig.setDescriptionSimFunction(new Average());
        nnConfig.addMapping(new Attribute("age", PatientDescription.class), new Interval(23));
        nnConfig.addMapping(new Attribute("bts", PatientDescription.class), new DTW());
        nnConfig.addMapping(new Attribute("control", PatientDescription.class), new DTW());
        nnConfig.addMapping(new Attribute("drugVec", PatientDescription.class), new Jaccard());
        
        similarities = new ArrayList();
        
        try{
            out = new BufferedWriter(new FileWriter(new File(outPath)));
            out.write("CaseID, ControlID, Similarity");
            out.newLine();
            System.out.println("CaseID, ControlID, Similarity");
        }catch(IOException e){
            throw new ExecutionException(e);
        }
    }

    @Override
    public CBRCaseBase preCycle() throws ExecutionException {
        casebase.init(connector); 
        Collection<CBRCase> cases = casebase.getCases();  
        for(CBRCase c: cases){
            casesMap.put(Integer.parseInt((String)c.getID()), c);
        }
        return casebase;
    }

    @Override
    public void cycle(CBRQuery cbrq) throws ExecutionException {
        //filter cases that are not same age as query case
        if(!casesMap.containsKey(Integer.parseInt((String)cbrq.getID()))){
            casesMap.put(Integer.parseInt((String)cbrq.getID()), null);        
        }
        //Collection<CBRCase> fCases = FilterBasedRetrievalMethod.filterCases(casebase.getCases(), cbrq, fConfig);
        
        //Collection<RetrievalResult> result = NNScoringMethod.evaluateSimilarity(fCases, cbrq, nnConfig);
        
        Collection<RetrievalResult> result = NNScoringMethod.evaluateSimilarity(casebase.getCases(), cbrq, nnConfig);
        Collection<RetrievalResult> retievedCases = SelectCases.selectTopKRR(result, 20);
        
        DecimalFormat df = new DecimalFormat("0.00"); 
        
        for (RetrievalResult retrievalResult : retievedCases) {
			best_total_sim += retrievalResult.getEval();
			break;
		}
                
        for(RetrievalResult rr:retievedCases){
            if(!retrievedCases.contains((String)rr.get_case().getID())){
                double sim = rr.getEval();
                total_sim += sim;
                similarities.add(sim);
                
                String output = cbrq.getID()+", "+rr.get_case().getID()+", "+df.format(sim);                
                try {
                    out.write(output);
                    out.newLine();
                } catch (IOException e) {
                    throw new ExecutionException(e);
                }
                System.out.println(output);
                retrievedCases.add((String)rr.get_case().getID());
                break;
            }
        }
        
    }

    public List<String> getRetrievedCases() {
        return retrievedCases;
    }
    
    public double[] getSimilarities(int[] controls){
        List<CBRCase> control_cases = new ArrayList();
        double[] similarities = new double[controls.length];
        for (int i=0; i<controls.length; i++){
            int id = controls[i];
            double sim = getSimilarity(casesMap.get(id), qCases.get(i));
            similarities[i] = sim;
        }
        return similarities;
    }    
    
    public double getSimilarity(CBRCase _case, CBRCase query){
        Average ave = new Average();
        return ave.compute(_case.getDescription(), query.getDescription(), _case, query, nnConfig);
    }

    @Override
    public void postCycle() throws ExecutionException {
        try{
            if(out != null){                
                out.close();
            }
        }catch(IOException e){
            throw new ExecutionException(e);
        }
    }

    public double getTotal_sim() {
        return total_sim;
    }    

    public List<CBRCase> getqCases() {
        return qCases;
    }

    public void setqCases(List<CBRCase> qCases) {
        this.qCases = qCases;
    }    
    
    public void setCasesMap(List<CBRCase> list) {
    	this.casesMap.clear();
    	for(CBRCase c: list){
            this.casesMap.put(Integer.parseInt((String)c.getID()), c);
        }
    }

	public double getBest_total_sim() {
		return best_total_sim;
	}

	public void writeSimilarities(CBRCase c, String path) throws IOException {
		CSVWriter csvWriter = new CSVWriter(path);
		Collection<RetrievalResult> result = NNScoringMethod.evaluateSimilarity(casebase.getCases(), c, nnConfig);
        Collection<RetrievalResult> retievedCases = SelectCases.selectAllRR(result);
		StringBuilder sb = new StringBuilder();
		sb.append(c.getID()).append(',');       
        for (RetrievalResult retrievalResult : retievedCases) {
			sb.append(retrievalResult.getEval()).append(',');
		}
        sb.append('\n');
        csvWriter.addLignesToFile(sb);
	}

	public void writeControls(String outSim) {
		CSVWriter csvWriter = new CSVWriter(outSim);
		StringBuilder sb = new StringBuilder();
        sb.append("id trial patient / id control patient").append(',');
        for (Map.Entry entry: casesMap.entrySet()) {
			sb.append(entry.getKey()).append(',');
		}		
        sb.append('\n');
        try {
			csvWriter.createCSVWithContent(sb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        System.out.println("Header created");
	}
    
}
