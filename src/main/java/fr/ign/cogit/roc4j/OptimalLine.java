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

import java.awt.Color;

// =================================================================================
// Class for computing optimal lines in ROC space for a specific problem instance
// Requires to know relative costs on each error (type I and II) and if possible, 
// prior probabilities on positive and negative instance.
// Date : 06/03/2017
// =================================================================================


public class OptimalLine {

	// Parameters
	private Context context;
	
	private double angle;
	
	// Style
	private Color optimalLineColor = Color.BLACK;
	private float optimalLineThickness = 1.5f;
	
	// Setters
	public void setContext(Context context){this.context = context;}
	public void setAngle(double angle){this.angle = angle;}
	public void setColor(Color color){optimalLineColor = color;}
	public void setThickness(float thickness){optimalLineThickness = thickness;}
	
	// Getters
	public Context getContext(){return this.context;}
	public Color getColor(){return optimalLineColor;}
	public float getThickness(){return optimalLineThickness;}
	public double getAngle(){return angle;}
	
	// Main constructor
	public OptimalLine(Context context){
		
		double cost_false_positive = context.getCostFalsePositive();
		double cost_false_negative = context.getCostFalseNegative();
		
		double prior_positive = context.getPriorPositive();
		double prior_negative = context.getPriorNegative();
		
		if (cost_false_positive <= 0 || cost_false_negative <= 0){

			System.err.println("Error : false positive and false negative costs must be strictly positive");
			System.exit(1);

		}
		
		if (prior_positive <= 0 || prior_negative <= 0){

			System.err.println("Error : prior probabilities must be strictly positive");
			System.exit(1);

		}
		
		if (Math.abs(prior_positive + prior_negative - 1.0) > Math.pow(10, -5)){

			System.err.println("Error : prior probabilities must sum to one");
			System.exit(1);

		}

		this.angle = -Math.atan((cost_false_positive*prior_negative)/(cost_false_negative*prior_positive))*180.0/Math.PI;
		
	}

	
	// Optimal ROC space point computing
	public OperatingPoint computeOptimalOperatingPoint(ReceiverOperatingCharacteristics roc){

		
		double[] XROC = roc.getXRoc();
		double[] YROC = roc.getYRoc();
		
		int index = 0;
		
		for (int i=1; i<XROC.length; i++){
			
			double x = XROC[i];
			double y = YROC[i];
			
			if (x == 0){
				
				continue;
				
			}

			
			if (Math.atan((1-y)/x)*180/Math.PI < -angle){
				
				index = i;
				break;
				
			}
			
		}
		
		double angle1 = Math.atan((1-YROC[index-1])/XROC[index-1])*180/Math.PI;
		double angle2 = Math.atan((1-YROC[index])/XROC[index])*180/Math.PI;
		
		double frac = (-angle-angle2)/(angle1-angle2);
		
		double xp = frac*XROC[index-1]+(1.0-frac)*XROC[index];
		double yp = frac*YROC[index-1]+(1.0-frac)*YROC[index];
		
		OperatingPoint point = new OperatingPoint(xp, yp, 1.0-(double)index/(double)(XROC.length));
		
		
		return point;

	}
	
	
}
