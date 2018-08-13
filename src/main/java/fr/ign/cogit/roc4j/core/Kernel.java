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

// =================================================================================
// Abstract class for handling kernel functions
// Kernel functions are used for kernel smoothing of ROC curve. When implemented we
// must define K(.) and its integral value from - infinity to x functions. One may 
// find some more common kernels in PredefiniteKernel class.
// Date : 08/03/2017
//=================================================================================


public abstract class Kernel {

	// Distribution support
	private double xmin = Double.MIN_VALUE;
	private double xmax = Double.MAX_VALUE;
	private double h = 0.01;

	public void setSupportLowerBound(double xmin){this.xmin = xmin;}
	public void setSupportUpperBound(double xmax){this.xmax = xmax;}
	public void setBandwidth(double h){this.h = h;}
	
	public double getSupportLowerBound(){return xmin;}
	public double getSupportUpperBound(){return xmax;}
	public double getBandwidth(){return h;}
	
	// Kernel efficiency
	public double getEfficiency(){
		
		System.err.println("Warning : efficiency has not been defined for this kernel");
		
		return 0;
				
	};
	
	// Kernel copy
	public abstract Kernel copy();
		

	// Kernel function
	public abstract double pdf(double x);


	// Kernel integral function
	public abstract double cdf(double x);
	

}

