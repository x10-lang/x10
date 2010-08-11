#!/bin/bash

timestamp=`date +%Y%m%d%H%S`
build_folder=`pwd`/../I${timestamp:0:8}-${timestamp:8:4}
mirror_loc=${build_folder}/dependencies/repos/mirror/plugins

function get_absolute_jar_path() {
  echo `pwd`/`find ${mirror_loc} -name "$1"`
}

function usage() {
cat << EOF
usage: $0 [options]
Run X10DT build.
OPTIONS:
   -h      Show this message
   -x      Delete X10 distribution cache.
   -e      Delete Eclipse distribution cache.
   -p      Publish update site.
EOF
}

while getopts “hxep” OPTION
do
  case $OPTION in
    h)
      usage
      exit 1
      ;;
    x)
      rm -fr ../eclipse
      rm -fr ../delta-pack
      ;;
    e)
      rm -fr ../x10-build
      ;;
    p)
      publish_site=true
      ;;
  esac
done

mkdir ${build_folder}

echo "*** Download eclipse and delta pack..."
ant -q -f get-eclipse.xml
if [ $? -ne 0 ]; then
  echo -e "\nBuild process interrupted since we could not get all eclipse download to finish properly."
  exit 1
fi
launcher_jar_loc=`find ../eclipse/plugins -name "org.eclipse.equinox.launcher_*.jar"`

echo -e "\n*** Get LPG and Polyglot..."
java -Xmx512M -jar $launcher_jar_loc -application org.eclipse.ant.core.antRunner -buildfile dependencies-repo.xml -Dbuild.qualifier=${timestamp} -Dbuild.folder=${build_folder}
if [ $? -ne 0 ]; then
  echo -e "\nGetting LPG and Polyglot failed."
  exit 1
fi

echo -e "\n*** Build X10 dist..."
compiler_build_path=$(get_absolute_jar_path "polyglot3_*.jar"):$(get_absolute_jar_path "lpg.runtime_*.jar"):$(get_absolute_jar_path "lpg.runtime.java_*.jar")
ant -f x10-build.xml -Declipse.target.platform=../../eclipse -Dx10.compiler.extra.build.path=${compiler_build_path} -Dbuild.qualifier=${timestamp} -Dbuild.folder=${build_folder}

if [ $? -ne 0 ]; then
  echo -e "\nX10 dist build failed."
  exit 1
fi
echo -e "\n*** Start X10DT build..."
if [ -z "${publish_site+xxx}" ]; then
  java -Xmx512M -jar $launcher_jar_loc -application org.eclipse.ant.core.antRunner -buildfile x10dt-build.xml -Dbuilder=`pwd` -Dbuild.qualifier=${timestamp} -Dbuild.folder=${build_folder}
else
  java -Xmx512M -jar $launcher_jar_loc -application org.eclipse.ant.core.antRunner -buildfile x10dt-build.xml -Dbuilder=`pwd` -Dbuild.qualifier=${timestamp} -Dbuild.folder=${build_folder} -Dpublish.update.site=true
fi