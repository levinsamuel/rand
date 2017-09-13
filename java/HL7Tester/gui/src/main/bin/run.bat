@echo off


REM Debugging Options
REM set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xnoagent -Djava.compiler -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9876

REM Log config file
set JAVA_OPTS=%JAVA_OPTS% "-Dlog4j.configuration=file:///%CD%/${log4j.file.name}"

start "" javaw %JAVA_OPTS% -jar ${pom.artifactId}-${pom.version}.jar
