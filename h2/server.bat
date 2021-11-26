cd ..
mvn exec:java -Dexec.mainClass=org.h2.tools.Server -Dexec.args="-tcp -tcpAllowOthers -ifExists"