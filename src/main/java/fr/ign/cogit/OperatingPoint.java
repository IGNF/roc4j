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

//=================================================================================
// Class for storing operating points in the roc space
// Its main features are threshold, TPR and FPR values.
// Date : 06/03/2017
//=================================================================================

public class OperatingPoint {

	// Parameters
	private double fpr;
	private double tpr;
	private double threshold;

	// Graphics
	private Color color = Color.BLACK;
	private String style = "fo";
	private double size = 7;

	public static String STYLE_FILLED_CIRCLE = "fo";
	public static String STYLE_CIRCLE = "o";
	public static String STYLE_FILLED_TRIANGLE = "f^";
	public static String STYLE_TRIANGLE = "^";
	public static String STYLE_FILLED_UPSIDE_DOWN_TRIANGLE = "fv";
	public static String STYLE_UPSIDE_DOWN_TRIANGLE = "v";
	public static String STYLE_CROSS = "x";


	// Getters
	public double getTpr(){return tpr;}
	public double getFpr(){return fpr;}
	public double getThreshold(){return threshold;}

	public Color getColor(){return color;}
	public String getStyle(){return style;}
	public double getSize(){return size;}

	// Setters
	public void setTpr(double tpr){this.tpr = tpr;}
	public void setFpr(double fpr){this.fpr = fpr;}
	public void setThreshold(double t){this.threshold = t;}

	public void setColor(Color color){this.color = color;}
	public void setStyle(String style){this.style = style;}
	public void setSize(double size){this.size = size;}

	// Main constructor
	public OperatingPoint(double fpr, double tpr, double threshold){

		this.tpr = tpr;
		this.fpr = fpr;
		this.threshold = threshold;

	}


	public OperatingPoint(double fpr, double tpr){

		this(fpr, tpr, -1);

	}

	public boolean equals(OperatingPoint pt){

		return ((this.tpr == pt.tpr) && (this.fpr == pt.fpr)&& (this.threshold == pt.threshold));

	}

	public String toString(){

		return "[FPR = "+fpr+", TPR = "+tpr+", threshold = "+threshold+"]";

	}

	// Method to get precision (PPV) on operational point
	public double getPositivePredictiveValue(double prior_positive, double prior_negative){

		return (tpr*prior_positive)/(fpr*prior_negative+tpr*prior_positive);

	}

	// Method to get NPV on operational point
	public double getNegativePredictiveValue(double prior_positive, double prior_negative){

		return ((1-fpr)*prior_negative)/((1-fpr)*prior_negative+(1-tpr)*prior_positive);

	}
	
	
	// Methods to get cost of a point
	public double getCost(){
		
		return getCost(1, 1);
		
	}
	
	public double getCost(double cost_false_positive, double cost_false_negative){
		
		return getCost(cost_false_positive, cost_false_negative, 0.5, 0.5);
		
	}
	
	public double getCost(double cost_false_positive, double cost_false_negative, double prior_positive, double prior_negative){
		
		return getCost(cost_false_positive, cost_false_negative, 0, 0, prior_positive, prior_negative);
		
	}
	
	public double getCost(double cost_false_positive, double cost_false_negative, double cost_true_positive, double cost_true_negative, double prior_positive, double prior_negative){
		
		if (Math.abs(prior_positive + prior_negative - 1.0) > Math.pow(10, -5)){

			System.err.println("Error : prior probabilities must sum to one");
			System.exit(1);

		}
		
		return prior_positive*cost_false_negative*(1-getTpr())+prior_negative*cost_false_positive*getFpr()+prior_positive*cost_true_positive*getTpr()+prior_negative*cost_true_negative*(1-getFpr());
				
	}

}
