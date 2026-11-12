param([string]$JdkHome)
$ErrorActionPreference='Stop'
if(!$JdkHome){
 # Windows PowerShell 5.1 treats redirected native stderr as ErrorRecords.
 # Java writes property settings to stderr even when the command succeeds.
 $savedErrorActionPreference=$ErrorActionPreference
 try {
  $ErrorActionPreference='Continue'
  $settings=& java -XshowSettings:properties -version 2>&1
  $javaSettingsExitCode=$LASTEXITCODE
 } finally {
  $ErrorActionPreference=$savedErrorActionPreference
 }
 if($javaSettingsExitCode -ne 0){throw 'Cannot read Java settings. Check Java on PATH or provide -JdkHome.'}
 $javaHomeLine=$settings | ForEach-Object { $_.ToString() } | Where-Object { $_ -match '^\s*java.home\s*=' } | Select-Object -First 1
 if(!$javaHomeLine){throw 'Cannot locate JDK; provide -JdkHome.'}
 $JdkHome=($javaHomeLine -split '=',2)[1].Trim()
}
$javac=Join-Path $JdkHome 'bin\javac.exe'
$jar=Join-Path $JdkHome 'bin\jar.exe'
$projectRoot=Split-Path -Parent $PSScriptRoot
$stage=Join-Path $projectRoot ('build\package-'+[guid]::NewGuid().ToString('N'))
$classes=Join-Path $stage 'WEB-INF\classes'
$dist=Join-Path $projectRoot 'dist'
New-Item -ItemType Directory -Path $classes,$dist -Force | Out-Null
Get-ChildItem -LiteralPath (Join-Path $projectRoot 'WebContent') -Force | ForEach-Object {
 Copy-Item -LiteralPath $_.FullName -Destination $stage -Recurse -Force
}
$javaFiles=@(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Recurse -Filter '*.java' | ForEach-Object FullName)
& $javac --release 8 -Xlint:-options -encoding UTF-8 -d $classes @javaFiles
if($LASTEXITCODE -ne 0){throw 'Java compilation failed.'}
Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Filter '*.properties' -File | ForEach-Object {
 Copy-Item -LiteralPath $_.FullName -Destination $classes -Force
}
$war=Join-Path $dist 'ShopMan.war'
& $jar cf $war -C $stage .
if($LASTEXITCODE -ne 0){throw 'WAR packaging failed.'}
Write-Output ('Built: '+$war)
