param(
 [string]$TomcatHome=(Join-Path $env:USERPROFILE 'Downloads\apache-tomcat-9.0.112'),
 [string]$RuntimeBase,
 [ValidateRange(1024,65535)][int]$HttpPort=8081,
 [switch]$SkipBuild
)
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
if(!$RuntimeBase){$RuntimeBase=Join-Path $projectRoot '.runtime\intellij-tomcat'}
$RuntimeBase=[IO.Path]::GetFullPath($RuntimeBase)
$TomcatHome=[IO.Path]::GetFullPath($TomcatHome)
if(!(Test-Path -LiteralPath (Join-Path $TomcatHome 'bin\bootstrap.jar'))){throw 'Tomcat 9 not found. Supply -TomcatHome.'}
if(!$SkipBuild){& (Join-Path $PSScriptRoot 'build-module1.ps1')}
foreach($directory in @('conf','logs','temp','webapps','work')){New-Item -ItemType Directory -Path (Join-Path $RuntimeBase $directory) -Force | Out-Null}
Get-ChildItem -LiteralPath (Join-Path $TomcatHome 'conf') -Force | ForEach-Object {Copy-Item -LiteralPath $_.FullName -Destination (Join-Path $RuntimeBase 'conf') -Recurse -Force}
$server=@"
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1">
 <Service name="Catalina">
  <Connector address="127.0.0.1" port="$HttpPort" protocol="HTTP/1.1" URIEncoding="UTF-8" connectionTimeout="20000"/>
  <Engine name="Catalina" defaultHost="localhost">
   <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="false" deployOnStartup="true"/>
  </Engine>
 </Service>
</Server>
"@
[IO.File]::WriteAllText((Join-Path $RuntimeBase 'conf\server.xml'),$server,[Text.UTF8Encoding]::new($false))
Copy-Item -LiteralPath (Join-Path $projectRoot 'dist\ShopMan.war') -Destination (Join-Path $RuntimeBase 'webapps\ShopMan.war') -Force
Write-Output ('Prepared Tomcat: '+$RuntimeBase)
Write-Output ('URL: http://localhost:'+$HttpPort+'/ShopMan/')
