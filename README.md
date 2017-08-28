
Handling ROC curves with roc4j
===================

The package roc4j is designed for estimating and handling Receiver Operating Characteristics (ROC) curves of binary classifiers in JAVA. Among its main features, it contains:

- ROC curve computation
- ROC curve filtering and smoothing
- Confidence bands computation
- Graphical plots (exportable in png, svg, jpg...)
- Validation process handler
- Optimal operating points computation

### Tutorial

A complete tutorial on how to use roc4j may be found at the following address :

### Installation

#### Option 1: 

Download roc4j jar file at:

https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/fr/ign/cogit/roc4j/1.0-SNAPSHOT/


#### Option 2: 

Insert the following lines in your Maven pom.xml:

In <dependencies>

```xml
<dependency>
	<groupId>fr.ign.cogit</groupId>
	<artifactId>roc4j</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

<repositories>

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


