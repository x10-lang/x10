#!/bin/bash

#Yoav's runner = RunTestSuite.  It's invoked with essentially the same flags as "java" in x10c.

prog="$(readlink "$0" 2>&1)"
[ $? -eq 127 -o "$prog" = "" ] && prog="$0"
TOP="$(cd "$(dirname "$prog")/../.." && pwd)"
if [[ "$UNAME" = CYGWIN* ]]; then PATH="$PATH:$TOP/lib"; TOP="$(cygpath -am "$TOP")"; fi

[ -n "$JAVA_HOME" ] || JAVA_HOME='/usr/lib/jvm/java-6-sun'
[ -n "$JRE_HOME" ] || JRE_HOME='/usr/lib/jvm/java-6-sun-1.6.0.26/jre'
if [[ "$UNAME" = CYGWIN* ]]; then JAVA_HOME="$(cygpath -am "$JAVA_HOME")"; JRE_HOME="$(cygpath -am "$JRE_HOME")"; fi
if [ -n "$JAVA_HOME" -a -e "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
    JAVAC="$JAVA_HOME/bin/javac"
elif [ -n "$JRE_HOME" -a -e "$JRE_HOME/bin/java" ]; then
    JAVA="$JRE_HOME/bin/java"
    JAVAC="$JRE_HOME/bin/javac"
else
    echo "JAVA_HOME ($JAVA_HOME) is not pointing to a JRE or a JDK"
    exit 1
fi
if [[ "$UNAME" = CYGWIN* ]]; then JAVA="$(cygpath -au "$JAVA")"; fi

if [ "$1" == "" ] ; then
    TESTS="$TOP/x10.tests"
else
    TESTS="$(pwd)/$1"
fi

#absolute path

TEST_DIR=$(mktemp -d)

cd "$TEST_DIR"

echo "Using temporary dir: \"$TEST_DIR\""
echo "Using local X10 svn checkout: \"$TOP\""

echo "Compiling the frontend test suite architecture"

"$JAVAC" -cp "$TOP/x10.dist/lib/x10c.jar:../x10.dist/lib/lpg.jar" -d . "$TOP/x10.compiler/src/x10/util/RunTestSuite.java"

echo "Running the tests..."

export X10_DIST="$TOP/x10.dist"

time nice "$JAVA" \
    -Xmx1G \
    -ea \
    -cp "$TOP:$X10_DIST/lib/x10c.jar:$X10_DIST/lib/lpg.jar" \
    -Dx10.dist="$TOP/x10.dist" \
    x10.util.RunTestSuite \
    "$TESTS" \
    -extclass x10c.ExtensionInfo \
    -d out \
    -sourcepath "$X10_DIST/stdlib/x10.jar" \
    -sourcepath "$TOP/x10.tests/tests/x10lib" \
    -sourcepath "$X10_DIST/samples" \
    -sourcepath "$X10_DIST/samples/tutorial" \
    -sourcepath "$X10_DIST/samples/CUDA" \
    -sourcepath "$X10_DIST/samples/work-stealing" \
    -sourcepath "$TOP/x10.tests/tests-inactive/Annotations/dims"

echo "Done."
