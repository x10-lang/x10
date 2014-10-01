#!/bin/bash

# exit immediately on any error.
set -e 

hosts="serenity.watson.ibm.com triloka1.pok.ibm.com bellatrix.watson.ibm.com p7ih bgqfen1.watson.ibm.com"

x10dt_hosts="serenity.watson.ibm.com triloka1.pok.ibm.com bellatrix.watson.ibm.com"

# TODO: we should get svn info by parsing svn info URL and extracting revision from there.
while [ $# != 0 ]; do
  case $1 in
    -version)
        version=$2
	shift
    ;;

    -tag)
        tag=$2
	shift
    ;;

    -hosts)
        hosts=$2
	shift
    ;;

    -x10dt_hosts)
        hosts=$x10dt_hosts
    ;;

    -nodebug)
	debug_arg="-nodebug"
    ;;

    -skip_source)
        pushed_source="done"       
    ;;

   esac
   shift
done

if [[ -z "$version" ]]; then
    echo "usage: $0 must give version name -version <version name>"
    exit 1
fi

if [[ -z "$tag" ]]; then
    echo "usage: $0 must give SF tag name -tag <tag>"
    exit 1
fi


ssh orquesta.pok.ibm.com "mkdir -p /var/www/localhost/htdocs/x10dt/x10-rc-builds/$version"

for host in $hosts
do
    echo "Launching buildRelease.sh on $host"
    scp buildRelease.sh $host:/tmp 
    ssh $host "bash -l -c 'cd /tmp; ./buildRelease.sh -dir /tmp/x10-rc-$USER -version $version -tag $tag $debug_arg'"
    ssh $host "(cd /tmp; rm ./buildRelease.sh)"
    echo "transfering binary build from $host to orquesta"
    scp "$host:/tmp/x10-rc-$USER/x10-$version/x10.dist/x10-$version*.tgz" "orquesta.pok.ibm.com:/var/www/localhost/htdocs/x10dt/x10-rc-builds/$version/"

    if [[ -z "$pushed_source" ]]; then
	echo "transfering source build and testsuite from $host to orquesta"
	scp "$host:/tmp/x10-rc-$USER/x10-$version/x10-$version*.tar.bz2" "orquesta.pok.ibm.com:/var/www/localhost/htdocs/x10dt/x10-rc-builds/$version/"
	echo "Packaging benchmarks"
	./packageBenchmarks.sh -dir /tmp/x10-bench-$USER -version $version -tag $tag
	echo "transfering benchmarks tar to orquesta"
	scp "/tmp/x10-bench-$USER/x10-benchmarks-$version.tar.bz2" "x10dt@orquesta.pok.ibm.com:/var/www/localhost/htdocs/x10dt/x10-rc-builds/$version/"

	export pushed_source="done"
    fi

    ssh $host "rm -rf /tmp/x10-rc-$USER"
done
