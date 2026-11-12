param([string]$TomcatHome=(Join-Path $env:USERPROFILE 'Downloads\apache-tomcat-9.0.112'))
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'build-module1.ps1')
if(!(Test-Path -LiteralPath (Join-Path $TomcatHome 'bin\bootstrap.jar'))){throw 'Provide -TomcatHome with the installed Tomcat 9 path.'}
$classes=Join-Path $projectRoot '.verification\test-classes'
New-Item -ItemType Directory -Path $classes -Force | Out-Null
$driver=Join-Path $projectRoot 'WebContent\WEB-INF\lib\mysql-connector-j-9.5.0.jar'
$files=@(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Recurse -Filter '*.java' | ForEach-Object FullName)
$files+=@(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'test') -Recurse -Filter '*.java' | ForEach-Object FullName)
& javac --release 8 -Xlint:-options -encoding UTF-8 -cp $driver -d $classes @files
if($LASTEXITCODE -ne 0){throw 'Test compilation failed.'}
& java '-Dfile.encoding=UTF-8' -cp "$classes;$driver" unittest.Module1IntegrationTest6 $projectRoot $TomcatHome
if($LASTEXITCODE -ne 0){throw 'Module 1 checks failed.'}
