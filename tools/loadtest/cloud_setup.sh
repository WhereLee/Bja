#!/bin/bash
set -e
cd /home/ubuntu
echo SETUP_DB:
sudo mysql < /home/ubuntu/_loadtest_setup.sql && echo setup_ok
echo IMPORT_SCHEMA:
sudo mysql inteink_loadtest < /home/ubuntu/inteink-faster.sql && echo import_ok
echo TABLE_COUNT:
sudo mysql inteink_loadtest -N -e "SHOW TABLES" | wc -l
echo HAS_ROD:
sudo mysql inteink_loadtest -N -e "SHOW TABLES LIKE 'biz_lifting_rod'"
echo HAS_TOKEN:
sudo mysql inteink_loadtest -N -e "SHOW TABLES LIKE 'sys_user_token'"
echo DONE
