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

package fr.ign.cogit.roc4j.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import fr.ign.cogit.roc4j.graphics.ColorMap;
import fr.ign.cogit.roc4j.graphics.OperatingArea;
import fr.ign.cogit.roc4j.graphics.OperatingLine;
import fr.ign.cogit.roc4j.graphics.OperatingPoint;
import fr.ign.cogit.roc4j.graphics.RocSpace;
import fr.ign.cogit.roc4j.graphics.RocSpaceStyle;
import fr.ign.cogit.roc4j.optimization.OptimalLine;



// =================================================================================
// Class for computing receivier operating curve
// Requires one vector of expected classification (0 or 1) and another vector of 
// assigned probabilities P(Y=1|X).
// Date : 11/01/2017
// =================================================================================


public class ReceiverOperatingCharacteristics {

	// Number of points
	protected int resolution;

	// ROC curve
	protected double XROC[];
	protected double YROC[];

	// Graphics
	private Color color = Color.red;
	private Float thickness = 1.5f;
	private ColorMap cmap = null;

	// Name and legends
	private String name = "Receiving Operator Curve Space";
	private String xlabel = "FPR";
	private String ylabel = "TPR";

	// Plot size
	private int width = 600;
	private int height = 600;

	// Data number
	private int TP = 0;
	private int TN = 0;

	// Resampled curve
	private double[] XROC_RESAMPLED;
	private double[] YROC_RESAMPLED;
	private double[] THRESHOLD;

	// Smoothed version
	protected double a = Double.NaN;
	protected double b = Double.NaN;

	// Saving points
	private double[] POS_SCORES = {0.0};
	private double[] NEG_SCORES = {0.0};

	// Smoothing methods
	public static int SMOOTH_BINORMAL_REGRESSION = 1;
	public static int SMOOTH_CONVEXIFY = 2;
	public static int SMOOTH_KERNEL = 3;


	// Getters
	public int getResolution(){return resolution;}
	public double[] getXRoc(){return XROC;}
	public double[] getYRoc(){return YROC;} 
	public Color getColor(){return color;}
	public float getThickness(){return thickness;}
	public String getName(){return name;}
	public String getXLabel(){return xlabel;}
	public String getYLabel(){return ylabel;}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public int getPositiveInstancesNumber(){return TP;}
	public int getNegativeInstancesNumber(){return TN;}
	public ColorMap getColorMap(){return cmap;}
	
	public double[] getPositiveScore(){return POS_SCORES;}
	public double[] getNegativeScore(){return NEG_SCORES;}

	// Setters
	public void setColor(ColorMap cmap){this.cmap = cmap;}
	public void setThickness(float thickness){this.thickness = thickness;}
	public void setName(String name){this.name = name;}
	public void setXLabel(String xlabel){this.xlabel = xlabel;}
	public void setYLabel(String ylabel){this.ylabel = ylabel;}
	public void setPlotWidth(int width){this.width = width;}	
	public void setPlotHeight(int height){this.height = height;}
	public void setPositiveInstancesNumber(int P){this.TP = P;}
	public void setNegativeInstancesNumber(int N){this.TN = N;}

	// ColorMap sepcial setters
	public void setColor(Color... color){
		
		if (color.length == 1){
			
			this.color = color[0]; cmap = null;
			
		}
		else{
			
			ColorMap cmap = new ColorMap();
			
			for (int i=0; i<color.length; i++){
				
				cmap.add(color[i], (double)i/ (double)(color.length-1));
				
			}
			
			setColor(cmap);
			
		}
		
		
	}
	
	// Smoothed version getters
	public double getSmoothedIntercept(){

		if (Double.isNaN(this.b)){

			System.err.println("Error : roc curve must be smoothed with binormal regression before attempting to get intercept parameter");
			System.exit(1);

		}

		return this.b;

	}
	// Smoothed version getters
	public double getSmoothedSlope(){

		if (Double.isNaN(this.a)){

			System.err.println("Error : roc curve must be smoothed with binormal regression before attempting to get slope parameter");
			System.exit(1);

		}

		return this.a;

	}



	// ---------------------------------------------------------------------------
	// Main constructor
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics(int[] expected, double[] probabilities){

		this(expected, probabilities, 1000);

	}

	// ---------------------------------------------------------------------------
	// Main constructor bis
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics(int[] expected, double[] probabilities, int resolution){

		this.resolution = resolution;

		// Dataset size
		int n = expected.length;

		// Control
		if (probabilities.length != n){

			System.err.println("Error : expected and probabilities vectors must have same dimension");

		}

		XROC = new double[resolution];
		YROC = new double[resolution];

		int P = 0;
		int N = 0;

		// Computing ROC curve
		for (int k=0; k<resolution; k++){

			// Threshold
			double t = 1.0-(double)(k)/(double)(resolution-1);


			// Confusions
			int TP = 0;
			int TN = 0;
			int FP = 0;
			int FN = 0;

			for (int i=0; i<n; i++){

				if ((probabilities[i] >= t) && (expected[i] == 1)){TP ++;}

				if ((probabilities[i] >= t) && (expected[i] == 0)){FP ++;}

				if ((probabilities[i] < t) && (expected[i] == 1)){FN ++;}

				if ((probabilities[i] < t) && (expected[i] == 0)){TN ++;}

			}

			P = TP + FN;
			N = FP + TN;


			// Rates
			double TPR = (double)(TP)/(double)(P);
			double FPR = 1.0-(double)(TN)/(double)(N);

			// Point ROC
			XROC[k] = FPR;
			YROC[k] = TPR;

		}

		// Saving data
		POS_SCORES = new double[P];
		NEG_SCORES = new double[N];

		int cp = 0;
		int cn = 0;

		for (int i=0; i<expected.length; i++){

			if (expected[i] == 0){

				NEG_SCORES[cn] = probabilities[i];
				cn++;

			}

			if (expected[i] == 1){

				POS_SCORES[cp] = probabilities[i];
				cp++;

			}

		}

		this.TP = P;
		this.TN = N;

		// Filling resampled vectors
		fillResampledVectors(1000);

	}

	// ---------------------------------------------------------------------------
	// Direct constructor from XROC and YROC
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics(ArrayList<Double> FPR, ArrayList<Double> TPR){

		if (FPR.size() != TPR.size()){

			System.err.println("Error : TPR and FPR rates must have same dimension to build ROC curve");
			System.exit(1);

		}

		this.resolution = FPR.size();


		this.XROC = new double[resolution];
		this.YROC = new double[resolution];

		for (int i=0; i<resolution; i++){

			this.XROC[i] = FPR.get(i);
			this.YROC[i] = TPR.get(i);

		}

		// Filling resampled vectores
		fillResampledVectors(1000);

	}

	// ---------------------------------------------------------------------------
	// Direct constructor from ROC data file
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics(String rocFile, int headline, int fpr_column, int tpr_column){

		File fichier = new File(rocFile);

		ArrayList<Double> FPR = new ArrayList<Double>();
		ArrayList<Double> TPR = new ArrayList<Double>();

		Scanner scan = null;
		try {
			scan = new Scanner(fichier);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		// Headline
		for (int i=0; i<headline; i++){
			scan.nextLine();
		}

		// Reading data
		while(scan.hasNextLine()){

			String line = scan.nextLine();

			StringTokenizer st1 = new StringTokenizer(line, " ");
			StringTokenizer st2 = new StringTokenizer(line, " ");

			// Headcolumns fpr
			for (int i=0; i<fpr_column; i++){

				st1.nextToken();

			}

			// Headcolumns tpr
			for (int i=0; i<tpr_column; i++){

				st2.nextToken();

			}

			double fpr = Double.parseDouble(st1.nextToken());
			double tpr = Double.parseDouble(st2.nextToken());

			FPR.add(fpr);
			TPR.add(tpr);

			this.resolution = FPR.size();


			this.XROC = new double[resolution];
			this.YROC = new double[resolution];

			for (int i=0; i<resolution; i++){

				this.XROC[i] = FPR.get(i);
				this.YROC[i] = TPR.get(i);

			}

		}

		// Filling resampled vectores
		fillResampledVectors(1000);

	}

	// ---------------------------------------------------------------------------
	// Direct constructor from XROC and YROC
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics(double[] FPR, double[] TPR){

		if (FPR.length != TPR.length){

			System.err.println("Error : TPR and FPR rates must have same dimension to build ROC curve");
			System.exit(1);

		}

		this.resolution = FPR.length;


		this.XROC = new double[resolution];
		this.YROC = new double[resolution];

		for (int i=0; i<resolution; i++){

			this.XROC[i] = FPR[i];
			this.YROC[i] = TPR[i];

		}

		// Filling resampled vectores
		fillResampledVectors(1000);

	}


	// ---------------------------------------------------------------------------
	// Method for computing Area Under Curve of a ROC curve
	// ---------------------------------------------------------------------------
	public double computeAUC(){

		double area = 0.0;

		for (int i=0; i<XROC.length-1; i++){

			double dx = Math.max(XROC[i], XROC[i+1])-Math.min(XROC[i], XROC[i+1]);
			double ymean = 0.5*(YROC[i]+YROC[i+1]);

			area += dx*ymean;

		}

		return area;

	}



	// ---------------------------------------------------------------------------
	// Method to plot ROC curve
	// ---------------------------------------------------------------------------
	public void plot(){

		RocSpace space = new RocSpace();

		space.addRocCurve(this);

		// Container
		JFrame fen = new JFrame();
		fen.setSize(new Dimension(width, height));
		fen.setLocationRelativeTo(null);
		fen.setContentPane(space);
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		fen.setVisible(true);

	}

	// ---------------------------------------------------------------------------
	// Method for resampling ROC curve regularly
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics resample(){

		return resample(getResolution());

	}

	// ---------------------------------------------------------------------------
	// Method for resampling ROC curve regularly at specific resolution
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics resample(int new_resolution){

		double[] Y = new double[new_resolution];
		double[] X = new double[new_resolution];

		for (int j=0; j<new_resolution; j++){

			double x = (double)(j)/(double)(new_resolution);

			X[j] = x;

			for (int k=1; k<resolution; k++){

				double xk = this.getXRoc()[k];

				if (xk > x){

					double y1 = this.getYRoc()[k-1];
					double y2 = this.getYRoc()[k];

					double d1 = x - this.getXRoc()[k-1];
					double d2 = xk - x;

					double w1 = d2/(d1+d2);
					double w2 = d1/(d1+d2);


					Y[j] = w1*y1+w2*y2;
					break;

				}

			}

		}

		ReceiverOperatingCharacteristics roc = new ReceiverOperatingCharacteristics(X, Y);

		return roc;

	}

	// ---------------------------------------------------------------------------
	// Method for filling resampled vectors
	// ---------------------------------------------------------------------------
	private void fillResampledVectors(int new_resolution){

		XROC_RESAMPLED = new double[new_resolution];
		YROC_RESAMPLED = new double[new_resolution];
		THRESHOLD = new double[new_resolution];


		for (int j=0; j<new_resolution; j++){

			double x = (double)(j)/(double)(new_resolution);

			XROC_RESAMPLED[j] = x;

			for (int k=1; k<resolution; k++){

				double xk = this.getXRoc()[k];

				if (xk > x){

					double y1 = this.getYRoc()[k-1];
					double y2 = this.getYRoc()[k];

					double d1 = x - this.getXRoc()[k-1];
					double d2 = xk - x;

					double w1 = d2/(d1+d2);
					double w2 = d1/(d1+d2);


					YROC_RESAMPLED[j] = w1*y1+w2*y2;
					THRESHOLD[j] = w1*(double)(k-1)/(double)resolution+w2*(double)k/(double)resolution;
					break;

				}

			}

		}

	}

	// ---------------------------------------------------------------------------
	// Method for averaging two ROC curves
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics averageWith(ReceiverOperatingCharacteristics roc){

		return averageWith(roc, 0.5);

	}

	// ---------------------------------------------------------------------------
	// Method for averaging two ROC curves with different weights
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics averageWith(ReceiverOperatingCharacteristics roc, double w1){

		if ((w1 > 1) || (w1 < 0)){

			System.err.println("Error : weight must be in [0;1]");
			System.exit(1);

		}

		RocCurvesCollection ROCS = new RocCurvesCollection(new ArrayList<ReceiverOperatingCharacteristics>(), true);

		for (int i=0; i<100; i++){

			if (i/100.0 < w1){

				ROCS.add(this);

			}
			else{

				ROCS.add(roc);

			}

		}

		return average(ROCS);

	}


	// ---------------------------------------------------------------------------
	// Method for averaging a list of ROC curves
	// ---------------------------------------------------------------------------
	public static ReceiverOperatingCharacteristics average(ReceiverOperatingCharacteristics... rocs){

		RocCurvesCollection ROCS = new RocCurvesCollection(new ArrayList<ReceiverOperatingCharacteristics>(), true);
		
		for (int i=0; i<rocs.length; i++){
			
			ROCS.add(rocs[i]);
			
		}
		
		return average(ROCS);
		
	}


	// ---------------------------------------------------------------------------
	// Method for averaging a list of ROC curves
	// ---------------------------------------------------------------------------
	public static ReceiverOperatingCharacteristics average(RocCurvesCollection ROCS){

		if (ROCS.size() <= 1){

			System.err.println("Error : ROC curves array list must contain at least 2 curves");
			System.exit(1);

		}

		ConfidenceBands bands = new ConfidenceBands(ROCS, ConfidenceBands.METHOD_THRESHOLD_AVERAGING);

		return bands.getCentralROC();

	}

	// ---------------------------------------------------------------------------
	// Special getter FPR -> TPR
	// ---------------------------------------------------------------------------
	public double getTruePositiveRate(double falsePositiveRate){

		int index = (int)(falsePositiveRate*XROC_RESAMPLED.length);

		index = Math.max(Math.min(index, YROC_RESAMPLED.length-1), 0);

		return YROC_RESAMPLED[index];

	}

	// ---------------------------------------------------------------------------
	// Special getter TPR -> FPR
	// ---------------------------------------------------------------------------
	public double getFalsePositiveRate(double truePositiveRate){

		int index = 0;

		while(YROC_RESAMPLED[index] < truePositiveRate){

			index ++;

		}

		return XROC_RESAMPLED[index];

	}

	// ---------------------------------------------------------------------------
	// Special getter FPR -> threshold
	// ---------------------------------------------------------------------------
	public double getThresholdFromFpr(double falsePositiveRate){

		falsePositiveRate = Math.max(Math.min(falsePositiveRate, 0.99999), 0);

		int index = (int)(falsePositiveRate*XROC_RESAMPLED.length);

		return 1-THRESHOLD[index];

	}

	// ---------------------------------------------------------------------------
	// Special getter TPR -> threshold
	// ---------------------------------------------------------------------------
	public double getThresholdFromTpr(double truePositiveRate){

		int index = 0;

		while(YROC_RESAMPLED[index] < truePositiveRate){

			index ++;

		}

		return 1-THRESHOLD[index];

	}



	// ---------------------------------------------------------------------------
	// Method to convert a ROC curve to an array list of points
	// ---------------------------------------------------------------------------
	public ArrayList<OperatingPoint> toOperatingPointsSequency(){

		ArrayList<OperatingPoint> POINTS = new ArrayList<OperatingPoint>();

		for (int i=0; i<getXRoc().length; i++){

			POINTS.add(new OperatingPoint(getXRoc()[i], getYRoc()[i]));

		}

		return POINTS;

	}

	// ---------------------------------------------------------------------------
	// Method to convert a ROC curve to an array list of points with resampling
	// ---------------------------------------------------------------------------
	public ArrayList<OperatingPoint> toOperatingPointsSequency(int resolution){

		OperatingPoint model = new OperatingPoint(0, 0);		

		return toOperatingPointsSequency(resolution, model);

	}


	// ---------------------------------------------------------------------------
	// Method to convert a ROC curve to an array list of points with resampling
	// ---------------------------------------------------------------------------
	public ArrayList<OperatingPoint> toOperatingPointsSequency(OperatingPoint model){

		return toOperatingPointsSequency(resolution, model);

	}


	// ---------------------------------------------------------------------------
	// Method to convert a ROC curve to an array list of points with resampling
	// ---------------------------------------------------------------------------
	public ArrayList<OperatingPoint> toOperatingPointsSequency(int resolution, OperatingPoint model){

		ReceiverOperatingCharacteristics roc = resample(resolution);

		ArrayList<OperatingPoint> POINTS = new ArrayList<OperatingPoint>();

		for (int i=0; i<roc.getXRoc().length; i++){

			OperatingPoint point = new OperatingPoint(roc.getXRoc()[i], roc.getYRoc()[i]);
			point.setStyle(model.getStyle());
			point.setColor(model.getColor());
			point.setSize(model.getSize());

			POINTS.add(point);

		}

		return POINTS;

	}


	// ---------------------------------------------------------------------------
	// Method for copying a ROC curve
	// ---------------------------------------------------------------------------
	public OperatingPoint computeOptimalOperatingPoint(OptimalLine line){

		return line.computeOptimalOperatingPoint(this);

	}

	// ---------------------------------------------------------------------------
	// Method for optimal ROC space point computing
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics copy(){

		ReceiverOperatingCharacteristics copy = new ReceiverOperatingCharacteristics(XROC, YROC);

		copy.setNegativeInstancesNumber(getNegativeInstancesNumber());
		copy.setPositiveInstancesNumber(getPositiveInstancesNumber());

		copy.POS_SCORES = this.POS_SCORES;
		copy.NEG_SCORES = this.NEG_SCORES;

		return copy;

	}

	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics makeSmoothVersion(){

		return makeSmoothVersion(SMOOTH_BINORMAL_REGRESSION);

	}

	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics makeSmoothVersion(int method){

		return makeSmoothVersion(method, new PredefiniteKernel(PredefiniteKernel.GAUSSIAN), true);

	}

	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics makeSmoothVersion(int method, Kernel kernel){

		return makeSmoothVersion(method, kernel, true);

	}

	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics makeSmoothVersion(int method, Kernel kernel, boolean verbose){

		return makeSmoothVersion(method, kernel, kernel, verbose);

	}

	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics makeSmoothVersion(int method, Kernel kernel_pos, Kernel kernel_neg, boolean verbose){

		ReceiverOperatingCharacteristics roc = copy();

		roc.smooth(method, kernel_pos, kernel_neg, verbose);

		return roc;

	}

	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public void smooth(){

		smooth(SMOOTH_BINORMAL_REGRESSION);

	}

	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public void smooth(int method){

		smooth(method, new PredefiniteKernel(PredefiniteKernel.GAUSSIAN), true);

	}


	// ---------------------------------------------------------------------------
	// Method for making a smooth version of a ROC curve
	// ---------------------------------------------------------------------------
	public void smooth(int method, Kernel kernel){

		smooth(method, kernel, true);

	}


	// ---------------------------------------------------------------------------
	// Method for smoothing a ROC curve
	// ---------------------------------------------------------------------------
	public void smooth(int method, Kernel kernel, boolean verbose){

		smooth(method, kernel, kernel, verbose);

	}

	// ---------------------------------------------------------------------------
	// Method for smoothing a ROC curve
	// ---------------------------------------------------------------------------
	public void smooth(int method, Kernel kp, Kernel kn, boolean verbose){


		if (method == SMOOTH_CONVEXIFY){

			RocCurveSmoother.convexify(this);
			this.fillResampledVectors(1000);

		}

		if (method == SMOOTH_KERNEL){

			RocCurveSmoother.kernelSmoothing(this, kp, kn, verbose);
			this.fillResampledVectors(1000);

		}


		if (method == SMOOTH_BINORMAL_REGRESSION){

			RocCurveSmoother.binormalSmoothing(this, verbose);
			this.fillResampledVectors(1000);

		}


	}


	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public void plotScoringSpace(){

		plotScoringSpace(new PredefiniteKernel(PredefiniteKernel.GAUSSIAN));

	}

	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public void plotScoringSpace(Kernel k){

		plotScoringSpace(k, k, RocSpaceStyle.STYLE_PLAIN);

	}

	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public void plotScoringSpace(Kernel kp, Kernel kn){

		plotScoringSpace(kp, kn, RocSpaceStyle.STYLE_PLAIN);

	}

	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public void plotScoringSpace(Kernel k, RocSpaceStyle style){

		plotScoringSpace(k, k, style);

	}

	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public RocSpace getScoringSpace(){

		return getScoringSpace(new PredefiniteKernel(PredefiniteKernel.GAUSSIAN));

	}


	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public RocSpace getScoringSpace(Kernel k){

		return getScoringSpace(k, k, RocSpaceStyle.STYLE_PLAIN);

	}

	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public RocSpace getScoringSpace(Kernel k, RocSpaceStyle style){

		return getScoringSpace(k, k, style);

	}

	// ---------------------------------------------------------------------------
	// Method for plotting instances scores distribution
	// ---------------------------------------------------------------------------
	public void plotScoringSpace(Kernel kp, Kernel kn, RocSpaceStyle style){

		RocSpace scoringSpace = getScoringSpace(kp, kn, style);

		JFrame fen = new JFrame();
		fen.setSize(900, 500);
		fen.setContentPane(scoringSpace);
		fen.setLocationRelativeTo(null);
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fen.setVisible(true);

	}

	// ---------------------------------------------------------------------------
	// Method for getting instances scores distribution
	// ---------------------------------------------------------------------------
	public RocSpace getScoringSpace(Kernel kp, Kernel kn, RocSpaceStyle style){

		kp = kp.copy();
		kn = kn.copy();

		int n = 1000;

		// Security test
		if (Double.isNaN(this.b) && POS_SCORES.length <= 1){

			System.err.println("Error : roc curve must be provided with expected and probabilities samples or smoothed with binormal regression before attempting to get scoring space");
			System.exit(1);

		}

		// Computing optimal kernel if needed
		if ((kp instanceof PredefiniteKernel) && (((PredefiniteKernel)(kp)).getType() == PredefiniteKernel.AUTOMATIC)){

			kp = new PredefiniteKernel(PredefiniteKernel.EPANECHNIKOV);

			double mx = 0;
			double mx2 = 0;

			for (int i=0; i<POS_SCORES.length; i++){

				mx += POS_SCORES[i];
				mx2 += Math.pow(POS_SCORES[i], 2);

			}

			mx /= (double)(POS_SCORES.length);
			mx2 /= (double)(POS_SCORES.length);

			double sx = Math.sqrt(mx2-Math.pow(mx, 2));
			sx = Math.sqrt(POS_SCORES.length/(POS_SCORES.length-1))*sx;

			kp.setBandwidth(1.06*sx*Math.pow(POS_SCORES.length, -0.2));


		}

		// Computing optimal kernel if needed
		if ((kn instanceof PredefiniteKernel) && (((PredefiniteKernel)(kn)).getType() == PredefiniteKernel.AUTOMATIC)){

			kn = new PredefiniteKernel(PredefiniteKernel.EPANECHNIKOV);

			double my = 0;
			double my2 = 0;

			for (int i=0; i<NEG_SCORES.length; i++){

				my += NEG_SCORES[i];
				my2 += Math.pow(NEG_SCORES[i], 2);

			}

			my /= (double)(NEG_SCORES.length);
			my2 /= (double)(NEG_SCORES.length);

			double sy = Math.sqrt(my2-Math.pow(my, 2));
			sy = Math.sqrt(NEG_SCORES.length/(NEG_SCORES.length-1))*sy;

			kn.setBandwidth(1.06*sy*Math.pow(NEG_SCORES.length, -0.2));

		}


		double hp = kp.getBandwidth();
		double hn = kn.getBandwidth();

		if (Double.isNaN(this.b)){

			RocSpace scoringSpace = new RocSpace();

			scoringSpace.setStyle(style);

			scoringSpace.setDiagonalVisible(false);

			int ip = 0;
			int in = 0;

			if (POS_SCORES.length < NEG_SCORES.length){

				ip = 1;
				in = (int) NEG_SCORES.length/POS_SCORES.length;

			}else{

				in = 1;
				ip = (int) POS_SCORES.length/NEG_SCORES.length;

			}


			for (int i=0; i<POS_SCORES.length; i+=ip){

				OperatingPoint point = new OperatingPoint(POS_SCORES[i], 0);
				point.setColor(Color.GREEN);
				point.setSize(4);
				scoringSpace.addOperatingPoint(point);

			}

			for (int i=0; i<NEG_SCORES.length; i+=in){

				OperatingPoint point = new OperatingPoint(NEG_SCORES[i], 0);
				point.setColor(Color.RED);
				point.setSize(4);
				scoringSpace.addOperatingPoint(point);

			}


			double[] DX = new double[n];
			double[] DY1 = new double[n];
			double[] DY2 = new double[n];

			for (int i=0; i<DX.length; i++){

				double x = (double)i/(double)resolution;

				DY1[i] = 0;
				DY2[i] = 0;

				DX[i] = x;

				for (int j=0; j<POS_SCORES.length; j++){

					DY2[i] += kp.pdf((x-POS_SCORES[j])/hp);

				}

				for (int j=0; j<NEG_SCORES.length; j++){

					DY1[i] += kn.pdf((x-NEG_SCORES[j])/hn);

				}

				DY2[i] /= POS_SCORES.length*hp;
				DY1[i] /= NEG_SCORES.length*hn;


			}

			double ymax1 = 0;
			double ymax2 = 0;

			for (int i=0; i<n; i++){

				if (DY1[i] > ymax1){

					ymax1 = DY1[i];

				}

				if (DY2[i] > ymax2){

					ymax2 = DY2[i];

				}

			}

			DY1[0] = 0;
			DY2[0] = 0;
			DY1[DY1.length-1] = 0;
			DY2[DY2.length-1] = 0;

			OperatingLine ol1 = new OperatingLine();
			OperatingLine ol2 = new OperatingLine();

			double max = Math.max(ymax1, ymax2);

			for (int i=0; i<n; i++){

				ol1.addOperatingPoint(new OperatingPoint(DX[i], DY1[i]/max*0.9));
				ol2.addOperatingPoint(new OperatingPoint(DX[i], DY2[i]/max*0.9));

			}



			ol1.setColor(Color.RED);
			ol2.setColor(Color.GREEN);

			ol1.setStroke(new BasicStroke(2.f));
			ol2.setStroke(new BasicStroke(2.f));


			// -----------------------------------------------------------------------
			// Plot
			// -----------------------------------------------------------------------

			scoringSpace.addOperatingLine(ol1);
			scoringSpace.addOperatingLine(ol2);

			OperatingLine ol21 = ol1.copy();
			OperatingLine ol22 = ol2.copy();

			ol21.addOperatingPoint(ol21.getOperatingPoint(0));
			ol22.addOperatingPoint(ol22.getOperatingPoint(0));

			scoringSpace.addOperatingArea(new OperatingArea(ol21, new Color(1.f, 0.f, 0.f, 0.3f), new Color(1.f, 0.f, 0.f, 0.f)));
			scoringSpace.addOperatingArea(new OperatingArea(ol22, new Color(0.f, 1.f, 0.f, 0.3f), new Color(0.f, 1.f, 0.f, 0.f)));

			scoringSpace.setTitle("Scoring space");
			scoringSpace.setXLabel("Normalized classification score");
			scoringSpace.setYLabel("Probability density function");

			scoringSpace.setDy(1);


			return scoringSpace;

		}

		// -----------------------------------------------------------------------
		// Graphics preparation
		// -----------------------------------------------------------------------

		RocSpace scoringSpace = new RocSpace();

		scoringSpace.setStyle(style);

		scoringSpace.setDiagonalVisible(false);

		// -----------------------------------------------------------------------
		// Distributions
		// -----------------------------------------------------------------------

		double[] DX = new double[n];
		double[] DY1 = new double[n];
		double[] DY2 = new double[n];

		double s1 = 0.1;
		double s2 = this.a*s1;
		double mu1 = 3*s1;
		double mu2 = mu1+this.b*s1;

		double normalization = 3*s1+(mu2-mu1)+3*s2;

		for (int i=0; i<n; i++){

			double x = (double)i/(double)n*normalization;

			double y1 = 1/(s1*Math.sqrt(2*Math.PI))*Math.exp(-0.5*Math.pow((x-mu1)/s1,2));
			double y2 = 1/(s2*Math.sqrt(2*Math.PI))*Math.exp(-0.5*Math.pow((x-mu2)/s2,2));

			DX[i] = x/normalization;
			DY1[i] = y1;
			DY2[i] = y2;

		}

		double ymax1 = 0;
		double ymax2 = 0;

		for (int i=0; i<n; i++){

			if (DY1[i] > ymax1){

				ymax1 = DY1[i];

			}

			if (DY2[i] > ymax2){

				ymax2 = DY2[i];

			}

		}

		DY1[0] = 0;
		DY2[0] = 0;
		DY1[DY1.length-1] = 0;
		DY2[DY2.length-1] = 0;

		OperatingLine ol1 = new OperatingLine();
		OperatingLine ol2 = new OperatingLine();

		double max = Math.max(ymax1, ymax2);

		for (int i=0; i<n; i++){

			ol1.addOperatingPoint(new OperatingPoint(DX[i], DY1[i]/max*0.9));
			ol2.addOperatingPoint(new OperatingPoint(DX[i], DY2[i]/max*0.9));

		}


		ol1.setColor(Color.RED);
		ol2.setColor(Color.GREEN);

		ol1.setStroke(new BasicStroke(2.f));
		ol2.setStroke(new BasicStroke(2.f));


		// -----------------------------------------------------------------------
		// Plot
		// -----------------------------------------------------------------------

		scoringSpace.addOperatingLine(ol1);
		scoringSpace.addOperatingLine(ol2);

		OperatingLine ol21 = ol1.copy();
		OperatingLine ol22 = ol2.copy();

		ol21.addOperatingPoint(ol21.getOperatingPoint(0));
		ol22.addOperatingPoint(ol22.getOperatingPoint(0));

		OperatingArea area1 = new OperatingArea(ol21, new Color(1.f, 0.f, 0.f, 0.3f), new Color(1.f, 0.f, 0.f, 0.f));
		OperatingArea area2 = new OperatingArea(ol22, new Color(0.f, 1.f, 0.f, 0.3f), new Color(0.f, 1.f, 0.f, 0.f));


		scoringSpace.addOperatingArea(area1);
		scoringSpace.addOperatingArea(area2);


		scoringSpace.setTitle("Scoring space");
		scoringSpace.setXLabel("Normalized classification score");
		scoringSpace.setYLabel("Probability density function");

		scoringSpace.setDy(1);

		return scoringSpace;

	}

}

