@echo on

SET CURRENT_PATH=C:\WKSPs\Consolidado\SWT_ATM-Test\mozilla-build

cd %CURRENT_PATH%

del /Q *.java
del /Q *.xpt


for %%F in (*.idl) do call innerCompile.bat %CURRENT_PATH% %%~nF


del C:\WKSPs\Consolidado\XULRunner\mozilla\xulrunner\components\compreg.dat
del C:\WKSPs\Consolidado\XULRunner\mozilla\xulrunner\components\xpti.dat
copy *.xpt C:\WKSPs\Consolidado\XULRunner\mozilla\xulrunner\components\*.*

copy *.java C:\WKSPs\Consolidado\SWT_ATM-Test\src_SWT\org\mozilla\interfaces\*.*

pause

