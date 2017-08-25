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
import java.awt.Font;

public class RocSpaceStyle {

	// Styles
	public static RocSpaceStyle STYLE_PLAIN = new RocSpaceStyle(0);
	public static RocSpaceStyle STYLE_OSCILLO = new RocSpaceStyle(1);
	public static RocSpaceStyle STYLE_HUD = new RocSpaceStyle(2);
	public static RocSpaceStyle STYLE_OLD = new RocSpaceStyle(3);
	public static RocSpaceStyle STYLE_DOS_BLUE = new RocSpaceStyle(4);
	public static RocSpaceStyle STYLE_DOS_BLACK = new RocSpaceStyle(5);
	public static RocSpaceStyle STYLE_RANDOM = new RocSpaceStyle(6);
	
	// Style codes

	// Attributes
	private Color backgroundColor = Color.WHITE;
	private Color tickColor = Color.BLACK;
	private Color gridColor = new Color(0.f,0.f,0.f,0.3f);
	private Color labelColor = Color.BLACK;
	private int rx = 10;
	private int ry = 10;
	private Color diagonalColor = Color.BLACK;
	private Color frameColor = Color.BLACK;
	private Color titleColor = Color.BLACK;

	private Font f = new Font("Arial Bold", Font.ITALIC, 16);
	private Font flab = new Font("Arial", Font.PLAIN, 12);
	private Font ftick = new Font("Courrier New", Font.PLAIN, 10);

	// Setters
	public void setRx(int rx){this.rx = rx;}
	public void setRy(int ry){this.ry = ry;}

	public void setFrameColor(Color frameColor){this.frameColor = frameColor;}
	public void setGridColor(Color gridColor){this.gridColor = gridColor;}
	public void setLabelColor(Color labelColor){this.labelColor = labelColor;}
	public void setTitleColor(Color titleColor){this.titleColor = titleColor;}
	public void setTickColor(Color tickColor){this.tickColor = tickColor;}
	public void setBackgroundColor(Color bkgColor){this.backgroundColor = bkgColor;}
	public void setDiagonalColor(Color diagonalColor){this.diagonalColor = diagonalColor;}
	
	public void setFontTitle(Font f){this.f = f;}
	public void setFontLabel(Font f){this.flab = f;}
	public void setFontTicks(Font f){this.ftick = f;}
	
	public int getRx(){return this.rx;}
	public int getRy(){return this.ry;}

	public Color getFrameColor(){return this.frameColor;}
	public Color getGridColor(){return this.gridColor;}
	public Color getLabelColor(){return this.labelColor;}
	public Color getTitleColor(){return this.titleColor;}
	public Color getTickColor(){return this.tickColor;}
	public Color getBackgroundColor(){return this.backgroundColor;}
	public Color getDiagonalColor(){return this.diagonalColor;}
	
	public Font getFontTitle(){return this.f;}
	public Font getFontLabel(){return this.flab;}
	public Font getFontTicks(){return this.ftick;}
	


	public RocSpaceStyle(){
		
		backgroundColor = Color.WHITE;
		tickColor = Color.BLACK;
		gridColor = new Color(0.f,0.f,0.f,0.3f);
		labelColor = Color.BLACK;
		rx = 10;
		ry = 10;
		diagonalColor = Color.BLACK;
		frameColor = Color.BLACK;
		titleColor = Color.BLACK;
		
	}
	

	public RocSpaceStyle(int style){
		
		this();

		if (style == 0){

			backgroundColor = Color.WHITE;
			tickColor = Color.BLACK;
			gridColor = new Color(0.f,0.f,0.f,0.3f);
			labelColor = Color.BLACK;
			rx = 10;
			ry = 10;
			diagonalColor = Color.BLACK;
			frameColor = Color.BLACK;
			titleColor = Color.BLACK;

		}

		if (style == 1){

			backgroundColor = Color.BLACK;
			tickColor = Color.WHITE;
			gridColor = new Color(1.0f,1.0f,1.0f,0.25f);
			labelColor = Color.WHITE;
			diagonalColor = Color.ORANGE;
			frameColor = Color.WHITE;
			titleColor = Color.WHITE;

		}

		if (style == 2){

			backgroundColor = Color.BLACK;
			tickColor = Color.GREEN;
			gridColor = new Color(0.0f,1.0f,0.0f,0.45f);
			labelColor = Color.GREEN;
			rx = 10;
			ry = 10;
			diagonalColor = Color.GREEN;
			frameColor = Color.GREEN;
			titleColor = Color.GREEN;

		}

		if (style == 3){

			backgroundColor = Color.BLACK;
			tickColor = Color.WHITE;
			gridColor = new Color(1.0f,1.0f,1.0f,0.45f);
			labelColor = Color.WHITE;
			rx = 10;
			ry = 10;
			diagonalColor = Color.WHITE;
			frameColor = Color.WHITE;
			titleColor = Color.WHITE;

		}

		if (style == 4){

			backgroundColor = Color.BLUE;
			tickColor = Color.WHITE;
			gridColor = new Color(1.0f,1.0f,1.0f,0.45f);
			labelColor = Color.WHITE;
			rx = 10;
			ry = 10;
			diagonalColor = Color.WHITE;
			frameColor = Color.WHITE;
			titleColor = Color.WHITE;

			f = new Font("Courier New", Font.PLAIN, 16);
			flab = new Font("Courier New", Font.PLAIN, 12);
			ftick = new Font("Courier New", Font.PLAIN, 10);

		}

		if (style == 5){
			
			backgroundColor = Color.BLACK;
			tickColor = Color.WHITE;
			gridColor = new Color(1.0f,1.0f,1.0f,0.45f);
			labelColor = Color.WHITE;
			rx = 10;
			ry = 10;
			diagonalColor = Color.WHITE;
			frameColor = Color.WHITE;
			titleColor = Color.WHITE;

			f = new Font("Courier New", Font.PLAIN, 16);
			flab = new Font("Courier New", Font.PLAIN, 12);
			ftick = new Font("Courier New", Font.PLAIN, 10);

		}


		if (style == 6){

			backgroundColor = sampleColor(false);
			tickColor = sampleColor(false);
			gridColor = sampleColor(true);
			labelColor = sampleColor(false);
			rx = 10;
			ry = 10;
			diagonalColor = (sampleColor(false));
			frameColor = (sampleColor(false));
			titleColor = (sampleColor(false));

		}


	}

	// Sampling random color
	private static Color sampleColor(boolean transparency){

		float t = 1.f;

		if (transparency){

			t = (float)(Math.random());

		}

		Color color = new Color((float)(Math.random()), (float)(Math.random()), (float)(Math.random()), t);

		return color;

	}	

}
