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
import java.util.Iterator;
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
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
//import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.selection.SelectCases;
import model.CsvConnector;
import model.MockPatient;
import model.MockPatientConnector;
import model.PatientDescription;
import utils.IOhelpers.CSVWriter;

/**
 *
 * @author ss6035
 */
public class PatientSim  implements StandardCBRApplication {
    
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
    private List<String> controls;
    private Map<String, Double> controlsSim;
    private int nbrOfPossibleSwaps = 0;

  

    public PatientSim(String cbPath, String outPath) {
		this.cbPath = cbPath;
		this.outPath = outPath;
	}

	@Override
    public void configure() throws ExecutionException{
        casesMap = new HashMap();
        connector  = new MockPatientConnector(cbPath);
        casebase = new MyLinealCaseBase();
        controls = new ArrayList<>();
        controlsSim = new HashMap<>();

        
        nnConfig = new NNConfig();
        nnConfig.setDescriptionSimFunction(new Average());
        nnConfig.addMapping(new Attribute("attributs", MockPatient.class), new Euclidean());

        
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
            String controlId = (String)c.getID();
			casesMap.put(Integer.parseInt(controlId), c);
            controls.add(controlId);
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
            this.nbrOfPossibleSwaps ++;
        }
        
    }
    
    public int getNbrSwaps() {
    	return this.nbrOfPossibleSwaps;
    }
    
    public int getNbrqCases() {
    	return this.qCases.size();
    }
    
    public int getNbrCases() {
    	return this.casesMap.size();
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

	public void writeSimilarities(CBRCase c, CSVWriter csvWriter) {
		Collection<RetrievalResult> result = NNScoringMethod.evaluateSimilarity(casebase.getCases(), c, nnConfig);
        Collection<RetrievalResult> retievedCases = SelectCases.selectAllRR(result);
        for (RetrievalResult retrievalResult : retievedCases) {
			controlsSim.put((String)retrievalResult.get_case().getID(), retrievalResult.getEval());
		}
        csvWriter.writeCell(c.getID());
        for (String string : controls) {
			csvWriter.writeCell(controlsSim.get(string));	
		}
        csvWriter.newLine();
	}

	public void writeControls(CSVWriter csvWriter) throws IOException {
		csvWriter.writeCell("Trial/Control");
        for (Map.Entry entry: casesMap.entrySet()) {	
			csvWriter.writeCell(entry.getKey());
		}
        csvWriter.newLine();
	}
    
}
