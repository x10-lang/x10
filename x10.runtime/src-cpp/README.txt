================================================================================
DIRECTORIES
================================================================================

We now have a directory structure:

x10/package/File.{cc,h}  -  for c++ classes whose interfaces are compatible
                            with the x10/c++ object model and for files
                            that provide the implementation for
                            native methods in the X10 standard library.

x10aux/                  -  for auxiliary utilities and primitive 
                            language runtime support.

================================================================================
STYLE
================================================================================

If you need to extend / construct / dispatch on / look up fields from a class,
or otherwise use it in a manner which needs a definition, you should include
the .h file for that class.

If you need to pass around pointers to a class, just declare the class
manually:

namespace x10 {
    namespace lang {
        class String;
    }
}

Header files in x10aux are included in the prelude of every compiler-generated
file; therefore one should strive to minimize the code in such files to reduce
C++ compilation time.  We try to strike a balance between compilation time 
(reduce code in header) and performance of generated code (key functions need to be
in headers so they can be inlined). 

When doing major structural changes to header files, it is important to sanity
check using several C++ compilers (and include both 4.2 and 4.7 versions of g++). 
