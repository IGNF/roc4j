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

// =================================================================================
// Problem instance context for storing cost of wrong and correct decisions, and 
// class proportions (prior probabilities).
// Date : 11/03/2017
// =================================================================================

public class Context {


	private double cost_false_positive;
	private double cost_false_negative;
	private double cost_true_positive;
	private double cost_true_negative;

	private double prior_positive;
	private double prior_negative;

	public double getCostFalsePositive(){return cost_false_positive;}
	public double getCostFalseNegative(){return cost_false_negative;}
	public double getCostTruePositive(){return cost_true_positive;}
	public double getCostTrueNegative(){return cost_true_negative;}

	public double getPriorPositive(){return prior_positive;}
	public double getPriorNegative(){return prior_negative;}

	public void setCostFalsePositive(double cost_false_positive){this.cost_false_positive = cost_false_positive;}
	public void setCostFalseNegative(double cost_false_negative){this.cost_false_negative = cost_false_negative;}
	public void setCostTruePositive(double cost_true_positive){this.cost_true_positive = cost_true_positive;}
	public void setCostTrueNegative(double cost_true_negative){this.cost_true_negative = cost_true_negative;}

	public void setPriorPositive(double prior_positive){this.prior_positive = prior_positive;}
	public void setPriorNegative(double prior_negative){this.prior_negative = prior_negative;}

	// ------------------------------------------------------------------------------------------
	// Constructor 0 arg
	// ------------------------------------------------------------------------------------------
	public Context(){

		this(1, 1);

	}

	// ------------------------------------------------------------------------------------------
	// Constructor 2 args
	// ------------------------------------------------------------------------------------------
	public Context(double cost_false_positive, double cost_false_negative){

		this(cost_false_positive, cost_false_negative, 0.5);

	}

	// ------------------------------------------------------------------------------------------
	// Constructor 3 args
	// ------------------------------------------------------------------------------------------
	public Context(double cost_false_positive, double cost_false_negative, double prior_positive){

		this(cost_false_positive, cost_false_negative, 0, 0, prior_positive, 1-prior_positive);

	}


	// ------------------------------------------------------------------------------------------
	// Constructor 6 args
	// ------------------------------------------------------------------------------------------
	public Context(double cost_false_positive, double cost_false_negative, double cost_true_positive, double cost_true_negative, double prior_positive, double prior_negative){

		if (Math.abs(prior_positive + prior_negative - 1.0) > Math.pow(10, -5)){

			System.err.println("Error : prior probabilities must sum to one");
			System.exit(1);

		}
		
		this.cost_false_positive = cost_false_positive;
		this.cost_false_negative = cost_false_negative;
		this.cost_true_positive = cost_true_positive;
		this.cost_true_negative = cost_true_negative;

		this.prior_positive = prior_positive;
		this.prior_negative = prior_negative;

	}

}
