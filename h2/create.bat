cd ..
mvn exec:java -Dexec.mainClass=org.h2.tools.Shell -Dexec.args="-url 'jdbc:h2:./h2/pilot' -user sa" < h2/h2_engine_7.16.0.sql 
mvn exec:java -Dexec.mainClass=org.h2.tools.Shell -Dexec.args="-url 'jdbc:h2:./h2/pilot' -user sa" < h2/h2_identity_7.16.0.sql
