@echo off
rem Setting up classpath including javakara.jar and json-20210307.jar
set CLASSPATH="E:\Privat\All Code\JavaKara\Kara\javakara.jar;E:\Privat\All Code\JavaKara\libs\json-20210307.jar;E:\Privat\All Code\JavaKara\src"

rem Compile the Java program
javac -cp %CLASSPATH% "E:\Privat\All Code\JavaKara\src\Main.java"

rem Check if compilation was successful
if %errorlevel% equ 0 (
    echo Compilation successful.
) else (
    echo Compilation failed.
)

pause
