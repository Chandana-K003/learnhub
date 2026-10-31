@echo off
rem Maven wrapper: downloads Apache Maven once into %USERPROFILE%\.m2\wrapper, then runs it.
rem Usage:  mvnw.cmd spring-boot:run
setlocal
set "MAVEN_VERSION=3.9.9"
set "MAVEN_HOME_DIR=%USERPROFILE%\.m2\wrapper\apache-maven-%MAVEN_VERSION%"

if not exist "%MAVEN_HOME_DIR%\bin\mvn.cmd" (
  echo Downloading Apache Maven %MAVEN_VERSION% ^(first run only^)...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $zip=Join-Path $env:TEMP 'apache-maven-%MAVEN_VERSION%-bin.zip'; Invoke-WebRequest -UseBasicParsing -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile $zip; Expand-Archive -Force $zip (Join-Path $env:USERPROFILE '.m2\wrapper'); Remove-Item $zip"
  if errorlevel 1 (
    echo Failed to download Maven. Check your internet connection.
    exit /b 1
  )
)

call "%MAVEN_HOME_DIR%\bin\mvn.cmd" %*
