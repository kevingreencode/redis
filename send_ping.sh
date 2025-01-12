#!/bin/bash
echo -e "*1\r\n$4\r\nPING\r\n" | nc localhost 6379
