Optimization Algorithm Toolkit (OAT)
http://optalgtoolkit.sourceforge.net/

To Install:
--------------------------------
   1. Ensure you have the latest version of Java installed: http://www.java.com
   2. Download Optimization Algorithm Toolkit (OAT): http://downloads.sourceforge.net/optalgtoolkit
   3. Unzip the downloaded .zip file into a directory, example /oat
   4. To Start:
         1. Windows: Double click the jar file: optalgtoolkit.jar
         2. Command Line: In the installation directory type: java -jar optalgtoolkit.jar
   5. For proxy support for usage with the Huygens Probe package, at the command line type: 
   		java -Dhttp.proxySet=true -Dhttp.proxyHost=HOST -Dhttp.proxyPort=PORT -jar optalgtoolkit.jar
        (be sure to replace HOST and PORT with your specific proxy host and port information)

Software Versions used for Build:
--------------------------------
    * Java: 1.6, update 2 (java.sun.com)
    * Eclipse: 3.3.0 Europa (www.eclipse.org)
    * JFreeChart: 1.0.2 (www.jfree.org)
    * Open Source Physics: 1.0.4 (www.opensourcephysics.org)
    * JUnit: 4.2 (www.junit.org)
    * Huygens WS Client v2.1; 21 June 2006 (http://gungurru.csse.uwa.edu.au/cgi-bin/WebObjects/huygensWS)
    * Jakarta Commons (Math1.1) (commons.apache.org)
    * Colt 1.2.0 (http://dsd.lbl.gov/~hoschek/colt/)
    * SSJ: Stochastic Simulation in Java 1.2.2 (http://www.iro.umontreal.ca/~simardr/ssj/)
    * SOCR: Statistics Online 1.2 Computational Resource (http://www.socr.ucla.edu)

Acknowledgements:
--------------------------------
* Thanks to Daniel Angus for implementing some algorithm and problem instances in early 2006
* Thanks to lime_anneberit for her creative commons image (http://flickr.com/photos/lime_anneberit/38240592/) used in the splash start-up

General Change History:
--------------------------------
1.4
* Added support in the API and the GUI for automatic algorithm configuration
* Fixed inconsistent properties files
* Added a massive experimentation API (!!!)
* Added simple experiment interface for designing, running, and analyzing experiments
* Normality tests Anderson-Darling, Cramer-von-Mises Criterion, Kolmogorov-Smirnov Test
* Sample Comparison test One-Way Analysis of Variance (ANOVA), Kruskal-Wallis Test, Student's t-test, Mann-Whitney U Test
* Maintain experiments and results off-line on disc 
* Major reorganization of packages into domains, explorer, and experimenter
* Large scale package and class renaming to suit new convention
* Added a generic epoch-based (population of solutions) algorithm
* Reorganized bit-string algorithms in continuous optimization to binary optimization
* Added support for binary optimization algorithms in continuous optimization domain
* Restructured the webpage and added support section
* Created a launcher as the main entry point for the application with a domain selector and experiment launcher
* Added a check list for tasks to complete before a release (mainly testing tasks)
* Rewrote the guts of algorithm and problem to use RunProbes and StopConditions API
* Updated explorer GUI to reflect API changes for stop conditions and run probes
* Changed CFO plots to use a queue of points
* Updated the software license from GPL to LGPL
* All ACO algorithms support automatic configuration for TSP

1.3
* Added generic crowding replacement strategy, and modified RTS and simple crowding to make use of it
* Added a generic elitism, used for GA's
* Removed elitism from Fitness Sharing - causes a bug, does not belong
* Made fitness sharing generic and modified FSGA to use it
* Cleaned up aspects of Differential Evolution implementation
* Cleaned up PSO implementation
* Added Evolutionary Programming (EP) - fr #1608132 
* Added support for fast evolutionary programming (FEP) - fr # 1608135 
* Minor bug fixes to utility classes
* Added a solution quality line graph
* Added a mutation and parallel mutation hill climber to binary problem
* Added generic measure utilities
* Modified character recognition to use average error as the system (solution) error
* Improved general problem configuration exceptions
* Added support for problem instance configuration in the GUI
* Added more effective problem info and problem configuration validation for all configurable problems
* Removed problem details panel from the GUI, uses a an about dialog instead.
* FuncOpt GUI changed to use a standardized plot
* Made the binary character recognition problem more robust, deleted clonalg, added parallel random hill climber

1.2
* Added Restricted the Tournament Selection algorithm - fr #1608162 
* Separated simple crowding and deterministic crowding into separate algorithms, fixed various bugs in implementations
* Added probabilistic crowding - fr #1608163
* Implemented a one dimensional function plot for function optimization - fr #1608366 
* Changed Mahfoud functions to be 1D as specified in the paper, updated GUI elements to draw correctly.
* Deleted uniform search as it is not scalable.
* Added binary domain with simple trap functions and random search algorithm - fr #1605717
* Added more binary benchmark problems and unit tests
* Updated the splash image, added "OAT" to it
* Modified Timmis F1 and F2 to be 1D as specified in the source paper
* Added opt-IA for binary problem domains
* Added CLONALG and variants (CLONALG1, CLONALG2) to binary problem domain 
* Added opt-IA for numerical function optimization
* Added a centralized version interface
* Added opt-IMMALG algorithm - fr #1608143 
* Generalized immune system probability mutation functions with error checking
* Consolidated utility methods into utility classes, and utility classes into com.oat.utils.*
* Created new EvolutionUtils and made all generic GA functions truly generic, simpler, more error checking (flushed out lots of little bugs)
* Fixed bug in ACS
* Fixed bug in diffuse GA that allowed pop-overlap & selection with units that had no evaluation yet
* Made fitness sharing GA more defensive
* Added better unexpected exception handling in the GUI during an algorithm run
* Added Simple Immune Algorithm (SIA)
* Created a bean utils class for working with java beans
* Added CLIGA Algorithm
* fixed bug where some functions report NaN for coord values - bug #1607284
* Removed GUI code from all algorithm's and made it generic using reflection (sweet!) 
* Added the graph coloring problem domain and 56 standard benchmark problem instances - fr #1612848 
* Added Immune Algorithm (IA) for graph coloring problem
* Support for relative and absolute coordinate systems in protein folding problem, and simplified domain definition  
* Added CLONALG TSP implementation
* Renamed ants algorithms to more-correct aco (ant colony optimization)
* Improved the robustness and efficiency, of the ACO approaches (fixed bugs, lots more error checking, more abstracted, etc...)
* Removed many compiler warnings and improve use of generics throughout the framework
* Added preliminary binary character recognition with random search and plots
* Implemented CLONALG for binary character recognition
* Provided algorithm reference information on algorithm list page, updated for all algorithms
* Draw solutions in one-dimensional plot for native 1D functions - awesome for niching approaches
* Added about java to about dialog with features to run gc, finalization, exit and system properties
* Basic problem details for binary trap functions
* Made algorithm about a modal dialog box, rather than a popup
* Increased max function evaluations for binary trap functions to 10K
* Fixed a bug in MMAS related to pheromone update and tmin tmax updates
* Improved problem.cost() path, and made solution.evaluated more strict across the framework
* Simplified parallel two-opt and greedy search for TSP domain
* Instigated change management in the header of each java file
* Numerous minor algorithm bug fixes
* Removed solution stripping functionality from each algorithm loop

1.1
* Cleaned up accessor and mutator for algorithm configuration across all algorithms - bug #1604163
* Added JUnit tests for algorithm run consistency - fr #1604157
* Moved the Pop-ACO for func opt into the swarm package
* Changed default behaviour of a miss-configured algorithm to throw an exception, rather than auto-correct
* Repaired bug in ACS where automatic parameters are remembered between runs
* Separated algorithm GUI configuration from algorithm parameter validation (fixed algorithm config API) - bug #1603549
* Deleted unpublished niching ACO algorithms
* Minor algorithm configuration validation improvements and bug fixes
* Implemented a generic getConfigurationDetails() in Algorithm - fr #1604216
* Added unit test to run all algorithms on all problems for each domain (using properties files for lists)
* Fixed bug #1604863 ACS on TSP a208
* Fixed bug #1604945 FitnessSharing on Easom's Function (bug in normalized relative fitness)
* Simplified algorithm dialog box, use pack method on JFrame, removed numParameters from algorithm, added ok button - bug #1604872
* Changed RandomSearch behaviour to evaluate in batches of 1000 solutions at a time (when possible) for all domains
* Huygens Probe breaks up very large queries into batches of 1000 to send to the server - bug #1604211
* Updated the required jar's for the Applet .html files to only use those jars that are needed - bug #1604860
* Screenshots not displayed in IE cannot be reproduced - bug #1604108
* Added code examples for each problem domain, located in com.oat.examples
* Added a JTree chooser for algorithms and problems in the gui - fr 1603547
* Fixed drawing bug with the AdjacencyMatrix - bug #1605150 
* Adjusted parameters of the 3D plot for function optimization to improve speed and usability
* Added more benchmark function optimization problems, organized based on source - fr #1603546 
* Added a progress indicator for a run as per fr #1603543 
* Added support for variable dimensionality in function optimization in the API, code example, JUnit test - fr #1603548
* Created a FAQ. Moved much of the content from the project webpage to a FAQ, also added code examples to the FAQ
* Moved all algorithm specific information into the code and provided "about" algorithm in the API and GUI - fr #1603553  
* Provided functionality to stop a run from another thread, also provided in the GUI - fr #1603561 
* Support for convergence detection and stop run after convergence in API, GUI and example code - fr #1604214 
* Minor bug fix - do not count problem evaluations above the max, added check in unit tests
* Draw the confirmation red if it is incomplete for protein folding - fr #1606513
* Provided support for clearing points and optionally drawing optima on the interpolated plot - fr #1606512
* Added license agreement to each source file as suggested in the GPL
* Added a generic GUI properties, and remember Huygens user email - fr #1603544
* Added a Help->About menu to the JFrame entry point
* Added a splash window for the off-line application
* Added context sensitive help (tool tips) to all suitable GUI elements
* Added GPL text file to the project
* Added ant script for compiling and preparing releases - fr #1607286 
* Added an image to the splash screen of oats

1.0
* Initial Public Release
