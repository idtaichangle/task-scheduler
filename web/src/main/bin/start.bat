@echo off
 set script-dir=%~dp0
 set script-dir=%script-dir:~0,-1%

 for %%d in (%script-dir%) do set BASEDIR=%%~dpd
 ::echo BASEDIR=%BASEDIR%
 set CLASSPATH=%BASEDIR%conf;%BASEDIR%lib\*;
 ::echo %CLASSPATH%
java -cp %CLASSPATH% com.cvnavi.schduler.web.WebApplication