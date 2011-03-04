#!/bin/bash
echo build x10dt... command line ant 
export ANT_OPTS="-Xmx512m"
ant -propertyfile build.ant.properties -f exportUpdate.xml 
