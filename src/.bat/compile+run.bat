@echo off
rem Setting up classpath including javakara.jar and json-20210307.jar
set "BASE_DIR=E:\Privat\All Code\JavaKara"
set "CLASSPATH=%BASE_DIR%\javakara.jar;%BASE_DIR%\libs\json-20210307.jar;%BASE_DIR%\src"

rem Compile the Java program
javac -cp "%CLASSPATH%" "%BASE_DIR%\src\Main.java"

rem Check if compilation was successful
if %errorlevel% equ 0 (
    echo Compilation successful.
    rem Run the program
    java -cp "%CLASSPATH%" src.Main
) else (
    echo Compilation failed.
)

pause
