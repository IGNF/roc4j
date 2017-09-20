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

package fr.ign.cogit.roc4j.validation;

import java.util.ArrayList;
import java.util.Collections;


// =================================================================================
// Class for listing data instances to compute cross validation
// Date : 01/04/2017
// =================================================================================

public class DataSet {

	private ArrayList<Object> FEATURES;
	private ArrayList<Integer> TARGETS;


	// Constructor
	public DataSet(){

		FEATURES = new ArrayList<Object>();
		TARGETS = new ArrayList<Integer>();

	}

	// New data
	public void addData(Object features, int target){

		FEATURES.add(features);
		TARGETS.add(target);

	}


	public Object getFeatures(int index){

		return FEATURES.get(index);

	}

	public ArrayList<Object> getFeatures(){

		return FEATURES;

	}

	public int getTarget(int index){

		return TARGETS.get(index);

	}


	public int[] getTargets(){

		int[] targets = new int[getSize()];

		for (int i=0; i<targets.length; i++){

			targets[i] = TARGETS.get(i);

		}

		return targets;

	}


	public int getSize(){

		return TARGETS.size();

	}

	public void removeData(int index){

		if (index >= getSize()){

			System.err.println("Error : can't remove data number "+index);
			System.exit(0);

		}

		FEATURES.remove(index);
		TARGETS.remove(index);

	}

	public DataSet copy(){

		DataSet dataset = new DataSet();

		for (int i=0; i<getSize(); i++){

			dataset.addData(getFeatures(i), getTarget(i));

		}

		return dataset;

	}


	public ArrayList<DataSet> randomSplit(double ratio){

		DataSet dataset1 = new DataSet();
		DataSet dataset2 = copy();

		int Nd1 = (int)(ratio*getSize());

		while(dataset1.getSize() < Nd1){

			int index = (int)(Math.random()*dataset2.getSize());

			dataset1.addData(dataset2.getFeatures(index), dataset2.getTarget(index));

			dataset2.removeData(index);

		}

		ArrayList<DataSet> DATASETS = new ArrayList<DataSet>();

		DATASETS.add(dataset1);
		DATASETS.add(dataset2);

		return DATASETS;

	}
	
	public ArrayList<DataSet> divide(int n){
		
		if (n > getSize()/10){
			
			System.err.println("Error : number of repetions should be lower than dataset size / 10 = "+((int) getSize()/10));
			System.exit(1);
			
		}
		
		ArrayList<DataSet> DATASETS = new ArrayList<DataSet>();
		
		int N = (int)(getSize()/10);
		
		for (int i=0; i<n; i++){
			
			DataSet dataset = new DataSet();
			
			for (int j=0; j<N; j++){
				
				dataset.addData(getFeatures(i*N+j), getTarget(i*N+j));
				
			}
			
			DATASETS.add(dataset);
			
		}
		
		return DATASETS;		
		
	}

	public DataSet shuffle(){

		DataSet permutation = new DataSet();


		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (int i=0; i<this.getSize(); i++){

			indices.add(i);

		}

		Collections.shuffle(indices);

		for (int i=0; i<indices.size(); i++){
			
			int indexShift = indices.get(i);

			permutation.addData(this.getFeatures(indexShift), this.getTarget(indexShift));

		}
		
		return permutation;

	}


}
