#!/bin/bash


java --add-opens java.base/java.io=ALL-UNNAMED \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
     -javaagent:/home/basti/opt/jabs/agent/target/agent-shaded.jar \
     -jar /home/basti/opt/jabs/app/target/app-0.1.jar

