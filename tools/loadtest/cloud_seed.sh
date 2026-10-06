#!/bin/bash
set -e
cd /home/ubuntu
M(){ sudo mysql inteink_loadtest -N -e "$1"; }
# 500 根杆
M "INSERT INTO biz_lifting_rod (rod_name,rod_addr,rod_state,rod_offline,rod_status,rod_createtime,rod_updatetime) WITH RECURSIVE s(n) AS (SELECT 1 UNION ALL SELECT n+1 FROM s WHERE n<500) SELECT CONCAT('lt_rod',n),'addr',0,1,0,UNIX_TIMESTAMP(),UNIX_TIMESTAMP() FROM s"
# 500 台设备，绑定各杆，端口=18000+rod_id
M "INSERT INTO biz_converter (converter_sn,converter_ip,converter_port,rod_id,converter_status,converter_createtime,converter_updatetime) SELECT CONCAT('DEV',18000+rod_id),'127.0.0.1',18000+rod_id,rod_id,0,UNIX_TIMESTAMP(),UNIX_TIMESTAMP() FROM biz_lifting_rod WHERE rod_name LIKE 'lt_rod%'"
# 压测用 token（user1=developer，拥有全部权限）
M "DELETE FROM sys_user_token WHERE user_id=1"
M "INSERT INTO sys_user_token (user_id,token,expiretime,updatetime) VALUES (1,'loadtest-token',2000000000,UNIX_TIMESTAMP())"
echo rods=$(M "SELECT COUNT(*) FROM biz_lifting_rod")
echo convs=$(M "SELECT COUNT(*) FROM biz_converter")
echo sample=$(M "SELECT CONCAT(converter_sn,' ',converter_ip,':',converter_port,' rod=',rod_id) FROM biz_converter ORDER BY converter_id LIMIT 2")
echo DONE
