#!/bin/bash

#JAVA_OPTS="-Dlog4j.configuration=/jsr/dist/ds3/dev/config/log4j.config.dev.xml"
# previous version not employing HDF Java Object
JAVA_OPTS="-Djava.library.path=/jsr/dist/hdf-java/lib/linux -Dlog4j.debug=false -Dlog4j.configuration=ds3.log4j.config.prd.xml -cp $DS3_DIR/ds3-1.0-SNAPSHOT-jar-with-dependencies.jar"

#JAVA_OPTS="-Dlog4j.debug=false -Dlog4j.configuration=ds3.log4j.config.dev.xml -cp target/ds3-1.0-SNAPSHOT-jar-with-dependencies.jar"

#JAVA_OPTS="-Djava.library.path=/usr/lib -Dlog4j.debug=false -Dlog4j.configuration=log4j.config.dev.xml -cp target/ds3-1.0-SNAPSHOT-jar-with-dependencies.jar"

#CMDLINE="java $JAVA_OPTS -cp /jsr/dist/ds3/dev/jar/ds3.jar nz.co.jsrsolutions.ds3.DataScraper3"
CMDLINE="java $JAVA_OPTS nz.co.jsrsolutions.ds3.DataScraper3 -environment dev"
echo $LD_LIBRARY_PATH
echo $CMDLINE

$CMDLINE "$@"
