cd ..
#mvn exec:java -Dexec.mainClass=org.h2.tools.Shell -Dexec.args="-url 'jdbc:h2:~/pilot;INIT=runscript from 'h2/h2_engine_7.16.0.sql'' -user sa"
mvn exec:java -Dexec.mainClass=org.h2.tools.Shell -Dexec.args='-url "jdbc:h2:./h2/pilot" -user sa' < h2/h2_engine_7.16.0.sql
