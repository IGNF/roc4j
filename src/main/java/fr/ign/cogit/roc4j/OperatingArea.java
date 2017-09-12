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

//=================================================================================
// Class for storing operating area in the roc space
// Its main feature is a closed operating line
// Date : 07/03/2017
//=================================================================================

public class OperatingArea {
	
	
	private OperatingLine boundary;
	private Color bkgColor;
	private Color borderColor;


	public Color getBorderColor(){return borderColor;}
	public Color getBackGroundColor(){return bkgColor;}
	public OperatingLine getBoundary(){return this.boundary;}
	
	public void setBorderColor(Color color){this.borderColor = color;}
	public void setBackGroundColor(Color color){this.bkgColor = color;}

	
	public OperatingArea(OperatingLine line){
		
		this(line, new Color(0.f, 1.f, 0.f, 0.3f), Color.GREEN);
		
	}
	
	public OperatingArea(OperatingLine line, Color backGroundColor, Color borderColor){
		
		if (!line.getOperatingPoint(line.size()-1).equals(line.getOperatingPoint(0))){
			
			System.err.println("Error : operating area object should be defined from a closed operating line");
			System.exit(0);
			
		}
		
		this.boundary = line;
		this.bkgColor = backGroundColor;
		this.borderColor = borderColor;
		
	}
	
	public boolean contains(OperatingPoint point){
		
		double[] X = new double[boundary.size()];
		double[] Y = new double[boundary.size()];
		
		for (int i=0; i<X.length; i++){
			
			X[i] = boundary.getOperatingPoint(i).getFpr();
			Y[i] = boundary.getOperatingPoint(i).getTpr();
			
		}
		
		return Tools.inside(point.getFpr(), point.getTpr(), X, Y);
		
	}
	

}
