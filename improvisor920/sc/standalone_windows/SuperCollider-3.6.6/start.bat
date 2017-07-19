@rem run superColider program
@echo off copy "C:\Remoting.config-Training" "C:\Remoting.config"
start "SuperCollider Output" "%~dp0sclang.exe" %~dp0Windows.scd