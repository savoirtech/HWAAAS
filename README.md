# HWAAAS
Hardware Acceleration As A Service

 Requirements:
 Java 1.7
 Maven 3.0.5


 Build Instructions:

 mvn install

 Deploy Instructions:

 feature:repo-add mvn:com.savoirtech.opencl.feature/opencl-feature/0.0.1-SNAPSHOT/xml/features
 
 install -s mvn:javax.xml/jaxb-impl/2.1
 
 feature:repo-add mvn:org.apache.camel.karaf/apache-camel/2.15.2/xml/features
 
 feature:install camel 
 
 feature:install opencl-feature


 Note: This project contains libraries from JavaCL Project. See https://code.google.com/p/javacl/ for more details.
