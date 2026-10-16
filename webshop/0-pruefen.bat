@echo off
rem Prueft Java, GlassFish, Pfade und Ports. Aendert nichts.
call "%~dp0einstellungen.bat"
set "BERICHT=%PROJEKT%pruefbericht.txt"
set FEHLER=0

echo Pruefbericht Webshop > "%BERICHT%"
echo Datum: %DATE% %TIME% >> "%BERICHT%"
echo.

echo [1] Java
where java >nul 2>&1
if errorlevel 1 (
    echo     FEHLER: java wird nicht gefunden. JDK 21 installieren, Haken "Add to PATH".
    echo [1] FEHLER: java nicht gefunden >> "%BERICHT%"
    set FEHLER=1
) else (
    java -version 2>&1 | findstr /i "version"
    java -version >> "%BERICHT%" 2>&1
)

echo [2] JAVA_HOME
if "%JAVA_HOME%"=="" (
    echo     HINWEIS: JAVA_HOME ist nicht gesetzt. Klappt das Bauen nicht, im Java-Installer den Haken "Set JAVA_HOME" setzen.
    echo [2] JAVA_HOME nicht gesetzt >> "%BERICHT%"
) else (
    echo     %JAVA_HOME%
    echo [2] JAVA_HOME=%JAVA_HOME% >> "%BERICHT%"
    if not exist "%JAVA_HOME%\bin\java.exe" (
        echo     FEHLER: Unter JAVA_HOME liegt kein bin\java.exe. Pfad stimmt nicht.
        echo     JAVA_HOME muss genau EIN Ordner sein: der JDK-Ordner selbst, ohne \bin am Ende
        echo     und ohne Semikolon. Beispiel: C:\Program Files\Java\jdk-21
        echo     Aendern: Windows-Taste, "Umgebungsvariablen" eingeben, JAVA_HOME bearbeiten.
        echo [2] FEHLER: JAVA_HOME zeigt ins Leere >> "%BERICHT%"
        set FEHLER=1
    )
)

echo [3] GlassFish
if exist "%ASADMIN%" (
    echo     gefunden: %GLASSFISH%
    echo [3] GlassFish gefunden: %GLASSFISH% >> "%BERICHT%"
) else (
    echo     FEHLER: %ASADMIN% fehlt. GlassFish nach %GLASSFISH% entpacken oder Pfad in einstellungen.bat anpassen.
    echo [3] FEHLER: asadmin.bat fehlt unter %GLASSFISH% >> "%BERICHT%"
    set FEHLER=1
)

echo [4] Maven Wrapper
if exist "%PROJEKT%mvnw.cmd" (
    echo     mvnw.cmd vorhanden
    echo [4] mvnw.cmd vorhanden >> "%BERICHT%"
) else (
    echo     FEHLER: mvnw.cmd fehlt im Projektordner.
    echo [4] FEHLER: mvnw.cmd fehlt >> "%BERICHT%"
    set FEHLER=1
)

echo [5] Projektpfad
echo     %PROJEKT%
echo [5] Projektpfad: %PROJEKT% >> "%BERICHT%"
set "OHNELEER=%PROJEKT: =%"
if not "%OHNELEER%"=="%PROJEKT%" (
    echo     WARNUNG: Der Pfad enthaelt Leerzeichen. Besser: C:\projekte\webshop
    echo [5] WARNUNG: Leerzeichen im Pfad >> "%BERICHT%"
)
echo %PROJEKT% | findstr /i "OneDrive" >nul
if not errorlevel 1 (
    echo     WARNUNG: Das Projekt liegt in OneDrive. Besser: C:\projekte\webshop
    echo [5] WARNUNG: OneDrive >> "%BERICHT%"
)

echo [6] Ports
netstat -ano | findstr /r /c:":8080 .*:0 " >nul
if errorlevel 1 (echo     8080 frei oder GlassFish laeuft noch nicht) else (echo     8080 belegt, laeuft GlassFish schon?)
netstat -ano | findstr /r /c:":4848 .*:0 " >nul
if errorlevel 1 (echo     4848 frei) else (echo     4848 belegt, laeuft GlassFish schon?)
netstat -ano | findstr /r /c:":3306 .*:0 " >nul
if errorlevel 1 (
    echo     FEHLER: Auf Port 3306 hoert nichts. Laeuft MariaDB?
    echo [6] FEHLER: MariaDB Port 3306 nicht offen >> "%BERICHT%"
    set FEHLER=1
) else (
    echo     3306 belegt: MariaDB laeuft
)
netstat -ano | findstr /r /c:":8080 .*:0 " >> "%BERICHT%"
netstat -ano | findstr /r /c:":4848 .*:0 " >> "%BERICHT%"
netstat -ano | findstr /r /c:":3306 .*:0 " >> "%BERICHT%"

echo.
if "%FEHLER%"=="0" (
    echo ERGEBNIS: Alles bereit. Weiter mit 1-server-starten.bat
    echo ERGEBNIS: alles bereit >> "%BERICHT%"
) else (
    echo ERGEBNIS: Es gibt Fehler, siehe oben. Bericht: pruefbericht.txt
    echo ERGEBNIS: Fehler gefunden >> "%BERICHT%"
)
echo.
if not defined NOPAUSE pause
