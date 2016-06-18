# LabSD
Repositorio del Laboratorio de Sistemas Distribuidos



### COMPILE CHORD EXAMPLE
#### Chord
javac -classpath peersim-1.0.5.jar:djep-1.0.0.jar:jep-2.3.0.jar:classes/ -d classes/ src/peersim/chord/*.java
#### Pastry
javac -classpath peersim-1.0.5.jar:djep-1.0.0.jar:jep-2.3.0.jar:classes/ -d classes/ src/peersim/pastry/*.java

### EXECUTE CHORD EXAMPLE
#### Chord
java -cp peersim-1.0.5.jar:djep-1.0.0.jar:jep-2.3.0.jar:classes/ peersim.Simulator example.cfg
#### Pastry
java -cp peersim-1.0.5.jar:djep-1.0.0.jar:jep-2.3.0.jar:classes/ peersim.Simulator example.cfg


Estos comandos deben ser ejecutados en la carpeta "chord" o "pastry".

#### Integrantes
* Bryan Guzmán
* Robinson Oyarzún

#### Profesor
* Nicolás Hidalgo

#### Ayudante
* Luis Celedón
