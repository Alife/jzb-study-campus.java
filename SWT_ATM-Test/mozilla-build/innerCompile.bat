@echo off

set XPIDL_EXE=C:\WKSPs\Consolidado\XULRunner\mozilla\xulrunner-sdk\bin\xpidl.exe
set XPIDL_INC=C:\WKSPs\Consolidado\XULRunner\mozilla\idl

%XPIDL_EXE% -m typelib -w -v -I %1 -I %XPIDL_INC%  -o ./%2 ./%2.idl
%XPIDL_EXE% -m java -w -v -I %1 -I %XPIDL_INC%  -o ./%2 ./%2.idl

