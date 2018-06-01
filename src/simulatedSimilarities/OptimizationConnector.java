package simulatedSimilarities;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import model.CsvConnector;
import utils.IOhelpers.CSVWriter;

public class OptimizationConnector {

	
	private static final int FIRST_COLUMN_INDEX = 1;

	private CsvConnector connector;
	
	private TIntArrayList controlPatientsId;
	private TIntArrayList trialPatientsId;
	
	private	TIntObjectMap<TIntDoubleHashMap> trialControlAssociation;
	private String inPath;
	private String outPath;
	
	public OptimizationConnector(String inPath,String outPath) {
		//this.connector = new CsvConnector(inPath);
		this.inPath = inPath;
		this.controlPatientsId = new TIntArrayList();
		this.trialPatientsId = new TIntArrayList();
		
		this.trialControlAssociation = new TIntObjectHashMap<>();
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
				this.controlPatientsId.add(Integer.valueOf(curLine[i]));
			}
		    while (sc.hasNextLine()) {
		        curLine = sc.nextLine().split(",");
		        int patientId = Integer.valueOf(curLine[0]);
		        TIntDoubleHashMap tempMap = new TIntDoubleHashMap();
				for (int j = FIRST_COLUMN_INDEX; j < lineLength; j++) {
					tempMap.put(controlPatientsId.get(j - 1), Double.valueOf(curLine[j]));
				}
				trialControlAssociation.put(patientId, tempMap);
				trialPatientsId.add(patientId);				
		    }
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
	
	public void writeMatrix(String filePath, TIntArrayList resultControlId) {
		if (filePath == null) {
			filePath = this.outPath;
		}
		CSVWriter csvWriter = new CSVWriter(filePath);
		csvWriter.writeCell("Trial/Control");
		int resControlIdLength = resultControlId.size();
		for (int i = 0; i < resControlIdLength; i++) {
			csvWriter.writeCell(resultControlId.get(i));
		} 			
		
		NumberFormat nf = NumberFormat.getInstance(new java.util.Locale( "USA" ));
		csvWriter.newLine();
		int nbrTrial = trialPatientsId.size();
		for (int i = 0; i < nbrTrial ; i++) {
			int trialId = trialPatientsId.get(i);
			csvWriter.writeCell(trialId);
			for (int j = 0; j < resControlIdLength; j++) {
				double sim = trialControlAssociation.get(trialId).get(resultControlId.get(i));
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
	
	public void mergeMatrixAndData(TIntObjectMap<TDoubleArrayList> resultMatrix, TIntArrayList resultControlId) {
		for (int i = 0; i < resultMatrix.size(); i++) {
			TDoubleArrayList line = resultMatrix.get(i);
			int trialId = trialPatientsId.get(i);
			TIntDoubleHashMap tempMap = trialControlAssociation.get(trialId);
			for (int j = 0; j < line.size(); j++) {
				tempMap.put(resultControlId.get(j), line.get(j));
			}
			trialControlAssociation.put(trialId, tempMap);
		}
	}
	
	public TIntArrayList getControlPatientsId() {
		return controlPatientsId;
	}

	public TIntArrayList getTrialPatientsId() {
		return trialPatientsId;
	}


	public TIntObjectMap<TIntDoubleHashMap> getTrialControlAssociation() {
		return trialControlAssociation;
	}


}
