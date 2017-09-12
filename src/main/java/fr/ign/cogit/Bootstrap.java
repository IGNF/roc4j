
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

import fr.ign.cogit.ReceiverOperatingCharacteristics;


//=================================================================================
// Class for bootstraping a receiver operating curve
// Requires that ROC curve has been constructed with expected and score vectors
// Date : 11/09/2017
//=================================================================================
public class Bootstrap {

	// ---------------------------------------------------------------------------
	// Method for generating a sample of B bootstraps of a ROC curve
	// ---------------------------------------------------------------------------
	public static ArrayList<ReceiverOperatingCharacteristics> sample(ReceiverOperatingCharacteristics roc, int B){

		// Output bootstraps
		ArrayList<ReceiverOperatingCharacteristics> ROCS = new ArrayList<ReceiverOperatingCharacteristics>();

		// Security test
		if (roc.POS_SCORES.length == 1){

			System.err.println("Error : expected and probabilities vector are expected to compute kernel smoothing");
			System.exit(1);

		}

		// Number of instances
		int np = roc.POS_SCORES.length;
		int nn = roc.NEG_SCORES.length;
		int n = nn + np;

		// Generating score and expected vectors

		int[] expected = new int[n];
		double[] predicted = new double[n];

		// Negative scores
		for (int i=0; i<nn; i++){

			expected[i] = 0;
			predicted[i] = roc.NEG_SCORES[i];

		}

		// Positive scores
		for (int i=0; i<np; i++){

			expected[nn+i] = 1;
			predicted[nn+i] = roc.POS_SCORES[i];

		}

		// Generating bootstrap samples

		int[] b_exp = new int[n];
		double[] b_pred = new double[n];

		for(int b=0; b<B; b++){

			// Bootstrapping vectors
			for (int i=0; i<n; i++){

				int index = (int)(Math.random()*n);

				b_exp[i] = expected[index];
				b_pred[i] = predicted[index];

			}

			// Generating roc curve with expected and posterior probabilities
			ROCS.add(new ReceiverOperatingCharacteristics(b_exp, b_pred, roc.resolution));

		}

		return ROCS;

	}

	// ---------------------------------------------------------------------------
	// Method for computing AUC of an array list of roc curves
	// ---------------------------------------------------------------------------
	public static ArrayList<Double> computeAreaUnderCurve(ArrayList<ReceiverOperatingCharacteristics> ROCS){

		ArrayList<Double> AUC = new ArrayList<Double>();

		for (int i=0; i<ROCS.size(); i++){

			AUC.add(ROCS.get(i).computeAUC());

		}

		return AUC;

	}


	// ---------------------------------------------------------------------------
	// Method for computing optimal operating point of an array list of roc curves
	// ---------------------------------------------------------------------------
	public static ArrayList<OperatingPoint> computeOptimalOperatingPoints(ArrayList<ReceiverOperatingCharacteristics> ROCS, OptimalLine line){

		ArrayList<OperatingPoint> OOP = new ArrayList<OperatingPoint>();

		for (int i=0; i<ROCS.size(); i++){

			OOP.add(ROCS.get(i).computeOptimalOperatingPoint(line));

		}

		return OOP;

	}


	// ---------------------------------------------------------------------------
	// Method for computing confidence interval of a set of double values
	// Confidence level is given in %
	// ---------------------------------------------------------------------------
	public static double[] computeConfidenceInterval(ArrayList<Double> values, double level){

		// Security tests

		if (level > 100){

			System.err.println("Error : confidence level cannot be greater than 100");
			System.exit(1);

		}

		if (level < 0){

			System.err.println("Error : confidence level cannot be negative");
			System.exit(1);

		}

		double level_inf = (1-level/100.0)/2.0;
		double level_sup = 1.0-(1-level/100.0)/2.0;

		double[] CI = new double[2];
		
		CI[0] = Tools.computeQuantile(values, level_inf);
		CI[1] = Tools.computeQuantile(values, level_sup);

		return CI;

	}




}
