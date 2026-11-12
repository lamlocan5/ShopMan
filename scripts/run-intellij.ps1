param(
 [string]$TomcatHome=(Join-Path $env:USERPROFILE 'Downloads\apache-tomcat-9.0.112'),
 [ValidateRange(1024,65535)][int]$HttpPort=8081
)
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'prepare-intellij.ps1') -TomcatHome $TomcatHome -HttpPort $HttpPort
$base=Join-Path $projectRoot '.runtime\intellij-tomcat'
$classpath=(Join-Path $TomcatHome 'bin\bootstrap.jar')+';'+(Join-Path $TomcatHome 'bin\tomcat-juli.jar')
Write-Output 'Press Ctrl+C in this terminal to stop ShopMan.'
& java "-Dcatalina.home=$TomcatHome" "-Dcatalina.base=$base" '-Dfile.encoding=UTF-8' '-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager' "-Djava.util.logging.config.file=$base\conf\logging.properties" -cp $classpath org.apache.catalina.startup.Bootstrap start
if($LASTEXITCODE -ne 0){throw 'Tomcat stopped with an error. Check the console above.'}
