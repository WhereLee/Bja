#!/bin/bash
echo "RUNNING_PID=$(pgrep -f app.jar | head -1)"
echo "PORT=$(ss -ltn | grep -c :9090)"
echo "=== tail app.log ==="
tail -n 45 /home/ubuntu/app.log
