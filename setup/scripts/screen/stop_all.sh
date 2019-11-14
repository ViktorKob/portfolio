#!/usr/bin/env bash
screen -S Proxy -X kill
screen -S Infrastructure -X kill
screen -S Admin -X kill
screen -S Analytics -X kill
screen -S Render -X kill
screen -S Legal -X kill
screen -S HbaseIndex -X kill
screen -S UsageData -X kill
screen -S Nexus -X kill 
