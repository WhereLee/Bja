#!/usr/bin/env python3
# 虚拟道闸设备（可多实例）
# - 每台带 sn + port，状态存内存：0=默认 1=升 2=降
# - 仅监听 127.0.0.1，经 SSH 隧道从本地访问，不公网暴露
# 用法：python3 barrier_device.py --port 18080 --sn DEV001
# 接口：
#   GET  /state   -> {"sn":.., "port":.., "state": n}
#   GET  /info    -> 同 /state
#   POST /command {"action":1|2} -> 改状态并返回 {"sn":.., "port":.., "state": action}
import argparse
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

parser = argparse.ArgumentParser()
parser.add_argument("--port", type=int, default=18080)
parser.add_argument("--sn", default="DEV000")
ARGS = parser.parse_args()

state = 0


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
        else:
            self._send(404, {"error": "not found"})

    def do_POST(self):
        global state
        if self.path != "/command":
            self._send(404, {"error": "not found"})
            return
        length = int(self.headers.get("Content-Length", 0))
        try:
            body = json.loads(self.rfile.read(length) or b"{}")
            action = int(body.get("action"))
            if action not in (1, 2):
                self._send(400, {"error": "action must be 1 or 2"})
                return
            state = action
            self._send(200, self._info())
        except Exception as e:
            self._send(400, {"error": str(e)})

    def log_message(self, *args):
        pass


if __name__ == "__main__":
    print("barrier device sn=%s on 127.0.0.1:%d, initial state=%d" % (ARGS.sn, ARGS.port, state))
    ThreadingHTTPServer(("127.0.0.1", ARGS.port), Handler).serve_forever()
