@echo off
rem Startet GlassFish im Debug-Modus (Port 9009)
call "%~dp0einstellungen.bat"
call "%ASADMIN%" stop-domain >nul 2>&1
call "%ASADMIN%" start-domain --debug
if not defined NOPAUSE pause
