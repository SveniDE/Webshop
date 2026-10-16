@echo off
rem ==========================================================================
rem  Stoppt GlassFish. Vor dem Herunterfahren nicht noetig, aber sauber.
rem ==========================================================================
call "%~dp0einstellungen.bat"
call "%ASADMIN%" stop-domain
if not defined NOPAUSE pause
