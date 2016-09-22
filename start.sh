rm -f *.Rout
nohup target/universal/stage/bin/play-r  -J-Xms128M -J-Xmx512m -J-server   > /dev/null 2>&1 & echo $! > RUNNING_PID
