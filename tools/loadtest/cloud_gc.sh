#!/bin/bash
# 汇总 gc.log 的 GC 次数与最近停顿，供前后对比
echo YOUNG=$(grep -ac 'Pause Young' /home/ubuntu/gc.log)
echo FULL=$(grep -ac 'Pause Full' /home/ubuntu/gc.log)
echo --- last GC ---
grep -a 'Pause' /home/ubuntu/gc.log | tail -3
echo --- heap/threads ---
PID=$(pgrep -f 'java -Xms512m' | head -1)
echo PID=$PID
