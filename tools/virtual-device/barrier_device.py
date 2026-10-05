#!/usr/bin/env python3
# 极简虚拟道闸设备（演示用）
# - 单台设备，状态存内存：0=默认 1=升 2=降
# - 仅监听 127.0.0.1，经 SSH 隧道从本地访问，不公网暴露
# 接口：
#   GET  /state           -> {"state": n}
#   POST /command {"action":1|2} -> 把状态改成 action 并返回 {"state": action}
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

HOST, PORT = "127.0.0.1", 18080
state = 0


class Handler(BaseHTTPRequestHandler):
    def _send(self, code, obj):
        data = json.dumps(obj).encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def do_GET(self):
        if self.path == "/state":
            self._send(200, {"state": state})
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
            self._send(200, {"state": state})
        except Exception as e:
            self._send(400, {"error": str(e)})

    def log_message(self, *args):
        pass


if __name__ == "__main__":
    print("barrier virtual device on %s:%d, initial state=%d" % (HOST, PORT, state))
    ThreadingHTTPServer((HOST, PORT), Handler).serve_forever()
