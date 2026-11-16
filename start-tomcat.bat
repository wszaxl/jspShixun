@echo off
rem Start Tomcat 9.0.115 for the JSP shixun project.
rem Keep this window open; press Ctrl+C or close it to stop the server.
rem The system JAVA_HOME is broken, so pin a working JDK here.
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
call "%~dp0apache-tomcat-9.0.115\bin\catalina.bat" run
