================================================================================
DIRECTORIES
================================================================================

We now have a directory structure:

x10/package/File.{cc,h}  -  for c++ classes whose interfaces are compatible
                            with the x10/c++ object model

x10aux/                  -  for auxiliary utilities needed to implement the
                            above



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


