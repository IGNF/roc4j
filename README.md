
Handling ROC curves with roc4j
===================

The package roc4j is designed for estimating and handling Receiver Operating Characteristics (ROC) curves of binary classifiers in Java. Robust and accurate estimation of ROC curve is of utmost importance in statistical learning.

Among roc4j main features:

- ROC curve computation
- ROC curve filtering and smoothing
- Confidence bands computation
- Graphical plots (exportable in png, svg, jpg...)
- Validation process handler
- Optimal operating points computation

<br>

### Screenshots

<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/binormalSmoothing.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/ks5.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/auc.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/fixedwidthband.png" width="200"/>
<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/bandsNonSmoothed.png" width="200"/>  <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/rainbow.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/errorbars3.png" width="200"/>   <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/smoothing4.png" width="200"/>   

<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="5"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/scoringSpace.png" height="185"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="22"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/oscillo.png" height="185"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="23"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/customized.png" height="185"/>

<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/multiple.png" height="220"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="20"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/scoring1.png" height="230"/> 


<br>

### Installation
------------------------

#### Option 1: 

Download roc4j jar file at:

https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/fr/ign/cogit/roc4j/1.0-SNAPSHOT/


#### Option 2: 

Insert the following lines in your Maven pom.xml:

```xml
<dependency>
	<groupId>fr.ign.cogit</groupId>
	<artifactId>roc4j</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

```xml
<repository>
	<id>cogit-snapshots</id>
	<name>Cogit Snapshots Repository</name>
	<url>https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/</url>
</repository>
```


<br>

### Tutorial
---------

A complete tutorial on how to use roc4j may be found online at the following address:

http://recherche.ign.fr/labos/cogit/demo/roc4j-doc/index.html

Or in PDF version: https://github.com/IGNF/roc4j/blob/master/doc/roc4j-doc.pdf


<br>

### Quick start
----------

```java
import java.util.Random;
import javax.swing.JFrame;

import fr.ign.cogit.roc4j.RocSpace;
import fr.ign.cogit.roc4j.RocSpaceStyle;
import fr.ign.cogit.roc4j.ConfidenceBands;
import fr.ign.cogit.roc4j.ReceiverOperatingCharacteristics;

//-----------------------------------------------------------------
// Program to compute a simple ROC curve on n simulated instances
// Confidence bands are computed at 95% level (default parameter)
// with Komogorov-Smirnov test statistic. ROC curve and its 
// associated confidence bands are then depicted in a ROC space 
//-----------------------------------------------------------------

public class Main {

	public static void main(String[] args) {
		
		// --------------------------------------------------------
		// Parameters
		// --------------------------------------------------------
		int n = 500;              // Number of validation instances
		double noise = 0.1;       // Standard deviation of noise
		// --------------------------------------------------------
		
		// Setting random seed
		Random generator = new Random(123456789);

		int[] expected = new int[n];
		double[] score= new double[n];

		// Instances generation
		for (int i=0; i<n; i++){

		    double rand1 = generator.nextDouble();
		    double rand2 = generator.nextGaussian();

		    expected[i] = (int)(rand1+0.5);
		    score[i] = noise*rand2 + 0.2*expected[i] + 0.4;

		    score[i] = Math.max(score[i], 0);
		    score[i] = Math.min(score[i], 1);

		}

		// Roc curve computation
		ReceiverOperatingCharacteristics roc = new ReceiverOperatingCharacteristics(expected, score);
		
		// Confidence bands computation with Kolmogorov-Smirnov method
		ConfidenceBands bands = new ConfidenceBands(roc, ConfidenceBands.METHOD_KOLMOGOROV_SMIRNOV);
		
		// Creating ROC space
		RocSpace space = new RocSpace();
		space.setStyle(RocSpaceStyle.STYLE_OSCILLO);

		space.addRocCurve(roc);		
		space.addConfidenceBands(bands);

		// Graphical display
		JFrame fen = new JFrame();
		fen.setSize(700, 700);
		fen.setContentPane(space);
		fen.setLocationRelativeTo(null);
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fen.setVisible(true);
		
	}

}
```

<br>

### Real Application - Castle detection from building database with Random Forest
--------

<br>

**Authors:** Marie-Dominique Van Damme and Yann Méneroux

<br>

The data used for this experimentation may be found on the same github repository:

https://github.com/IGNF/roc4j/blob/master/sample/chateau.dat

<br>

#### Goal

The objective of this experimentation is to discriminate castle and non-castle in a building database.
The dataset is an extract of the data we used, containing 600 buildings, 50% of them being castles. 

The first line of the file contains header with parameter names. First columns contains boolean value (1 if the building described on the row is a castle, 
0 otherwise). 

Each row contains 13 parameters, which have been computed from BDTOPO&copy; (IGN building database):

<div>

<span style="float:right;width:100px;"><img src="https://github.com/IGNF/roc4j/blob/master/doc/images/BatiParameters.png" width="100"/></span>

<span style="float:left;">

| Option | Description |
| ------ | ----------- |
| 1 - **hauteur** | height of the building |
| 2 - **nb_orientation_mur** | number of wall orientations  | 
| 3 - **orientation_generale** | general orientation of building |
| 4 - **orientation_principale_mur** | main orientation of building walls |
| 5 - **elongation** | length/width ratio of minimum bounding rectangle |
| 6 - **concavite** | area of footprints out of convex hull |
| 7 - **perimetre** | perimeter of footprint |
| 8 - **nb_convexe** | number of parts in convex decomposition |
| 9 - **compacite** | compacity index |
| 10 - **granularite** | shortest wall |
| 11 - **nb_concave** | number of concave parts |
| 12 - **nb_pt_squelette** | number of points in geometric skeletton |
| 13 - **long_squelette** | geometric skeletton length |

</span>

</div>

<br>

Classification has been done with a Random Forest model (100 trees), using SMILE library.

The code provided below:  
- reads the castle datafile (needs to be downloaded in local first)  
- stores features and labels data in a DataSet object  
- creates a ClassifierModel, wrapping SMILE RandomForest object  
- designs a protocol for validation (15-fold cross validation)  
- validates the classifier on the dataset and computes ROC curve  
- performs boostrap to generate 20 replications of ROC curve  
- computes smoothed version of the ROC curves with kernel estimation  
- computes the average of all generated roc curves  
- estimates ROC curve confidence bands at 95% with Fixed-Width Band method  
- computes area under ROC curve (AUC) as a general performance index  
- estimates the 95% confidence interval of AUC  
- Displays ROC curves, confidence bands and numerical results in a plot  

Note that the computation of confidence bands with Fixed-Width Band method require a few seconds (dependind upon the number of ROC curves generated by 
bootstrap sampling).

<br>

```java

import javax.swing.JFrame;

import smile.classification.RandomForest;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import fr.ign.cogit.roc4j.Bootstrap;
import fr.ign.cogit.roc4j.ClassifierModel;
import fr.ign.cogit.roc4j.ConfidenceBands;
import fr.ign.cogit.roc4j.DataSet;
import fr.ign.cogit.roc4j.ReceiverOperatingCharacteristics;
import fr.ign.cogit.roc4j.RocSpace;
import fr.ign.cogit.roc4j.Tools;
import fr.ign.cogit.roc4j.ValidationProcess;

public class Main {

	public static void main(String[] args) {

		// Data labels and features
		DataSet dataset = new DataSet();

		String datafile_path = "C:/Users/ymeneroux/Desktop/chateau.dat";

		int Nf = 13;   // Number of features (max 13)

		// ----------------------------------------------------------------------
		// Reading building dataset
		// ----------------------------------------------------------------------

		try {

			Scanner scan = new Scanner(new File(datafile_path));

			// Headline
			scan.nextLine();

			// Number of features

			while(scan.hasNextLine()){

				// Splitting line
				StringTokenizer splitter = new StringTokenizer(scan.nextLine(), ",");

				// Data labels
				int y = Integer.parseInt(splitter.nextToken());

				// Data features

				double x[] = new double[Nf];

				for (int i=0; i<Nf; i++){

					x[i] = Double.parseDouble(splitter.nextToken());

				}

				dataset.addData(x, y);

			}

		} catch (FileNotFoundException e) {

			System.out.println("No datafile found at the specified address");
			System.exit(1);

		}



		// ----------------------------------------------------------------------
		// Training a classifier model
		// ----------------------------------------------------------------------

		ClassifierModel classifier = new ClassifierModel() {

			@Override
			public void train(DataSet trainingData) {

				int n = trainingData.getSize();

				double[][] X = new double[n][4];
				int[] Y = new int[n];

				for (int i=0; i<X.length; i++){

					X[i] = (double[]) trainingData.getFeatures(i);
					Y[i] = trainingData.getTarget(i);

				}

				this.model = new RandomForest(X, Y, 100);


			}

			@Override
			public double posterior(Object dataFeatures) {

				RandomForest rf = (RandomForest) this.model;

				double[] posterior = new double[2];

				rf.predict((double[]) dataFeatures, posterior);

				return posterior[1];

			}
			
		};


		// ----------------------------------------------------------------------
		// Validation of the classifier model
		// ----------------------------------------------------------------------

		// 15-fold cross validation
		ValidationProcess validation = new ValidationProcess(dataset, classifier, ValidationProcess.METHOD_CROSS_VALIDATION);
		validation.setNumberOfFolds(15);
		
		ReceiverOperatingCharacteristics roc = validation.run();

		// Replication of 20 bootstrap samples
		ArrayList<ReceiverOperatingCharacteristics> ROCS = Bootstrap.sample(roc, 20);

		// Kernel smoothing of curves
		for (int i=0; i<ROCS.size(); i++){

			ROCS.get(i).smooth(ReceiverOperatingCharacteristics.SMOOTH_KERNEL);
			ROCS.get(i).setThickness(0.1f);

		}

		// Confidence bands computation at 95% with FWB method
		ConfidenceBands bands = new ConfidenceBands(ROCS, ConfidenceBands.METHOD_FIXED_WIDTH_BAND, 95.0);
		bands.getCentralROC().setThickness(2.f);
		bands.setBordersTransparency(0.5f);
		

		// ----------------------------------------------------------------------
		// Area Under Curve computation
		// ----------------------------------------------------------------------
		
		// Areas under bootstrapped curves computation
		ArrayList<Double> AUC = Bootstrap.computeAreaUnderCurve(ROCS);
		
		// Average of area under curve
		double auc = Tools.round(100.0*Tools.computeMean(AUC), 1);
		
		// Confidence interval on area under curve
		double conf_inf = Tools.round(100.0*Tools.computeQuantile(AUC, 0.025), 1);
		double conf_sup = Tools.round(100.0*Tools.computeQuantile(AUC, 0.975), 1);
		

		// ----------------------------------------------------------------------
		// Data representation
		// ----------------------------------------------------------------------

		RocSpace space = new RocSpace();

		// Adding data to graphics
		space.addRocCurve(bands.getCentralROC());
		space.addRocCurve(ROCS);
		space.addConfidenceBands(bands);
		
		// Writing on graphics
		space.setTitle("ROC curve of castle detection from building database");
		space.writeText("AUC = "+auc+" %", 400, 500, 14, Color.BLACK);
		space.writeText("IC @ 95% = ["+conf_inf+", "+conf_sup+"]", 400, 520, 14, Color.BLACK);

		// Display
		JFrame fen = new JFrame();
		fen.setSize(700, 700);
		fen.setContentPane(space);
		fen.setLocationRelativeTo(null);
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fen.setVisible(true);

	}

}


```

It is possible to try the code above with different number of features, confidence level, bootstrap replication number, SMILE classifier model, 
smoothing method, validation process... and observe the change in plot and results.

For the default code provided here, we got an AUC equal to 86.2 %. Confidence interval indicated that the true unknown AUC is somewhere in [84.6%, 88.2%].
Increasing the number of buildings may enable to decrease the uncertainty.

<p align="center"> 
<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/castle.png" width="600"/>
</p>

<p align="center"> 
<i>Receiver Operating Characteristics curves (after bootstrap sampling) and 95% confidence bands for Random Forest classifier on castle detection problem</i>
</p>

Note that adding some extrinsic parameters (such as distance to the nearest road) enables to reach up to 94% classification performance (AUC). However, in order to get 
aesthetic ROC curves, we did not provide all the features in the dataset.


