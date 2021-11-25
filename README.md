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

## Database setup

Assuming the you have h2 jar file already somewhere in your local Maven repostory you can create database file using command

### on Linux
```
java -cp .m2/repository/com/h2database/h2/1.4.200/h2-*.jar org.h2.tools.Shell \
    -url "jdbc:h2:~/pilot;INIT=runscript from '~/camunda-bpm-tomcat-7.12.0/sql/create/h2_engine_7.12.0.sql'" \
    -user sa -password '' -sql "SHOW TABLES"
```

