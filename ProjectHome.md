## Description ##
JUnit Flux is an Eclipse plugin that will execute related JUnit tests automatically when you save Java or Groovy class. Related tests are found by searching inside current package and checking that unit test name follows naming convention of prefixing or suffixing with "Test" (`".*"+name+".*Test.*" or ".*Test.*"+name+".*"`).

These conventions allow JUnit Flux to find relevant unit tests very quickly, thus providing you with immediate feedback on your code change. JUnit Flux will react on multiple file changes as well, like those resulting from automatic refactorings. Enjoy.


## Update site ##
http://junitflux.googlecode.com/svn/trunk/com.google.code.junitFlux.site

## Manual Installation ##
1. Download plugin and place it into plugins folder of your Eclipse (3.5 - 4.2) installation.

2. Add JUnit Flux nature by right clicking on the Java project in Eclipse and selecting "Add JUnit Flux nature".


## Links ##
Short description of the plugin in blog of Diego Lemos: http://diegolemos.net/2011/09/12/junit-flux-eclipse-plugin/