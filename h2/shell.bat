cd ..
mvn exec:java -Dexec.mainClass=org.h2.tools.Shell -Dexec.args="-url 'jdbc:h2:tcp://localhost/./h2/pilot' -user sa"
