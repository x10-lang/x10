#!/usr/bin/env bash
SCRIPTDIR="$(cd "$(dirname "$0")" && pwd)"

BINDIR=$SCRIPTDIR/bin
[[ -d "$BINDIR" ]] || mkdir -p "$BINDIR"

echo "Compile with X10c"
x10c -d $BINDIR $SCRIPTDIR/src-x10/*.x10
echo "Run with X10"
x10 -cp $BINDIR FunctionalInterfaceX10


echo "Compile with Javac"
x10cj -cp $BINDIR -d $BINDIR $SCRIPTDIR/src-java/*.java
echo "Run with Java"
runjava -cp $BINDIR FunctionalInterfaceJava
