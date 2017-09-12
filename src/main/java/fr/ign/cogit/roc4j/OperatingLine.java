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
import java.awt.Stroke;
import java.util.ArrayList;
import java.awt.BasicStroke;

//=================================================================================
// Class for storing operating lines in the roc space
// Its main feature is a list of operating points
// Date : 06/03/2017
//=================================================================================


public class OperatingLine {
	

	// Parameters
	private ArrayList<OperatingPoint> VERTICES;
	
	// Graphics
	private Color color = Color.BLACK;
	private Stroke stroke = new BasicStroke(1.f);
	
	// Getters
	public Color getColor(){return color;}
	public int size(){return VERTICES.size();}
	public Stroke getStroke(){return stroke;}
	public OperatingPoint getOperatingPoint(int index){return VERTICES.get(index);}
	
	// Setters
	public void setColor(Color color){this.color = color;}
	public void setStroke(Stroke stroke){this.stroke = stroke;}
	
	// Stroke example
	public static Stroke DASHED = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2,1}, 0);
	
	// Stroke factory
	public static Stroke makeDashedStroke(float a, float b){
		
		return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{a,b}, 0);
		
	}

	
	public OperatingLine(){
		
		VERTICES = new ArrayList<OperatingPoint>();
		
	}
	
	
	public OperatingLine(ArrayList<OperatingPoint> POINTS){
		
		this.VERTICES = POINTS;
		
	}
	
	public void addOperatingPoint(OperatingPoint point){
		
		VERTICES.add(point);
		
	}
	
	public OperatingLine copy(){
		
		OperatingLine line = new OperatingLine();
		
		for (int i=0; i<VERTICES.size(); i++){
			
			line.addOperatingPoint(new OperatingPoint(VERTICES.get(i).getFpr(), VERTICES.get(i).getTpr(), VERTICES.get(i).getThreshold()));
			
		}
		
		line.setColor(color);
		line.setStroke(stroke);
		
		return line;
		
	}
	
}
