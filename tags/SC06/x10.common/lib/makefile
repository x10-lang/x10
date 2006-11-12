# This makefile compiles the native libraries AND
# installs them in ~/lib/.  The assumption is that 
# you have set LD_LIBRARY_PATH=$HOME/lib or something
# like that.
# Oh, and I'm not sure which C files were supposed to go
# into which SO files, so please fix the mappings if there
# is something wrong or missing.

all: libBasicExtern1.so libJacobiExtern.so libIntArrayExternUnsafe.so libRandomAccessLong.so
clean:
	rm -f *.class *.so *.o *.so.1 *.so.1.0 
libBasicExtern1.so: BasicExternTest1Impl.o
	gcc -shared -Wl,-soname,libBasicExtern1.so.1 -o libBasicExtern1.so.1.0 BasicExternTest1Impl.o
	rm -f libBasicExtern1.so.1
	rm -f libBasicExtern1.so
	ln -s libBasicExtern1.so.1.0 libBasicExtern1.so.1
	ln -s libBasicExtern1.so.1 libBasicExtern1.so
	cp libBasicExtern1.so* ~/lib
libJacobiExtern.so: JacobiExternImpl.o
	gcc -shared -Wl,-soname,libJacobiExtern.so.1 -o libJacobiExtern.so.1.0 JacobiExternImpl.o
	rm -f libJacobiExtern.so.1
	rm -f libJacobiExtern.so
	ln -s libJacobiExtern.so.1.0 libJacobiExtern.so.1
	ln -s libJacobiExtern.so.1 libJacobiExtern.so
	cp libJacobiExtern.so* ~/lib
libIntArrayExternUnsafe.so: IntArrayExternUnsafeImpl.o 
	gcc -shared -Wl,-soname,libIntArrayExternUnsafe.so.1 -o libIntArrayExternUnsafe.so.1.0 IntArrayExternUnsafeImpl.o 
	rm -f libIntArrayExternUnsafe.so.1
	rm -f libIntArrayExternUnsafe.so
	ln -s libIntArrayExternUnsafe.so.1.0 libIntArrayExternUnsafe.so.1
	ln -s libIntArrayExternUnsafe.so.1 libIntArrayExternUnsafe.so
	cp libIntArrayExternUnsafe.so* ~/lib

libRandomAccessLong.so: RandomAccess_longExternImpl.o
	g++ -shared -Wl,-soname,libRandomAccessLong.so.1 -o libRandomAccessLong.so.1.0 RandomAccess_longExternImpl.o
	rm -f libRandomAccessLong.so.1
	rm -f libRandomAccessLong.so
	ln -s libRandomAccessLong.so.1.0 libRandomAccessLong.so.1
	ln -s libRandomAccessLong.so.1 libRandomAccessLong.so
	cp libRandomAccessLong.so* ~/lib


# Tell versions [3.59,3.63) of GNU make to not export all variables.
# Otherwise a system limit (for SysV at least) may be exceeded.
.NOEXPORT:
