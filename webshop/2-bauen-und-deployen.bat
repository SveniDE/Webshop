@echo off
rem Baut den Shop und bringt ihn auf GlassFish
call "%~dp0einstellungen.bat"
cd /d "%PROJEKT%"

echo [1/3] Bauen: mvnw clean package
echo       Beim ersten Mal laedt Maven einige Minuten Bibliotheken herunter.
call "%PROJEKT%mvnw.cmd" clean package
if errorlevel 1 (
    echo.
    echo FEHLER beim Bauen. Die Ursache steht oben in den Zeilen mit [ERROR].
    echo Haeufig: Tippfehler im Java-Code, Datei im falschen Ordner, kein Internet.
    if not defined NOPAUSE pause
    exit /b 1
)

echo [2/3] Laeuft GlassFish?
call "%ASADMIN%" list-domains | findstr /c:"domain1 running" >nul
if errorlevel 1 (
    echo       Nein, wird gestartet ...
    call "%ASADMIN%" start-domain
    if errorlevel 1 (
        echo FEHLER: GlassFish startet nicht. 0-pruefen.bat ausfuehren.
        if not defined NOPAUSE pause
        exit /b 1
    )
)

echo [3/3] Deployen: target\webshop.war
call "%ASADMIN%" deploy --force=true "%PROJEKT%target\webshop.war"
if errorlevel 1 (
    echo.
    echo FEHLER beim Deployen. Genaue Meldung im Server-Log:
    echo %GLASSFISH%\glassfish\domains\domain1\logs\server.log
    if not defined NOPAUSE pause
    exit /b 1
)

echo.
echo Fertig. Der Browser oeffnet http://localhost:8080/webshop/
start "" "http://localhost:8080/webshop/"
if not defined NOPAUSE timeout /t 5
