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

package fr.ign.cogit.roc4j;

import java.util.ArrayList;


// =================================================================================
// Protected class for computing smoothed versions of Receiver Operating Curves
// ---------------------------------------------------------------------------------
// - Method convexify : renders a convex version of ROC curve.
//---------------------------------------------------------------------------------
// - Method kernel smoothing : given positive and negative kernel functions, this 
// method computes a kernel density estimation of cumulative distribution 
// functions (cdf) associated with positive and negative instances distributions.
// Output ROC curve is then computed by composition of F and G estimated cdfs.
//---------------------------------------------------------------------------------
// - Binormal regression smoothing : assuming that positive and negative instances 
// are distributed according to normal probability density function, ROC curve is
// expressed by : ROC(t) = Phi(a-b*Phi_inv(t)) where : Phi is the standard normal 
// cumulative distribution function and (a,b) is a couple of real parameters to 
// be estimated. Regression is done under L2 cost function (least squares). 
//---------------------------------------------------------------------------------
// Date : 13/03/2017
//=================================================================================

class RocCurveSmoother {

	// ---------------------------------------------------------------------------
	// Method to make ROC curve convex
	// Input : Receiver Operating Curve
	// Output : none (void) modified ROC curve
	// ---------------------------------------------------------------------------
	protected static void convexify(ReceiverOperatingCharacteristics roc){

		ArrayList<Double> XROCnew = new ArrayList<Double>();
		ArrayList<Double> YROCnew = new ArrayList<Double>();

		XROCnew.add(roc.XROC[0]);
		YROCnew.add(roc.YROC[0]);

		int index_courant = 0;

		// --------------------------------------------
		// Convexification process
		// --------------------------------------------

		while (index_courant < roc.XROC.length){

			double slope = 0;
			int argmax = 0;

			for (int i=index_courant+1; i<roc.XROC.length; i++){

				double s = (roc.YROC[i]-roc.YROC[index_courant])/(roc.XROC[i]-roc.XROC[index_courant]);

				if (roc.XROC[i] == roc.XROC[index_courant]){

					continue;

				}

				if (s > slope){

					slope = s;
					argmax = i;

				}

			}

			if (argmax == 0){

				break;

			}

			for (int i=index_courant+1; i<=argmax; i++){

				double t = (double)(i-index_courant)/(double)(argmax-index_courant);

				XROCnew.add(roc.XROC[argmax]*t+roc.XROC[index_courant]*(1-t));
				YROCnew.add(roc.YROC[argmax]*t+roc.YROC[index_courant]*(1-t));

			}

			index_courant = argmax;

		}

		// --------------------------------------------
		// Interpolation
		// --------------------------------------------

		if ((XROCnew.get(XROCnew.size()-1) != 1) || (YROCnew.get(XROCnew.size()-1) != 1)){

			for (int i=index_courant+1; i<roc.XROC.length; i++){

				double t = (double)(i-index_courant)/(double)((roc.XROC.length-1)-index_courant);

				XROCnew.add(roc.XROC[roc.XROC.length-1]*t+roc.XROC[index_courant]*(1-t));
				YROCnew.add(roc.YROC[roc.XROC.length-1]*t+roc.YROC[index_courant]*(1-t));

			}

		}

		// --------------------------------------------
		// Output result
		// --------------------------------------------

		roc.XROC = new double[XROCnew.size()];
		roc.YROC = new double[YROCnew.size()];

		for (int i=0; i<roc.XROC.length; i++){

			roc.XROC[i] = XROCnew.get(i);
			roc.YROC[i] = YROCnew.get(i);

		}


		roc.resolution = roc.XROC.length;

		roc.resample();

	}



	// ---------------------------------------------------------------------------
	// Method to smooth a roc curve with kernel estimation
	// Inputs : Receiver Operating Curve, positive and negaitive instances 
	// kernels and verbose mode (default true)
	// Output : none (void) modified ROC curve
	// ---------------------------------------------------------------------------
	protected static void kernelSmoothing(ReceiverOperatingCharacteristics roc, Kernel kp, Kernel kn, boolean verbose){

		Kernel kernel_pos = kp.copy();
		Kernel kernel_neg = kn.copy();

		// Security test
		if (roc.POS_SCORES.length == 1){

			System.err.println("Error : expected and probabilities vector are expected to compute kernel smoothing");
			System.exit(1);

		}

		// --------------------------------------------
		// Computing optimal kernel if needed
		// --------------------------------------------

		if ((kernel_pos instanceof PredefiniteKernel) && (((PredefiniteKernel)(kernel_pos)).getType() == PredefiniteKernel.AUTOMATIC)){

			kernel_pos = new PredefiniteKernel(PredefiniteKernel.EPANECHNIKOV);

			double mx = 0;
			double mx2 = 0;

			for (int i=0; i<roc.POS_SCORES.length; i++){

				mx += roc.POS_SCORES[i];
				mx2 += Math.pow(roc.POS_SCORES[i], 2);

			}

			mx /= (double)(roc.POS_SCORES.length);
			mx2 /= (double)(roc.POS_SCORES.length);

			double sx = Math.sqrt(mx2-Math.pow(mx, 2));
			sx = Math.sqrt(roc.POS_SCORES.length/(roc.POS_SCORES.length-1))*sx;

			kernel_pos.setBandwidth(1.06*sx*Math.pow(roc.POS_SCORES.length, -0.2));


		}

		// --------------------------------------------
		// Computing optimal kernel if needed
		// --------------------------------------------

		if ((kernel_neg instanceof PredefiniteKernel) && (((PredefiniteKernel)(kernel_neg)).getType() == PredefiniteKernel.AUTOMATIC)){

			kernel_neg = new PredefiniteKernel(PredefiniteKernel.EPANECHNIKOV);

			double my = 0;
			double my2 = 0;

			for (int i=0; i<roc.NEG_SCORES.length; i++){

				my += roc.NEG_SCORES[i];
				my2 += Math.pow(roc.NEG_SCORES[i], 2);

			}

			my /= (double)(roc.NEG_SCORES.length);
			my2 /= (double)(roc.NEG_SCORES.length);

			double sy = Math.sqrt(my2-Math.pow(my, 2));
			sy = Math.sqrt(roc.NEG_SCORES.length/(roc.NEG_SCORES.length-1))*sy;

			kernel_neg.setBandwidth(1.06*sy*Math.pow(roc.NEG_SCORES.length, -0.2));

		}


		double hp = kernel_pos.getBandwidth();
		double hn = kernel_neg.getBandwidth();


		double[] F = new double[roc.resolution];
		double[] G = new double[roc.resolution];

		// --------------------------------------------
		// Kernel estimation
		// --------------------------------------------
		
		for (int i=0; i<roc.resolution; i++){

			double x = (double)i/(double)roc.resolution;

			F[i] = 0;
			G[i] = 0;

			// Positive instances estimation
			for (int j=0; j<roc.POS_SCORES.length; j++){

				G[i] += kernel_pos.cdf((x-roc.POS_SCORES[j])/hp);


			}

			// Negative instances estimation
			for (int j=0; j<roc.NEG_SCORES.length; j++){

				F[i] += kernel_neg.cdf((x-roc.NEG_SCORES[j])/hn);

			}

			// Normalization
			G[i] /= roc.POS_SCORES.length;
			F[i] /= roc.NEG_SCORES.length;

		}

		// Fonctions composition
		for (int i=0; i<roc.resolution; i++){

			roc.XROC[i] = (double)i/(double)roc.resolution;

			double arg = 0;
			
			// Generalized inverse of F
			for (int j=0; j<roc.resolution; j++){

				if (F[j] > 1-roc.XROC[i]){

					arg = (double)j/(double)roc.resolution;
					break;

				}

			}
			
			// Composition with G
			roc.YROC[i] = 1-G[(int)(arg*roc.resolution)];

		}
		
		// Output

		roc.YROC[0] = 0;
		roc.YROC[roc.YROC.length-1] = 1;

	}



	// ---------------------------------------------------------------------------
	// Method to smooth a roc curve with binormal estimation
	// Inputs : Receiver Operating Curve, positive and negaitive instances 
	// kernels and verbose mode (default true)
	// Output : none (void) modified ROC curve
	// ---------------------------------------------------------------------------
	// Estimation of parameters a and b is done by least squares estimation
	// ---------------------------------------------------------------------------
	protected static void binormalSmoothing(ReceiverOperatingCharacteristics roc, boolean verbose){

		// --------------------------------------------
		// Statistics computation
		// --------------------------------------------

		double mx = 0;
		double my = 0;

		double mx2 = 0;
		double my2 = 0;

		double sx = 0;
		double sy = 0;

		// Removing side points
		ArrayList<Double> X = new ArrayList<Double>();
		ArrayList<Double> Y = new ArrayList<Double>();

		for (int i=0; i<roc.XROC.length; i++){

			if ((roc.XROC[i] > 0.99) || (roc.XROC[i] < 0.01)){

				continue;

			}

			if ((roc.YROC[i] > 0.99) || (roc.YROC[i] < 0.01)){

				continue;

			}


			X.add(roc.XROC[i]);
			Y.add(roc.YROC[i]);

		}


		// Standard inverse transformation

		for (int i=0; i<X.size(); i++){

			mx += Tools.phiInv(X.get(i));
			my += Tools.phiInv(Y.get(i));

			mx2 += Math.pow(Tools.phiInv(X.get(i)), 2);
			my2 += Math.pow(Tools.phiInv(Y.get(i)), 2);

		}

		mx = mx/X.size();
		my = my/Y.size();

		sx = Math.sqrt(mx2/X.size()-mx*mx);
		sy = Math.sqrt(my2/Y.size()-my*my);


		// --------------------------------------------
		// Regression coefficient computation
		// --------------------------------------------

		double r = 0;

		for (int i=0; i<X.size(); i++){

			r += ((Tools.phiInv(X.get(i))-mx)/sx) * ((Tools.phiInv(Y.get(i))-my)/sy);

		}

		r = r/X.size();

		// --------------------------------------------
		// Regression parameters computation
		// --------------------------------------------
		double a = sy/sx*r;
		double b = my - a*mx;

		double avg_err = 0;
		double rmse = 0;

		for (int i=0; i<roc.XROC.length; i++){

			if (roc.XROC[i] > 0.99){ 

				roc.YROC[i] = 1.0;
				continue;

			}

			if (roc.XROC[i] < 0.01){

				roc.YROC[i] = 0.0; 
				continue;

			}

			double val = Tools.phi(a*Tools.phiInv(roc.XROC[i])+b);

			avg_err += (val-roc.YROC[i]);
			rmse += Math.pow((val-roc.YROC[i]), 2);

			roc.YROC[i] = val;


		}

		// Update
		roc.a = a;
		roc.b = b;

		// Adjustment errors
		avg_err /= roc.YROC.length;
		rmse = Math.sqrt(rmse/roc.YROC.length);

		double std = Math.round(rmse/Math.sqrt(roc.YROC.length)*10000)/10000.0;

		if (verbose){

			System.out.println("-----------------------------------------");
			System.out.println("       ROC CURVE SMOOTHING RESULTS       ");
			System.out.println("-----------------------------------------");
			System.out.print("Intercept = "+Math.round(b*100)/100.0+"   ");
			System.out.println("Slope = "+Math.round(a*100)/100.0);
			System.out.println("Regression coefficient r = "+Math.round(r*1000)/1000.0);
			System.out.println("Determination coefficient r² = "+Math.round(r*r*10000)/100.0+" %");
			System.out.println("Average error = "+Math.round(avg_err*1000)/1000.0+" (+/- "+std+")");
			System.out.println("Root mean square error = "+Math.round(rmse*1000)/1000.0);
			System.out.println("-----------------------------------------");


		}


	}

}
