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
import java.util.ArrayList;

//=================================================================================
//Class for handling a collection of receiver operating curves
//Date : 19/09/2017
//=================================================================================

public class RocCurvesCollection{

	private boolean independent;
	private ArrayList<ReceiverOperatingCharacteristics> ROCS;

	// Getters
	public boolean areRocCurvesStatisticallyIndependent(){return independent;}

	// Setters
	public void setRocCurvesStatisticallyIndependent(boolean independent){this.independent = independent;}


	// ----------------------------------------------------------------------------
	// Constructor
	// ----------------------------------------------------------------------------s
	public RocCurvesCollection(boolean independent){

		this.ROCS = new ArrayList<ReceiverOperatingCharacteristics>();
		this.independent = independent;

	}

	// ----------------------------------------------------------------------------
	// Constructor
	// ----------------------------------------------------------------------------s
	public RocCurvesCollection(ArrayList<ReceiverOperatingCharacteristics> ROCS, boolean independent){

		this.ROCS = ROCS;
		this.independent = independent;

	}

	// ----------------------------------------------------------------------------
	// Method to add Receiver Operating Characteristics curve to collection
	// ----------------------------------------------------------------------------
	public void add(ReceiverOperatingCharacteristics roc){

		ROCS.add(roc);

	}

	// ----------------------------------------------------------------------------
	// Remove Receiver Operating Characteristics curve from collection
	// ----------------------------------------------------------------------------
	public void remove(int i){

		ROCS.remove(i);

	}

	// ----------------------------------------------------------------------------
	// Get Receiver Operating Characteristics curve from collection with index
	// ----------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics get(int i){

		return ROCS.get(i);

	}

	// ----------------------------------------------------------------------------
	// Get collection size
	// ----------------------------------------------------------------------------
	public int size(){

		return ROCS.size();

	}

	// ----------------------------------------------------------------------------
	// Compute Areas Under Curve of all roc curves
	// ----------------------------------------------------------------------------
	public ArrayList<Double> computeAreasUnderCurves(){

		ArrayList<Double> AUC = new ArrayList<Double>();

		for (ReceiverOperatingCharacteristics roc : ROCS) {

			AUC.add(roc.computeAUC());

		}       

		return AUC;

	}


	// ----------------------------------------------------------------------------
	// Method to smooth all ROC curves
	// ----------------------------------------------------------------------------
	public RocCurvesCollection makeSmoothVersion(){

		return makeSmoothVersion(ReceiverOperatingCharacteristics.SMOOTH_BINORMAL_REGRESSION);

	}

	// ----------------------------------------------------------------------------
	// Method to smooth all ROC curves
	// ----------------------------------------------------------------------------
	public RocCurvesCollection makeSmoothVersion(int method){

		return makeSmoothVersion(method, new PredefiniteKernel(PredefiniteKernel.GAUSSIAN));

	}


	// ----------------------------------------------------------------------------
	// Method to smooth all ROC curves
	// ----------------------------------------------------------------------------
	public RocCurvesCollection makeSmoothVersion(int method, Kernel k){

		return makeSmoothVersion(method, k, k, true);

	}



	// ----------------------------------------------------------------------------
	// Method to make smooth versions of all ROC curves
	// ----------------------------------------------------------------------------
	public RocCurvesCollection makeSmoothVersion(int method, Kernel kp, Kernel kn, boolean verbose){

		ArrayList<ReceiverOperatingCharacteristics> OUTPUT = new ArrayList<ReceiverOperatingCharacteristics>();

		for (ReceiverOperatingCharacteristics roc : ROCS) {

			OUTPUT.add(roc.makeSmoothVersion(method, kp, kn, verbose));

		}   

		return new RocCurvesCollection(OUTPUT, independent);

	}


	// ----------------------------------------------------------------------------
	// Method to smooth all ROC curves
	// ----------------------------------------------------------------------------
	public void smooth(){

		smooth(ReceiverOperatingCharacteristics.SMOOTH_BINORMAL_REGRESSION);

	}

	// ----------------------------------------------------------------------------
	// Method to smooth all ROC curves
	// ----------------------------------------------------------------------------
	public void smooth(int method){

		smooth(method, new PredefiniteKernel(PredefiniteKernel.GAUSSIAN));

	}


	// ----------------------------------------------------------------------------
	// Method to smooth all ROC curves
	// ----------------------------------------------------------------------------
	public void smooth(int method, Kernel k){

		smooth(method, k, k, true);

	}


	// ----------------------------------------------------------------------------
	// Method to smooth all ROC curves
	// ----------------------------------------------------------------------------
	public void smooth(int method, Kernel kp, Kernel kn, boolean verbose){

		for (ReceiverOperatingCharacteristics roc : ROCS) {

			roc.smooth(method, kp, kn, verbose);

		}   

	}


	// ----------------------------------------------------------------------------
	// Compute average roc curve
	// ----------------------------------------------------------------------------
	public ReceiverOperatingCharacteristics average(){

		return ReceiverOperatingCharacteristics.average(this);

	}

	// ----------------------------------------------------------------------------
	// Graphics parameters
	// ----------------------------------------------------------------------------
	public void setColor(Color... color){

		for (ReceiverOperatingCharacteristics roc : ROCS) {

			roc.setColor(color);

		}

	}

	public void setThickness(float thickness){

		for (ReceiverOperatingCharacteristics roc : ROCS) {

			roc.setThickness(thickness);

		}

	}

}