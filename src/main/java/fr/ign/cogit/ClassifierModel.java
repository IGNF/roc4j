/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 *
 * @author Yann Méneroux
 ******************************************************************************/

package fr.ign.cogit;

import java.util.ArrayList;

// =================================================================================
// Class for modeling a classifier for validation process
// Date : 01/04/2017
// =================================================================================

public abstract class ClassifierModel {
	
	// Store classifier model after training
	public Object model = null;
	
	// Method to train classifier
	public abstract void train(DataSet trainingData);
	
	// Method to compute posterior probability
	public abstract double posterior(Object dataFeatures);
	
	// Method to compute a list of posterior probabilities
	public double[] posteriors(ArrayList<Object> dataFeatures){
		
		double[] posteriors = new double[dataFeatures.size()];
		
		for (int i=0; i<posteriors.length; i++){
			
			posteriors[i] = posterior(dataFeatures.get(i));
			
		}
		
		return posteriors;
		
	}

}
