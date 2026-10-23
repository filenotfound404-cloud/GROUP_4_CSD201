@echo off
setlocal

set "SOURCE_ROOT=%~dp0"
set "OUTPUT_DIR=%TEMP%\GROUP_4_CSD201_KKMP_CommonTests"

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

javac -encoding UTF-8 -d "%OUTPUT_DIR%" ^
  "%SOURCE_ROOT%CommonTestSuite.java"

if errorlevel 1 (
  echo.
  echo Compilation failed. Check that the JDK bin directory is on PATH.
  pause
  exit /b 1
)

java -cp "%OUTPUT_DIR%" CommonTestSuite
pause
endlocal
