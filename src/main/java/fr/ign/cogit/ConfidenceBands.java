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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;


//=================================================================================
// Class for computing confidence bands from a ROC curve
// Requires a ReceivingOperatorCharacteristic object, a confidence level (between 
// 0 and 100 in %) and a computation method (integer listed in top of the code) 
// It is also possible (for some of the methods) to specify the distribution 
// assumption between normal (or binormal) and binomial.
// Date : 12/01/2017
//=================================================================================

public class ConfidenceBands {

	// Computation methods
	public static int METHOD_VERTICAL_AVERAGING = 1;
	public static int METHOD_THRESHOLD_AVERAGING = 2;
	public static int METHOD_KOLMOGOROV_SMIRNOV = 3;
	public static int METHOD_FIXED_WIDTH_BAND = 4;

	// Distribution
	public static int DISTRIBUTION_NORMAL = 1;
	public static int DISTRIBUTION_BINORMAL = 2;
	public static int DISTRIBUTION_BINOMIAL = 3;
	public static int DISTRIBUTION_STUDENT = 4;

	// Instance number mode
	public static int INSTANCE_NUMBER_SUM = 1;
	public static int INSTANCE_NUMBER_AVG  = 2;
	public static int INSTANCE_NUMBER_MAX = 3;
	public static int INSTANCE_NUMBER_FST = 4;

	// Bands
	private double[] upperband_y;
	private double[] upperband_x;
	private double[] lowerband_y;
	private double[] lowerband_x;

	// Central ROC
	private double[] XROC;
	private double[] YROC;

	// Type
	private int method;

	// Z Value
	private double zValue;

	// Confidence level
	private double confidenceLevel;

	// Size
	private int resolution;

	// Graphics
	private Color color = new Color(0.f,1.f,0.f,0.15f);
	private Color borderColor = Color.GREEN;
	private boolean borderVisible = true;
	private boolean backgroundFilled = true;

	// ROC data
	private ArrayList<ReceiverOperatingCharacteristics> ROCS;

	// Instances number
	private int TP = 0;
	private int TN = 0;

	// Stroke
	private int dash_on = 5;
	private int dash_off = 5; 

	// Instance number computation mode
	private static int instancesNumberComputationMode = INSTANCE_NUMBER_SUM;

	// Resampled curves
	private double[] TPR;
	private double[] FPR;
	private double[] FPR_upper;
	private double[] TPR_upper;
	private double[] TPR_lower;
	private double[] FPR_lower;

	// Error bars attributes
	private double horizontal = 0.02;
	private double ecart = 0.05;
	private double thickness = 1.0;
	private boolean errorBarsVisible = false;
	private boolean errorBarsModeXy = false;
	private Color errorBarsColor = Color.BLACK;

	// Dichotomy iteration number
	private static double fixedBwSearchIterationNumber = 20;

	// Verbose mode
	private static boolean verbose = false;

	// Central Roc curve
	private ReceiverOperatingCharacteristics centralRoc;

	// Special error bars for threshold method
	private double[] H;
	private double[] h;

	// Setters
	public void setColor(Color color){this.color = color;}
	public void setBordersColor(Color color){this.borderColor = color;}
	public void setBordersVisible(boolean bool){this.borderVisible = bool;}
	public void setBackgroundVisible(boolean bool){this.backgroundFilled = bool;}
	public void setErrorBarsWidth(double w){horizontal = w;}
	public void setErrorBarsResolution(double r){ecart = r;}
	public void setErrorBarsThickness(double t){thickness = t;}
	public void setErrorBarsColor(Color c){errorBarsColor = c;}
	public void setErrorBarsVisible(boolean visible){errorBarsVisible = visible;}

	public void setDashLength(int l){dash_on = l;}
	public void setDashInterval(int l){dash_off = l;}

	public static void setVerbose(boolean bool){verbose = bool;}
	public static void setfixedBwSearchIterationNumber(int n){fixedBwSearchIterationNumber = n;}

	public void setErrorBarsModeXY(boolean bool){

		if ((bool) && (method != METHOD_THRESHOLD_AVERAGING)){

			String met = "";

			if (method == METHOD_KOLMOGOROV_SMIRNOV){
				met = "Kolmgogorov-Smirnov";
			}
			if (method == METHOD_VERTICAL_AVERAGING){
				met = "vertical averaging";
			}
			if (method == METHOD_FIXED_WIDTH_BAND){
				met = "fixed band-width";
			}

			System.err.println("Error : XY mode for error bars is not valid for "+met+" computation method");
			System.exit(1);

		}

		this.errorBarsModeXy = bool;

	}

	public void setErrorBarsTransparency(float t){

		float r = (float)(errorBarsColor.getRed()/255.0);
		float v = (float)(errorBarsColor.getGreen()/255.0);
		float b = (float)(errorBarsColor.getBlue()/255.0);

		errorBarsColor = new Color(r, v, b, t);

	}

	public static void setInstancesNumberComputationMode(int mode){instancesNumberComputationMode = mode;}


	public void setTransparency(float transparency){

		this.color = new Color(color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, transparency);

	}

	public void setBordersTransparency(float transparency){

		this.borderColor = new Color(borderColor.getRed()/255.0f, borderColor.getGreen()/255.0f, borderColor.getBlue()/255.0f, transparency);

	}


	// Getters
	public Color getColor(){return this.color;}
	public int getResolution(){return this.resolution;}
	public int getMethod(){return this.method;}
	public ReceiverOperatingCharacteristics getCentralROC(){return centralRoc;}
	public ReceiverOperatingCharacteristics getResampledCentralROC(){return new ReceiverOperatingCharacteristics(FPR, TPR);}
	public static int getInstancesNumberComputationMode(){return instancesNumberComputationMode;}


	public double[] getUpperBandX(){return this.upperband_x;}
	public double[] getUpperBandY(){return this.upperband_y;}
	public double[] getLowerBandX(){return this.lowerband_x;}
	public double[] getLowerBandY(){return this.lowerband_y;}

	public double[] getResampledUpperBandX(){return this.FPR_upper;}
	public double[] getResampledUpperBandY(){return this.TPR_upper;}
	public double[] getResampledLowerBandX(){return this.FPR_lower;}
	public double[] getResampledLowerBandY(){return this.TPR_lower;}

	public double getConfidenceLevel(){return this.confidenceLevel;}
	public double getZValue(){return this.zValue;}

	public Color getBordersColor(){return borderColor;}
	public boolean isBordersColorVisible(){return borderVisible;}
	public boolean isBackgroundFilled(){return backgroundFilled;}

	public double getErrorBarsWidth(){return horizontal;}
	public double getErrorBarsResolution(){return ecart;}
	public double getErrorBarsThickness(){return thickness;}
	public boolean getErrorBarsVisible(){return errorBarsVisible;}
	public boolean getErrorBarsModeXY(){return errorBarsModeXy;}
	public Color getErrorBarsColor(){return errorBarsColor;}

	public int getDashLength(){return dash_on;}
	public int getDashInterval(){return dash_off;}

	protected double[] getErrorBarsH(){return H;}
	protected double[] getErrorBarsh(){return h;}



	// ---------------------------------------------------------------------------
	// Main constructor 1-1
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ReceiverOperatingCharacteristics roc){

		this(roc, METHOD_THRESHOLD_AVERAGING, 95.0, DISTRIBUTION_NORMAL);

	}


	// ---------------------------------------------------------------------------
	// Main constructor 1-2
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ReceiverOperatingCharacteristics roc, double confidenceLevel){

		this(roc, METHOD_THRESHOLD_AVERAGING, confidenceLevel, DISTRIBUTION_NORMAL);

	}


	// ---------------------------------------------------------------------------
	// Main constructor 1-3
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ReceiverOperatingCharacteristics roc, int method){

		this(roc, method, 95.0, DISTRIBUTION_NORMAL);

	}

	// ---------------------------------------------------------------------------
	// Main constructor 1-4
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ReceiverOperatingCharacteristics roc, int method, double confidenceLevel){

		this(roc, method, confidenceLevel, DISTRIBUTION_NORMAL);

	}

	// ---------------------------------------------------------------------------
	// Main constructor 1-5
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ReceiverOperatingCharacteristics roc, int method, double confidenceLevel, int distribution){

		this(new ArrayList<ReceiverOperatingCharacteristics>(Arrays.asList(roc)), method, confidenceLevel, distribution);

	}


	// ---------------------------------------------------------------------------
	// Main constructor 2-1
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ArrayList<ReceiverOperatingCharacteristics> rocs){

		this(rocs, METHOD_THRESHOLD_AVERAGING, 95.0, DISTRIBUTION_NORMAL);

	}


	// ---------------------------------------------------------------------------
	// Main constructor 2-2
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ArrayList<ReceiverOperatingCharacteristics> rocs, double confidenceLevel){

		this(rocs, METHOD_THRESHOLD_AVERAGING, confidenceLevel, DISTRIBUTION_NORMAL);

	}


	// ---------------------------------------------------------------------------
	// Main constructor 2-3
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ArrayList<ReceiverOperatingCharacteristics> rocs, int method){

		this(rocs, method, 95.0, DISTRIBUTION_NORMAL);

	}


	// ---------------------------------------------------------------------------
	// Main constructor 2-4
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ArrayList<ReceiverOperatingCharacteristics> rocs, int method, double confidenceLevel){

		this(rocs, method, confidenceLevel, DISTRIBUTION_NORMAL);

	}


	// ---------------------------------------------------------------------------
	// Main constructor 2-5
	// ---------------------------------------------------------------------------
	public ConfidenceBands(ArrayList<ReceiverOperatingCharacteristics> rocs, int method, double confidenceLevel, int distribution){

		// Control
		if ((confidenceLevel >= 100) || (confidenceLevel <= 0)){

			System.err.println("Error : confidence level must be included in ]0,100[ interval");
			System.exit(1);

		}

		if (rocs.size() == 0){

			System.err.println("Error : ROC set must include at least one ROC curve");
			System.exit(1);

		}


		if (method != METHOD_KOLMOGOROV_SMIRNOV){

			if ((distribution != DISTRIBUTION_BINOMIAL) || (method == METHOD_FIXED_WIDTH_BAND)){

				if (rocs.size() < 2){

					String message = "Error : ROC set must include at least 2 ROC curves. Please use Kolmogorov-Smirnov computation method or binomial distribution assumption"
							+ " (with averaging methods only)\r\n";

					System.err.println(message);
					System.exit(1);

				}

			}

		}

		this.method = method;

		ROCS = rocs;

		// Determining size of bands

		this.resolution = rocs.get(0).getResolution();

		upperband_y = new double[this.resolution];
		upperband_x = new double[this.resolution];
		lowerband_y = new double[this.resolution];
		lowerband_x = new double[this.resolution];

		XROC = new double[this.resolution];
		YROC = new double[this.resolution];

		for (int i=1; i<rocs.size(); i++){

			if (rocs.get(i).getResolution() != this.resolution){

				System.err.println("Error : ROC curves must be of same resolutions");
				System.exit(1);

			}

		}

		this.confidenceLevel = confidenceLevel;

		// ----------------------------------------
		// Computing Z-value from confidence level
		// ----------------------------------------

		if (distribution != DISTRIBUTION_STUDENT){

			this.zValue = Tools.getZValue(confidenceLevel/100.0);

		}

		if (distribution == DISTRIBUTION_STUDENT){

			this.zValue = Tools.student((confidenceLevel/100.0+1.0)/2.0, rocs.size()-1);

		}


		// -----------------------------------------------------------------------
		// Computing bands with vertical averaging method and normal distribution
		// -----------------------------------------------------------------------
		if ((method == METHOD_VERTICAL_AVERAGING) && (distribution != DISTRIBUTION_BINOMIAL)){

			// Security test
			if (rocs.size() <= 1){

				String message = "Error : at least 2 ROC curves must be defined to compute confidence bands with ";
				message += "vertical averaging method and non-binomial distribution";

				System.err.println(message);
				System.exit(1);

			}

			// Interpolation

			ArrayList<Double[]> INTERPOLATED = new ArrayList<Double[]>();

			for (int i=0; i<ROCS.size(); i++){

				double[] a = ROCS.get(i).resample().getYRoc();

				Double[] d = new Double[a.length];

				for (int j=0; j<a.length; j++){

					d[j] = a[j];

				}

				INTERPOLATED.add(d);

			}


			// Averaging

			for (int i=0; i<resolution; i++){

				double x = (double)(i)/(double)(resolution);

				ArrayList<Double> datay = new ArrayList<Double>();

				for (int j=0; j<INTERPOLATED.size(); j++){

					datay.add(INTERPOLATED.get(j)[i]);

				}


				XROC[i] = x;
				YROC[i] = Tools.computeMean(datay);

				upperband_x[i] = XROC[i];
				upperband_y[i] = YROC[i] + zValue*Tools.computeStandardDeviation(datay)/Math.sqrt(ROCS.size());

				lowerband_x[i] = XROC[i];
				lowerband_y[i] = YROC[i] - zValue*Tools.computeStandardDeviation(datay)/Math.sqrt(ROCS.size());

				// Truncation
				upperband_y[i] = Math.min(upperband_y[i], 1);
				lowerband_y[i] = Math.max(lowerband_y[i], 0);

				upperband_x[i] = Math.max(upperband_x[i], 0);
				lowerband_x[i] = Math.max(lowerband_x[i], 0);

				upperband_x[i] = Math.min(upperband_x[i], 1);
				lowerband_x[i] = Math.min(lowerband_x[i], 1);

			}

		}


		// -------------------------------------------------------------------------
		// Computing bands with vertical averaging method and binomial distribution
		// -------------------------------------------------------------------------
		if ((method == METHOD_VERTICAL_AVERAGING) && (distribution == DISTRIBUTION_BINOMIAL)){

			// Security test
			if (rocs.size() == 0){

				String message = "Error : at least 1 ROC curve must be defined to compute confidence bands with ";
				message += "vertical averaging method and binomial distribution";

				System.err.println(message);
				System.exit(1);

			}

			computeInstancesNumber(rocs, instancesNumberComputationMode);

			if ((TP == 0) || (TN == 0)){

				String message = "Error : true positive and negative instances numbers must be defined (strictly positive) ";
				message = message + "before attempting to compute ROC curve confidence bands with ";
				message = message + "vertical averaging method and binomial distribution";

				System.err.println(message);
				System.exit(1);

			}

			// Interpolation

			ArrayList<Double[]> INTERPOLATED = new ArrayList<Double[]>();

			for (int i=0; i<ROCS.size(); i++){

				INTERPOLATED.add(new Double[resolution]);

				for (int j=0; j<resolution; j++){

					double x = (double)(j)/(double)(resolution);

					for (int k=1; k<resolution; k++){

						double xk = ROCS.get(i).getXRoc()[k];

						if (xk > x){

							double y1 = ROCS.get(i).getYRoc()[k-1];
							double y2 = ROCS.get(i).getYRoc()[k];

							double d1 = x - ROCS.get(i).getXRoc()[k-1];
							double d2 = xk - x;

							double w1 = d2/(d1+d2);
							double w2 = d1/(d1+d2);


							INTERPOLATED.get(i)[j] = w1*y1+w2*y2;

							break;

						}

					}

				}

			}


			// Averaging

			for (int i=0; i<resolution; i++){

				double x = (double)(i)/(double)(resolution);

				ArrayList<Double> datay = new ArrayList<Double>();

				for (int j=0; j<INTERPOLATED.size(); j++){

					datay.add(INTERPOLATED.get(j)[i]);

				}

				XROC[i] = x;
				YROC[i] = Tools.computeMean(datay);


				// -----------------------------------------------------
				// Wilson confidence interval with continuity correction
				// -----------------------------------------------------

				double quotient = TP+Math.pow(zValue,2);

				double w1 = TP/quotient;
				double w2 =Math.pow(zValue,2)/quotient;

				double ptild = w1*YROC[i] + w2*0.5;
				double se = Math.sqrt(w1*YROC[i]*(1-YROC[i])/quotient  +  w2*0.25/quotient);


				upperband_x[i] = XROC[i];
				upperband_y[i] = ptild + zValue*se + 0.5/quotient;

				lowerband_x[i] = XROC[i];
				lowerband_y[i] = ptild - zValue*se - 0.5/quotient;

				// Truncation
				upperband_y[i] = Math.min(upperband_y[i], 1);
				lowerband_y[i] = Math.max(lowerband_y[i], 0);

				upperband_x[i] = Math.max(upperband_x[i], 0);
				lowerband_x[i] = Math.max(lowerband_x[i], 0);

				upperband_x[i] = Math.min(upperband_x[i], 1);
				lowerband_x[i] = Math.min(lowerband_x[i], 1);

			}

		}


		// -----------------------------------------------------------------------
		// Computing bands with threshold averaging method and normal distribution
		// -----------------------------------------------------------------------
		if ((method == METHOD_THRESHOLD_AVERAGING) && (distribution != DISTRIBUTION_BINOMIAL)){

			// Security test
			if (rocs.size() <= 1){

				String message = "Error : at least 2 ROC curves must be defined to compute confidence bands with ";
				message += "threshold averaging method and non-binomial distribution";

				System.err.println(message);
				System.exit(1);

			}

			H = new double[resolution];
			h = new double[resolution];

			for (int i=0; i<resolution; i++){

				ArrayList<Double> datax = new ArrayList<Double>();
				ArrayList<Double> datay = new ArrayList<Double>();

				for (int j=0; j<rocs.size(); j++){

					datax.add(rocs.get(j).getXRoc()[i]);
					datay.add(rocs.get(j).getYRoc()[i]);

				}

				XROC[i] = Tools.computeMean(datax);
				YROC[i] = Tools.computeMean(datay);

				double sx = Tools.computeStandardDeviation(datax);
				double sy = Tools.computeStandardDeviation(datay);

				upperband_x[i] = XROC[i] - zValue*sx/Math.sqrt(ROCS.size());
				upperband_y[i] = YROC[i] + zValue*sy/Math.sqrt(ROCS.size());

				lowerband_x[i] = XROC[i] + zValue*sx/Math.sqrt(ROCS.size());
				lowerband_y[i] = YROC[i] - zValue*sy/Math.sqrt(ROCS.size());

				// Truncation
				upperband_y[i] = Math.min(upperband_y[i], 1);
				lowerband_y[i] = Math.max(lowerband_y[i], 0);

				upperband_x[i] = Math.max(upperband_x[i], 0);
				lowerband_x[i] = Math.max(lowerband_x[i], 0);

				upperband_x[i] = Math.min(upperband_x[i], 1);
				lowerband_x[i] = Math.min(lowerband_x[i], 1);

				// Special bars
				H[i] = zValue*sy/Math.sqrt(ROCS.size());
				h[i] = zValue*sx/Math.sqrt(ROCS.size());


			}

		}

		// -------------------------------------------------------------------------
		// Computing bands with threshold averaging method and binomial distribution
		// -------------------------------------------------------------------------
		if ((method == METHOD_THRESHOLD_AVERAGING) && (distribution == DISTRIBUTION_BINOMIAL)){

			// Security test
			if (rocs.size() == 0){

				String message = "Error : at least 1 ROC curve must be defined to compute confidence bands with ";
				message += "threshold averaging method and binomial distribution";

				System.err.println(message);
				System.exit(1);

			}

			computeInstancesNumber(rocs, instancesNumberComputationMode);

			if ((TP == 0) || (TN == 0)){

				String message = "Error : positive and instances numbers must be defined (strictly positive) ";
				message = message + "before attempting to compute ROC curve confidence bands with ";
				message = message + "threshold averaging method and binomial distribution";

				System.err.println(message);
				System.exit(1);

			}

			H = new double[resolution];
			h = new double[resolution];

			for (int i=0; i<resolution; i++){

				ArrayList<Double> datax = new ArrayList<Double>();
				ArrayList<Double> datay = new ArrayList<Double>();

				for (int j=0; j<rocs.size(); j++){

					datax.add(rocs.get(j).getXRoc()[i]);
					datay.add(rocs.get(j).getYRoc()[i]);

				}

				XROC[i] = Tools.computeMean(datax);
				YROC[i] = Tools.computeMean(datay);


				double quotientP = TP+Math.pow(zValue,2);

				double w1P = TP/quotientP;
				double w2P =Math.pow(zValue,2)/quotientP;

				double ptildP = w1P*YROC[i] + w2P*0.5;
				double seP = Math.sqrt(w1P*YROC[i]*(1-YROC[i])/quotientP  +  w2P*0.25/quotientP);

				double quotientN = TN+Math.pow(zValue,2);

				double w1N = TN/quotientN;
				double w2N =Math.pow(zValue,2)/quotientN;

				double ptildN = w1N*XROC[i] + w2N*0.5;
				double seN = Math.sqrt(w1N*XROC[i]*(1-XROC[i])/quotientN  +  w2N*0.25/quotientN);


				upperband_x[i] = ptildN - zValue*seN - 0.5/quotientN;
				upperband_y[i] = ptildP + zValue*seP +  0.5/quotientP;

				lowerband_x[i] = ptildN  + zValue*seN +  0.5/quotientN;
				lowerband_y[i] = ptildP  - zValue*seP -  0.5/quotientP;

				// Truncation
				upperband_y[i] = Math.min(upperband_y[i], 1);
				lowerband_y[i] = Math.max(lowerband_y[i], 0);

				upperband_x[i] = Math.max(upperband_x[i], 0);
				lowerband_x[i] = Math.max(lowerband_x[i], 0);

				upperband_x[i] = Math.min(upperband_x[i], 1);
				lowerband_x[i] = Math.min(lowerband_x[i], 1);

				// Special bars
				H[i] = zValue*Math.sqrt(YROC[i]*(1-YROC[i])/TP);
				h[i] = zValue*Math.sqrt(XROC[i]*(1-XROC[i])/TN);

			}

		}


		// -----------------------------------------------------------------------
		// Computing bands with fixed width bands method
		// -----------------------------------------------------------------------
		if (method == METHOD_FIXED_WIDTH_BAND){

			// Security test
			if (rocs.size() <= 0){

				String message = "Error : at least 2 ROC curves must be defined to compute confidence bands with ";
				message += "fixed-band width method";

				System.err.println(message);
				System.exit(1);

			}

			// Consistance test
			if (distribution != DISTRIBUTION_NORMAL){

				System.out.println("Warning : fixed band-width confidence interval computation method does not assume any specific probability distribution");

			}

			computeInstancesNumber(rocs, instancesNumberComputationMode);

			if ((TP == 0) || (TN == 0)){

				String message = "Error : true positive and negative instance numbers must be defined (strictly positive) ";
				message = message + "before attempting to compute ROC curve confidence bands with ";
				message = message + "fixed band-width method";

				System.err.println(message);
				System.exit(1);

			}


			// Computing band

			double b = 0.0;
			double ratio = 0;
			boolean[] outlier = new boolean[rocs.size()];

			double binf = 0;
			double bsup = Math.sqrt(TN+TP)/15.0;

			// ----------------------------------------------------------------
			// Dichotomic search of band-width b
			// ----------------------------------------------------------------

			for (int search=0; search<fixedBwSearchIterationNumber; search++){

				b = (binf+bsup)/2.0;

				for (int k=0; k<outlier.length; k++){

					outlier[k] = false;

				}

				// ------------------------------------------------------------
				// Test on each position in ROC space
				// ------------------------------------------------------------

				for (int i=0; i<resolution; i++){

					ArrayList<Double> datax = new ArrayList<Double>();
					ArrayList<Double> datay = new ArrayList<Double>();

					// --------------------------------------------------------
					// Test for each ROC curve
					// --------------------------------------------------------

					for (int j=0; j<rocs.size(); j++){

						datax.add(rocs.get(j).getXRoc()[i]);
						datay.add(rocs.get(j).getYRoc()[i]);

					}

					XROC[i] = Tools.computeMean(datax);
					YROC[i] = Tools.computeMean(datay);

					// Confidence bands trial computation

					upperband_x[i] = XROC[i] - b/Math.sqrt(TN);
					upperband_y[i] = YROC[i] + b/Math.sqrt(TP);

					lowerband_x[i] = XROC[i] + b/Math.sqrt(TN);
					lowerband_y[i] = YROC[i] - b/Math.sqrt(TP);

					// Truncation
					upperband_y[i] = Math.min(upperband_y[i], 1);
					lowerband_y[i] = Math.max(lowerband_y[i], 0);

					upperband_x[i] = Math.max(upperband_x[i], 0);
					lowerband_x[i] = Math.max(lowerband_x[i], 0);

					upperband_x[i] = Math.min(upperband_x[i], 1);
					lowerband_x[i] = Math.min(lowerband_x[i], 1);


					// Correction
					if (upperband_y[0] > 0.5){

						upperband_x[0] = 1.0; 
						upperband_y[0] = 1.0; 

						upperband_x[upperband_y.length-1] = 0.0; 
						upperband_y[upperband_y.length-1] = 0.0;

					}
					else{

						upperband_x[0] = 0.0; 
						upperband_y[0] = 0.0; 

						lowerband_x[0] = 0.0; 
						lowerband_y[0] = 0.0;

						upperband_x[upperband_y.length-1] = 1.0; 
						upperband_y[upperband_y.length-1] = 1.0;

					}


				}

				// ------------------------------------------------------------
				// Transform confidence bands into polygon
				// ------------------------------------------------------------

				double[] poly_x = new double[upperband_x.length+lowerband_x.length+1];
				double[] poly_y = new double[upperband_x.length+lowerband_y.length+1];


				for (int k=0; k<upperband_x.length; k++){

					poly_x[k] = upperband_x[k];
					poly_y[k] = upperband_y[k];

				}

				for (int k=0; k<lowerband_x.length; k++){

					poly_x[poly_x.length-k-2] = lowerband_x[k];
					poly_y[poly_y.length-k-2] = lowerband_y[k];

				}

				poly_x[poly_x.length-1] = poly_x[0];
				poly_y[poly_y.length-1] = poly_y[0];

				// ------------------------------------------------------------
				// ROC curve inclusion test
				// ------------------------------------------------------------

				// For eeach point in ROC space

				for (int i=0; i<rocs.size(); i++){

					// For each ROC curve

					for (int j=0; j<rocs.get(i).getXRoc().length; j++){

						double x = rocs.get(i).getXRoc()[j];
						double y = rocs.get(i).getYRoc()[j];

						// Remove non robust side points
						if (x*(1-x) + y*(1-y) <= 0.02){

							continue;

						}

						// Inclusion test
						if (!Tools.inside(x, y, poly_x, poly_y)){

							outlier[i] = true;
							break;

						}

					}

				}

				// ------------------------------------------------------------
				// Computing ratio of ROC curve in confidence bands
				// ------------------------------------------------------------

				ratio = 0.0;

				for (int j=0; j<outlier.length; j++){

					if (outlier[j]){ratio ++;}

				}

				ratio = 1 - ratio / (double) outlier.length;

				// ------------------------------------------------------------
				// Console output (if verbose mode activated)
				// ------------------------------------------------------------

				if(verbose){

					System.out.println("[Threshold = "+Math.round(b*10)/10.0+" - ratio = "+Math.round(ratio*10000)/100.0+" %]");

				}

				// ------------------------------------------------------------
				// Binary search condition
				// ------------------------------------------------------------

				if (ratio < confidenceLevel/100){

					binf = b;

				}
				else{

					bsup = b;

				}

			}

			// Final result
			this.zValue = b;

			// ------------------------------------------------------------
			// Console output (if verbose mode activated)
			// ------------------------------------------------------------

			if(verbose){

				System.out.println("----------------------------------------");
				System.out.println("True positive instance number : "+TP);
				System.out.println("True negative instance number : "+TN);
				System.out.println("----------------------------------------");
				System.out.println("Search iteration number : "+fixedBwSearchIterationNumber);
				System.out.println("Final resolution : "+(bsup-binf));
				System.out.println("Band width estimation : b = "+Math.round(b*100)/100.0);
				System.out.println("Effective confidence level : "+Math.min(Math.round(ratio*10000)/100.0, 99.99)+" %");
				System.out.println("----------------------------------------");

			}

		}

		// -----------------------------------------------------------------------
		// Computing bands with Kolmogorov-Smirnov method
		// -----------------------------------------------------------------------
		if (method == METHOD_KOLMOGOROV_SMIRNOV){

			// Security test
			if (rocs.size() == 0){

				String message = "Error : at least 1 ROC curve must be defined to compute confidence bands with ";
				message += "Kolmogorov-Smirnov method";

				System.err.println(message);
				System.exit(1);

			}
			
			double zv = 1.07;

			// Kolmogorov table
			if (confidenceLevel > 82.5){zv = 1.14;}
			if (confidenceLevel > 87.5){zv = 1.22;}
			if (confidenceLevel > 92.5){zv = 1.36;}
			if (confidenceLevel > 97.0){zv = 1.63;}

			this.zValue = Tools.kolmogorovSmirnov(confidenceLevel/100.0);
			
			zv = this.zValue;

			computeInstancesNumber(rocs, instancesNumberComputationMode);

			// Security test
			if ((TP == 0) || (TN == 0)){

				String message = "Error : true positive and negative instances numbers must be defined (strictly positive) ";
				message = message + "before attempting to compute ROC curve confidence bands with ";
				message = message + "Kolmogorov-Smirnov method";

				System.err.println(message);

				System.exit(0);

			}
			
			// Warning
			if ((TP <= 30) || (TN <= 30)) {

				String message = "Warning : at least 30 instances in each class are required for computing confidence bands with ";
				message += "Kolmogorov-Smirnov method";

				System.out.println(message);

			}


			// For each point in ROC space
			for (int i=0; i<resolution; i++){

				ArrayList<Double> datax = new ArrayList<Double>();
				ArrayList<Double> datay = new ArrayList<Double>();

				// For each ROC curve
				for (int j=0; j<rocs.size(); j++){

					datax.add(rocs.get(j).getXRoc()[i]);
					datay.add(rocs.get(j).getYRoc()[i]);

				}

				XROC[i] = Tools.computeMean(datax);
				YROC[i] = Tools.computeMean(datay);

				// Computing confidence bands

				upperband_x[i] = XROC[i] - zv/Math.sqrt(TN);
				upperband_y[i] = YROC[i] + zv/Math.sqrt(TP);

				lowerband_x[i] = XROC[i] + zv/Math.sqrt(TN);
				lowerband_y[i] = YROC[i] - zv/Math.sqrt(TP);

				// Truncation
				upperband_y[i] = Math.min(upperband_y[i], 1);
				lowerband_y[i] = Math.max(lowerband_y[i], 0);

				upperband_x[i] = Math.max(upperband_x[i], 0);
				lowerband_x[i] = Math.max(lowerband_x[i], 0);

				upperband_x[i] = Math.min(upperband_x[i], 1);
				lowerband_x[i] = Math.min(lowerband_x[i], 1);

				// Correction
				if (upperband_y[0] > 0.5){

					upperband_x[0] = 1.0; 
					upperband_y[0] = 1.0; 

					upperband_x[upperband_y.length-1] = 0.0; 
					upperband_y[upperband_y.length-1] = 0.0;

				}
				else{

					upperband_x[0] = 0.0; 
					upperband_y[0] = 0.0; 

					lowerband_x[0] = 0.0; 
					lowerband_y[0] = 0.0;

					upperband_x[upperband_y.length-1] = 1.0; 
					upperband_y[upperband_y.length-1] = 1.0;

				}

			}

		}


		// Resampling confidence bands
		resample(ROCS.get(0).getResolution());

		// Resampling central ROC curve
		centralRoc = new ReceiverOperatingCharacteristics(XROC, YROC);

		ReceiverOperatingCharacteristics roc_central_resampled = getCentralROC().resample();

		FPR = roc_central_resampled.getXRoc();
		TPR = roc_central_resampled.getYRoc();

		getCentralROC().setPositiveInstancesNumber(TP);
		getCentralROC().setNegativeInstancesNumber(TN);

	}

	// ---------------------------------------------------------------------------
	// Method for computing Area Under Curve index confidence interval
	// ---------------------------------------------------------------------------
	public double computeAUCConfidenceInterval(double confidenceLevel, int distribution){

		// Z-value
		double z = Tools.getZValue(confidenceLevel/100.0);

		double std = 0;

		// Compute uncertainty on AUC list

		ArrayList<Double> AUC = new ArrayList<Double>();

		for (int i=0; i<ROCS.size(); i++){

			AUC.add(ROCS.get(i).computeAUC());

		}
		
		std = Tools.computeStandardDeviation(AUC);
		

		if (distribution == DISTRIBUTION_BINORMAL){

			System.err.println("Error : binormal distribution is not a valid option for AUC index confidence interval computation");
			System.exit(1);

		}


		if  (distribution == DISTRIBUTION_STUDENT) {
			
			return Tools.student(0.5*(1+confidenceLevel/100), ROCS.size())*std/Math.sqrt(ROCS.size());

		}

		if (distribution == DISTRIBUTION_BINOMIAL){


			System.err.println("Error : binomial distribution is not a valid option for AUC index confidence interval computation");
			System.exit(1);

		}

		return z*std/Math.sqrt(ROCS.size());

	}

	// ---------------------------------------------------------------------------
	// Method for computing instances number
	// ---------------------------------------------------------------------------
	private void computeInstancesNumber(ArrayList<ReceiverOperatingCharacteristics> ROCS, int method){

		if (method == INSTANCE_NUMBER_SUM){

			for (int i=0; i<ROCS. size(); i++){

				TP += ROCS.get(i).getPositiveInstancesNumber();
				TN += ROCS.get(i).getNegativeInstancesNumber();

			}

		}

		if (method == INSTANCE_NUMBER_AVG){

			for (int i=0; i<ROCS. size(); i++){

				TP += ROCS.get(i).getPositiveInstancesNumber();
				TN += ROCS.get(i).getNegativeInstancesNumber();

			}

			TP /= ROCS.size();
			TN /= ROCS.size();

		}

		if (method == INSTANCE_NUMBER_MAX){

			for (int i=0; i<ROCS. size(); i++){

				int TPt = ROCS.get(i).getPositiveInstancesNumber();
				int TNt = ROCS.get(i).getNegativeInstancesNumber();

				if (TPt > TP){TP = TPt;}
				if (TNt > TN){TN = TNt;}

			}

		}


		if (method == INSTANCE_NUMBER_FST){

			TP = ROCS.get(0).getPositiveInstancesNumber();
			TN = ROCS.get(0).getNegativeInstancesNumber();

		}

	}

	// ---------------------------------------------------------------------------
	// Method for computing confidence bands isolines
	// ---------------------------------------------------------------------------
	public static ArrayList<ConfidenceBands> makeConfidenceIsolines(ArrayList<ReceiverOperatingCharacteristics> rocs){

		return makeConfidenceIsolines(rocs, 1, 99, 1);

	}

	// ---------------------------------------------------------------------------
	// Method for computing confidence bands isolines
	// ---------------------------------------------------------------------------
	public static ArrayList<ConfidenceBands> makeConfidenceIsolines(ArrayList<ReceiverOperatingCharacteristics> rocs, double confidence_min, double confidence_max, double confidence_step, ColorMap cmap){

		return makeConfidenceIsolines(rocs, confidence_min, confidence_max, confidence_step, ConfidenceBands.METHOD_THRESHOLD_AVERAGING, ConfidenceBands.DISTRIBUTION_BINOMIAL, cmap);

	}


	// ---------------------------------------------------------------------------
	// Method for computing confidence bands isolines
	// ---------------------------------------------------------------------------
	public static ArrayList<ConfidenceBands> makeConfidenceIsolines(ArrayList<ReceiverOperatingCharacteristics> rocs, double confidence_min, double confidence_max, double confidence_step){

		return makeConfidenceIsolines(rocs, confidence_min, confidence_max, confidence_step, ConfidenceBands.METHOD_THRESHOLD_AVERAGING, ConfidenceBands.DISTRIBUTION_BINOMIAL, ColorMap.TYPE_MATHS);

	}

	// ---------------------------------------------------------------------------
	// Method for computing confidence bands isolines
	// ---------------------------------------------------------------------------
	public static ArrayList<ConfidenceBands> makeConfidenceIsolines(ArrayList<ReceiverOperatingCharacteristics> rocs, double confidence_min, double confidence_max, double confidence_step, int method, int distribution, ColorMap cmap){

		ArrayList<ConfidenceBands> BANDS = new ArrayList<ConfidenceBands>();

		for (double confidence=confidence_min; confidence<=confidence_max; confidence+=confidence_step){

			ConfidenceBands bands = new ConfidenceBands(rocs, method, confidence, distribution);

			bands.setBackgroundVisible(false);
			bands.setBordersColor(cmap.interpolate(confidence, confidence_min, confidence_max));
			bands.setDashInterval(0);

			BANDS.add(bands);

		}

		return BANDS;

	}

	// ---------------------------------------------------------------------------
	// Method for computing confidence bands raster
	// ---------------------------------------------------------------------------
	public static ArrayList<OperatingArea> makeConfidenceRaster(ArrayList<ReceiverOperatingCharacteristics> rocs){

		return makeConfidenceRaster(rocs, 1, 99, 1);

	}

	// ---------------------------------------------------------------------------
	// Method for computing confidence bands raster
	// ---------------------------------------------------------------------------
	public static ArrayList<OperatingArea> makeConfidenceRaster(ArrayList<ReceiverOperatingCharacteristics> rocs, double confidence_min, double confidence_max, double confidence_step, ColorMap cmap){

		return makeConfidenceRaster(rocs, confidence_min, confidence_max, confidence_step, ConfidenceBands.METHOD_THRESHOLD_AVERAGING, ConfidenceBands.DISTRIBUTION_BINORMAL, cmap);

	}


	// ---------------------------------------------------------------------------
	// Method for computing confidence bands raster
	// ---------------------------------------------------------------------------
	public static ArrayList<OperatingArea> makeConfidenceRaster(ArrayList<ReceiverOperatingCharacteristics> rocs, double confidence_min, double confidence_max, double confidence_step){

		return makeConfidenceRaster(rocs, confidence_min, confidence_max, confidence_step, ConfidenceBands.METHOD_THRESHOLD_AVERAGING, ConfidenceBands.DISTRIBUTION_BINORMAL, ColorMap.TYPE_MATHS);

	}

	// ---------------------------------------------------------------------------
	// Method for computing confidence bands raster
	// ---------------------------------------------------------------------------
	public static ArrayList<OperatingArea> makeConfidenceRaster(ArrayList<ReceiverOperatingCharacteristics> rocs, double confidence_min, double confidence_max, double confidence_step, int method, int distribution, ColorMap cmap){

		ArrayList<OperatingArea> BANDS = new ArrayList<OperatingArea>();

		Color borders = new Color(0.f, 0.f, 0.f, 0.f);

		double conf = 0;

		// For each confidence level
		for (double confidence=confidence_max-confidence_step; confidence>=confidence_min+confidence_step; confidence-=confidence_step){

			// Update
			conf = confidence;

			// Boundary line
			OperatingLine lineU = new OperatingLine();
			OperatingLine lineL = new OperatingLine();

			// Confidence bands computation
			ConfidenceBands bands1 = new ConfidenceBands(rocs, method, confidence, distribution);
			ConfidenceBands bands2 = new ConfidenceBands(rocs, method, confidence+confidence_step, distribution);

			// Lower boundary 1
			for (int j=0; j<bands1.getUpperBandX().length; j++){

				lineU.addOperatingPoint(new OperatingPoint(bands1.getUpperBandX()[j], bands1.getUpperBandY()[j]));

			}

			// Lower boundary 2
			for (int j=bands2.getUpperBandX().length-1; j>=0; j--){

				lineU.addOperatingPoint(new OperatingPoint(bands2.getUpperBandX()[j], bands2.getUpperBandY()[j]));

			}

			// Lower boundary 1
			for (int j=0; j<bands1.getLowerBandX().length; j++){

				lineL.addOperatingPoint(new OperatingPoint(bands1.getLowerBandX()[j], bands1.getLowerBandY()[j]));

			}

			// Lower boundary 2
			for (int j=bands2.getLowerBandX().length-1; j>=0; j--){

				lineL.addOperatingPoint(new OperatingPoint(bands2.getLowerBandX()[j], bands2.getLowerBandY()[j]));

			}

			// Boundary closure
			lineU.addOperatingPoint(lineU.getOperatingPoint(0));
			lineL.addOperatingPoint(lineL.getOperatingPoint(0));

			// Polygonization
			OperatingArea areaU = new OperatingArea(lineU);
			OperatingArea areaL = new OperatingArea(lineL);

			// Style
			areaU.setBackGroundColor(cmap.interpolate(confidence, confidence_min, confidence_max));
			areaU.setBorderColor(borders);
			areaL.setBackGroundColor(cmap.interpolate(confidence, confidence_min, confidence_max));
			areaL.setBorderColor(borders);

			BANDS.add(areaU);
			BANDS.add(areaL);

		}

		// Central band

		ConfidenceBands bands = new ConfidenceBands(rocs, method, conf, distribution);

		OperatingLine line = new OperatingLine();

		// Upper boundary 
		for (int j=0; j<bands.getUpperBandX().length; j++){

			line.addOperatingPoint(new OperatingPoint(bands.getUpperBandX()[j], bands.getUpperBandY()[j]));

		}

		// Lower boundary 2
		for (int j=bands.getLowerBandX().length-1; j>=0; j--){

			line.addOperatingPoint(new OperatingPoint(bands.getLowerBandX()[j], bands.getLowerBandY()[j]));

		}

		// Boundary closure
		line.addOperatingPoint(line.getOperatingPoint(0));

		// Polygonization
		OperatingArea area = new OperatingArea(line);

		// Style
		area.setBackGroundColor(cmap.interpolate(conf, confidence_min, confidence_max));
		area.setBorderColor(borders);

		BANDS.add(area);


		return BANDS;

	}


	// ---------------------------------------------------------------------------
	// Method for resampling confidence bands
	// ---------------------------------------------------------------------------
	private void resample(int new_resolution){

		FPR_upper = new double[new_resolution];
		TPR_upper = new double[new_resolution];

		FPR_lower = new double[new_resolution];
		TPR_lower = new double[new_resolution];

		FPR_upper[0] = 0;
		TPR_upper[0] = 0;
		FPR_lower[0] = 0;
		TPR_lower[0] = 0;

		for (int j=0; j<new_resolution; j++){

			double x = (double)(j)/(double)(new_resolution);

			FPR_upper[j] = x;
			FPR_lower[j] = x;

			for (int k=1; k<resolution; k++){

				double xk = upperband_x[k];

				if (xk > x){

					double y1 = upperband_y[k-1];
					double y2 = upperband_y[k];

					double d1 = x - upperband_x[k-1];
					double d2 = xk - x;

					double w1 = d2/(d1+d2);
					double w2 = d1/(d1+d2);


					TPR_upper[j] = w1*y1+w2*y2;
					break;

				}

			}

			for (int k=1; k<resolution; k++){

				double xk = lowerband_x[k];

				if (xk > x){

					double y1 = lowerband_y[k-1];
					double y2 = lowerband_y[k];

					double d1 = x - lowerband_x[k-1];
					double d2 = xk - x;

					double w1 = d2/(d1+d2);
					double w2 = d1/(d1+d2);


					TPR_lower[j] = w1*y1+w2*y2;
					break;

				}

			}

		}

	}

}

