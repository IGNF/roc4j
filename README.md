
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

