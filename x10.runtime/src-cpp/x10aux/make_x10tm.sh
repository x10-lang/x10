g++ -I../ -fPIC -c x10tm.cc -o x10tm.o
g++ -fPIC -dynamiclib -install_name '@rpath/libx10tm.so' -o libx10tm.so x10tm.o
cp libx10tm.so ../../../x10.dist/stdlib/lib
cp x10tm.h ../../../x10.dist/stdlib/include/x10tm.h
cp x10tm.h ../../../x10.dist/stdlib/include/x10aux/x10tm.h
