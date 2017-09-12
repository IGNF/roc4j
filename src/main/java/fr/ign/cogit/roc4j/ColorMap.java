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
import java.util.ArrayList;


//==========================================================================
/** Class for defining color scales applied to maps
 * Colors are interpolated linearly
 * Date : 27/12/2013
 * Contact : yannmeneroux@yahoo.fr */
//==========================================================================

public class ColorMap {

	// ---------------------------- ATTRIBUTES ----------------------------

	private ArrayList<Color> colors;
	private ArrayList<Double> thresholds;

	// Auto-generated color maps 
	public static ColorMap TYPE_STANDARD = new ColorMap(0);
	public static ColorMap TYPE_RAINBOW = new ColorMap(1);
	public static ColorMap TYPE_BLACK_AND_WHITE = new ColorMap(2);
	public static ColorMap TYPE_MOUNTAIN = new ColorMap(3);
	public static ColorMap TYPE_RANDOM = new ColorMap(4);
	public static ColorMap TYPE_COMPLEX_RANDOM = new ColorMap(5);
	public static ColorMap TYPE_WATER_FRONT = new ColorMap(6);
	public static ColorMap TYPE_SEA_SHORE = new ColorMap(7);
	public static ColorMap TYPE_STANDARD_TERRAIN = new ColorMap(8);
	public static ColorMap TYPE_LOWER_RAINBOW = new ColorMap(9);
	public static ColorMap TYPE_UPPER_RAINBOW = new ColorMap(10);
	public static ColorMap TYPE_VOLCANO = new ColorMap(11);
	public static ColorMap TYPE_GRAVITY = new ColorMap(12);
	public static ColorMap TYPE_MATHS = new ColorMap(13);
	public static ColorMap TYPE_FIRE = new ColorMap(14);
	public static ColorMap TYPE_WATER = new ColorMap(15);



	// --------------------------- CONSTRUCTORS ---------------------------

	public ColorMap(){

		colors = new ArrayList<Color>() ;
		thresholds = new ArrayList<Double>();


	}

	// ----------------------------- GETTERS ------------------------------

	public Color getColor(int index){return colors.get(index);}
	public double getThreshold(int index){return thresholds.get(index);}
	public int getColorNumber(){return colors.size();}

	// ----------------------------- SETTERS ------------------------------

	public void setColor(int index, Color c){colors.set(index,c);}
	public void setThreshold(int index, double t){thresholds.set(index,t);}

	// ----------------------------- METHODS ------------------------------

	//---------------------------------------------------------------------
	/** Method to add a couple color-threshold to the color map
	 * @param color Color at threshold t 
	 * @param threshold Associated threshold t */
	//---------------------------------------------------------------------
	public void add(Color color, double threshold){

		if (thresholds.size() != 0){
			if (threshold <= thresholds.get(thresholds.size()-1)){

				System.err.println("Error : new threshold should be greater than previous one");
				System.exit(1);

			}
		}

		if (threshold > 1){

			System.err.println("Error : threshold should be lesser than 1");
			System.exit(0);

		}


		colors.add(color);
		thresholds.add(threshold);

	}

	//---------------------------------------------------------------------
	/** Method to invert a color map (base color is becomming top color)
	 * @return Inverted color map */
	//---------------------------------------------------------------------
	public ColorMap invert(){

		ColorMap cmOut = new ColorMap();

		int inv;

		for (int i=0; i<getColorNumber(); i++){

			inv = (getColorNumber()-1)-i;
			cmOut.add(getColor(inv), getThreshold(i));

		}

		return cmOut;

	}

	//---------------------------------------------------------------------
	/** Method to invert RVB components of each color map
	 * @return Inverted color map */
	//---------------------------------------------------------------------
	public ColorMap invertColors(){

		ColorMap cmOut = new ColorMap();

		Color c;
		Color cIni;

		for (int i=0; i<getColorNumber(); i++){

			cIni = getColor(i);
			c = new Color(255-cIni.getRed(),255-cIni.getGreen(),255-cIni.getBlue());

			cmOut.add(c, getThreshold(i));

		}

		return cmOut;

	}

	//---------------------------------------------------------------------
	/** Method to produce darker ColorMap
	 * @return Darkened color map */
	//---------------------------------------------------------------------
	public ColorMap darker(){

		ColorMap cmOut = new ColorMap();

		for (int i=0; i<getColorNumber(); i++){

			cmOut.add(getColor(i).darker(), getThreshold(i));

		}

		return cmOut;

	}

	//---------------------------------------------------------------------
	/** Method to produce brighter color map
	 * @return Brightened color map */
	//---------------------------------------------------------------------
	public ColorMap brighter(){

		ColorMap cmOut = new ColorMap();

		for (int i=0; i<getColorNumber(); i++){

			cmOut.add(getColor(i).brighter(), getThreshold(i));

		}

		return cmOut;

	}

	//---------------------------------------------------------------------
	/** Method to produce darker color map
	 * param iteration Number of times 'darker' method is called
	 * @return Darkened color map */
	//---------------------------------------------------------------------
	public ColorMap darker(int iteration){

		ColorMap cmOut = this.copy();

		for (int i=0; i<iteration; i++){cmOut = cmOut.darker();}

		return cmOut;

	}

	//---------------------------------------------------------------------
	/** Method to produce brighter color map
	 * param iteration Number of times brighter method is called
	 * @return Darkened color map */
	//---------------------------------------------------------------------
	public ColorMap brighter(int iteration){

		ColorMap cmOut = this.copy();

		for (int i=0; i<iteration; i++){cmOut = cmOut.brighter();}

		return cmOut;

	}

	//---------------------------------------------------------------------
	/** Method to copy colormap
	 * Input : none
	 * Output : copy of the colormap */
	//---------------------------------------------------------------------
	public ColorMap copy(){

		ColorMap cmOut = new ColorMap();

		for (int i=0; i<getColorNumber(); i++){

			cmOut.add(getColor(i), getThreshold(i));

		}

		return cmOut;

	}

	//---------------------------------------------------------------------
	/** Method to interpolate color on a given altitude z
	 * @param z Altitude
	 * @param zmin Minimal altitude
	 * @param zmax Maximal altitude
	 * @return Interpolated color */
	//---------------------------------------------------------------------
	public Color interpolate(double z, double zmin, double zmax){

		validate();

		if (zmin == zmax){return colors.get((int)(colors.size()/2));}

		double tm = (z-zmin)/(zmax-zmin);

		if (tm == 0){return colors.get(0);}
		if (tm >= 1){return colors.get(colors.size()-1);}

		int index = 0;

		while (tm >= thresholds.get(index)){

			index ++;

		}

		// Case z = min
		if (index == 0){index ++;}

		float r1 = colors.get(index-1).getRed();    float r2 = colors.get(index).getRed(); 
		float g1 = colors.get(index-1).getGreen();  float g2 = colors.get(index).getGreen();
		float b1 = colors.get(index-1).getBlue();   float b2 = colors.get(index).getBlue();
		float a1 = colors.get(index-1).getAlpha();  float a2 = colors.get(index).getAlpha();


		float w2 = (float) ((tm-thresholds.get(index-1))/(thresholds.get(index)-thresholds.get(index-1))); 
		float w1 = (float) (1-w2);                    

		float rc = (float) (w1*r1+w2*r2);    
		float gc = (float) (w1*g1+w2*g2);  
		float bc = (float) (w1*b1+w2*b2); 
		float ac = (float) (w1*a1+w2*a2); 

		if (rc > 255){rc = 255;}
		if (gc > 255){gc = 255;}
		if (bc > 255){bc = 255;}  
		if (ac > 255){ac = 255;}  

		return new Color(rc/255,gc/255,bc/255,ac/255); 

	}

	//---------------------------------------------------------------------
	/** Method to make colormap transparent
	 * @param : transparency (float)
	 * @return Inverted color map */
	//---------------------------------------------------------------------
	public ColorMap makeTransparent(float transparency){

		ColorMap cmOut = new ColorMap();

		for (int i=0; i<getColorNumber(); i++){

			float r = getColor(i).getRed()/255.f;
			float g = getColor(i).getGreen()/255.f;
			float b = getColor(i).getBlue()/255.f;

			cmOut.add(new Color(r, g, b, transparency), getThreshold(i));

		}

		return cmOut;

	}


	//---------------------------------------------------------------------
	/** Method to make colormap transparent gradually
	 * @param : transparency (float)
	 * @return shaded color map */
	//---------------------------------------------------------------------
	public ColorMap makeShaded(){
		
		ColorMap cm = this.copy();
		
		for (int i=0; i<cm.getColorNumber(); i++){


			float r = cm.getColor(i).getRed()/255.f;
			float g = cm.getColor(i).getGreen()/255.f;
			float b = cm.getColor(i).getBlue()/255.f;

			Color c = new Color(r, g, b, 1.f-(float)((double)i/(double)cm.getColorNumber()));

			cm.setColor(i, c);

		}
		
		return cm;
		
	}


	//---------------------------------------------------------------------
	/** Method to validate color map consistency (end if not consistent) */
	//---------------------------------------------------------------------
	public void validate(){

		if (thresholds.get(0) != 0){

			System.err.println("Error : first threshold in ColorMap must be equal to 0");
			System.exit(1);

		}

		if (thresholds.get(thresholds.size()-1) != 1){

			System.err.println("Error : last threshold in ColorMap must be equal to 1");
			System.exit(1);

		}

	}


	//---------------------------------------------------------------------
	/** Special constructor to generate random color map
	 * @param colorNumber Number of colors in random map
	 * @return Random ColorMap */
	//---------------------------------------------------------------------
	public static ColorMap generateRandomMap(int colorNumber){

		ColorMap cmap = new ColorMap();

		for (int i=0; i<colorNumber; i++){

			cmap.add(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)),(double)i/(colorNumber-1));


		}

		return cmap;

	}


	//---------------------------------------------------------------------
	/** Special constructor to generate automatically color maps
	 * @param colormapCode Code between 1 and 15
	 * @return Generated color map */
	//---------------------------------------------------------------------
	public ColorMap(int colormapCode){

		this();

		if (colormapCode == 0){

			add(Color.RED.darker(), 0.0);
			add(Color.RED, 0.3);
			add(Color.ORANGE, 0.5);
			add(Color.YELLOW, 0.55);
			add(Color.GREEN, 0.6);
			add(Color.BLUE, 0.65);
			add(Color.MAGENTA, 1.0);

		}

		if (colormapCode == 1){

			add(Color.MAGENTA,0.0);
			add(Color.BLUE,0.2);
			add(Color.GREEN,0.4);
			add(Color.YELLOW,0.6);
			add(Color.ORANGE,0.8);
			add(Color.RED,1.0);

		}

		if (colormapCode == 2){

			add(Color.BLACK,0.0);
			add(Color.WHITE,1.0);

		}

		if (colormapCode == 3){

			add(Color.GREEN.darker().darker().darker().darker().darker(),0.0);
			add(Color.GREEN.darker().darker(),0.3);
			add(new Color(91,59,17),0.55);
			add(new Color(111,79,37),0.7);
			add(new Color(150,150,150),0.8);
			add(Color.WHITE,1.0);

		}

		if (colormapCode == 4){

			add(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)),0.0);
			add(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)),1.0);

		}

		if (colormapCode == 5){

			for (int i=0; i<7; i++){

				add(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)),(double)i/6);

			}

		}

		if (colormapCode == 6){

			add(Color.BLUE,0.0);
			add(Color.BLUE,0.001);
			add(Color.GREEN.darker().darker(),0.002);
			add(Color.GREEN,1.0);

		}

		if (colormapCode == 7){

			add(Color.BLUE,0.0);
			add(Color.BLUE,0.001);
			add(new Color(71,39,7),0.002);
			add(new Color(151,119,77),1.0);

		}

		if (colormapCode == 8){

			add(Color.GREEN.darker().darker().darker().darker().darker(),0.0);
			add(Color.GREEN.darker().darker().darker(),0.4);
			add(new Color(91,59,17),0.75);
			add(new Color(131,109,57),1.0);

		}

		if (colormapCode == 9){

			add(Color.MAGENTA,0.0);
			add(Color.BLUE,0.3);
			add(Color.GREEN,0.6);
			add(Color.YELLOW,1.0);

		}

		if (colormapCode == 10){

			add(Color.GREEN.darker(),0.0);
			add(Color.YELLOW,0.3);
			add(Color.ORANGE,0.6);
			add(Color.RED,1.0);

		}

		if (colormapCode == 11){

			add(Color.YELLOW,0.0);
			add(Color.ORANGE,0.2);
			add(Color.RED,0.4);
			add(new Color(71,39,7),0.42);
			add(new Color(151,119,77),1.0);

		}

		if (colormapCode == 12){

			add(Color.BLUE.darker().darker(),0.0);
			add(Color.BLUE,0.05);
			add(Color.CYAN,0.2);
			add(Color.GREEN,0.4);
			add(Color.YELLOW,0.6);
			add(Color.ORANGE,0.8);
			add(Color.RED,0.95);
			add(Color.RED.darker(),1.0);

		}

		if (colormapCode == 13){

			add(Color.BLUE.darker().darker(),0.0);
			add(Color.BLUE,0.05);
			add(Color.CYAN,0.3);
			add(Color.YELLOW,0.5);
			add(Color.ORANGE,0.65);
			add(Color.RED,0.95);
			add(Color.RED.darker(),1.0);

		}

		if (colormapCode == 14){

			add(Color.RED.darker(),0.0);
			add(Color.YELLOW.brighter(),1.0);

		}

		if (colormapCode == 15){

			add(Color.BLUE.darker().darker().darker(),0.0);
			add(Color.CYAN.darker(),1.0);

		}

	}

}

