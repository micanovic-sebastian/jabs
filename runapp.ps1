# PowerShell-Skript zum Ausführen der Java-Anwendung mit angehängtem JABS-Agenten.
# Führen Sie dieses Skript aus dem Stammverzeichnis des Projekts aus.

# --- Konfiguration ---
# Pfade zu den JAR-Dateien des Agenten und der Anwendung, relativ zum Projektstamm.
$agentJar = "agent/target/agent-shaded.jar"
$appJar = "app/target/app-0.1-SNAPSHOT.jar"

# --- Überprüfung der Dateien ---
# Stellt sicher, dass die JAR-Dateien vorhanden sind, bevor das Skript ausgeführt wird.
if (-not (Test-Path $agentJar)) {
    Write-Error "Agenten-JAR nicht gefunden unter: $agentJar. Bitte erstellen Sie das Projekt zuerst mit 'mvn clean package'."
    exit 1
}
if (-not (Test-Path $appJar)) {
    Write-Error "Anwendungs-JAR nicht gefunden unter: $appJar. Bitte erstellen Sie das Projekt zuerst mit 'mvn clean package'."
    exit 1
}

# --- JVM-Argumente ---
# Diese sind erforderlich, damit der Agent auf JDK-Interna zugreifen kann, insbesondere ab Java 9+.
$jvmArgs = @(
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED"
)

# --- Ausführung ---
Write-Host "Anwendung wird mit JABS-Agenten gestartet..."
Write-Host "Agenten-JAR: $agentJar"
Write-Host "App-JAR:     $appJar"
Write-Host "-------------------------------------------------"

# Der `&`-Operator wird verwendet, um den Befehl auszuführen.
# Die Argumente werden an den 'java'-Befehl übergeben.
& java $jvmArgs -javaagent:"$agentJar" -jar "$appJar"

Write-Host "-------------------------------------------------"
Write-Host "Anwendung beendet."
