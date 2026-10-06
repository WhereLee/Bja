#!/bin/bash
U=http://127.0.0.1:9090/api/biz/rod/liveStatus
H='token: loadtest-token'
echo "--- body(截断) ---"
curl -s -H "$H" "$U" | head -c 320
echo
echo "--- 串行3次计时(单次 liveStatus 全表耗时) ---"
for i in 1 2 3; do curl -s -o /dev/null -w "call$i total=%{time_total}s http=%{http_code}\n" -H "$H" "$U"; done
