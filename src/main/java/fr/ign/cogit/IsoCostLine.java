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

// =================================================================================
// Class for handling isocost lines of ROC space
// Each line is defined by the cost on each wrong decision (cfp and cfn) and prior
// probabilities on each class P(pos) and P(neg). Slope of the line is then computed
// with following expression :
//
//						m = (P(neg)*cfp)/(P(pos)*cfn)
//
// Then for each given intercept b, the line TPR(t) = m*t+b has a fixed given cost 
// that may be computed from b, according to :
//
//                     expected_cost = cfn*(1-b)*P(pos)
//
// Same decision criteria may be applied when true decision also entails positive 
// cost, hence leading to Bayes decision :

//   E(C) = P(p)*cfn*P(0|p) + P(n)*cfp*P(1|n) + P(p)*ctp*P(1|p) + P(n)*ctn*P(0|n)
//   E(C) = P(p)*cfn*(1-TPR) + P(n)*cfp*FPR + P(p)*ctp*TPR + P(n)*ctn*(1-FPR)

// Then, two classifier (FPR1, TPR1) and (FPR2, TPR2) have same expected cost if :
//   		
//   		  (TPR2-TPR1)/(FPR2-FPR1) = [P(n)*(cfp-ctn)]/[P(p)(cfn-ctp)]
//
// Of course, assuming that a wrong decision always entails larger cost than a 
// good decision in each class, i.e. cfp > ctn and cfn > ctp, so that line keeps 
// postive slope. With this second formulation : the expected cost of a line is 
// also computed from its intercept, on (FPR = 0, TPR = b) ROC space point :
// 
//          expected_cost = P(pos)*cfn*(1-b) + P(pos)*ctp*b + P(neg)*ctn
//
// Date : 11/03/2017
//=================================================================================

public class IsoCostLine {



	private Context context;

	private double slope;
	private double intercept;

	private Color color = Color.BLACK;

	private double searchResolution = Math.pow(2, -8);

	public Context getContext(){return this.context;}

	public double getSlope(){return slope;}
	public double getIntercept(){return intercept;}

	public Color getColor(){return this.color;}

	public void setContext(Context context){this.context = context;}

	public void setColor(Color color){this.color = color;}

	public void setIntercept(double intercept){

		if ((intercept < 0) || (intercept > 1)){

			System.err.println("Error : intercept in isocost line must be in [0;1]");
			System.exit(1);

		}

		this.intercept = intercept;

	}

	// ------------------------------------------------------------------------------------------
	// Constructor 0 arg
	// ------------------------------------------------------------------------------------------
	public IsoCostLine(){

		this(new Context());

	}



	// ------------------------------------------------------------------------------------------
	// Constructor 1 arg
	// ------------------------------------------------------------------------------------------
	public IsoCostLine(Context context){

		this(context, 0);

	}

	// ------------------------------------------------------------------------------------------
	// Constructor 2 args
	// ------------------------------------------------------------------------------------------
	public IsoCostLine(Context context, double intercept){
		
		this.context = context;
		
		double cost_false_positive = context.getCostFalsePositive();
		double cost_false_negative = context.getCostFalseNegative();
		double cost_true_positive = context.getCostTruePositive();
		double cost_true_negative = context.getCostTrueNegative();
		
		
		if (cost_true_positive >= cost_false_negative){

			System.err.println("Error : false negative cost must always be greater than true positive cost");
			System.exit(1);

		}

		if (cost_true_negative >= cost_false_positive){

			System.err.println("Error : false positive cost must always be greater than true negative cost");
			System.exit(1);

		}

		if ((cost_false_positive <= 0) ||  (cost_false_negative <= 0)){

			System.err.println("Error : wrong decision costs must always be strictly positive");
			System.exit(1);

		}

		if ((cost_true_positive < 0) ||  (cost_true_negative < 0)){

			System.err.println("Error : good decision costs cannot be negative");
			System.exit(1);

		}

		if ((intercept < 0) || (intercept > 1)){

			System.err.println("Error : intercept in isocost line must be in [0;1]");
			System.exit(1);

		}

		this.intercept = intercept;

		computeSlope();

	}


	// ---------------------------------------------------------------
	// Cost of a given isoline
	// ---------------------------------------------------------------
	public double getCost(){
		
		double cost_false_negative = context.getCostFalseNegative();
		double cost_true_positive = context.getCostTruePositive();
		double cost_true_negative = context.getCostTrueNegative();

		double prior_positive = context.getPriorPositive();
		double prior_negative = context.getPriorNegative();


		double correct_decision_cost = prior_negative*cost_true_negative;
		correct_decision_cost += prior_positive*cost_true_positive*intercept;

		double wrong_decision_cost = cost_false_negative*(1-intercept)*prior_positive;

		return   wrong_decision_cost + correct_decision_cost;

	}

	// ---------------------------------------------------------------
	// Find optimal position of isocost line to maximize cost
	// ---------------------------------------------------------------
	public OperatingPoint optimize(ReceiverOperatingCharacteristics roc){

		OperatingPoint intersection = null;

		double r = searchResolution;

		AreaUnderCurve auc = new AreaUnderCurve(roc);

		OperatingArea area = auc.getOperatingArea();

		for (double inter=0.0001; inter<1; inter+=r){

			double x1 = 0;

			double x2 = 1;
			double y2 = slope+inter;

			if (y2 > 1){

				x2 = (1-inter)/slope;
				y2 = 1;

			}

			boolean bool = false;

			for (double t=x1; t<x2; t+=r){

				double y = inter + slope*t;

				OperatingPoint point = new OperatingPoint(t, y, roc.getThresholdFromFpr(t));

				if (area.contains(point)){

					bool = true;
					intersection = point;
					break;

				}

			}

			if (!bool){

				intercept = inter-r;
				return intersection;

			}

		}

		return intersection;

	}


	// ---------------------------------------------------------------
	// Compute isoline slope
	// ---------------------------------------------------------------
	private void computeSlope(){
		
		double cost_false_positive = context.getCostFalsePositive();
		double cost_false_negative = context.getCostFalseNegative();
		double cost_true_positive = context.getCostTruePositive();
		double cost_true_negative = context.getCostTrueNegative();

		double prior_positive = context.getPriorPositive();
		double prior_negative = context.getPriorNegative();

		double delta_cn = cost_false_positive-cost_true_negative;
		double delta_cp = cost_false_negative-cost_true_positive;

		this.slope = (prior_negative*delta_cn)/(prior_positive*delta_cp);

	}

	// ---------------------------------------------------------------
	// Moves an Isoline upward in ROC space landscape (if possible)
	// ---------------------------------------------------------------
	public void moveUpward(double step){

		this.intercept = Math.min(Math.max(this.intercept+step, 0), 1);

	}

	// ---------------------------------------------------------------
	// Moves an Isoline downward in ROC space landscape (if possible)
	// ---------------------------------------------------------------
	public void moveDownward(double step){

		this.intercept = Math.min(Math.max(this.intercept-step, 0), 1);

	}

	// ---------------------------------------------------------------
	// Returns copy of an isoline
	// ---------------------------------------------------------------
	public IsoCostLine copy(){

		return new IsoCostLine(context);
		
	}


}
