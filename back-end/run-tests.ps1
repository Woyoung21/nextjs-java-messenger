param(
    [string]$Test = "",
    [switch]$Loop
)

$scriptDir = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
Set-Location $scriptDir

function Ensure-Maven {
    if (Get-Command mvn -ErrorAction SilentlyContinue) { return "mvn" }

    $mavenVersion = "3.8.8"
    $localMavenDir = Join-Path $scriptDir ".maven"
    $mavenDir = Join-Path $localMavenDir "apache-maven-$mavenVersion"
    $mvnCmd = Join-Path $mavenDir "bin\mvn.cmd"

    if (-not (Test-Path $mvnCmd)) {
        Write-Host "Maven not found locally. Downloading Apache Maven $mavenVersion..."
        New-Item -ItemType Directory -Force -Path $localMavenDir | Out-Null
        $zipPath = Join-Path $localMavenDir "apache-maven-$mavenVersion-bin.zip"
        $url = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
        Invoke-WebRequest -Uri $url -OutFile $zipPath -UseBasicParsing
        Expand-Archive -Path $zipPath -DestinationPath $localMavenDir -Force
        Remove-Item $zipPath -Force
    }

    if (Test-Path $mvnCmd) { return $mvnCmd }
    throw "Failed to obtain Maven. Please install Maven or run this script with internet access."
}

function Run-Maven([string]$args) {
    $mvn = Ensure-Maven
    if ($mvn -eq "mvn") {
        & mvn $args
        return $LASTEXITCODE
    } else {
        & $mvn $args
        return $LASTEXITCODE
    }
}

if ($Loop) {
    Get-ChildItem -Path .\src\test\java -Recurse -Filter *Test.java | ForEach-Object {
        $name = $_.BaseName
        Write-Host "=== Running $name ==="
        $exit = Run-Maven ("-Dtest=$name test")
        if ($exit -ne 0) { Write-Host "Test $name failed (exit $exit)"; exit $exit }
    }
    exit 0
}

<# If a specific test class/method was provided, run it #>
if ($Test -ne "") {
    $exit = Run-Maven ("-Dtest=$Test test")
    exit $exit
}

# default: run full suite
$exit = Run-Maven "test"
exit $exit
