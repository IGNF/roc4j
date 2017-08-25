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


import java.util.ArrayList;


// =================================================================================
// Class for operating validation on a set of data instances
// Date : 01/04/2017
// =================================================================================

public class ValidationProcess {

	public static int METHOD_TRAINING_VALIDATION_SPLIT = 1;
	public static int METHOD_CROSS_VALIDATION = 2;
	public static int METHOD_LEAVE_ONE_OUT = 3;


	private int method = 0;
	private double splitRatio = 0.5;
	private int foldNumber = 10;
	private boolean verbose = true;
	private DataSet dataset;
	private ClassifierModel classifier;

	// Setters
	public void setSplitRatio(double ratio){

		this.splitRatio = ratio;

	}

	// Setters

	public void setVerbose(boolean verbose){this.verbose = verbose;}

	public void setNumberOfFolds(int folds){

		if (folds < 2){

			System.err.println("Error : number of folds should be at least equal to 2");
			System.exit(1);

		}

		if (folds > dataset.getSize()/2){

			System.err.println("Error : number of folds should be at most equal to dataset size / 2 = "+dataset.getSize()/2);
			System.exit(0);

		}

		this.foldNumber = folds;

	}

	// Getters
	public double getSplitRatio(){return splitRatio;}
	public int getNumberOfFolds(){return foldNumber;}


	// Constructor
	public ValidationProcess(DataSet dataset, ClassifierModel classifier, int method){

		this.dataset = dataset;
		this.classifier = classifier;
		this.method = method;

	}

	// --------------------------------------
	// Validation process for 1 ROC curve
	// --------------------------------------
	public ReceiverOperatingCharacteristics run(){
		
		return run(dataset);
		
	}
	
	private ReceiverOperatingCharacteristics run(DataSet dataset){

		if (this.method == METHOD_TRAINING_VALIDATION_SPLIT){

			return runTrainingValidationSplit(dataset);

		}

		if (this.method == METHOD_CROSS_VALIDATION){

			return runCrossValidation(dataset);

		}

		if (this.method == METHOD_LEAVE_ONE_OUT){

			return runLeaveOneOut(dataset);

		}

		return null;

	}

	// --------------------------------------
	// Validation process for n ROC curves
	// --------------------------------------
	public ArrayList<ReceiverOperatingCharacteristics> run(int n){
		
		return run(n, false);
		
	}
	
	public ArrayList<ReceiverOperatingCharacteristics> run(int n, boolean independentSampling){

		boolean verbose_save = verbose;

		verbose = false;

		ArrayList<ReceiverOperatingCharacteristics> ROCS = new ArrayList<ReceiverOperatingCharacteristics>();


		if (independentSampling){
			
			ArrayList<DataSet> DATASETS = dataset.divide(n);

			for (int i=0; i<n; i++){

				if (verbose_save){

					System.out.println("Computing ROC curve number "+(i+1));

				}

				ROCS.add(run(DATASETS.get(i)));

			}

		}
		else{

			for (int i=0; i<n; i++){

				if (verbose_save){

					System.out.println("Computing ROC curve number "+(i+1));

				}

				ROCS.add(run());

			}

		}

		verbose = verbose_save;

		return ROCS;

	}


	// --------------------------------------
	// Validation processes
	// --------------------------------------

	// Simple split
	public ReceiverOperatingCharacteristics runTrainingValidationSplit(DataSet dataset){

		ArrayList<DataSet> DATASETS = dataset.randomSplit(splitRatio);

		DataSet trainingDataset = DATASETS.get(0);
		DataSet validationDataset = DATASETS.get(1);
		
		

		// --------------------------------------
		// Training
		// --------------------------------------

		classifier.train(trainingDataset);

		// --------------------------------------
		// Validation
		// --------------------------------------

		int[] expected = validationDataset.getTargets();
		double[] posteriors = classifier.posteriors(validationDataset.getFeatures());
		
		return new ReceiverOperatingCharacteristics(expected, posteriors);

	}


	// Leave-one-out cross validation
	public ReceiverOperatingCharacteristics runLeaveOneOut(DataSet dataset){

		if (verbose){

			System.out.println("Validation with leave-one-out-process");

		}

		int[] expected = dataset.getTargets();
		double[] posteriors = new double[dataset.getSize()];

		for (int i=0; i<dataset.getSize(); i++){

			DataSet trainingDataset = dataset.copy();
			trainingDataset.removeData(i);

			// --------------------------------------
			// Training
			// --------------------------------------

			classifier.train(trainingDataset);

			// --------------------------------------
			// Validation
			// --------------------------------------

			posteriors[i] = classifier.posterior(dataset.getFeatures(i));


			if (verbose){

				System.out.println("Data instance number "+i+" validation ("+(int)(10000*i/dataset.getSize())/100.0+" %)");

			}

		}

		return new ReceiverOperatingCharacteristics(expected, posteriors);

	}

	// Cross validation
	public ReceiverOperatingCharacteristics runCrossValidation(DataSet dataset){

		if (verbose){

			System.out.println("Validation with "+foldNumber+"-fold cross validation method");

		}

		DataSet permutation = dataset.shuffle();

		int n = (int)(permutation.getSize()/foldNumber);
		int N = n*foldNumber;

		int[] expected = new int[N];
		double[] posteriors = new double[N];

		for (int i=0; i<N; i++){

			expected[i] = permutation.getTarget(i);

		}

		for (int k=0; k<foldNumber; k++){

			int n1 = k*n;
			int n2 = (k+1)*n;

			DataSet trainingDataset = new DataSet();
			DataSet validationDataset = new DataSet();

			for (int i=0; i<permutation.getSize(); i++){

				if ((i>=n1) && (i<n2)){

					validationDataset.addData(permutation.getFeatures(i), permutation.getTarget(i));

				}
				else{

					trainingDataset.addData(permutation.getFeatures(i), permutation.getTarget(i));

				}

			}

			// --------------------------------------
			// Training
			// --------------------------------------

			classifier.train(trainingDataset);

			// --------------------------------------
			// Validation
			// --------------------------------------

			for (int i=0; i<validationDataset.getSize(); i++){

				posteriors[n1+i] = classifier.posterior(validationDataset.getFeatures(i));

			}

			if (verbose){

				System.out.println("Fold number "+k+" validation ("+(int)(10000*k/foldNumber)/100.0+" %)");

			}

		}

		return new ReceiverOperatingCharacteristics(expected, posteriors);

	}

}
