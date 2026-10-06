#!/bin/bash
cd /home/ubuntu
Q(){ sudo mysql inteink_loadtest -N -e "$1"; }
echo "users=$(Q 'SELECT COUNT(*) FROM sys_user')"
echo "roles=$(Q 'SELECT COUNT(*) FROM sys_role')"
echo "user_role=$(Q 'SELECT COUNT(*) FROM sys_user_role')"
echo "menus=$(Q 'SELECT COUNT(*) FROM sys_menu')"
echo "biz_menus=$(Q 'SELECT COUNT(*) FROM sys_menu WHERE perms LIKE \"biz:%\"')"
echo "u1_roles=$(Q 'SELECT role_id FROM sys_user_role WHERE user_id=1')"
echo "rods=$(Q 'SELECT COUNT(*) FROM biz_lifting_rod')"
echo "convs=$(Q 'SELECT COUNT(*) FROM biz_converter')"