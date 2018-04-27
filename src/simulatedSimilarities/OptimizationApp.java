package simulatedSimilarities;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.RowFilter.Entry;

import optimisation.utils.CSVWriter;
import patientMatching.CsvConnector;

public class OptimizationApp{
	private String outPath;
	private String inPath;

	private List<String> controlPatientsId;
	private List<String> resultControlId;
	private List<String> trialPatientsId;
	private List<List<Double>> resultMatrix;
	
	
	private Map<String, Map<String, Double>> trialControlAssociation;
	
	
	private CsvConnector connector;
	private double simSum;
	private double maxSimSum;

	public OptimizationApp(String inPath, String resPath) {
		this.outPath = resPath;
		this.inPath = inPath;

		this.trialControlAssociation = new HashMap<>();

		this.controlPatientsId = new ArrayList<>();
		this.trialPatientsId = new ArrayList<>();
		this.resultControlId = new ArrayList<>();
		this.resultMatrix = new ArrayList<>();
		
		this.connector = new CsvConnector(inPath);
		this.simSum = 0;
		this.maxSimSum = 0;
	}
	
	public void configure() throws FileNotFoundException {
		List<String[]> data = connector.parse();
		String[] curLine = data.get(0);
		for (int i = 1; i < curLine.length - 1; i++) {
			this.controlPatientsId.add(curLine[i]);
		}
		for (int i = 1; i < data.size(); i++) {
			curLine = data.get(i);
			Map<String, Double> sims = new HashMap<>();
			for (int j = 1; j < curLine.length - 1; j++) {
				sims.put(controlPatientsId.get(j - 1), Double.valueOf(curLine[j]));
			}
			
			String patientId = curLine[0];
			
			trialPatientsId.add(patientId);
			trialControlAssociation.put(patientId, sims);
		}
	}

	public void cycle() {
		for (String trialId : trialPatientsId) {
			this.resultControlId.add(findIndexOfMax(trialControlAssociation.get(trialId)));
		}
		writeMatrix();
	}

	private void writeMatrix() {
		CSVWriter csvWriter = new CSVWriter(outPath);
		csvWriter.writeCell("Trial/Control");
		for (String string : resultControlId) {
			csvWriter.writeCell(string);
		}
		NumberFormat nf = NumberFormat.getInstance( new java.util.Locale( "USA" ));
		csvWriter.newLine();
		int counter = 1;
		for (String trialId : trialPatientsId) {			
			csvWriter.writeCell(trialId);
			List<Double> sims = new ArrayList<>();
			for(int i = 0; i < counter; i++) {
				double sim = trialControlAssociation.get(trialId).get(resultControlId.get(i));
				sims.add(sim);
				csvWriter.writeCell(nf.format(sim));
			}
			resultMatrix.add(sims);
			csvWriter.newLine();
			counter ++;
		}
		csvWriter.endFile();
	}

	private String findIndexOfMax(Map<String, Double> controlMap) {
		double max = Integer.MIN_VALUE;
		String maxPos = "";
		for (Map.Entry<String, Double> control_sim : controlMap.entrySet()) {
			double sim = control_sim.getValue();
			if (sim > max && !resultControlId.contains(control_sim.getKey())) {
				max = sim;
				maxPos = control_sim.getKey();
			}
		}
		return maxPos;
	}
	
	public void computeSimilaritiesSum() {
		 this.simSum = 0;
		 this.maxSimSum = 0;
		 int length = trialPatientsId.size();
		 for (int i = 0; i < length; i++) {
			 simSum += resultMatrix.get(i).get(i);
			 maxSimSum += Collections.max(resultMatrix.get(i));
		 }
		 System.out.println("simSum : " + this.simSum);
		 System.out.println("maxSimSum : " + this.maxSimSum);
	}

	public void increaseDiff(int nbrIteration) {
		for (int i = 0; i < nbrIteration; i++) {
			Random rand = new Random(System.currentTimeMillis());
			int indice1 = rand.nextInt(trialPatientsId.size());
			List<Double> lines = resultMatrix.get(indice1);
			int indice2 = rand.nextInt(lines.size());
			double amelioratedSim = lines.get(indice2);
			amelioratedSim *= 1.2;
			if (amelioratedSim > 1) {
				amelioratedSim = 1;
			}
			lines.set(indice2, amelioratedSim);
			resultMatrix.set(indice1, lines);
		}
		computeSimilaritiesSum();
	}
	
	public void mergeMatrixAndData() {
		for (int i = 0; i < resultMatrix.size(); i++) {
			List<Double> line = resultMatrix.get(i);
			String trialId = trialPatientsId.get(i);
			Map<String, Double> control_sim = trialControlAssociation.get(trialId);
			for (int j = 0; j < line.size(); j++) {
				control_sim.put(controlPatientsId.get(j), line.get(j));
			}
			trialControlAssociation.put(trialId, control_sim);
		}
	}
	
	public void displayMatrix() {
		for ( List<Double> sims : resultMatrix) {
			System.out.println(sims);
		}
	}
	
	public double[] getSimilarities(int[] solutions) {
		int solLength = solutions.length;
		double[] similarities = new double[solLength];

		for (int i = 0; i < solLength; i++) {
			similarities[i] = trialControlAssociation.get(trialPatientsId.get(i)).get(resultControlId.get(solutions[i]));
		}
		return similarities;
	}

	public int getNumberSolutions() {
		return resultControlId.size();
	}

}
