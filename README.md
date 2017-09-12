
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

### Screenshots

<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/binormalSmoothing.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/ks5.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/auc.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/fixedwidthband.png" width="200"/>
<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/bandsNonSmoothed.png" width="200"/>  <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/rainbow.png" width="200"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/errorbars3.png" width="200"/>   <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/smoothing4.png" width="200"/>   

<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="5"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/scoringSpace.png" height="185"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="22"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/oscillo.png" height="185"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="23"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/customized.png" height="185"/>

<img src="https://github.com/IGNF/roc4j/blob/master/doc/images/multiple.png" height="220"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/space.png" width="20"/> <img src="https://github.com/IGNF/roc4j/blob/master/doc/images/scoring1.png" height="230"/> 

### Installation

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
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
	<releases>
		<enabled>false</enabled>
	</releases>
</repository>
```

### Tutorial

A complete tutorial on how to use roc4j may be found online at the following address:

http://recherche.ign.fr/labos/cogit/demo/roc4j-doc/index.html

Or in PDF version: https://github.com/IGNF/roc4j/blob/master/doc/roc4j-doc.pdf


### Quick start

```java
import java.util.Random;
import javax.swing.JFrame;

import fr.ign.cogit.RocSpace;
import fr.ign.cogit.RocSpaceStyle;
import fr.ign.cogit.ConfidenceBands;
import fr.ign.cogit.ReceiverOperatingCharacteristics;

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

"\

"

### Real Application - Castle detection from building database

Authors: Marie-Dominique Van Damme and Yann Méneroux

```java

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import smile.classification.RandomForest;
import fr.ign.cogit.Bootstrap;
import fr.ign.cogit.ClassifierModel;
import fr.ign.cogit.ConfidenceBands;
import fr.ign.cogit.DataSet;
import fr.ign.cogit.ReceiverOperatingCharacteristics;
import fr.ign.cogit.RocSpace;
import fr.ign.cogit.Tools;
import fr.ign.cogit.ValidationProcess;



public class Main {


	@SuppressWarnings("resource")
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

