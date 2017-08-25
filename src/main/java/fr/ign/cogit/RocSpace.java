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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class RocSpace extends JComponent implements MouseListener, MouseMotionListener {

	private int margin = 60;
	private int grid_rx = 10;
	private int grid_ry = 10;
	private double dx = 0.20;
	private double dy = 0.20;

	private String xlabel = "FPR";
	private String ylabel = "TPR";
	private String xlabel2 = "FPR";
	private String ylabel2 = "TPR";

	private String title = "Receiver Operating Characteristic Space";
	private Font f = new Font("Arial Bold", Font.ITALIC, 16);
	private Font flab = new Font("Arial", Font.PLAIN, 12);
	private Font ftick = new Font("Courrier New", Font.PLAIN, 11);

	private Color backgroundColor = Color.WHITE;
	private Color frameColor = Color.BLACK;
	private Color titleColor = Color.BLACK;
	private Color labelColor = Color.BLACK;
	private Color tickColor = Color.BLACK;
	private Color diagonalColor = Color.BLACK;
	private Color gridColor = new Color(0.f,0.f,0.f,0.3f);
	private boolean tickXVisible = true;
	private boolean tickYVisible = true;
	private boolean diagonalVisible = true;

	private boolean plotCurvesOnTop = true;

	private boolean plotOptimalLine = true;

	private boolean coordinatesVisible = false;
	private boolean coordinateStylePercent = true;

	private boolean projectionVisible = false;
	private float projectionThickness = 1.f;
	private Color projectionColor = Color.BLACK;
	private float projectionTransparency = 1.0f;
	private boolean attachProjectionToFPR = false;
	private boolean attachProjectionToTPR = false;
	private int attachToRoc = 0;
	private String thresholdLabel = "p";

	private int gridDashLength = 2;
	private int gridDashInterval = 1;
	private float gridThickness = 0.3f;

	private int diagonalDashLength = 10;
	private int diagonalDashInterval = 10;
	private float diagonalThickness = 2.f;

	private boolean thresholdVisible = false;
	private Color thresholdColor = Color.BLACK;
	private Font thresholdFont = new Font("Arial", Font.PLAIN, 10);

	private int click_x = 0;
	private int click_y = 0;

	private int xp1 = 0;
	private int yp1 = 0;
	private int xp2 = 0;
	private int yp2 = 0;
	private int xp3 = 0;
	private int yp3 = 0;
	private int xp4 = 0;
	private int yp4 = 0;

	// Export formats
	public static int FORMAT_JPG = 1;
	public static int FORMAT_PNG = 2;
	public static int FORMAT_BMP = 3;
	public static int FORMAT_GIF = 4;
	public static int FORMAT_SVG = 5;


	// ROC curves
	private ArrayList<ReceiverOperatingCharacteristics> ROCS;

	// Confidence bands
	private ArrayList<ConfidenceBands> CB;

	// Opimal lines
	private ArrayList<OptimalLine> OPTLINE;

	// Operating points
	private ArrayList<OperatingPoint> POINTS;

	// Operating lines
	private ArrayList<OperatingLine> LINES;

	// Areas under curves
	private ArrayList<AreaUnderCurve> AUC;

	// Operating areas
	private ArrayList<OperatingArea> AREAS;

	//Texts
	private ArrayList<String> TEXTS;
	private ArrayList<Integer> TEXTS_X;
	private ArrayList<Integer> TEXTS_Y;
	private ArrayList<Integer> TEXTS_S;
	private ArrayList<Color> TEXTS_C;

	// Legends
	private ArrayList<String> LEGENDS;
	private ArrayList<Integer> LEGENDS_S;
	private ArrayList<Color> LEGENDS_C;

	// Isocost line
	private ArrayList<IsoCostLine> ISOLINES;

	// Setters
	public void setTitle(String title){this.title = title;}
	public void setMargin(int margin){this.margin = margin;}
	public void setDx(double dx){this.dx = dx;}
	public void setDy(double dy){this.dy = dy;}
	public void setRx(int rx){this.grid_rx = rx;}
	public void setRy(int ry){this.grid_ry = ry;}
	public void setOpimalLineVisible(boolean bool){plotOptimalLine = bool;}
	public void setCoordinatesVisible(boolean bool){coordinatesVisible = bool;}
	public void setCoordinatesPercent(boolean bool){coordinateStylePercent = bool;}
	public void setXLabel(String xlabel){this.xlabel = xlabel; this.xlabel2 = xlabel;}
	public void setYLabel(String ylabel){this.ylabel = ylabel; this.ylabel2 = ylabel;}
	public void setProjectionVisible(boolean bool){projectionVisible = bool;}
	public void setProjectionThickness(float t){projectionThickness = t;}
	public void setProjectionColor(Color c){projectionColor = c;}
	public void setProjectionTransparency(float t){projectionTransparency = t;}
	public void setThresholdColor(Color c){thresholdColor = c;}
	public void setThresholdFont(Font f){thresholdFont = f;}
	public void setThresholdLabel(String label){thresholdLabel = label;}
	public void setDiagonalVisible(boolean visible){diagonalVisible = visible;}
	public void setTickXVisible(boolean visible){tickXVisible = visible;}
	public void setTickYVisible(boolean visible){tickYVisible = visible;}
	public void setFontTick(Font f){ftick = f;}
	public void setFontLabel(Font f){flab = f;}
	public void setFontTitle(Font f){this.f = f;}
	public void setGridThickness(float t){this.gridThickness = t;}
	public void setGridDashInterval(int t){this.gridDashInterval = t;}
	public void setGridDashLength(int t){this.gridDashLength = t;}
	public void setDiagonalThickness(float t){this.diagonalThickness = t;}
	public void setDiagonalDashInterval(int t){this.diagonalDashInterval = t;}
	public void setDiagonalDashLength(int t){this.diagonalDashLength = t;}
	
	public void setGridOpacity(float t){this.gridColor = new Color(gridColor.getRed()/255.f, gridColor.getGreen()/255.f, gridColor.getBlue()/255.f, t);}


	public void addIsoCostLines(Context context, double resolution){

		for (double b=resolution; b<1-resolution; b+=resolution){

			IsoCostLine line = new IsoCostLine(context, b);

			addIsoCostLine(line);

		}

	}

	public void setProjectionAttachedToRoc(int index){

		if (index >= ROCS.size()){

			String message = "Error : projection must be attached to an existing ROC curve index. ";
			message += "Index "+index+" is invalid. ";
			message += "Only "+ROCS.size()+" ROC curve(s) are associated to ROC space.";
			System.err.println(message);
			System.exit(1);

		}

		attachToRoc = index;

	}

	public void setThresholdVisible(boolean bool){

		if (bool && !projectionVisible){

			System.err.println("Error : projection must be set visible to display threshold probability value");
			System.exit(1);

		}

		if (bool && !attachProjectionToFPR && !attachProjectionToTPR){

			System.err.println("Error : projection must be attached to an axis to display threshold probability value");
			System.exit(1);

		}

		thresholdVisible = bool;

	}

	public void setProjectionAttachedToFpr(boolean bool){

		attachProjectionToFPR = bool;
		attachProjectionToTPR = !bool;

	}

	public void setProjectionAttachedToTpr(boolean bool){

		attachProjectionToFPR = !bool;
		attachProjectionToTPR = bool;

	}


	public void setFrameColor(Color frameColor){this.frameColor = frameColor;}
	public void setGridColor(Color gridColor){this.gridColor = gridColor;}
	public void setLabelColor(Color labelColor){this.labelColor = labelColor;}
	public void setTitleColor(Color titleColor){this.titleColor = titleColor;}
	public void setTickColor(Color tickColor){this.tickColor = tickColor;}
	public void setBackgroundColor(Color bkgColor){this.backgroundColor = bkgColor;}
	public void setDiagonalColor(Color diagonalColor){this.diagonalColor = diagonalColor;}

	public void plotCurvesOnTop(boolean bool){this.plotCurvesOnTop = bool;}

	// Getters
	public String getTitle(){return title;}
	public String getXLabel(){return xlabel;}
	public String getYLabel(){return ylabel;}
	public int getMargin(){return margin;}
	public int getRx(){return grid_rx;}
	public int getRy(){return grid_ry;}
	public double getDx(){return dx;}
	public double getDy(){return dy;}


	public RocSpace(){

		ROCS = new ArrayList<ReceiverOperatingCharacteristics>();
		CB = new ArrayList<ConfidenceBands>();
		OPTLINE = new ArrayList<OptimalLine>();
		POINTS = new ArrayList<OperatingPoint>();
		LINES = new ArrayList<OperatingLine>();
		AREAS = new ArrayList<OperatingArea>();
		AUC = new ArrayList<AreaUnderCurve>();
		TEXTS = new ArrayList<String>();
		TEXTS_X = new ArrayList<Integer>();
		TEXTS_Y = new ArrayList<Integer>();
		TEXTS_S = new ArrayList<Integer>();
		TEXTS_C = new ArrayList<Color>();
		LEGENDS = new ArrayList<String>();
		LEGENDS_S = new ArrayList<Integer>();
		LEGENDS_C = new ArrayList<Color>();
		ISOLINES = new ArrayList<IsoCostLine>();

	}


	// Add ROC curve to the graphics
	public void addRocCurve(ReceiverOperatingCharacteristics roc){

		ROCS.add(roc);

	}

	// Add list of ROC curves to the graphics
	public void addRocCurve(ArrayList<ReceiverOperatingCharacteristics> rocs){

		for (int i=0; i<rocs.size(); i++){

			ROCS.add(rocs.get(i));

		}

	}

	// Add confidence bands to the graphics
	public void addConfidenceBands(ConfidenceBands cb){

		CB.add(cb);

	}

	// Add list of confidence bands to the graphics
	public void addConfidenceBands(ArrayList<ConfidenceBands> cbs){

		for (int i=0; i<cbs.size(); i++){

			CB.add(cbs.get(i));

		}

	}

	// Add an optimal line to the space
	public void addOptimalLine(OptimalLine line){

		OPTLINE.add(line);

	}

	// Add a list of optimal lines
	public void addOptimalLine(ArrayList<OptimalLine> LINES){

		for (int i=0; i<LINES.size(); i++){

			OPTLINE.add(LINES.get(i));

		}

	}


	// Add an operating point to the space
	public void addOperatingPoint(OperatingPoint point){

		POINTS.add(point);

	}

	// Add a list of operating points
	public void addOperatingPoint(ArrayList<OperatingPoint> PTS){

		for (int i=0; i<PTS.size(); i++){

			POINTS.add(PTS.get(i));

		}

	}
	// Add an operating line to the space
	public void addOperatingLine(OperatingLine line){

		LINES.add(line);

	}

	// Add a list of operating lines
	public void addOperatingLine(ArrayList<OperatingLine> LNS){

		for (int i=0; i<LNS.size(); i++){

			LINES.add(LNS.get(i));

		}

	}

	// Add an operating area to the space
	public void addOperatingArea(OperatingArea area){

		AREAS.add(area);

	}

	// Add a list of operating areas
	public void addOperatingArea(ArrayList<OperatingArea> ARS){

		for (int i=0; i<ARS.size(); i++){

			AREAS.add(ARS.get(i));

		}

	}

	// Add an area under curve
	public void addAreaUnderCurve(AreaUnderCurve auc){

		AUC.add(auc);

	}

	// Add a list of areas under curves
	public void addAreaUnderCurve(ArrayList<AreaUnderCurve> auc){

		for (int i=0; i<auc.size(); i++){

			AUC.add(auc.get(i));

		}

	}

	// Add an isocost line
	public void addIsoCostLine(IsoCostLine isoline){

		ISOLINES.add(isoline);

	}

	// Add a list of isocost lines
	public void addIsoCostLine(ArrayList<IsoCostLine> isolines){

		for (int i=0; i<isolines.size(); i++){

			ISOLINES.add(isolines.get(i));

		}

	}

	// Add a text to the list
	public void writeText(String text, int x, int y){

		writeText(text, x, y, 12, Color.BLACK);

	}


	// Add a text to the list
	public void writeText(String text, int x, int y, int s, Color c){

		TEXTS.add(text);
		TEXTS_X.add(x);
		TEXTS_Y.add(y);
		TEXTS_S.add(s);
		TEXTS_C.add(c);

	}

	// Add a text to legend
	public void addLegend(String text){

		addLegend(text, Color.BLACK);

	}

	// Add a text to legend
	public void addLegend(String text, Color color){

		addLegend(text, color, 14);

	}

	// Add a text to legend
	public void addLegend(String text, Color color, int size){

		LEGENDS.add(text);
		LEGENDS_S.add(size);
		LEGENDS_C.add(color);

	}

	// Cost raster
	public void displayCostField(Context context, ColorMap cmap){

		displayCostField(context, 0.03, 0.3, cmap);

	}

	public void displayCostField(Context context, double resolution, double transparency, ColorMap cmap){

		double cfp = context.getCostFalsePositive();
		double cfn = context.getCostFalseNegative();
		double ctp = context.getCostTruePositive();
		double ctn = context.getCostTrueNegative();
		double pp = context.getPriorPositive();
		double pn = context.getPriorNegative();

		for (double x=0.001; x<1; x+=resolution){

			for (double y=0.001; y<1; y+=resolution){

				OperatingPoint point = new OperatingPoint(x, y);

				double cost = 2*point.getCost(cfp, cfn, ctp, ctn, pp, pn)/(cfp+cfn+ctp+ctn);

				Color col = cmap.interpolate(cost, 0, 1);

				Color color1 = new Color(col.getRed()/255.f, col.getGreen()/255.f, col.getBlue()/255.f, (float)transparency);
				Color color2 = new Color(col.getRed()/255.f, col.getGreen()/255.f, col.getBlue()/255.f, 0.f);


				OperatingLine line = new OperatingLine(); 
				line.addOperatingPoint(new OperatingPoint(x, y));
				line.addOperatingPoint(new OperatingPoint(x, Math.min(y+resolution, 1)));
				line.addOperatingPoint(new OperatingPoint(Math.min(x+resolution, 1), Math.min(y+resolution,1)));
				line.addOperatingPoint(new OperatingPoint(Math.min(x+resolution, 1), y));
				line.addOperatingPoint(new OperatingPoint(x, y));

				OperatingArea area = new OperatingArea(line, color1, color2);

				addOperatingArea(area);

			}

		}

	}


	// Graphics
	public void paint(Graphics g) {

		addMouseListener(this);
		addMouseMotionListener(this);

		// Background
		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight()); 

		// Frame
		g.setColor(frameColor);
		g.drawRect(margin, margin, getWidth()-2*margin,  getHeight()-2*margin);

		// Grid
		double rx = (double)(getWidth()-2*margin)/grid_rx;
		double ry = (double)(getHeight()-2*margin)/grid_ry;

		// Style
		Graphics2D g2d = (Graphics2D) g.create();
		Stroke dashed = new BasicStroke(gridThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{gridDashLength,gridDashInterval}, 0);
		g2d.setStroke(dashed);
		g2d.setColor(gridColor);

		// X_axis grid
		for (int xg=1; xg<=grid_rx; xg++){

			g2d.drawLine((int)(xg*rx+margin), margin, (int)(xg*rx+margin), getHeight()-margin);

		}

		// Y-axis grid
		for (int yg=1; yg<=grid_ry; yg++){

			g2d.drawLine(margin, (int)(yg*ry+margin), getWidth()-margin, (int)(yg*ry+margin));

		}

		// Plot operating areas

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i=0; i<AREAS.size(); i++){


			g.setColor(AREAS.get(i).getBackGroundColor());

			int[] Xl = new int[AREAS.get(i).getBoundary().size()];
			int[] Yl = new int[AREAS.get(i).getBoundary().size()];

			for (int j=0; j<AREAS.get(i).getBoundary().size(); j++){

				int[] temp = transformCoordinates(AREAS.get(i).getBoundary().getOperatingPoint(j).getFpr(), AREAS.get(i).getBoundary().getOperatingPoint(j).getTpr());

				Xl[j] = temp[0];
				Yl[j] = temp[1];

			}

			g.fillPolygon(Xl, Yl, Xl.length);

			g.setColor(AREAS.get(i).getBorderColor());

			g.drawPolyline(Xl, Yl, Xl.length);


		}

		// Title
		g.setColor(titleColor);
		g.setFont(f);
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(title, getWidth()/2 - metrics.stringWidth(title)/2, margin/2+metrics.getHeight()/2);


		// X label
		if (!coordinatesVisible){xlabel2 = xlabel;}
		g.setColor(labelColor);
		g.setFont(flab);
		metrics = g.getFontMetrics(g.getFont());
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(xlabel2, getWidth()/2 - metrics.stringWidth(xlabel2)/2, getHeight()-margin/2+10);

		// Y label
		if (!coordinatesVisible){ylabel2 = ylabel;}
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		Font rotatedFont = flab.deriveFont(affineTransform);
		g2d.setFont(rotatedFont); g2d.setColor(labelColor);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawString(ylabel2, margin/2-10, getHeight()/2+metrics.stringWidth(ylabel2)/2);

		// Numbers
		g.setColor(tickColor);
		g.setFont(ftick);
		metrics = g.getFontMetrics(g.getFont());


		if (tickXVisible){

			// Abscissa
			for (double x=0.0; x<=1.0; x+=dx){

				String xchar = Math.round(x*100)/100.0+"";

				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawString(xchar, margin+(int)(x*(getWidth()-2*margin))-metrics.stringWidth(xchar)/2, getHeight()-margin+20);	

			}

		}

		if (tickYVisible){

			// Ordonnate
			for (double y=0.0; y<=1.0; y+=dy){

				String ychar = Math.round(y*100)/100.0+"";

				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawString(ychar, margin-10-metrics.stringWidth(ychar), getHeight()-(margin+(int)(y*(getHeight()-2*margin))));	

			}

		}

		// Sheer random classifier
		if (diagonalVisible){
			dashed = new BasicStroke(diagonalThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{diagonalDashLength,diagonalDashInterval}, 0);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(dashed);
			g2d.setColor(diagonalColor);
			int[] P1 = transformCoordinates(0.0,0.0);
			int[] P2 = transformCoordinates(1.0,1.0);
			g2d.drawLine(P1[0], P1[1], P2[0], P2[1]);
		}

		// Plot AUC visible
		for (int i=0; i<AUC.size(); i++){

			g.setColor(AUC.get(i).getColor());

			int[] AUCX = new int[AUC.get(i).getRoc().getXRoc().length+2];
			int[] AUCY = new int[AUC.get(i).getRoc().getXRoc().length+2];

			for (int j=0; j<AUC.get(i).getRoc().getXRoc().length; j++){

				int[] temp = transformCoordinates(AUC.get(i).getRoc().getXRoc()[j], AUC.get(i).getRoc().getYRoc()[j]);

				AUCX[j] = temp[0];
				AUCY[j] = temp[1];

			}

			int[] temp1 = transformCoordinates(1.0, 0.0);
			int[] temp2 = transformCoordinates(0.0, 0.0);

			AUCX[AUCX.length-2] = temp1[0];
			AUCY[AUCX.length-2] = temp1[1];
			AUCX[AUCX.length-1] = temp2[0];
			AUCY[AUCX.length-1] = temp2[1];


			g.fillPolygon(AUCX, AUCY, AUCX.length);

		}

		if (plotCurvesOnTop){

			// Plotting confidence bands
			plotConfidenceBands(g);

			// Plotting ROC curves
			plotRocCurves(g);

		}
		else{

			// Plotting ROC curves
			plotRocCurves(g);

			// Plotting confidence bands
			plotConfidenceBands(g);

		}

		// Plotting error bars
		plotErrorBars(g);

		// Plot optimal line
		if (plotOptimalLine){

			for (int i=0; i<OPTLINE.size(); i++){

				double theta = (90+OPTLINE.get(i).getAngle())*Math.PI/180;

				double x = 0;
				double y = 0;

				if (OPTLINE.get(i).getAngle() > -45){

					x = 1.0;
					y = 1-Math.tan(-OPTLINE.get(i).getAngle()*Math.PI/180);

				}
				else{

					x = Math.tan(theta);

				}

				dashed = new BasicStroke(OPTLINE.get(i).getThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10,10}, 0);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setStroke(dashed);
				g2d.setColor(OPTLINE.get(i).getColor());
				int[] P3 = transformCoordinates(0.0,1.0);
				int[] P4 = transformCoordinates(x,y);
				g2d.drawLine(P3[0], P3[1], P4[0], P4[1]);

			}

		}

		// Plot operating points
		for (int i=0; i<POINTS.size(); i++){

			int[] coords = transformCoordinates(POINTS.get(i).getFpr(), POINTS.get(i).getTpr());

			int taille = (int)POINTS.get(i).getSize();
			String style = POINTS.get(i).getStyle();

			g.setColor(POINTS.get(i).getColor());


			if (style.equals("o")){
				g.drawOval(coords[0]-taille/2, coords[1]-taille/2, taille, taille);
			}

			if (style.equals("fo")){

				g.fillOval(coords[0]-taille/2, coords[1]-taille/2,taille , taille);

			}

			if (style.equals("s")){

				g.drawRect(coords[0]-taille/2, coords[1]-taille/2, taille, taille);

			}

			if (style.equals("fs")){

				g.fillRect(coords[0]-taille, coords[1]-taille, taille, taille);

			}

			if (style.equals("x")){

				g.drawLine(coords[0]-taille/2, coords[1]-taille/2, coords[0]+taille/2, coords[1]+taille/2);
				g.drawLine(coords[0]-taille/2, coords[1]+taille/2, coords[0]+taille/2, coords[1]-taille/2);

			}

			if (style.equals("^")){

				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {(int)(coords[1]+0.3*taille), (int)(coords[1]-taille+0.3*taille), (int)(coords[1]+0.3*taille)};

				g.drawPolygon(TX, TY, 3);

			}

			if (style.equals("f^")){

				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {(int)(coords[1]+0.3*taille), (int)(coords[1]-taille+0.3*taille), (int)(coords[1]+0.3*taille)};

				g.fillPolygon(TX, TY, 3);

			}

			if (style.equals("v")){

				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {coords[1], coords[1]+taille, coords[1]};

				g.drawPolygon(TX, TY, 3);

			}

			if (style.equals("fv")){


				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {coords[1], coords[1]+taille, coords[1]};

				g.fillPolygon(TX, TY, 3);

			}

		}

		// Plot operating lines
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i=0; i<LINES.size(); i++){

			((Graphics2D) g).setStroke(LINES.get(i).getStroke());

			int[] Xl = new int[LINES.get(i).size()];
			int[] Yl = new int[LINES.get(i).size()];

			for (int j=0; j<LINES.get(i).size(); j++){

				int[] temp = transformCoordinates(LINES.get(i).getOperatingPoint(j).getFpr(), LINES.get(i).getOperatingPoint(j).getTpr());

				Xl[j] = temp[0];
				Yl[j] = temp[1];

			}


			g.setColor(LINES.get(i).getColor());

			g.drawPolyline(Xl, Yl, Xl.length);

		}

		// Plot isocost lines
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i=0; i<ISOLINES.size(); i++){

			((Graphics2D) g).setStroke(new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3,3}, 0));

			double m = ISOLINES.get(i).getSlope();
			double b = ISOLINES.get(i).getIntercept();

			double x1 = 0;
			double y1 = b;

			double x2 = 1;
			double y2 = m+b;

			if (y2 > 1){

				x2 = (1-b)/m;
				y2 = 1;

			}


			int[] temp1 = transformCoordinates(x1, y1);
			int[] temp2 = transformCoordinates(x2, y2);

			g.setColor(ISOLINES.get(i).getColor());


			g.drawLine(temp1[0], temp1[1], temp2[0], temp2[1]);

		}


		// Plotting projection axis
		if (projectionVisible){

			if ((xp3 != 0) && (yp1 != 0)){

				double th = ROCS.get(attachToRoc).getThresholdFromFpr(transformCoordinatesReverse(xp3, yp1)[0]);

				ColorMap cmap = ROCS.get(attachToRoc).getColorMap();

				if ((cmap != null) && ((attachProjectionToFPR) || (attachProjectionToTPR))){
					Color c = cmap.interpolate(1-th, 0, 1);

					float r = (float)(c.getRed()/255.0);
					float v = (float)(c.getGreen()/255.0);
					float b = (float)(c.getBlue()/255.0);

					g2d.setColor(new Color(r, v, b, projectionTransparency));

				}
				else{

					if ((attachProjectionToFPR) || (attachProjectionToTPR)){

						float r = (float)(ROCS.get(attachToRoc).getColor().getRed()/255.0);
						float v = (float)(ROCS.get(attachToRoc).getColor().getGreen()/255.0);
						float b = (float)(ROCS.get(attachToRoc).getColor().getBlue()/255.0);

						g2d.setColor(new Color(r, v, b, projectionTransparency));

					}
					else{

						float r = (float)(projectionColor.getRed()/255.0);
						float v = (float)(projectionColor.getGreen()/255.0);
						float b = (float)(projectionColor.getBlue()/255.0);

						g2d.setColor(new Color(r, v, b, projectionTransparency));

					}

				}

			}

			dashed = new BasicStroke(projectionThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5,5}, 0);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(dashed);


			g2d.drawLine(xp1, yp1, xp2, yp2);
			g2d.drawLine(xp3, yp3, xp4, yp4);

		}

		// Writing threshold probability value
		if (thresholdVisible){

			g.setColor(thresholdColor);
			g.setFont(thresholdFont);

			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if ((xp3 != 0) && (yp1 != 0)){

				double th = ROCS.get(attachToRoc).getThresholdFromFpr(transformCoordinatesReverse(xp3, yp1)[0]);

				ColorMap cmap = ROCS.get(attachToRoc).getColorMap();

				if (cmap != null){

					g.setColor(cmap.interpolate(1-th, 0, 1));

				}
				else{

					float r = (float)(ROCS.get(attachToRoc).getColor().getRed()/255.0);
					float v = (float)(ROCS.get(attachToRoc).getColor().getGreen()/255.0);
					float b = (float)(ROCS.get(attachToRoc).getColor().getBlue()/255.0);

					g.setColor(new Color(r, v, b, projectionTransparency));

				}

				String text = thresholdLabel+" = "+(int)(1000.0*th)/1000.0;

				g.drawString(text, xp3+20, yp1+20);

			}


		}

		// Texts
		for (int i=0; i<TEXTS.size(); i++){

			g.setFont(new Font("Arial", Font.PLAIN, TEXTS_S.get(i)));
			g.setColor(TEXTS_C.get(i));

			g.drawString(TEXTS.get(i), TEXTS_X.get(i), TEXTS_Y.get(i));

		}

		// Legends
		for (int i=0; i<LEGENDS.size(); i++){

			g.setFont(new Font("Arial", Font.PLAIN, (int)(LEGENDS_S.get(i)/700.0*Math.min(this.getHeight(), this.getWidth()))));
			g.setColor(LEGENDS_C.get(i));
			metrics = g.getFontMetrics(g.getFont());

			int[] temp = transformCoordinates(1, 0);

			int x = temp[0]-metrics.stringWidth(LEGENDS.get(i)) - (int)(0.01*this.getWidth()); 
			int y = temp[1]-2*(LEGENDS.size()-1-i)*metrics.getHeight()-(int)(metrics.getHeight());

			g.drawString(LEGENDS.get(i), x, y);

		}

	}

	// -----------------------------------------------------------------------------
	// Plot ROC curves
	// -----------------------------------------------------------------------------
	private void plotRocCurves(Graphics g){


		for (int k=0; k<ROCS.size(); k++){

			ColorMap cmap = ROCS.get(k).getColorMap();

			if (cmap == null){

				double[] XROC = ROCS.get(k).getXRoc();
				double[] YROC = ROCS.get(k).getYRoc();

				int resolution = ROCS.get(k).getResolution();
				float thickness = ROCS.get(k).getThickness();

				Color color = ROCS.get(k).getColor();

				int[] X = new int[resolution];
				int[] Y = new int[resolution];

				for (int i=0; i<resolution; i++){

					int[] T = transformCoordinates(XROC[i], YROC[i]);

					X[i] = T[0];
					Y[i] = T[1];

				}

				// Plot
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(color);
				g2.setStroke(new BasicStroke(thickness));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.drawPolyline(X, Y, resolution);

			}
			else{

				Graphics2D g2 = (Graphics2D) g;

				double[] XROC = ROCS.get(k).getXRoc();
				double[] YROC = ROCS.get(k).getYRoc();

				int resolution = ROCS.get(k).getResolution();
				float thickness = ROCS.get(k).getThickness();

				g2.setStroke(new BasicStroke(thickness));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int[] X = new int[resolution];
				int[] Y = new int[resolution];

				for (int i=0; i<resolution; i++){

					int[] T = transformCoordinates(XROC[i], YROC[i]);

					X[i] = T[0];
					Y[i] = T[1];

				}

				// Plot
				for (int i=0; i<resolution-1; i++){

					g2.setColor(cmap.interpolate(i, 0, resolution-1));

					g2.drawLine(X[i], Y[i], X[i+1], Y[i+1]);

				}

			}

		}

	}

	// -----------------------------------------------------------------------------
	// Plot confidence bands
	// -----------------------------------------------------------------------------
	private void plotConfidenceBands(Graphics g){

		for (int l=0; l<CB.size(); l++){

			ConfidenceBands cb = CB.get(l);
			int resolution = cb.getResolution();

			int[] CBX = new int[2*resolution];
			int[] CBY = new int[2*resolution];

			// Upper band
			double[] CBUX = cb.getUpperBandX();
			double[] CBUY = cb.getUpperBandY();

			for (int i=0; i<resolution; i++){

				int[] T = transformCoordinates(CBUX[i], CBUY[i]);

				CBX[i] = T[0];
				CBY[i] = T[1];

			}

			// Lower band
			double[] CBLX = cb.getLowerBandX();
			double[] CBLY = cb.getLowerBandY();

			for (int i=0; i<resolution; i++){

				int[] T = transformCoordinates(CBLX[i], CBLY[i]);

				CBX[2*resolution-1-i] = T[0];
				CBY[2*resolution-1-i] = T[1];

			}

			// Plot
			Graphics2D g2 = (Graphics2D) g;

			// Background
			if (cb.isBackgroundFilled()){
				g2.setColor(cb.getColor());
				g2.fillPolygon(CBX, CBY, 2*resolution);
			}

			// Borders
			if (cb.isBordersColorVisible()){

				g2.setColor(cb.getBordersColor());
				g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{cb.getDashLength(), cb.getDashInterval()}, 0));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.drawPolygon(CBX, CBY, 2*resolution);

			}

		}

	}

	// -----------------------------------------------------------------------------
	// Plot error bars
	// -----------------------------------------------------------------------------
	private void plotErrorBars(Graphics g){

		for (int l=0; l<CB.size(); l++){

			ConfidenceBands cb = CB.get(l);

			if (cb.getErrorBarsModeXY()){

				int resolution = cb.getResolution();

				if (!cb.getErrorBarsVisible()){continue;}


				int integer_gap = (int)(resolution*cb.getErrorBarsResolution());

				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(cb.getErrorBarsColor());


				double[] centralx = cb.getCentralROC().getXRoc();
				double[] centraly = cb.getCentralROC().getYRoc();
				double[] uppery = cb.getErrorBarsH();

				double[] rightx = cb.getErrorBarsh();

				g2.setStroke(new BasicStroke((float)(cb.getErrorBarsThickness())));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


				for (int i=integer_gap; i<resolution-integer_gap; i+=integer_gap){

					int[] pos1 = transformCoordinates(centralx[i], centraly[i]-uppery[i]);
					int[] pos2 = transformCoordinates(centralx[i], centraly[i]+uppery[i]);

					g2.drawLine(pos1[0], pos1[1], pos2[0], pos2[1]);

					int[] pos11 = transformCoordinates(centralx[i]-cb.getErrorBarsWidth()/2.0, centraly[i]-uppery[i]);
					int[] pos12 = transformCoordinates(centralx[i]+cb.getErrorBarsWidth()/2.0, centraly[i]-uppery[i]);

					g2.drawLine(pos11[0], pos11[1], pos12[0], pos12[1]);

					int[] pos21 = transformCoordinates(centralx[i]-cb.getErrorBarsWidth()/2.0, centraly[i]+uppery[i]);
					int[] pos22 = transformCoordinates(centralx[i]+cb.getErrorBarsWidth()/2.0, centraly[i]+uppery[i]);

					g2.drawLine(pos21[0], pos21[1], pos22[0], pos22[1]);

				}

				for (int i=integer_gap; i<resolution-integer_gap; i+=integer_gap){

					int[] pos1 = transformCoordinates(centralx[i]-rightx[i], centraly[i]);
					int[] pos2 = transformCoordinates(centralx[i]+rightx[i], centraly[i]);

					g2.drawLine(pos1[0], pos1[1], pos2[0], pos2[1]);

					int[] pos11 = transformCoordinates(centralx[i]-rightx[i], centraly[i]-cb.getErrorBarsWidth()/2.0);
					int[] pos12 = transformCoordinates(centralx[i]-rightx[i], centraly[i]+cb.getErrorBarsWidth()/2.0);

					g2.drawLine(pos11[0], pos11[1], pos12[0], pos12[1]);

					int[] pos21 = transformCoordinates(centralx[i]+rightx[i], centraly[i]-cb.getErrorBarsWidth()/2.0);
					int[] pos22 = transformCoordinates(centralx[i]+rightx[i], centraly[i]+cb.getErrorBarsWidth()/2.0);

					g2.drawLine(pos21[0], pos21[1], pos22[0], pos22[1]);

				}

			}
			else{

				int resolution = cb.getResolution();

				if (!cb.getErrorBarsVisible()){continue;}


				int integer_gap = (int)(resolution*cb.getErrorBarsResolution());

				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(cb.getErrorBarsColor());


				double[] centralx = cb.getResampledCentralROC().getXRoc();
				double[] lowery = cb.getResampledLowerBandY();
				double[] uppery = cb.getResampledUpperBandY();

				g2.setStroke(new BasicStroke((float)(cb.getErrorBarsThickness())));
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


				for (int i=integer_gap; i<resolution-integer_gap; i+=integer_gap){

					int[] pos1 = transformCoordinates(centralx[i], lowery[i]);
					int[] pos2 = transformCoordinates(centralx[i], uppery[i]);

					g2.drawLine(pos1[0], pos1[1], pos2[0], pos2[1]);

					int[] pos11 = transformCoordinates(centralx[i]-cb.getErrorBarsWidth()/2.0, lowery[i]);
					int[] pos12 = transformCoordinates(centralx[i]+cb.getErrorBarsWidth()/2.0, lowery[i]);

					g2.drawLine(pos11[0], pos11[1], pos12[0], pos12[1]);

					int[] pos21 = transformCoordinates(centralx[i]-cb.getErrorBarsWidth()/2.0, uppery[i]);
					int[] pos22 = transformCoordinates(centralx[i]+cb.getErrorBarsWidth()/2.0, uppery[i]);

					g2.drawLine(pos21[0], pos21[1], pos22[0], pos22[1]);

				}

			}

		}

	}


	// Roc space style setting
	public void setStyle(RocSpaceStyle style){

		setBackgroundColor(style.getBackgroundColor());
		setTickColor(style.getTickColor());
		setGridColor(style.getGridColor());
		setLabelColor(style.getLabelColor());
		setRx(style.getRx());
		setRy(style.getRy());
		setDiagonalColor(style.getDiagonalColor());
		setFrameColor(style.getFrameColor());
		setTitleColor(style.getTitleColor());

		f = style.getFontTitle();
		flab = style.getFontLabel();
		ftick = style.getFontTicks();

	}


	public void mouseDragged(MouseEvent e) {


	}
	public void mouseMoved(MouseEvent e) {

		if (!projectionVisible && !coordinatesVisible){

			return;

		} 

		double[] transfo = transformCoordinatesReverse(e.getX(), e.getY());

		double fprs = transfo[0];
		double tprs = transfo[1];

		xlabel2 = xlabel;
		ylabel2 = ylabel;

		xp1 = 0;
		yp1 = 0;
		xp2 = 0;
		yp2 = 0;
		xp3 = 0;
		yp3 = 0;
		xp4 = 0;
		yp4 = 0;

		if (transfo[0] >= 0 && transfo[1] >= 0 && transfo[0] <= 1 && transfo[1] <= 1){

			if (projectionVisible){

				int tr0[] = transformCoordinates(0,0);
				int tr1[] = transformCoordinates(1,1);

				if (!attachProjectionToFPR && !attachProjectionToTPR){

					xp1 = tr0[0];
					yp1 = e.getY();
					xp2 = tr1[0];
					yp2 = e.getY();

					xp3 = e.getX();
					yp3 = tr0[1];
					xp4 = e.getX();
					yp4 = tr1[1];

				}

				if (attachProjectionToFPR){

					double tpr = ROCS.get(attachToRoc).getTruePositiveRate(fprs);

					tprs = tpr;

					int[] tprt = transformCoordinates(0, tpr);

					xp1 = tr0[0];
					yp1 = tprt[1];
					xp2 = tr1[0];
					yp2 = tprt[1];

					xp3 = e.getX();
					yp3 = tr0[1];
					xp4 = e.getX();
					yp4 = tr1[1];

				}

				if (attachProjectionToTPR){

					double fpr = ROCS.get(attachToRoc).getFalsePositiveRate(tprs);

					int[] fprt = transformCoordinates(fpr, 0);

					fprs = fpr;

					xp1 = tr0[0];
					yp1 = e.getY();
					xp2 = tr1[0];
					yp2 = e.getY();

					xp3 = fprt[0];
					yp3 = tr0[1];
					xp4 = fprt[0];
					yp4 = tr1[1];

				}

			}

			double factor = 1000.0;

			if (coordinateStylePercent){

				factor = 10.0;

			}

			fprs = (int)(1000*fprs)/factor;
			tprs = (int)(1000*tprs)/factor;

			if (!coordinateStylePercent){

				xlabel2 = xlabel+" = "+fprs;
				ylabel2 = ylabel+" = "+tprs;

			}

			else{

				xlabel2 = xlabel+" = "+fprs+" %";
				ylabel2 = ylabel+" = "+tprs+" %";

			}

		}

		repaint();

	}

	// Conversion ROC space coordinates to frame coordinates
	private int[] transformCoordinates(double xnorm, double ynorm){

		int[] transformation = {0, 0};

		transformation[0] = (int)(xnorm*(getWidth()-2*margin))+margin;
		transformation[1] = (int)((1-ynorm)*(getHeight()-2*margin))+margin;

		return transformation;

	}

	// Conversion frame coordinates to ROC space coordinates
	private double[] transformCoordinatesReverse(int posx, int posy){

		double[] transformation = {0, 0};

		transformation[0] = (double)(posx-margin)/(double)(getWidth()-2*margin);
		transformation[1] = 1.0-(double)(posy-margin)/(double)(getHeight()-2*margin);

		return transformation;

	}

	// Conversion ROC space coordinates to SVG frame coordinates
	private int[] transformCoordinatesSVG(double xnorm, double ynorm, double h, double w){

		int[] transformation = {0, 0};

		transformation[0] = (int)(xnorm*(w-2*margin))+margin;
		transformation[1] = (int)((1-ynorm)*(h-2*margin))+margin;

		return transformation;

	}

	public void mouseClicked(MouseEvent e) {

		if (e.getButton() == 1){

			if (e.getClickCount() == 2){

				if (e.getX() != click_x && e.getY() != click_y){

					click_x = e.getX();
					click_y = e.getY();

					attachToRoc++; 

					attachToRoc = attachToRoc%ROCS.size();

				}

			}

		}

		if (e.getButton() == 3){

			if (e.getX() != click_x && e.getY() != click_y){

				click_x = e.getX();
				click_y = e.getY();

				if (attachProjectionToFPR){attachProjectionToFPR = false; attachProjectionToTPR = true; return;}
				if (attachProjectionToTPR){attachProjectionToTPR = false; thresholdVisible = false; return;}
				if (!attachProjectionToFPR && !attachProjectionToTPR){projectionVisible = true; 
				thresholdVisible = true; attachProjectionToFPR = true; attachToRoc = 0; return;}

			}

		}

	}
	public void mousePressed(MouseEvent e) {


	}
	public void mouseReleased(MouseEvent e) {


	}
	public void mouseEntered(MouseEvent e) {


	}
	public void mouseExited(MouseEvent e) {


	}

	// -----------------------------------------------------------------------------
	// Method to save graphics
	// -----------------------------------------------------------------------------
	public void save(String file){

		save(file, FORMAT_PNG);

	}



	// -----------------------------------------------------------------------------
	// Method to save graphics
	// -----------------------------------------------------------------------------
	public void save(String file, int format){

		save(file, format, 800, 800);

	}

	// -----------------------------------------------------------------------------
	// Method to save graphics
	// -----------------------------------------------------------------------------
	public void save(String file, int format, int svg_height, int svg_width){

		if (format == FORMAT_SVG){

			try {

				writeSVG(file, svg_height, svg_width);

			} catch (IOException e) {

				e.printStackTrace();
			}

			return;

		}

		if (this.getWidth() == 0){

			System.err.println("Error : except for SVG file format, ROC space graphics must be displayed before saving plot");
			System.exit(0);

		}

		BufferedImage bImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D cg = bImg.createGraphics();
		this.paintAll(cg);

		String type = "";

		if (format == FORMAT_BMP){type = "bmp";}
		if (format == FORMAT_JPG){type = "jpg";}
		if (format == FORMAT_PNG){type = "png";}
		if (format == FORMAT_GIF){type = "gif";}

		try {
			if (ImageIO.write(bImg, type, new File(file)))
			{
				System.out.println("File "+file+" has been saved with success");
			}
		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	// -----------------------------------------------------------------------------
	// Method to save graphics
	// -----------------------------------------------------------------------------
	public void writeSVG(String file) throws IOException{

		writeSVG(file, 800, 800);

	}

	// -----------------------------------------------------------------------------
	// Method to save graphics
	// -----------------------------------------------------------------------------
	private void writeSVG(String file, int svg_width, int svg_height) throws IOException{

		margin = (int)(1.5*margin);

		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));

		String begin = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
		begin += "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\""+(svg_width+100)+"\" height=\""+(svg_height+100)+"\"> \r\n";
		begin += "\t <title>ROC space SVG export</title>\r\n";
		begin += "\t <desc>Roc space generated with roc4j open source program</desc>\r\n";

		writer.write(begin);

		String body = convertToSVGText(svg_width, svg_height);

		writer.write(body);

		String end = "</svg>";

		writer.write(end);	

		margin = (int)(margin/1.5);

		writer.close();

		System.out.println("File "+file+" has been saved with success");

	}

	// -----------------------------------------------------------------------------
	// Method to get SVG text
	// -----------------------------------------------------------------------------
	protected String convertToSVGText(int svg_width, int svg_height){

		String body = "";


		// --------------------------------------------
		// Frame
		// --------------------------------------------
		String bgcolor = makeSVGColor(backgroundColor);
		String stroke = makeSVGColor(frameColor);

		body += "\t <rect width=\""+(svg_width)+"\" height=\""+(svg_height)+"\" x=\""+0+"\" y=\""+0+"\" stroke=\""+ makeSVGColor(Color.WHITE)+"\" fill=\""+bgcolor+"\" />\r\n";


		// --------------------------------------------
		// Grid
		// --------------------------------------------
		double rx = (double)(svg_width-2*margin)/grid_rx;
		double ry = (double)(svg_height-2*margin)/grid_ry;

		// Style
		String grdcolor = makeSVGColor(gridColor);

		// X_axis grid
		for (int xg=1; xg<grid_rx; xg++){

			int x1 = (int)(xg*rx+margin);
			int y1 = margin;
			int x2 = (int)(xg*rx+margin);
			int y2 = svg_height-margin;

			body += "\t  <line stroke-opacity= \""+gridColor.getAlpha()/255.f+"\" stroke-dasharray=\""+gridDashLength+", "+gridDashInterval+"\" stroke-width=\""+gridThickness+"\" x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+x2+"\" y2=\""+y2+"\" stroke=\""+grdcolor+"\" /> \r\n";


		}

		// Y-axis grid
		for (int yg=1; yg<grid_ry; yg++){

			int x1 = margin;
			int y1 = (int)(yg*ry+margin);
			int x2 = svg_width-margin;
			int y2 = (int)(yg*ry+margin);

			body += "\t  <line stroke-opacity= \""+gridColor.getAlpha()/255.f+"\" stroke-dasharray=\""+gridDashLength+", "+gridDashInterval+"\" stroke-width=\""+gridThickness+"\" x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+x2+"\" y2=\""+y2+"\" stroke=\""+grdcolor+"\" /> \r\n";

		}

		body += "\t <rect fill-opacity=\"0.0\" width=\""+(svg_width-2*margin)+"\" height=\""+(svg_height-2*margin)+"\" x=\""+margin+"\" y=\""+margin+"\" stroke=\""+stroke+"\" fill=\""+bgcolor+"\" />\r\n";

		// --------------------------------------------
		// Plot operating areas
		// --------------------------------------------

		for (int i=0; i<AREAS.size(); i++){

			String colorBkg = makeSVGColor(AREAS.get(i).getBackGroundColor());
			String colorBrd =  makeSVGColor(AREAS.get(i).getBorderColor());

			double transparency = AREAS.get(i).getBorderColor().getAlpha();


			int[] Xl = new int[AREAS.get(i).getBoundary().size()];
			int[] Yl = new int[AREAS.get(i).getBoundary().size()];

			for (int j=0; j<AREAS.get(i).getBoundary().size(); j++){

				double x = AREAS.get(i).getBoundary().getOperatingPoint(j).getFpr();
				double y = AREAS.get(i).getBoundary().getOperatingPoint(j).getTpr();

				int[] temp = transformCoordinatesSVG(x, y, svg_height, svg_width);

				Xl[j] = temp[0];
				Yl[j] = temp[1];

			}

			String coords = "";

			for (int j=0; j<Xl.length; j++){

				coords += Xl[j]+","+Yl[j]+" ";

			}


			body += "\t <polygon fill-opacity=\""+AREAS.get(i).getBackGroundColor().getAlpha()/200.0+"\" fill=\""+colorBkg+"\" points=\""+coords+"\"/>\r\n";

			body += "\t <polyline stroke-opacity=\""+transparency+"\" stroke=\""+colorBrd+"\" style=\"fill:none\" points=\""+coords+"\"/>\r\n";


		}



		// --------------------------------------------
		// Diagonal
		// --------------------------------------------
		if (diagonalVisible){

			String diagColor = makeSVGColor(diagonalColor);

			int[] T1 = transformCoordinatesSVG(0, 0, svg_height, svg_width);
			int[] T2 = transformCoordinatesSVG(1, 1, svg_height, svg_width);

			body += "\t  <line stroke-dasharray=\""+diagonalDashLength+", "+diagonalDashInterval+"\" stroke-width=\""+diagonalThickness+"\" x1=\""+T1[0]+"\" y1=\""+T1[1]+"\" x2=\""+T2[0]+"\" y2=\""+T2[1]+"\" stroke=\""+diagColor+"\" /> \r\n";
		}


		// --------------------------------------------
		// Confidence bands
		// --------------------------------------------
		for (int l=0; l<CB.size(); l++){

			ConfidenceBands cb = CB.get(l);
			int resolution = cb.getResolution();

			int[] CBX = new int[2*resolution];
			int[] CBY = new int[2*resolution];


			// Upper band
			double[] CBUX = cb.getUpperBandX();
			double[] CBUY = cb.getUpperBandY();

			for (int i=0; i<resolution; i++){

				int[] T = transformCoordinatesSVG(CBUX[i], CBUY[i], svg_height, svg_width);

				CBX[i] = T[0];
				CBY[i] = T[1];

			}

			// Lower band
			double[] CBLX = cb.getLowerBandX();
			double[] CBLY = cb.getLowerBandY();

			for (int i=0; i<resolution; i++){

				int[] T = transformCoordinatesSVG(CBLX[i], CBLY[i], svg_height, svg_width);

				CBX[2*resolution-1-i] = T[0];
				CBY[2*resolution-1-i] = T[1];

			}

			// Plot

			String coords = "";

			for (int i=0; i<CBX.length; i++){

				coords += CBX[i]+","+CBY[i]+" ";

			}

			// Background
			if (cb.isBackgroundFilled()){

				String bcolor = makeSVGColor(cb.getColor());

				body += "\t <polygon fill-opacity=\""+cb.getColor().getAlpha()/200.0+"\" fill=\""+bcolor+"\" points=\""+coords+"\"/>\r\n";

			}

			// Borders
			if (cb.isBordersColorVisible()){

				String bcolor = makeSVGColor(cb.getBordersColor());


				body += "\t <polyline stroke-dasharray=\""+cb.getDashLength()+", "+cb.getDashInterval()+"\" stroke=\""+bcolor+"\" style=\"fill:none\" points=\""+coords+"\"/>\r\n";

			}

		}

		// --------------------------------------------
		// Plot AUC visible
		// --------------------------------------------

		for (int i=0; i<AUC.size(); i++){

			String aucColor = makeSVGColor(AUC.get(i).getColor());

			int[] AUCX = new int[AUC.get(i).getRoc().getXRoc().length+2];
			int[] AUCY = new int[AUC.get(i).getRoc().getXRoc().length+2];

			for (int j=0; j<AUC.get(i).getRoc().getXRoc().length; j++){

				int[] temp = transformCoordinatesSVG(AUC.get(i).getRoc().getXRoc()[j], AUC.get(i).getRoc().getYRoc()[j], svg_height, svg_width);

				AUCX[j] = temp[0];
				AUCY[j] = temp[1];

			}

			int[] temp1 = transformCoordinatesSVG(1.0, 0.0, svg_height, svg_width);
			int[] temp2 = transformCoordinatesSVG(0.0, 0.0, svg_height, svg_width);

			AUCX[AUCX.length-2] = temp1[0];
			AUCY[AUCX.length-2] = temp1[1];
			AUCX[AUCX.length-1] = temp2[0];
			AUCY[AUCX.length-1] = temp2[1];

			String coords = "";

			for (int j=0; j<AUCX.length; j++){

				coords += AUCX[j]+","+AUCY[j]+" ";

			}


			body += "\t <polygon fill-opacity=\""+AUC.get(i).getColor().getAlpha()/200.0+"\" fill=\""+aucColor+"\" points=\""+coords+"\"/>\r\n";

		}


		// --------------------------------------------
		// ROC curves
		// --------------------------------------------

		for (int k=0; k<ROCS.size(); k++){

			double[] XROC = ROCS.get(k).getXRoc();
			double[] YROC = ROCS.get(k).getYRoc();

			int resolution = ROCS.get(k).getResolution();
			float thickness = ROCS.get(k).getThickness();

			int[] X = new int[resolution];
			int[] Y = new int[resolution];

			for (int i=0; i<resolution; i++){

				int[] T = transformCoordinatesSVG(XROC[i], YROC[i], svg_height, svg_width);

				X[i] = T[0];
				Y[i] = T[1];

			}

			for (int i=0; i<resolution-1; i++){

				String color = makeSVGColor(ROCS.get(k).getColor());
				float opacity = ROCS.get(k).getColor().getAlpha();

				if (ROCS.get(k).getColorMap() != null){

					Color interp_color = ROCS.get(k).getColorMap().interpolate((double)i/(double)resolution, 0, 1);
					color = makeSVGColor(interp_color);

					opacity = interp_color.getAlpha();

				}

				opacity = opacity/255.f;

				body += "\t <line  stroke-opacity=\""+opacity+"\" stroke-width=\""+thickness+"\" x1=\""+X[i]+"\" y1=\""+Y[i]+"\" x2=\""+X[i+1]+"\" y2=\""+Y[i+1]+"\" stroke=\""+color+"\" /> \r\n";


			}

		}

		// --------------------------------------------
		// Plot operating points
		// --------------------------------------------

		for (int i=0; i<POINTS.size(); i++){

			int[] coords = transformCoordinatesSVG(POINTS.get(i).getFpr(), POINTS.get(i).getTpr(), svg_height, svg_width);

			int taille = (int)(POINTS.get(i).getSize() / 1.2);
			String style = POINTS.get(i).getStyle();

			String pcol = makeSVGColor(POINTS.get(i).getColor());


			if (style.equals("o")){

				body += "\t <circle cx=\""+coords[0]+"\" cy=\""+coords[1]+"\" r=\""+taille+"\" stroke=\""+pcol+"\" stroke-width=\"1\" fill-opacity=\"0\"  /> \r\n";

			}

			if (style.equals("fo")){

				body += "\t <circle cx=\""+coords[0]+"\" cy=\""+coords[1]+"\" r=\""+taille+"\" stroke=\""+pcol+"\" stroke-width=\"1\" fill=\""+pcol+"\" /> \r\n";

			}

			if (style.equals("s")){

				body += "\t <rect x=\""+coords[0]+"\" y=\""+coords[1]+"\" width=\""+taille+"\" height=\""+taille+"\" stroke=\""+pcol+"\" stroke-width=\"1\" fill-opacity=\"0\" /> \r\n";

			}

			if (style.equals("fs")){

				body += "\t <rect x=\""+coords[0]+"\" y=\""+coords[1]+"\" width=\""+taille+"\" height=\""+taille+"\" stroke=\""+pcol+"\" stroke-width=\"1\" fill=\""+pcol+"\"/> \r\n";

			}

			if (style.equals("x")){

				body += "\t <line x1=\""+(coords[0]-taille/2)+"\" y1=\""+(coords[1]-taille/2)+"\" x2=\""+(coords[0]+taille/2)+"\" y2=\""+(coords[1]+taille/2)+"\" stroke=\""+pcol+"\" stroke-width=\"1\" /> \r\n";
				body += "\t <line x1=\""+(coords[0]-taille/2)+"\" y1=\""+(coords[1]+taille/2)+"\" x2=\""+(coords[0]+taille/2)+"\" y2=\""+(coords[1]-taille/2)+"\" stroke=\""+pcol+"\" stroke-width=\"1\" /> \r\n";

			}

			if (style.equals("^")){

				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {(int)(coords[1]+0.3*taille), (int)(coords[1]-taille+0.3*taille), (int)(coords[1]+0.3*taille)};

				String coord = "";

				for (int j=0; j<TX.length; j++){

					coord += TX[j]+","+TY[j]+" ";

					body += "\t <polygon points=\""+coord+"\" stroke=\""+pcol+"\" fill-opacity=\"0\"/>\r\n";

				}

			}

			if (style.equals("f^")){

				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {(int)(coords[1]+0.3*taille), (int)(coords[1]-taille+0.3*taille), (int)(coords[1]+0.3*taille)};

				String coord = "";

				for (int j=0; j<TX.length; j++){

					coord += TX[j]+","+TY[j]+" ";

					body += "\t <polygon points=\""+coord+"\" stroke=\""+pcol+"\" fill=\""+pcol+"\"/>\r\n";

				}

			}

			if (style.equals("v")){

				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {coords[1], coords[1]+taille, coords[1]};

				String coord = "";

				for (int j=0; j<TX.length; j++){

					coord += TX[j]+","+TY[j]+" ";

					body += "\t <polygon points=\""+coord+"\" stroke=\""+pcol+"\" fill-opacity=\"0\"/>\r\n";

				}

			}

			if (style.equals("fv")){


				int[] TX = {(int) (coords[0]-0.7*taille), coords[0], (int) (coords[0]+0.7*taille)};
				int[] TY = {coords[1], coords[1]+taille, coords[1]};

				String coord = "";

				for (int j=0; j<TX.length; j++){

					coord += TX[j]+","+TY[j]+" ";

					body += "\t <polygon points=\""+coord+"\" stroke=\""+pcol+"\" fill=\""+pcol+"\"/>\r\n";

				}

			}

		}
		// --------------------------------------------
		// Plot operating lines
		// --------------------------------------------

		for (int i=0; i<LINES.size(); i++){


			int[] Xl = new int[LINES.get(i).size()];
			int[] Yl = new int[LINES.get(i).size()];

			for (int j=0; j<LINES.get(i).size(); j++){

				int[] temp = transformCoordinatesSVG(LINES.get(i).getOperatingPoint(j).getFpr(), LINES.get(i).getOperatingPoint(j).getTpr(), svg_height, svg_width);

				Xl[j] = temp[0];
				Yl[j] = temp[1];

			}

			String coords = "";

			for (int j=0; j<Xl.length; j++){

				coords += Xl[j]+","+Yl[j]+" ";

			}


			String col = makeSVGColor(LINES.get(i).getColor());

			body += "\t <polyline stroke-dasharray=\"5, 5\" stroke=\""+col+"\" style=\"fill:none\" points=\""+coords+"\"/>\r\n";


		}


		// --------------------------------------------
		// Error bars
		// --------------------------------------------

		for (int l=0; l<CB.size(); l++){

			ConfidenceBands cb = CB.get(l);

			if (cb.getErrorBarsModeXY()){

				int resolution = cb.getResolution();

				if (!cb.getErrorBarsVisible()){continue;}


				int integer_gap = (int)(resolution*cb.getErrorBarsResolution());

				String errCol = makeSVGColor(cb.getErrorBarsColor());
				double thickness = cb.getErrorBarsThickness();


				double[] centralx = cb.getCentralROC().getXRoc();
				double[] centraly = cb.getCentralROC().getYRoc();
				double[] uppery = cb.getErrorBarsH();

				double[] rightx = cb.getErrorBarsh();

				for (int i=integer_gap; i<resolution-integer_gap; i+=integer_gap){

					int[] pos1 = transformCoordinatesSVG(centralx[i], centraly[i]-uppery[i], svg_height, svg_width);
					int[] pos2 = transformCoordinatesSVG(centralx[i], centraly[i]+uppery[i], svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos1[0]+"\" y1=\""+pos1[1]+"\" x2=\""+pos2[0]+"\" y2=\""+pos2[1]+"\" stroke=\""+errCol+"\" /> \r\n";

					int[] pos11 = transformCoordinatesSVG(centralx[i]-cb.getErrorBarsWidth()/2.0, centraly[i]-uppery[i], svg_height, svg_width);
					int[] pos12 = transformCoordinatesSVG(centralx[i]+cb.getErrorBarsWidth()/2.0, centraly[i]-uppery[i], svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos11[0]+"\" y1=\""+pos11[1]+"\" x2=\""+pos12[0]+"\" y2=\""+pos12[1]+"\" stroke=\""+errCol+"\" /> \r\n";

					int[] pos21 = transformCoordinatesSVG(centralx[i]-cb.getErrorBarsWidth()/2.0, centraly[i]+uppery[i], svg_height, svg_width);
					int[] pos22 = transformCoordinatesSVG(centralx[i]+cb.getErrorBarsWidth()/2.0, centraly[i]+uppery[i], svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos21[0]+"\" y1=\""+pos21[1]+"\" x2=\""+pos22[0]+"\" y2=\""+pos22[1]+"\" stroke=\""+errCol+"\" /> \r\n";

				}

				for (int i=integer_gap; i<resolution-integer_gap; i+=integer_gap){

					int[] pos1 = transformCoordinatesSVG(centralx[i]-rightx[i], centraly[i], svg_height, svg_width);
					int[] pos2 = transformCoordinatesSVG(centralx[i]+rightx[i], centraly[i], svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos1[0]+"\" y1=\""+pos1[1]+"\" x2=\""+pos2[0]+"\" y2=\""+pos2[1]+"\" stroke=\""+errCol+"\" /> \r\n";

					int[] pos11 = transformCoordinatesSVG(centralx[i]-rightx[i], centraly[i]-cb.getErrorBarsWidth()/2.0, svg_height, svg_width);
					int[] pos12 = transformCoordinatesSVG(centralx[i]-rightx[i], centraly[i]+cb.getErrorBarsWidth()/2.0, svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos11[0]+"\" y1=\""+pos11[1]+"\" x2=\""+pos12[0]+"\" y2=\""+pos12[1]+"\" stroke=\""+errCol+"\" /> \r\n";

					int[] pos21 = transformCoordinatesSVG(centralx[i]+rightx[i], centraly[i]-cb.getErrorBarsWidth()/2.0, svg_height, svg_width);
					int[] pos22 = transformCoordinatesSVG(centralx[i]+rightx[i], centraly[i]+cb.getErrorBarsWidth()/2.0, svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos21[0]+"\" y1=\""+pos21[1]+"\" x2=\""+pos22[0]+"\" y2=\""+pos22[1]+"\" stroke=\""+errCol+"\" /> \r\n";

				}

			}
			else{

				int resolution = cb.getResolution();

				if (!cb.getErrorBarsVisible()){continue;}


				int integer_gap = (int)(resolution*cb.getErrorBarsResolution());

				String errCol = makeSVGColor(cb.getErrorBarsColor());

				double[] centralx = cb.getResampledCentralROC().getXRoc();
				double[] lowery = cb.getResampledLowerBandY();
				double[] uppery = cb.getResampledUpperBandY();

				double thickness = cb.getErrorBarsThickness();

				for (int i=integer_gap; i<resolution-integer_gap; i+=integer_gap){

					int[] pos1 = transformCoordinatesSVG(centralx[i], lowery[i], svg_height, svg_width);
					int[] pos2 = transformCoordinatesSVG(centralx[i], uppery[i], svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos1[0]+"\" y1=\""+pos1[1]+"\" x2=\""+pos2[0]+"\" y2=\""+pos2[1]+"\" stroke=\""+errCol+"\" /> \r\n";


					int[] pos11 = transformCoordinatesSVG(centralx[i]-cb.getErrorBarsWidth()/2.0, lowery[i], svg_height, svg_width);
					int[] pos12 = transformCoordinatesSVG(centralx[i]+cb.getErrorBarsWidth()/2.0, lowery[i], svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos11[0]+"\" y1=\""+pos11[1]+"\" x2=\""+pos12[0]+"\" y2=\""+pos12[1]+"\" stroke=\""+errCol+"\" /> \r\n";

					int[] pos21 = transformCoordinatesSVG(centralx[i]-cb.getErrorBarsWidth()/2.0, uppery[i], svg_height, svg_width);
					int[] pos22 = transformCoordinatesSVG(centralx[i]+cb.getErrorBarsWidth()/2.0, uppery[i], svg_height, svg_width);

					body += "\t <line  stroke-width=\""+thickness+"\" x1=\""+pos21[0]+"\" y1=\""+pos21[1]+"\" x2=\""+pos22[0]+"\" y2=\""+pos22[1]+"\" stroke=\""+errCol+"\" /> \r\n";

				}

			}

		}
		// --------------------------------------------
		// Plot isocost lines
		// --------------------------------------------

		for (int i=0; i<ISOLINES.size(); i++){

			double m = ISOLINES.get(i).getSlope();
			double b = ISOLINES.get(i).getIntercept();

			double x1 = 0;
			double y1 = b;

			double x2 = 1;
			double y2 = m+b;

			if (y2 > 1){

				x2 = (1-b)/m;
				y2 = 1;

			}

			int[] temp1 = transformCoordinatesSVG(x1, y1, svg_height, svg_width);
			int[] temp2 = transformCoordinatesSVG(x2, y2, svg_height, svg_width);

			String col = makeSVGColor(ISOLINES.get(i).getColor());

			body += "\t  <line stroke-dasharray=\"3, 3\" stroke-width=\"1.0\" x1=\""+temp1[0]+"\" y1=\""+temp1[1]+"\" x2=\""+temp2[0]+"\" y2=\""+temp2[1]+"\" stroke=\""+col+"\" /> \r\n";

		}


		// --------------------------------------------
		// Coordinates tick
		// --------------------------------------------



		String coltext = makeSVGColor(tickColor);

		// --------------------------------------------
		// Abscisse
		// --------------------------------------------

		if (tickXVisible){

			for (double x=0.0; x<=1.0; x+=dx){

				String xchar = Math.round(x*100)/100.0+"";


				int xs = margin+(int)(x*(svg_width-2*margin))-10;
				int ys = svg_height-margin+20;

				body += "\t <text font-size=\""+ftick.getSize()+"\" x=\""+xs+"\" y=\""+ys+"\" fill=\""+coltext+"\">"+xchar+"</text> \r\n";

			}

		}

		if (tickYVisible){

			// --------------------------------------------
			// Ordonnate
			// --------------------------------------------

			for (double y=0.0; y<=1.0; y+=dy){

				String ychar = Math.round(y*100)/100.0+"";

				int xs = margin-10-20;
				int ys = svg_height-(margin+(int)(y*(svg_height-2*margin)));

				body += "\t <text font-size=\""+ftick.getSize()+"\" x=\""+xs+"\" y=\""+ys+"\" fill=\""+coltext+"\">"+ychar+"</text> \r\n";

			}

		}

		// --------------------------------------------
		// Plot optimal line
		// --------------------------------------------

		if (plotOptimalLine){

			for (int i=0; i<OPTLINE.size(); i++){

				double theta = (90+OPTLINE.get(i).getAngle())*Math.PI/180;

				double x = 0;
				double y = 0;

				if (OPTLINE.get(i).getAngle() > -45){

					x = 1.0;
					y = 1-Math.tan(-OPTLINE.get(i).getAngle()*Math.PI/180);

				}
				else{

					x = Math.tan(theta);

				}

				String color = makeSVGColor(OPTLINE.get(i).getColor());
				double thickness = OPTLINE.get(i).getThickness();

				int[] P3 = transformCoordinatesSVG(0.0, 1.0, svg_height, svg_width);
				int[] P4 = transformCoordinatesSVG(x, y, svg_height, svg_width);

				body += "\t <line  stroke-dasharray=\"5, 5\" stroke-width=\""+thickness+"\" x1=\""+P3[0]+"\" y1=\""+P3[1]+"\" x2=\""+P4[0]+"\" y2=\""+P4[1]+"\" stroke=\""+color+"\" /> \r\n";

			}

		}

		// --------------------------------------------
		// Legends
		// --------------------------------------------
		for (int i=0; i<LEGENDS.size(); i++){

			int taille = (int)(LEGENDS_S.get(i)/700.0*Math.min(svg_height, svg_width));
			String color = makeSVGColor(LEGENDS_C.get(i));

			int[] temp = transformCoordinatesSVG(1, 0, svg_height, svg_width);

			int x = temp[0]-(int)(svg_width*0.02); 
			int y = temp[1]-2*(LEGENDS.size()-1-i)*taille-(int)(svg_height*0.02);

			body += "\t <text text-anchor=\"end\" font-family=\"Arial\" font-size=\""+taille+"\" fill=\""+color+"\" x=\""+x+"\" y=\""+y+"\">"+LEGENDS.get(i)+"</text>\r\n";


		}

		// --------------------------------------------
		// Texts
		// --------------------------------------------
		for (int i=0; i<TEXTS.size(); i++){

			body += "\t <text text-anchor=\"start\" font-family=\"Arial\" font-size=\""+TEXTS_S.get(i)+"\" fill=\""+makeSVGColor(TEXTS_C.get(i))+"\" x=\""+TEXTS_X.get(i)+"\" y=\""+TEXTS_Y.get(i)+"\">"+TEXTS.get(i)+"</text>\r\n";

		}


		// --------------------------------------------
		// Labels
		// --------------------------------------------
		body += "\t <text text-anchor=\"middle\" font-family=\"Arial\" font-size=\""+flab.getSize()+"\" fill=\""+makeSVGColor(labelColor)+"\" x=\""+svg_width/2+"\" y=\""+(svg_height-margin/3)+"\">"+xlabel+"</text>\r\n";

		body += "\t <g transform=\"translate("+margin/3+","+svg_height/2+")\">\r\n";
		body += "\t\t <text text-anchor=\"middle\" font-family=\"Arial\" font-size=\""+flab.getSize()+"\" fill=\""+makeSVGColor(labelColor)+"\" x=\""+0+"\" y=\""+0+"\" transform=\"rotate(-90)\">"+ylabel+"</text>\r\n";
		body += "\t </g>\r\n";


		// --------------------------------------------
		// Title
		// --------------------------------------------
		body += "\t <text font-weight=\"bold\" text-anchor=\"middle\" font-family=\"Arial\" font-size=\""+f.getSize()+"\" fill=\""+makeSVGColor(titleColor)+"\" x=\""+(svg_width/2)+"\" y=\""+(2*margin/3)+"\">"+title+"</text>\r\n";

		return body;

	}

	// -----------------------------------------------------------------------------
	// Method to convert Java color to SVG colors
	// -----------------------------------------------------------------------------
	public String makeSVGColor(Color color){

		return "rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")";

	}


}
