#!/bin/bash

cd src-x10
find -print0 -name '*.x10' | xargs -0 jar cf ../x10_gl.jar
cd ..

x10c++ src-x10/x10/gl/GL.x10 -o lib/libx10_gl.so -d include -post '# -shared # # -lGL -lglut -lGLEW'

cd include
find \( -\! -name '*.h' -a -\! -type d \) -print0 | xargs -0 rm
cd ..
