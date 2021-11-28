# pilot
Pilot for cluster nodes to accompany complex tasks using Camunda workflows.

## PanDA Pilot
This project is inspired by PanDA Pilot.

Pilot is a transient agent to execute a tailored workload (= a job: to be explained in a following section) on a worker node, reporting periodically various metrics to the PanDA server throughout its lifetime.

Links to PanDA Pilot:
* https://panda-wms.readthedocs.io/en/latest/
* https://github.com/PanDAWMS/pilot2
* https://github.com/PanDAWMS/pilot3
* https://cds.cern.ch/record/2285919/files/ATL-SOFT-SLIDE-2017-815.pdf
* https://inspirehep.net/files/6c1952977c13c5011f9baa5feffab309

## Main ideas

## Architecture overview
![alternative text](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/Biosoft-ru/pilot/main/docs/architecture.puml)

## Run from Docker image

```
docker run --rm -it developmentontheedge/ru.biosoft.uscience-pilot \
   mvn -q compile exec:java \
         -Dexec.mainClass=ru.biosoft.uscience.bpmn.PilotMain \
         -Dexec.args="-c -y d29ya2Zsb3c6IHByaW50VmFyaWFibGVzLmJwbW4KICA="
```

## Database setup

Assuming that you have H2 jar already somewhere in your local Maven repository you can create database file using command

### on Linux

Replace `h2/1.4.200/h2-1.4.200.jar` and `7.12.0/sql/create/h2_xxxxxx_7.12.0.sql` with versions that you have installed.

```
java -cp .m2/repository/com/h2database/h2/1.4.200/h2-1.4.200.jar org.h2.tools.Shell \
    -url "jdbc:h2:~/pilot;INIT=runscript from '~/camunda-bpm-tomcat-7.12.0/sql/create/h2_engine_7.12.0.sql'\;runscript from '~/camunda-bpm-tomcat-7.12.0/sql/create/h2_identity_7.12.0.sql'" \
    -user sa -password '' -sql "SHOW TABLES"
```

As result the database file named `pilot.mv.db` will be created in your home repostory.

Once you have created the database you can run the server using command
```
java -cp .m2/repository/com/h2database/h2/1.4.200/h2-1.4.200.jar org.h2.tools.Server \
    -tcp -tcpAllowOthers -ifExists
```

and test connection to the database using H2 shell in separate window
```
java -cp .m2/repository/com/h2database/h2/1.4.200/h2-*.jar org.h2.tools.Shell \
    -url "jdbc:h2:tcp://localhost/~/pilot" \
    -user sa -password '' 
```

## Configure Camunda to use H2 database

Open file `conf/server.xml` inside Camunda's Tomcat installation 
and add the following definition for connection pool

```
    <Resource name="jdbc/ProcessEngine"
              auth="Container"
              type="javax.sql.DataSource" 
              factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
              uniqueResourceName="process-engine"
              driverClassName="org.h2.Driver" 
              url="jdbc:h2:tcp://localhost/~/pilot;DB_CLOSE_ON_EXIT=FALSE"
              defaultTransactionIsolation="READ_COMMITTED"
              username="sa"  
              password=""
              maxActive="20"
              minIdle="5"
              maxIdle="20" />
```