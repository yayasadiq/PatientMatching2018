/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions;

import com.dtw.FastDTW;
import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;
import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 *
 * @author ss6035
 */
public class Jaccard implements LocalSimilarityFunction{

    @Override
    public double compute(Object o, Object o1) throws NoApplicableSimilarityFunctionException {
        if(o == null || o1 == null) return 0;
        if (!(o instanceof double[]))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
        if(!(o1 instanceof double[]))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
        double[] vals1 = (double[])o;
        double[] vals2 = (double[])o1;
        
        double ints=0;
        double union=0;
        for(int i=0; i<vals1.length; i++){
            if(vals1[i]>0 && vals2[i]>0){
                ints++;
                union++;
            }
            else if(vals1[i] > 0 || vals2[i]>0)
                union++;
        }
        double score = 0;
        if(union>0)
            score = ints/union;
        if (score>1)
            System.out.println("Jaccard: "+score);
        return score;
    }

    @Override
    public boolean isApplicable(Object o, Object o1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
