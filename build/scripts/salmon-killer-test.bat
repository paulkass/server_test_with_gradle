@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  salmon-killer-test startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and SALMON_KILLER_TEST_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\salmon-killer-test-3.3.3.jar;%APP_HOME%\lib\vertx-core-3.3.3.jar;%APP_HOME%\lib\vertx-web-3.3.3.jar;%APP_HOME%\lib\cassandra-driver-core-3.1.0.jar;%APP_HOME%\lib\cassandra-driver-mapping-3.1.0.jar;%APP_HOME%\lib\cassandra-driver-extras-3.1.0.jar;%APP_HOME%\lib\netty-common-4.1.5.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.5.Final.jar;%APP_HOME%\lib\netty-transport-4.1.5.Final.jar;%APP_HOME%\lib\netty-handler-4.1.5.Final.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.5.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.5.Final.jar;%APP_HOME%\lib\netty-codec-http2-4.1.5.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.5.Final.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.5.Final.jar;%APP_HOME%\lib\jackson-core-2.7.4.jar;%APP_HOME%\lib\jackson-databind-2.7.4.jar;%APP_HOME%\lib\vertx-auth-common-3.3.3.jar;%APP_HOME%\lib\guava-16.0.1.jar;%APP_HOME%\lib\metrics-core-3.1.2.jar;%APP_HOME%\lib\jnr-ffi-2.0.7.jar;%APP_HOME%\lib\jnr-posix-3.0.27.jar;%APP_HOME%\lib\netty-codec-socks-4.1.5.Final.jar;%APP_HOME%\lib\netty-codec-4.1.5.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.5.Final.jar;%APP_HOME%\lib\jackson-annotations-2.7.0.jar;%APP_HOME%\lib\slf4j-api-1.7.7.jar;%APP_HOME%\lib\jffi-1.2.10.jar;%APP_HOME%\lib\jffi-1.2.10-native.jar;%APP_HOME%\lib\asm-5.0.3.jar;%APP_HOME%\lib\asm-commons-5.0.3.jar;%APP_HOME%\lib\asm-analysis-5.0.3.jar;%APP_HOME%\lib\asm-tree-5.0.3.jar;%APP_HOME%\lib\asm-util-5.0.3.jar;%APP_HOME%\lib\jnr-x86asm-1.0.2.jar;%APP_HOME%\lib\jnr-constants-0.9.0.jar

@rem Execute salmon-killer-test
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %SALMON_KILLER_TEST_OPTS%  -classpath "%CLASSPATH%" io.vertx.example.HelloWorldEmbedded %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable SALMON_KILLER_TEST_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%SALMON_KILLER_TEST_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
