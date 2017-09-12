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


// =================================================================================
// Class for handling 7 common kernel functions
// Kernel functions are used for kernel smoothing of ROC curve. When implemented we
// must define K(.) and its integral value from - infinity to x functions.
// Date : 08/03/2017
//=================================================================================


public class PredefiniteKernel extends Kernel {

	// Attribute
	private int type;
	private double xmin;
	private double xmax;

	// Common kernel types
	public static int UNIFORM = 1;
	public static int TRIANGULAR = 2;
	public static int EPANECHNIKOV = 3;
	public static int QUARTIC = 4;
	public static int TRIWEIGHT = 5;
	public static int GAUSSIAN = 6;
	public static int COSINE = 7;

	public static int AUTOMATIC = -1;



	public int getType(){return type;}


	public Kernel copy(){
		
		Kernel kernel = new PredefiniteKernel(type);
		kernel.setBandwidth(getBandwidth());
		kernel.setSupportLowerBound(xmin);
		kernel.setSupportUpperBound(xmax);
		
		return kernel;
		
	}

	public PredefiniteKernel(int type) {

		this.type = type;

		if (type == UNIFORM)      {this.xmin = -1; this.xmax = 1;}
		if (type == TRIANGULAR)   {this.xmin = -1; this.xmax = 1;}
		if (type == EPANECHNIKOV) {this.xmin = -1; this.xmax = 1;}
		if (type == QUARTIC)      {this.xmin = -1; this.xmax = 1;}
		if (type == TRIWEIGHT)    {this.xmin = -1; this.xmax = 1;}
		if (type == COSINE)       {this.xmin = -1; this.xmax = 1;}

		if (type == GAUSSIAN)  {this.xmin = Double.MIN_VALUE; this.xmax = Double.MAX_VALUE;}

	}

	public double getEfficiency(){

		if (type == UNIFORM){return 0.929;}
		if (type == TRIANGULAR){return 0.986;}
		if (type == EPANECHNIKOV){return 1.0;}
		if (type == QUARTIC){return 0.994;}
		if (type == TRIWEIGHT){return 0.987;}
		if (type == COSINE){return 0.999;}
		if (type == GAUSSIAN){return 0.951;}

		return 0;

	}


	@Override
	public double pdf(double x) {

		if (type == UNIFORM){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 0;
			}

			return 0.5;


		}


		if (type == TRIANGULAR){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 0;
			}

			return 1-Math.abs(x);

		}


		if (type == EPANECHNIKOV){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 0;
			}

			return 0.75*(1-x*x);

		}

		if (type == QUARTIC){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 0;
			}

			return 15/16.0*Math.pow(1-x*x, 2);

		}


		if (type == TRIWEIGHT){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 0;
			}

			return 35/32.0*Math.pow(1-x*x, 3);

		}


		if (type == GAUSSIAN){
			return Tools.normalPdf(x);
		}



		if (type == COSINE){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 0;
			}

			return Math.PI/4.0*Math.cos(Math.PI/2.0);


		}

		return 0;

	}

	@Override
	public double cdf(double x) {

		if (type == UNIFORM){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 1;
			}

			return 0.5*(x+1);

		}

		if (type == TRIANGULAR){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 1;
			}
			if (x > 0){
				return 0.5*(1+x);
			}
			if (x < 0){
				return 0.5+0.5*x;
			}

		}


		if (type == EPANECHNIKOV){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 1;
			}

			return 0.75*((x-x*x*x/3.0)+2/3.0);

		}

		if (type == QUARTIC){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 1;
			}

			return 15/16.0*((x+1/5.0*Math.pow(x,5)-2/3.0*Math.pow(x,3))-(-1+1/5.0*Math.pow(-1,5)-2/3.0*Math.pow(-1,3)));

		}


		if (type == TRIWEIGHT){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 1;
			}

			double output = 35/32.0*(x+3/5.0*Math.pow(x, 5)-2/3.0*Math.pow(x, 3)-1/7.0*Math.pow(x, 7));
			output = output -35/32.0*(-1+3/5.0*Math.pow(-1, 5)-2/3.0*Math.pow(-1, 3)-1/7.0*Math.pow(-1, 7));

		}


		if (type == GAUSSIAN){
			return Tools.phi(x);
		}


		if (type == COSINE){

			if (x < xmin){
				return 0;
			}
			if (x > xmax){
				return 1;
			}

			return 0.5*(Math.sin(Math.PI/2.0*x)+1);

		}

		return 0;

	}

}
