#!/usr/bin/env bash
SCRIPTDIR="$(cd "$(dirname "$0")" && pwd)"

BINDIR=$SCRIPTDIR/bin
[[ -d "$BINDIR" ]] || mkdir -p "$BINDIR"

echo "Compile with Javac"
javac -d $BINDIR $SCRIPTDIR/src-java/*.java
echo "Run with Java"
java -cp $BINDIR InterfaceMethodsJava


echo "Compile with X10c"
x10c -cp $BINDIR -d $BINDIR $SCRIPTDIR/src-x10/*.x10
echo "Run with X10"
x10 -cp $BINDIR InterfaceMethodsX10
