#!/bin/bash


JAVA_PROPS="-Djava.library.path=/jsr/dist/hdf-java/lib/linux -Dlog4j.debug=false -Dlog4j.configuration=ds3.log4j.config.prd.xml"

JAVA_OPTS="-XX:-OmitStackTraceInFastThrow -cp $DS3_DIR/ds3-1.0-SNAPSHOT.jar"

CMDLINE="java $JAVA_OPTS $JAVA_PROPS nz.co.jsrsolutions.ds3.DataScraper3 -environment dev"

echo $LD_LIBRARY_PATH
echo $CMDLINE

$CMDLINE "$@"
