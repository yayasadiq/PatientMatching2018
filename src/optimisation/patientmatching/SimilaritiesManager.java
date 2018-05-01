package optimisation.patientmatching;

import java.util.Arrays;

import simulatedSimilarities.OptimizationApp;

public class SimilaritiesManager implements Problem {
	private OptimizationApp app;

	public SimilaritiesManager(OptimizationApp app) {
		this.app = app;
	}

	/**This method should return a score for a given ordering of patient index*/
	public double evaluate(int[] solution){
		double val = 0.0;
		double[] sims = app.getSimilarities(solution);
		for(double s:sims){
            val += s;
        }
		return val/sims.length;
	}
}
