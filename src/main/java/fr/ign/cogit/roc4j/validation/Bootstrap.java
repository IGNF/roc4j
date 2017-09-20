
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

package fr.ign.cogit.roc4j.validation;

import java.util.ArrayList;

import fr.ign.cogit.roc4j.core.ReceiverOperatingCharacteristics;
import fr.ign.cogit.roc4j.core.RocCurvesCollection;
import fr.ign.cogit.roc4j.graphics.OperatingPoint;
import fr.ign.cogit.roc4j.optimization.OptimalLine;


//=================================================================================
// Class for bootstraping a receiver operating curve
// Requires that ROC curve has been constructed with expected and score vectors
// Date : 11/09/2017
//=================================================================================
public class Bootstrap {

	// ---------------------------------------------------------------------------
	// Method for generating a sample of B bootstraps of a ROC curve
	// ---------------------------------------------------------------------------
	public static RocCurvesCollection sample(ReceiverOperatingCharacteristics roc, int B){

		// Output bootstraps
		ArrayList<ReceiverOperatingCharacteristics> ROCS = new ArrayList<ReceiverOperatingCharacteristics>();

		// Security test
		if (roc.getPositiveScore().length == 1){

			System.err.println("Error : expected and probabilities vector are expected to compute kernel smoothing");
			System.exit(1);

		}

		// Number of instances
		int np = roc.getPositiveScore().length;
		int nn = roc.getNegativeScore().length;
		int n = nn + np;

		// Generating score and expected vectors

		int[] expected = new int[n];
		double[] predicted = new double[n];

		// Negative scores
		for (int i=0; i<nn; i++){

			expected[i] = 0;
			predicted[i] = roc.getNegativeScore()[i];

		}

		// Positive scores
		for (int i=0; i<np; i++){

			expected[nn+i] = 1;
			predicted[nn+i] = roc.getPositiveScore()[i];

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
			ROCS.add(new ReceiverOperatingCharacteristics(b_exp, b_pred, roc.getResolution()));

		}

		return new RocCurvesCollection(ROCS, false);

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

}
