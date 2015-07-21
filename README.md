# HWAAAS
Hardware Acceleration As A Service

 Requirements:
 Java 1.7
 Maven 3.0.5


 Build Instructions:

 mvn install

 Deploy Instructions:

 feature:add-url file:/Path/to/opencl/feature/target/classes/feature.xml
 feature:install javacl-osgi
 feature:install opencl-commands
