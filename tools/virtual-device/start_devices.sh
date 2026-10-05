#!/bin/bash
cd /home/ubuntu || exit 1
# 清掉旧的（此脚本自身 argv 不含 barrier_device.py，不会误杀自己）
pkill -f barrier_device.py
sleep 1
nohup python3 barrier_device.py --port 18080 --sn DEV001 </dev/null >device1.log 2>&1 &
nohup python3 barrier_device.py --port 18081 --sn DEV002 </dev/null >device2.log 2>&1 &
sleep 2
echo "N=$(pgrep -fc barrier_device.py)"
echo "D1=$(curl -s http://127.0.0.1:18080/state)"
echo "D2=$(curl -s http://127.0.0.1:18081/state)"
