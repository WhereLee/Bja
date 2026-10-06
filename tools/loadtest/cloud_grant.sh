#!/bin/bash
M(){ sudo mysql inteink_loadtest -N -e "$1"; }
M "UPDATE sys_menu SET menu_perms=CONCAT(menu_perms,',biz:liftingrod:list') WHERE menu_id=119"
echo grant_done=$(M "SELECT menu_perms FROM sys_menu WHERE menu_id=119")
