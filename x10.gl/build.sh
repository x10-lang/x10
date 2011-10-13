#!/bin/bash

cd src-x10
find -print0 -name '*.x10' | xargs -0 jar cf ../x10_gl.jar
cd ..

../x10.dist/bin/x10c++ -O -NO_CHECKS -STATIC_CHECKS -buildx10lib . src-x10/x10/gl/GL.x10 -o x10_gl -d include -cxx-postarg -lGL -cxx-postarg -lglut -cxx-postarg -lGLEW

cd include
find -name '*.cc'  -print0 | xargs -0 rm
cd ..
