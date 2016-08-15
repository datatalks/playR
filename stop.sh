test -f target/universal/stage/RUNNING_PID && kill `cat target/universal/stage/RUNNING_PID` && sleep 5;
rm RUNNING_PID;