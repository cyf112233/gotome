@echo off
chcp 65001
setlocal enabledelayedexpansion

REM ====== Supported Minecraft versions ======
set MC_VERSIONS=1.20 1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8

REM ====== Version mapping for each MC version ======
set YARN_1.20=1.20.1+build.1
set YARN_1.20.1=1.20.1+build.1
set YARN_1.20.2=1.20.2+build.1
set YARN_1.20.3=1.20.3+build.1
set YARN_1.20.4=1.20.4+build.1
set YARN_1.20.5=1.20.5+build.1
set YARN_1.20.6=1.20.6+build.1
set YARN_1.21=1.21+build.9
set YARN_1.21.1=1.21.1+build.1
set YARN_1.21.2=1.21.2+build.1
set YARN_1.21.3=1.21.3+build.1
set YARN_1.21.4=1.21.4+build.1
set YARN_1.21.5=1.21.5+build.1
set YARN_1.21.6=1.21.6+build.1
set YARN_1.21.7=1.21.7+build.1
set YARN_1.21.8=1.21.8+build.1

set FABRIC_API_1.20=0.83.0+1.20
set FABRIC_API_1.20.1=0.83.0+1.20
set FABRIC_API_1.20.2=0.83.0+1.20
set FABRIC_API_1.20.3=0.83.0+1.20
set FABRIC_API_1.20.4=0.83.0+1.20
set FABRIC_API_1.20.5=0.83.0+1.20
set FABRIC_API_1.20.6=0.83.0+1.20
set FABRIC_API_1.21=0.83.0+1.20
set FABRIC_API_1.21.1=0.83.0+1.20
set FABRIC_API_1.21.2=0.83.0+1.20
set FABRIC_API_1.21.3=0.83.0+1.20
set FABRIC_API_1.21.4=0.83.0+1.20
set FABRIC_API_1.21.5=0.83.0+1.20
set FABRIC_API_1.21.6=0.83.0+1.20
set FABRIC_API_1.21.7=0.83.0+1.20
set FABRIC_API_1.21.8=0.83.0+1.20

set LOADER_1.20=0.14.21
set LOADER_1.20.1=0.14.21
set LOADER_1.20.2=0.14.21
set LOADER_1.20.3=0.14.21
set LOADER_1.20.4=0.15.10
set LOADER_1.20.5=0.15.10
set LOADER_1.20.6=0.15.10
set LOADER_1.21=0.16.14
set LOADER_1.21.1=0.16.14
set LOADER_1.21.2=0.16.14
set LOADER_1.21.3=0.16.14
set LOADER_1.21.4=0.16.14
set LOADER_1.21.5=0.16.14
set LOADER_1.21.6=0.16.14
set LOADER_1.21.7=0.16.14
set LOADER_1.21.8=0.16.14

REM ====== Batch build for all versions ======
for %%V in (%MC_VERSIONS%) do (
    echo Building for MC %%V
    REM Use src2 for 1.20 and 1.20.1, src4 for 1.21.6+, src3 for others
    if "%%V"=="1.20"   xcopy /E /I /Y src2 src >nul
    if "%%V"=="1.20.1" xcopy /E /I /Y src2 src >nul
    if "%%V"=="1.21.6" xcopy /E /I /Y src4 src >nul
    if "%%V"=="1.21.7" xcopy /E /I /Y src4 src >nul
    if "%%V"=="1.21.8" xcopy /E /I /Y src4 src >nul
    if not "%%V"=="1.20" if not "%%V"=="1.20.1" if not "%%V"=="1.21.6" if not "%%V"=="1.21.7" if not "%%V"=="1.21.8" xcopy /E /I /Y src3 src >nul

    REM Generate gradle.properties for this version
    (
        echo org.gradle.jvmargs=-Xmx1G
        echo minecraft_version=%%V
        echo yarn_mappings=!YARN_%%V!
        echo loader_version=!LOADER_%%V!
        echo fabric_version=!FABRIC_API_%%V!
        echo mod_version=1.0-%%V
        echo maven_group=byd.cxkcxkckx
        echo archives_base_name=gotome
    ) > gradle.properties

    call gradlew clean build

    if exist build\libs\gotome-*.jar (
        move build\libs\gotome-*.jar build\libs\gotome-%%V.jar
    )
    if not exist output mkdir output
    for %%F in (build\libs\gotome-*.jar) do (
        echo %%F | findstr /i /v "sources" >nul && copy /Y %%F output\
    )
)
echo All builds finished!
pause