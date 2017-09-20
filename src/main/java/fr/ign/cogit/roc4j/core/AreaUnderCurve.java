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

import java.awt.Color;

import fr.ign.cogit.roc4j.graphics.OperatingArea;
import fr.ign.cogit.roc4j.graphics.OperatingLine;
import fr.ign.cogit.roc4j.graphics.OperatingPoint;


//=================================================================================
// Class for handling area under a given ROC curve
// Date : 06/03/2017
//=================================================================================

public class AreaUnderCurve {

	// Parameters
	private ReceiverOperatingCharacteristics roc;
	private Color color;
	private float transparency;
	private ConfidenceBands bands;
	private double confidenceLevel = -1;


	// Getters
	public Color getColor(){return color;}
	public ReceiverOperatingCharacteristics getRoc(){return roc;}

	// Setters
	public void setColor(Color color){

		this.color = new Color(color.getRed()/255.f, color.getGreen()/255.f, color.getBlue()/255.f, this.transparency);

	}

	public void setTransparency(float t){

		transparency = t;
		this.color = new Color(this.color.getRed()/255.f, this.color.getGreen()/255.f, this.color.getBlue()/255.f, t);

	}


	// Main constructor from ROC curve
	public AreaUnderCurve(ReceiverOperatingCharacteristics roc){

		this.roc = roc;
		this.color = Color.GREEN;
		this.setTransparency(0.3f);
		this.bands = null;

	}

	// Alternatie constructor from confidence bands
	public AreaUnderCurve(ConfidenceBands bands){

		this.roc = bands.getCentralROC();
		this.bands = bands;
		this.color = Color.GREEN;
		this.setTransparency(0.3f);
		this.confidenceLevel = bands.getConfidenceLevel();

	}


	// Area Under Curve value
	public double getAreaValue(){

		return roc.computeAUC();

	}

	// Uncertainty on the area
	public double getAreaValueConfidenceInterval(){

		if (confidenceLevel == -1){
			
			return getAreaValueConfidenceInterval(95.0);
			
		}else{
			
			return getAreaValueConfidenceInterval(confidenceLevel);
			
		}

	}

	// Uncertainty on the area
	public double getAreaValueConfidenceInterval(double confidenceLevel){

		return getAreaValueConfidenceInterval(confidenceLevel, ConfidenceBands.DISTRIBUTION_NORMAL);

	}

	// Uncertainty on the area
	public double getAreaValueConfidenceInterval(double confidenceLevel, int distribution){

		if (bands == null){
			
			System.err.println("Error : Area Under Curve must be defined from ROC Confidence Bands to compute confidence intervals");
			System.exit(1);
			
		}
		
		return bands.computeAUCConfidenceInterval(confidenceLevel, distribution);

	}
	
	// Get polygon
	public OperatingArea getOperatingArea(){
		
		OperatingLine line = new OperatingLine(roc.toOperatingPointsSequency());
		
		line.addOperatingPoint(new OperatingPoint(1, 0));
		line.addOperatingPoint(line.getOperatingPoint(0));
		
		return new OperatingArea(line);
		
	}





}
