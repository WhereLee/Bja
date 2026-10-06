#!/bin/bash
cd /home/ubuntu
pkill -f barrier_device.py 2>/dev/null || true
pkill -f device_farm.py 2>/dev/null || true
sleep 1
nohup python3 /home/ubuntu/device_farm.py --start 18000 --count 600 > /home/ubuntu/farm.log 2>&1 &
sleep 3
echo PID=$(pgrep -f device_farm.py | head -1)
echo OPEN=$(ss -ltn | grep -c 127.0.0.1:18)
for p in 18010 18300 18509; do echo -n "port $p: "; curl -s http://127.0.0.1:$p/state; echo; done
echo LOG:; tail -n 3 /home/ubuntu/farm.log
