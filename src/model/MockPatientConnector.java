package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.csvreader.CsvReader;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.CaseBaseFilter;
import jcolibri.cbrcore.Connector;
import jcolibri.exception.InitializingException;

public class MockPatientConnector implements Connector {
	private String filepath;
    private File file;

    public MockPatientConnector(String filepath) {
        this.filepath = filepath;
        this.file = new File(filepath);
    }
    
    public List<String[]> parse() throws FileNotFoundException {
        
        List<String[]> data = new ArrayList();
        CsvReader reader =  new CsvReader(new FileReader(file));
        try{
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            data.add(headers);
            while(reader.readRecord()){
                String[] values = reader.getValues();
                data.add(values);
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        } 
        return data;      
    }

    @Override
    public void initFromXMLfile(URL url) throws InitializingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void storeCases(Collection<CBRCase> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteCases(Collection<CBRCase> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<CBRCase> retrieveAllCases() {
        Collection<CBRCase> cases = new ArrayList();        
        try{            
            List<String[]> data = this.parse();
            for(int i=1; i<data.size();i++){               
                cases.add(generateCase(data, i));
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
        return cases;
    }

	private CBRCase generateCase(List<String[]> data, int i) {
		String[] pRecord = data.get(i);
		CBRCase _case = new CBRCase();                
		int lineLength = pRecord.length;
		double[] result = new double[lineLength - 1];
		for (int j = 1; j < result.length; j++) {
			result[j] = Double.parseDouble(pRecord[j]);
		}
		MockPatient patient = new MockPatient();
		patient.setId(pRecord[0]); 
		patient.setAttributs(result);
		_case.setDescription(patient);
		return _case;
	}
    
   
    public Collection<CBRQuery> retrieveAllQueries() {
        Collection<CBRQuery> queries = new ArrayList();       
        try{            
            List<String[]> data = this.parse();
            for(int i=1; i<data.size();i++){
                queries.add(generateCase(data, i));
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
        return queries;
    }

    @Override
    public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter cbf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
