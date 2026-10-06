#!/bin/bash
cd /home/ubuntu
pkill -9 -f 'app.jar' 2>/dev/null || true
sleep 3
redis-cli -p 6390 ping 2>/dev/null | grep -q PONG || redis-server --port 6390 --daemonize yes
sleep 1
nohup java -Xms512m -Xmx1024m -Xlog:gc*:file=/home/ubuntu/gc.log:time,uptime:filecount=5,filesize=10m \
  -jar /home/ubuntu/app.jar \
  --spring.profiles.active=dev \
  --spring.redis.port=6390 \
  --server.port=9090 \
  --spring.datasource.druid.url='jdbc:mysql://127.0.0.1:3306/inteink_loadtest?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  --spring.datasource.druid.username=loadapp \
  --spring.datasource.druid.password="${DB_LOADTEST_PW:?set DB_LOADTEST_PW}" \
  --biz.device.mode=http \
  > /home/ubuntu/app.log 2>&1 &
sleep 22
echo PID=$(pgrep -f 'java -Xms512m' | head -1)
echo PORT9090=$(ss -ltn | grep -c :9090)
echo ---LOG---
grep -aE "Started InteinkFaster|APPLICATION FAILED|Caused by|Unsupported class|Access denied|Communications link" /home/ubuntu/app.log | head -8
