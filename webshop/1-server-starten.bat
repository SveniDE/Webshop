@echo off
rem Startet GlassFish
call "%~dp0einstellungen.bat"

if not exist "%ASADMIN%" (
    echo FEHLER: GlassFish nicht gefunden unter %GLASSFISH%
    echo Pfad in einstellungen.bat pruefen.
    if not defined NOPAUSE pause
    exit /b 1
)

call "%ASADMIN%" list-domains | findstr /c:"domain1 running" >nul
if not errorlevel 1 (
    echo GlassFish laeuft bereits.
    echo Startseite: http://localhost:8080   Verwaltung: http://localhost:4848
    if not defined NOPAUSE pause
    exit /b 0
)

echo GlassFish wird gestartet. Das dauert etwa 30 Sekunden ...
echo Fragt die Windows-Firewall nach: "Zugriff zulassen" klicken.
call "%ASADMIN%" start-domain
if errorlevel 1 (
    echo.
    echo FEHLER beim Start. Haeufigste Ursachen:
    echo  - Java fehlt oder ist aelter als 21: 0-pruefen.bat ausfuehren
    echo  - Port 8080 oder 4848 ist belegt: 0-pruefen.bat ausfuehren
    if not defined NOPAUSE pause
    exit /b 1
)
echo.
echo Fertig. Startseite: http://localhost:8080   Verwaltung: http://localhost:4848
if not defined NOPAUSE pause
