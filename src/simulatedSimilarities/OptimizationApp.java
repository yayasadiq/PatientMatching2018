package simulatedSimilarities;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OptimizationApp{
	
	private OptimizationConnector optimizationConnector;

	private String[] resultControlId;
	private String[] trialPatientsId;
	private List<List<Double>> resultMatrix;
	
	private Map<String, Map<String, Double>> trialControlAssociation;
	
	private double simSum;
	private double maxSimSum;

	public OptimizationApp(OptimizationConnector optimizationConnector) {
		this.optimizationConnector = optimizationConnector;
		this.trialControlAssociation = optimizationConnector.getTrialControlAssociation();

		optimizationConnector.getControlPatientsId();
		this.trialPatientsId = optimizationConnector.getTrialPatientsId();
		this.resultControlId = optimizationConnector.getResultControlId();
		this.resultMatrix = optimizationConnector.getResultMatrix();
		
		this.simSum = 0;
		this.maxSimSum = 0;
	}
	
	public void computeSimilaritiesSum() {
		 this.simSum = 0;
		 this.maxSimSum = 0;
		 int length = trialPatientsId.length;
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
			int indice1 = rand.nextInt(trialPatientsId.length);
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
		optimizationConnector.setResultMatrix(resultMatrix);
		optimizationConnector.mergeMatrixAndData();
	}
	
	
	public double[] getSimilarities(int[] solutions) {
		int solLength = solutions.length;
		double[] similarities = new double[solLength];

		for (int i = 0; i < solLength; i++) {
			similarities[i] = trialControlAssociation.get(trialPatientsId[i]).get(resultControlId[solutions[i]]);
		}
		return similarities;
	}


}
