#!/usr/bin/env python3
# 虚拟道闸设备（可多实例 + 故障注入）
# - 每台带 sn + port，状态存内存：0=默认 1=升 2=降
# - 仅监听 127.0.0.1，经 SSH 隧道从本地访问，不公网暴露
# 用法：python3 barrier_device.py --port 18080 --sn DEV001 [--latency-ms 0] [--fail-rate 0]
# 接口：
#   GET  /state | /info   -> {"sn","port","state"}
#   GET  /config          -> {"sn","port","state","latency_ms","fail_rate","offline"}
#   POST /config {"offline":bool,"fail_rate":f,"latency_ms":n} -> 更新注入配置并回显
#   POST /command {"action":1|2} -> 依配置注入延迟/拒绝/离线，成功则改状态并回 {"state":action}
import argparse
import json
import random
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

parser = argparse.ArgumentParser()
parser.add_argument("--port", type=int, default=18080)
parser.add_argument("--sn", default="DEV000")
parser.add_argument("--latency-ms", type=int, default=0)
parser.add_argument("--fail-rate", type=float, default=0.0)
ARGS = parser.parse_args()

state = 0
cfg = {"latency_ms": ARGS.latency_ms, "fail_rate": ARGS.fail_rate, "offline": False}


class Handler(BaseHTTPRequestHandler):
    def _send(self, code, obj):
        data = json.dumps(obj).encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def _info(self):
        return {"sn": ARGS.sn, "port": ARGS.port, "state": state}

    def do_GET(self):
        if self.path in ("/state", "/info"):
            self._send(200, self._info())
        elif self.path == "/config":
            out = self._info()
            out.update(cfg)
            self._send(200, out)
        else:
            self._send(404, {"error": "not found"})

    def do_POST(self):
        global state
        length = int(self.headers.get("Content-Length", 0))
        raw = self.rfile.read(length) or b"{}"
        if self.path == "/config":
            try:
                body = json.loads(raw)
            except Exception as e:
                self._send(400, {"error": str(e)})
                return
            if "offline" in body:
                cfg["offline"] = bool(body["offline"])
            if "fail_rate" in body:
                cfg["fail_rate"] = float(body["fail_rate"])
            if "latency_ms" in body:
                cfg["latency_ms"] = int(body["latency_ms"])
            out = self._info()
            out.update(cfg)
            self._send(200, out)
            return

        if self.path != "/command":
            self._send(404, {"error": "not found"})
            return
        try:
            body = json.loads(raw)
            action = int(body.get("action"))
        except Exception as e:
            self._send(400, {"error": str(e)})
            return
        # 1) 延迟（可能触发客户端超时）
        if cfg["latency_ms"] > 0:
            time.sleep(cfg["latency_ms"] / 1000.0)
        # 2) 离线
        if cfg["offline"]:
            self._send(503, {"error": "offline", **self._info()})
            return
        # 3) 概率性设备拒绝
        if cfg["fail_rate"] > 0 and random.random() < cfg["fail_rate"]:
            self._send(500, {"error": "device reject", **self._info()})
            return
        if action not in (1, 2):
            self._send(400, {"error": "action must be 1 or 2"})
            return
        state = action
        self._send(200, self._info())

    def log_message(self, *args):
        pass


if __name__ == "__main__":
    print("barrier device sn=%s on 127.0.0.1:%d, latency=%sms fail_rate=%s"
          % (ARGS.sn, ARGS.port, ARGS.latency_ms, ARGS.fail_rate))
    ThreadingHTTPServer(("127.0.0.1", ARGS.port), Handler).serve_forever()
