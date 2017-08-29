
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


### Tutorial

A complete tutorial on how to use roc4j may be found at the following address :

http://recherche.ign.fr/labos/cogit/demo/roc4j-doc/index.html

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


