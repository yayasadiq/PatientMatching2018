package optimisation.algorithm;

import java.util.ArrayList;
import java.util.Random;

public abstract class PermutationMutation {

	public Random rand;
	
	public PermutationMutation(Random rand){
		this.rand = rand;
	}
	
	public abstract int[] mutate (int[] sol, int distance, boolean fixedDistance);

	public abstract int[] mutate(int[] sol);

}
