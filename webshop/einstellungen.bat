@echo off
rem Pfade fuer alle Skripte. Nur hier anpassen.
set "GLASSFISH=C:\glassfish8"
rem Nur falls Java nicht gefunden wird: rem entfernen und eigenen JDK-Pfad eintragen
rem set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot"

set "ASADMIN=%GLASSFISH%\glassfish\bin\asadmin.bat"
set "PROJEKT=%~dp0"
