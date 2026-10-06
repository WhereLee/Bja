#!/usr/bin/env python3
# 压测用"设备农场"：单进程 asyncio 监听多个端口，每端口=一台虚拟道闸设备。
# - 只读 /state 便宜（看板 liveStatus 探测走它）；POST /command 改态。
# - 绑 127.0.0.1：仅同机 app 访问，不公网暴露。
# 用法：python3 device_farm.py --start 18000 --count 500
import argparse
import asyncio
import json

parser = argparse.ArgumentParser()
parser.add_argument("--start", type=int, default=18000)
parser.add_argument("--count", type=int, default=500)
ARGS = parser.parse_args()

state = {}


async def handle(reader, writer):
    try:
        reqline = await asyncio.wait_for(reader.readline(), timeout=5)
        if not reqline:
            return
        parts = reqline.decode(errors="ignore").split()
        method = parts[0] if parts else ""
        path = parts[1] if len(parts) > 1 else "/"
        length = 0
        while True:
            line = await reader.readline()
            if line in (b"\r\n", b"\n", b""):
                break
            ls = line.decode(errors="ignore").lower()
            if ls.startswith("content-length:"):
                try:
                    length = int(ls.split(":", 1)[1].strip())
                except ValueError:
                    length = 0
        body = await reader.readexactly(length) if length > 0 else b"{}"
        sock = writer.get_extra_info("sockname")
        port = sock[1] if sock else ARGS.start
        cur = state.get(port, 0)
        if method == "POST" and path == "/command":
            try:
                action = int(json.loads(body or b"{}").get("action"))
            except Exception:
                action = None
            if action in (1, 2):
                cur = action
                state[port] = cur
        payload = json.dumps({"sn": "DEV" + str(port), "port": port, "state": cur}).encode()
        resp = (b"HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: "
                + str(len(payload)).encode() + b"\r\nConnection: close\r\n\r\n" + payload)
        writer.write(resp)
        await writer.drain()
    except Exception:
        pass
    finally:
        try:
            writer.close()
        except Exception:
            pass


async def main():
    servers = []
    for i in range(ARGS.count):
        p = ARGS.start + i
        state[p] = 0
        servers.append(await asyncio.start_server(handle, "127.0.0.1", p))
    print("device farm: %d ports %d..%d" % (ARGS.count, ARGS.start, ARGS.start + ARGS.count - 1))
    await asyncio.gather(*[s.serve_forever() for s in servers])


if __name__ == "__main__":
    asyncio.run(main())
