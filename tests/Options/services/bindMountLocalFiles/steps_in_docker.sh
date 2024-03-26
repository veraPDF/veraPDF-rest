#!/usr/bin/env bash

ls -la /home/
apt-get update
apt-get install curl -y
cd /home/Res_tmp/
echo -e "\n"

curl -F "file=@/home/Res_tmp/a.pdf" localhost:8080/api/validate/3a > res_tmp_log.log -H 'accept: application/xml'
exit
