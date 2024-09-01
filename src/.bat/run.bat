@echo off
set CLASSPATH="E:\Privat\All Code\JavaKara\Kara\javakara.jar;E:\Privat\All Code\JavaKara\libs\json-20210307.jar;E:\Privat\All Code\JavaKara\src"
javac -cp %CLASSPATH% "E:\Privat\All Code\JavaKara\src\Main.java"
java -cp %CLASSPATH% src.Main