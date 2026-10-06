#!/bin/bash
awk '/APPLICATION FAILED TO START/{f=1} f{print; c++} c>28{exit}' /home/ubuntu/app.log
