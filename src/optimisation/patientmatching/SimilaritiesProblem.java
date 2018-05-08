package optimisation.patientmatching;

import simulatedSimilarities.OptimizationApp;

public class SimilaritiesProblem implements Problem {
	private OptimizationApp app;

	public SimilaritiesProblem(OptimizationApp app) {
		this.app = app;
	}

	/**This method should return a score for a given ordering of patient index*/
	public double evaluate(int[] solution){
		double val = 0.0;
		double[] sims = app.getSimilarities(solution);
		for(double s:sims){
            val += s;
        }
		return val;
	}
	
}
