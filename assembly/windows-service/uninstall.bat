@echo off
rem collapp windows service uninstaller

net stop collapp

%~dp0/collapp.exe uninstall