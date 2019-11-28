#!/bin/sh
source /etc/profile

BASEDIR="$( cd "$( dirname "$0" )"&& cd .. && pwd )"
CLASSPATH=$BASEDIR/conf

for i in `ls $BASEDIR/lib/*`
do
  CLASSPATH=${CLASSPATH}:${i}
done

nohup java -cp $CLASSPATH com.cvnavi.scheduler.web.WebApplication > /dev/null 2>&1 &
