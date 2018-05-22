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
public class Euclidean implements LocalSimilarityFunction{

    @Override
    public double compute(Object o, Object o1) throws NoApplicableSimilarityFunctionException {
        if(o == null || o1 == null) return 0;
        if (!(o instanceof double[]))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
        if(!(o1 instanceof double[]))
            throw new jcolibri.exception.NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
        double[] vals1 = (double[])o;
        double[] vals2 = (double[])o1;
        
        double dist=0;
        double sumV2 = 0;
        int tabLength = vals1.length;
		for(int i=0; i < tabLength; i++){
			double val2 = vals2[i];
			double val1 = vals1[i];
			if (val1 > val2) {
				dist += val1 - val2;
				sumV2 += val1;
			} else {
				dist += val2 - val1;
				sumV2 += val2;
			}
			dist +=val1-val2;
        }
       
        return dist/sumV2;
    }

    @Override
    public boolean isApplicable(Object o, Object o1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
