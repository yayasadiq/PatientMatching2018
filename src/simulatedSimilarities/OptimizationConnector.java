package simulatedSimilarities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import model.CsvConnector;
import utils.IOhelpers.CSVWriter;

public class OptimizationConnector {

	
	private static final int FIRST_COLUMN_INDEX = 1;

	private CsvConnector connector;
	
	private List<String> controlPatientsId;
	private List<String> trialPatientsId;
	
	private	Table<String, String, Double> trialControlAssociation;
	private String inPath;
	private String outPath;
	
	public OptimizationConnector(String inPath,String outPath) {
		//this.connector = new CsvConnector(inPath);
		this.inPath = inPath;
		this.controlPatientsId = new ArrayList<>();
		this.trialPatientsId = new ArrayList<>();
		
		this.trialControlAssociation = HashBasedTable.create();
		this.outPath = outPath;
	}

	public void configure() throws IOException {
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
		    inputStream = new FileInputStream(inPath);
		    sc = new Scanner(inputStream, "UTF-8");
		    String[] curLine = sc.nextLine().split(",");
		    int lineLength = curLine.length - 1;
			for (int i = FIRST_COLUMN_INDEX; i < lineLength; i++) {
				this.controlPatientsId.add(curLine[i]);
			}
		    while (sc.hasNextLine()) {
		        curLine = sc.nextLine().split(",");
		        String patientId = curLine[0];
				for (int j = FIRST_COLUMN_INDEX; j < lineLength; j++) {
					trialControlAssociation.put(patientId, controlPatientsId.get(j - 1), Double.valueOf(curLine[j]));
				}
				trialPatientsId.add(patientId);				
		    }
		    System.out.println(trialControlAssociation.row("0").size() + " " + trialControlAssociation.column("0").size());
		    if (sc.ioException() != null) {
		        throw sc.ioException();
		    }
		} finally {
		    if (inputStream != null) {
		        inputStream.close();
		    }
		    if (sc != null) {
		        sc.close();
		    }
		}
	}	
	
	public void writeMatrix(String filePath, List<String> resultControlId) {
		if (filePath == null) {
			filePath = this.outPath;
		}
		CSVWriter csvWriter = new CSVWriter(filePath);
		csvWriter.writeCell("Trial/Control");
		for (String string : resultControlId) {
			csvWriter.writeCell(string);
		}
		NumberFormat nf = NumberFormat.getInstance(new java.util.Locale( "USA" ));
		csvWriter.newLine();
		for (String trialId : trialPatientsId) {
			csvWriter.writeCell(trialId);
			for(String controlId : resultControlId) {
				double sim = trialControlAssociation.get(trialId, controlId);
				csvWriter.writeCell(nf.format(sim));
			}
			csvWriter.newLine();
		}
		try {
			csvWriter.createCSVWithContent();;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mergeMatrixAndData(List<List<Double>> resultMatrix, List<String> resultControlId) {
		for (int i = 0; i < resultMatrix.size(); i++) {
			List<Double> line = resultMatrix.get(i);
			String trialId = trialPatientsId.get(i);
			for (int j = 0; j < line.size(); j++) {
				trialControlAssociation.put(trialId, resultControlId.get(j), line.get(j));
			}
		}
	}
	
	public List<String> getControlPatientsId() {
		return controlPatientsId;
	}

	public List<String> getTrialPatientsId() {
		return trialPatientsId;
	}


	public Table<String, String, Double> getTrialControlAssociation() {
		return trialControlAssociation;
	}


}
