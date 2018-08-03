#! /bin/bash
mvn clean package -s ~/java/maven-3.5/conf/settings-sigma.xml
mvn dependency:resolve -Dclassifier=javadoc -s ~/java/maven-3.5/conf/settings-sigma.xml
mvn dependency:resolve -Dclassifier=source -s ~/java/maven-3.5/conf/settings-sigma.xml

