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
import java.util.Arrays;



//=================================================================================
// Static class for elementary statistics computations
// Date : 13/01/2017
//=================================================================================

public class Tools {


	// ------------------------------------------------------------
	// Method for computing mean of a dataset
	// ------------------------------------------------------------
	public static double computeMean(ArrayList<Double> data){

		double somme = 0;

		for (int i=0; i<data.size(); i++){

			somme += data.get(i);

		}

		somme /= data.size();

		return somme;

	}

	// ------------------------------------------------------------
	// Method for computing standard deviation of a dataset
	// ------------------------------------------------------------
	public static double computeStandardDeviation(ArrayList<Double> data){

		double somme = 0;
		double somme_des_carres = 0;

		for (int i=0; i<data.size(); i++){

			somme += data.get(i);
			somme_des_carres += data.get(i)*data.get(i);

		}

		somme /= data.size();
		somme_des_carres /= data.size();

		double variance = data.size()/(data.size()-1)*(somme_des_carres - somme*somme);

		return Math.sqrt(variance);

	} 
	
	// ------------------------------------------------------------
	// Method for computing rank statistics of a dataset
	// ------------------------------------------------------------
	public static double computeQuantile(ArrayList<Double> data, double quantile){
		
		// Security tests
		
		if (quantile > 1){
			
			System.err.println("Error : quantile cannot be greater than 1.0");
			System.exit(1);
			
		}
		
		if (quantile < 0){
			
			System.err.println("Error : quantile cannot be negative");
			System.exit(1);
			
		}
		
		// Convert to array
		double[] values = new double[data.size()];
		
		for (int i=0; i<values.length; i++){
			
			values[i] = data.get(i);
			
		}
		
		// Sort list
		Arrays.sort(values);
		
		if (quantile == 0){
			
			return values[0];
			
		}
		
		if (quantile == 1.0){
			
			return values[values.length-1];
			
		}

		// Relative quantile
		int index = (int)(quantile*values.length);
		
		return values[index];
		
	}


	// ------------------------------------------------------------
	// Method to compute Z-Value with approximation
	// Input : confidence level p between 0 and 1
	// Output : threshold t such that P(|x|<t) = p where P is 
	// standard normal distribution
	// ------------------------------------------------------------
	public static double getZValue(double confidenceLevel){

		return phiInv((1+confidenceLevel)/2);

	}

	// ------------------------------------------------------------
	// Method to get Z-Value from a normal distribution table
	// ------------------------------------------------------------
	public static double getZValueFromTable(double confidenceLevel){

		double[] DATA = new double[41];

		DATA[0] = .5000;
		DATA[1] = .5398;
		DATA[2] = .5793;
		DATA[3] = .6179;
		DATA[4] = .6554;
		DATA[5] = .6915;
		DATA[6] = .7257;
		DATA[7] = .7580;
		DATA[8] = .7881;
		DATA[9] = .8159;
		DATA[10] = .8413;

		DATA[11] = .8643;
		DATA[12] = .8849;
		DATA[13] = .9032;
		DATA[14] = .9192;
		DATA[15] = .9332;
		DATA[16] = .9452;
		DATA[17] = .9554;
		DATA[18] = .9641;
		DATA[19] = .9713;
		DATA[20] = .9772;

		DATA[21] = .9821;
		DATA[22] = .9861;
		DATA[23] = .9893;
		DATA[24] = .9918;
		DATA[25] = .9938;
		DATA[26] = .9953;
		DATA[27] = .9965;
		DATA[28] = .9974;
		DATA[29] = .9981;
		DATA[30] = .99865;

		DATA[31] = .99903;
		DATA[32] = .99931;
		DATA[33] = .99952;
		DATA[34] = .99966;
		DATA[35] = .99977;
		DATA[36] = .99984;
		DATA[37] = .99989;
		DATA[38] = .99993;
		DATA[39] = .99995;
		DATA[40] = .99997;

		confidenceLevel = 1.0-(1.0-confidenceLevel)/2.0;

		int index = 0;

		for (int i=0; i<DATA.length; i++){

			if (DATA[i] > confidenceLevel){

				index = i;
				break;

			}

		}

		if (confidenceLevel > DATA[DATA.length-1]){index = DATA.length-1;}

		double c1 = (confidenceLevel - DATA[index-1]);
		double c2 = (DATA[index] - confidenceLevel);

		double z = 0.1*(c1*index + c2*(index-1))/(c1+c2);

		return Math.round(z*100)/100.0;

	}


	// ------------------------------------------------------------
	// Method to get normal distribution cdf values
	// ------------------------------------------------------------
	public static double phi(double x){

		x = x/Math.sqrt(2.0);

		double p = 0.47047;

		double a1 = .3480242;
		double a2 = -.0958798;
		double a3 = .7478556;

		double t = 1/(1+p*Math.abs(x));

		double y = 1-(t*(a1+t*(a2+t*a3)))*Math.exp(-x*x);

		y = (1+y)/2;

		if (x < 0){

			y = 1-y;

		}


		return y;


	}

	// ------------------------------------------------------------
	// Method to get normal distribution cdf inverse values
	// ------------------------------------------------------------
	public static double phiInv(double p){

		if ((p >= 1) || (p <= 0)){

			System.err.println("Error : probability argument in reverse Erf function should be in ]0;1[");
			System.exit(1);

		}

		double t = 0;

		if (p<0.5){
			t = Math.sqrt(-2.0*Math.log(p));
		}
		else{
			t = Math.sqrt(-2.0*Math.log(1-p));
		}

		double c[] = {2.515517, 0.802853, 0.010328};
		double d[] = {1.432788, 0.189269, 0.001308};

		double x = t - ((c[2]*t + c[1])*t + c[0]) / (((d[2]*t + d[1])*t + d[0])*t + 1.0);

		if (p < 0.5){

			x = -x;

		}		

		return x;

	}

	// -----------------------------------------------------
	// Fonction equation cartesienne
	// Entree : segment
	// Sortie : liste de parametres (a,b,c)
	// -----------------------------------------------------
	private static double[] cartesienne(double[] segment){

		double x1 = segment[0];
		double y1 = segment[1];
		double x2 = segment[2];
		double y2 = segment[3];

		double u1 = x2-x1;
		double u2 = y2-y1;

		double b = -u1;
		double a = u2;

		double c = -(a*x1+b*y1);

		double[] parametres = {a, b, c};

		return parametres;

	}

	// -----------------------------------------------------
	// Fonction de test d'equation de droite
	// Entrees : paramatres et coords (x,y)
	// Sortie : en particulier 0 si le point 
	// appartient a la droite
	// -----------------------------------------------------
	private static double eval(double[] param, double x, double y){

		double a = param[0];
		double b = param[1];
		double c = param[2];

		return a*x+b*y+c;

	}


	// -----------------------------------------------------
	// Fonction booleenne d'intersection
	// Entrees : segment1 et segment2
	// Sortie : true s'il y a intersection
	// -----------------------------------------------------
	private static boolean  intersects(double[] segment1, double[] segment2){

		double[] param_1 = cartesienne(segment1);
		double[] param_2 = cartesienne(segment2);

		double x11 = segment1[0];
		double y11 = segment1[1];
		double x12 = segment1[2];
		double y12 = segment1[3];

		double x21 = segment2[0];
		double y21 = segment2[1];
		double x22 = segment2[2];
		double y22 = segment2[3];

		double val11 = eval(param_1,x21,y21);
		double val12 = eval(param_1,x22,y22);

		double val21 = eval(param_2,x11,y11);
		double val22 = eval(param_2,x12,y12);

		double val1 = val11*val12;
		double val2 = val21*val22;

		return (val1 < 0) & (val2 < 0);

	}

	// -----------------------------------------------------
	// Fonction de test d'inclusion : point in polygon
	// Entrees : coordonnées (x,y) du point, coordonnées 
	// (X,Y) en tableau du polygone.
	// Sortie : booléen (true si et seulement si inclusion)
	// -----------------------------------------------------
	public static boolean inside(double x, double y, double[] X, double[] Y){

		int count1 = 0;
		int count2 = 0;
		int count3 = 0;

		double[] segment_point1 = {x, y, 1.10, -0.1};
		double[] segment_point2 = {x, y, 1.10, 1.10};
		double[] segment_point3 = {x, y, -0.1, -0.1};

		for (int i=0; i<X.length-1; i++){

			double[] segment_poly = {X[i], Y[i], X[i+1], Y[i+1]};


			if (intersects(segment_point1, segment_poly)){

				count1 ++;

			}

			if (intersects(segment_point2, segment_poly)){

				count2 ++;

			}

			if (intersects(segment_point3, segment_poly)){

				count3 ++;

			}

		}


		boolean bool1 = (count1 % 2 == 1);
		boolean bool2 = (count2 % 2 == 1);
		boolean bool3 = (count3 % 2 == 1);

		int somme = 0;

		if (bool1){somme ++;}
		if (bool2){somme ++;}
		if (bool3){somme ++;}

		return (somme >= 2);

	}

	// ------------------------------------------------------------
	// Method to get standard normal distribution pdf values
	// ------------------------------------------------------------
	public static double normalPdf(double x){

		return normalPdf(x, 0, 1);

	}

	// ------------------------------------------------------------
	// Method to get normal distribution pdf values
	// ------------------------------------------------------------
	public static double normalPdf(double x, double mean, double sigma){

		return 1/(sigma*Math.sqrt(2*Math.PI))*Math.exp(-0.5*Math.pow((x-mean)/sigma, 2));

	}

	// -----------------------------------------------------------------------------
	// Method to get unilateral Student's t level
	// Input : p is a confidence level (between 0 and 1), k is degrees of freedom
	// Output : threshold value t such that P(x < t | k) = p
	// -----------------------------------------------------------------------------
	public static double student(double p, int k){

		double step = 0.01;

		if ((p >= 1) || (p <= 0)){

			System.err.println("Error : probability argument in reverse Erf function should be in ]0;1[");
			System.exit(1);

		}

		for (double x=0; x<20; x+=step){

			double y = x * (1-1/(4*k))/Math.sqrt(1+x*x/(2*k));

			if (Tools.phi(y) > p){

				return x;

			}

		}

		return -9999;

	}

	// -----------------------------------------------------------------------------
	// Method to get bilateral Kolmogorov-Student confidence interval
	// Input : p is a confidence level (between 0 and 1)
	// Output : threshold value (assumes that n > 29 data instances are available)
	// -----------------------------------------------------------------------------
	public static double kolmogorovSmirnov(double p){

		for (double c=0; c<100; c+=0.01){

			double alpha = 0;

			double f = Math.pow(c, 2);

			for (int r=1; r<10; r++){

				alpha += Math.pow(-1, r-1)*Math.exp(-2*f*Math.pow(r, 2));

			}

			alpha *= 2;

			if (alpha <= (1-p)){

				return round(c, 2);

			}

		}

		return 0;

	}

	// -----------------------------------------------------------------------------
	// Method to round number
	// -----------------------------------------------------------------------------
	public static double round(double x, int decimal){

		return Math.floor(x*Math.pow(10, decimal))/Math.pow(10, decimal);

	}

}
