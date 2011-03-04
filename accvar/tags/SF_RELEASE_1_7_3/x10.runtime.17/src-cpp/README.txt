================================================================================
OVERVIEW
================================================================================

These are the new x10 c++ runtime files, they are taken from 1.5 backend
x10lang files but with many changes, including

- new runtime type system
- support new language features (Rail, ValRail, etc)
- don't support old language features that are now just libraries (distributed
  arrays)



================================================================================
DIRECTORIES
================================================================================

We now have a directory structure:

x10/package/File.{cc,h}  -  for c++ classes whose interfaces are compatible
                            with the x10/c++ object model

x10aux/                  -  for auxiliary utilities needed to implement the
                            above



================================================================================
DEPENDENCIES
================================================================================

You must get pgas from GSA, configure, and build it

cvs -d /gsa/yktgsa/projects/u/upcc/CVS co pgas
cd pgas/common
./configure --with-transport=sockets
make

Now set the X10LIB environment variable to the pgas/common/work directory if
you didn't put pgas in the same place as x10.runtime.17

If you intend to run x10 code you also need to run make in pgas/common/control

This will give you two executables: launcher/launcher and manager/manager which
you will need to launch the pgas executables that we are ultimately building.



================================================================================
COMPILING AND USING
================================================================================

There is a file in the same directory as this text file:

x10rt17.h

This file includes all the others.  This makes it easy to include the right
headers in compiler-generated files -- one just includes this and you get
everything.  I have tried to make the headers work independently, e.g. you
should just be able to #include <x10/lang/String.h> but this hasn't been tested
and there are probably missing headers, etc.  In theory it should work.

There is also a Makefile.  Running "make" should get you an archive:

libx10rt17.a

This file contains all the implementations.  It corresponds to x10rt17.h in the
sense that if you include that header you should also link to this library.

These two files therefore take the place of x10lang.h and libx10lang.a

You still need to include the $X10LIB/include dir and link to the
$X10LIB/lib/libupcrts_sockets.a when compiling executables from x10 code.

There is also a file test.cc which you can compile with "make test.o".  This
tests that x10rt17.h can be included without errors.  This does not necessarily
follow from a successful compilation of libx10rt17.a, in which not all the
headers are used.  Note, however, that since this doesn't instantiate the
templates, there can still be problems.



================================================================================
STYLE
================================================================================

The "x10" directory contains files that match the x10 package hierarchy.
Nothing should be in there that isnt x10-visible.  That stuff belongs in
x10aux.  Note that generic classes like Rail do not have a .cc file since they
are entirely implemented in the header.

If you need to extend / construct / dispatch on / look up fields from a class,
or otherwise use it in a manner which needs a definition, you should include
the .h file for that class.  It is therefore rare for a header file to include
another header file.

If you need to pass around pointers to a class, just declare the class
manually:

namespace x10 {
    namespace lang {
        class String;
    }
}

Since it is rare to put lots of code in a header (exception: templates!) it is
consequently rare to include headers from headers and this should minimise the
risk of cycles occuring in the include graph.  Such cycles would cause compile
errors (even though we are using #ifndef guards).

For example, x10::lang::Object::toString() returns a String but Object.h does
not need to #include <x10/lang/String.h> because Object.h only contains the
*declaration* of toString and thus only talks about pointers to String.

Object.cc on the other hand needs to implement toString, therefore needs to
construct a String and consequently has to #include <x10/lang/String.h>


