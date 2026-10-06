#!/usr/bin/env python3
# 轻量压测器(纯标准库)：多线程打同一接口，统计 P50/P95/P99、QPS、错误率。
# 用法：python3 loadgen.py --url http://127.0.0.1:9090/api/biz/rod/liveStatus --token loadtest-token --threads 20 --seconds 30
import argparse
import statistics
import threading
import time
import urllib.request

ap = argparse.ArgumentParser()
ap.add_argument("--url", required=True)
ap.add_argument("--token", default="")
ap.add_argument("--threads", type=int, default=10)
ap.add_argument("--seconds", type=int, default=20)
A = ap.parse_args()

lat = []          # 每次成功耗时 ms
lock = threading.Lock()
stop_at = time.time() + A.seconds
errors = [0]
count = [0]


def worker():
    while time.time() < stop_at:
        t0 = time.time()
        try:
            req = urllib.request.Request(A.url, headers={"token": A.token})
            with urllib.request.urlopen(req, timeout=60) as r:
                r.read()
                code = r.status
            dt = (time.time() - t0) * 1000
            if code == 200:
                with lock:
                    lat.append(dt)
                    count[0] += 1
            else:
                with lock:
                    errors[0] += 1
        except Exception:
            with lock:
                errors[0] += 1


ts = [threading.Thread(target=worker) for _ in range(A.threads)]
for t in ts:
    t.start()
for t in ts:
    t.join()

n = len(lat)
if n:
    lat.sort()
    q = lambda p: lat[min(n - 1, int(p / 100 * n))]
    print("threads=%d seconds=%d  完成=%d 错误=%d" % (A.threads, A.seconds, count[0], errors[0]))
    print("QPS=%.1f  P50=%.0fms P95=%.0fms P99=%.0fms max=%.0fms avg=%.0fms"
          % (count[0] / A.seconds, q(50), q(95), q(99), max(lat), statistics.mean(lat)))
else:
    print("无成功请求, 错误=%d" % errors[0])
