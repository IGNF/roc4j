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

package fr.ign.cogit.roc4j.utils;

import java.util.ArrayList;

public class NaiveBayes {


	private ArrayList<Double> numbers1;
	private ArrayList<Double> numbers2;
	private ArrayList<Double> numbers3;
	private ArrayList<Double> numbers4;

	private double m1 = 0;
	private double m2 = 0;
	private double m3 = 0;
	private double m4 = 0;

	private double s1 = 0;
	private double s2 = 0;
	private double s3 = 0;
	private double s4 = 0;



	// --------------------------------------------------------
	// Training
	// --------------------------------------------------------
	// Input : points coordinates in 2D space Xt and Yt
	// Boolean labelling of training data
	// --------------------------------------------------------
	public NaiveBayes(double[] Xt, double[] Yt, int[] Zt){

		// --------------------------------------------------------
		// P(X1=x|c=p) 
		// --------------------------------------------------------
		numbers1 = new ArrayList<Double>();

		for (int i=0; i<Xt.length; i++){
			if (Zt[i] == 1){
				numbers1.add(Xt[i]);
			}
		}

		double[] stats1 = stats(numbers1);

		m1 = stats1[0]; 
		s1 = stats1[1];

		// --------------------------------------------------------
		// P(X1=x|c=n) 
		// --------------------------------------------------------

		numbers2 = new ArrayList<Double>();

		for (int i=0; i<Xt.length; i++){
			if (Zt[i] != 1){
				numbers2.add(Xt[i]);
			}
		}

		double[] stats2 = stats(numbers2);

		m2 = stats2[0]; 
		s2 = stats2[1];

		// --------------------------------------------------------
		// P(X2=x|c=p) 
		// --------------------------------------------------------

		numbers3 = new ArrayList<Double>();

		for (int i=0; i<Yt.length; i++){
			if (Zt[i] == 1){
				numbers3.add(Yt[i]);
			}
		}

		double[] stats3 = stats(numbers3);

		m3 = stats3[0]; 
		s3 = stats3[1];

		// --------------------------------------------------------
		// P(X2=x|c=n) 
		// --------------------------------------------------------

		numbers4 = new ArrayList<Double>();

		for (int i=0; i<Yt.length; i++){
			if (Zt[i] != 1){
				numbers4.add(Yt[i]);
			}
		}

		double[] stats4 = stats(numbers4);

		m4 = stats4[0]; 
		s4 = stats4[1];

	}

	// -----------------------------------------------------------------
	// Prediction
	// -----------------------------------------------------------------
	// Input : 2D coordinates of a new point
	// Output : P(z=1|x,y)
	// -----------------------------------------------------------------
	public double predict(double x, double y){
		
		// Priors
		double p0 = 0.5;
		double p1 = 0.5;

		// P(c=p|X)
		double p_pos = p1*Math.exp(-Math.pow((x-m1)/(2*s1), 2))/s1;
		p_pos *= Math.exp(-Math.pow((y-m3)/(2*s3), 2))/s3;

		// P(c=n|X)
		double p_neg = p0*Math.exp(-Math.pow((x-m2)/(2*s2), 2))/s1;
		p_neg *= Math.exp(-Math.pow((y-m4)/(2*s4), 2))/s4;

		// Normalization
		double posterior = p_pos/(p_pos+p_neg);

		return posterior;


	} 

	// -----------------------------------------------------------------
	// Prediction on a vector of data
	// -----------------------------------------------------------------
	// Input : 2D coordinates of an array of new points
	// Output : list of P(z=1|x,y)
	// -----------------------------------------------------------------
	public double[] predict(double[] X, double[] Y){
		
		double[] posterior = new double[X.length];
		
		for (int i=0; i<X.length; i++){
			
			posterior[i] = predict(X[i], Y[i]);
			
		}
		
		return posterior;
		
	}

	// -----------------------------------------------------------------
	// Compute mean and standard deviation
	// -----------------------------------------------------------------
	public static double[] stats(ArrayList<Double> NUMBERS){

		double[] output = new double[2];

		double s1 = 0;
		double s2 = 0;


		for (int i=0; i<NUMBERS.size(); i++){

			s1 += NUMBERS.get(i);
			s2 += NUMBERS.get(i)*NUMBERS.get(i);

		}

		s1 /= NUMBERS.size();
		s2 = Math.sqrt(s2/NUMBERS.size()-s1*s1);

		output[0] = s1;
		output[1] = s2;

		return output;

	}

}
