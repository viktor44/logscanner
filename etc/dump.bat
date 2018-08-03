setlocal

cd /D "%~dp0"

set LOGFILE_DATE=%DATE:~6,4%.%DATE:~3,2%.%DATE:~0,2%
set LOGFILE_TIME=%TIME:~0,2%.%TIME:~3,2%
set LOGFILE=%LOGFILE_DATE%-%LOGFILE_TIME%

set JAVA_HOME="C:\Program Files (x86)\Java\jdk1.8.0_171"
%JAVA_HOME%\bin\jmap -dump:file=..\..\dump\%LOGFILE%.hprof %1

endlocal