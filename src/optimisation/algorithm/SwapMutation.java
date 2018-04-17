package optimisation.algorithm;

import java.util.ArrayList;
import java.util.Random;

public class SwapMutation extends PermutationMutation{

	public SwapMutation(Random rand) {
		super(rand);
	}

	public int[] mutate(int[] sol, int distance, boolean fixedDistance) {
        
        int startPosition = 1+rand.nextInt(sol.length-1);
        int endPosition = 0;
        
        int dist = 0;
        
        if(fixedDistance){ // Fixed distance: distance will be the one specified
            dist = distance;
        }
        else{ // Unfixed distance: distance will be chosen in the interval [0 - specified distance]
            dist = rand.nextInt(distance)+1;
        }

        boolean toTheLeft = true;
        
        if (startPosition - dist < 1){ // Sometimes, we need to force the gene to be selected to be the one on the RIGHT of the starting gene
            toTheLeft = false;
        }
        else if (startPosition + dist > sol.length-1){ // Sometimes, we need to force the gene to be selected to be the one on the LEFT of the starting gene
            toTheLeft = true;
        }
        else{ // The rest of the time, we can either select the gene to be selected on the RIGHT OR LEFT at random
            toTheLeft = rand.nextBoolean();
        }
        
        int[] oldElements = new int[sol.length];
        System.arraycopy(sol, 0, oldElements, 0, sol.length);
        int[] newElements = new int[oldElements.length];
        
        if (toTheLeft){
            endPosition = startPosition - dist;
        }
        else{
            endPosition = startPosition + dist;
        }
                    
        for (int i=0; i<oldElements.length; i++){
            if (i == startPosition){
                newElements[i] = oldElements[endPosition];
            }
            else if (i == endPosition){
                newElements[i] = oldElements[startPosition];
            }
            else{
                newElements[i] = oldElements[i];
            }
        }
        
        return newElements;
    }

	@Override
    public int[] mutate(int[] sol) {
        int maxDistance = 0;
        if(sol.length%2 == 0){ // even
            maxDistance = (int)(sol.length-1)/2;
        }
        else{ // odd
            maxDistance = (int)sol.length/2;
        }
        boolean fixedDist = false;
        return this.mutate(sol, maxDistance, fixedDist);
    }
}
