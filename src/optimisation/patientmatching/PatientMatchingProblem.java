package optimisation.patientmatching;

import java.util.Random;
import patientMatching.PatientSim;

public class PatientMatchingProblem implements Problem{
    
    private PatientSim app;

	public PatientMatchingProblem(PatientSim app) {
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
