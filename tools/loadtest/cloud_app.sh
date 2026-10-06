#!/bin/bash
# 云 app 启动：HEAP(默认1024m)、CACHE(true=读快照/false=每请求现场探测)可用环境变量控制，做压测 A/B 与 OOM 演示。
cd /home/ubuntu
pkill -9 -f 'app.jar' 2>/dev/null || true
sleep 3
redis-cli -p 6390 ping 2>/dev/null | grep -q PONG || redis-server --port 6390 --daemonize yes
sleep 1
HEAP=${HEAP:-1024m}
CACHE=${CACHE:-true}
nohup java -Xms512m -Xmx${HEAP} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/ubuntu \
  -Xlog:gc*:file=/home/ubuntu/gc.log:time,uptime:filecount=5,filesize=10m \
  -jar /home/ubuntu/app.jar \
  --spring.profiles.active=dev \
  --spring.redis.port=6390 \
  --server.port=9090 \
  --spring.datasource.druid.url='jdbc:mysql://127.0.0.1:3306/inteink_loadtest?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  --spring.datasource.druid.username=loadapp \
  --spring.datasource.druid.password="${DB_LOADTEST_PW:?set DB_LOADTEST_PW}" \
  --biz.device.mode=http \
  --biz.dashboard.cache=${CACHE} \
  > /home/ubuntu/app.log 2>&1 &
sleep 22
echo PID=$(pgrep -f 'java -Xms512m' | head -1)
echo PORT9090=$(ss -ltn | grep -c :9090)
echo ---LOG---
grep -aE "Started InteinkFaster|APPLICATION FAILED|Port 9090" /home/ubuntu/app.log | head -5
