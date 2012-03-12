/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <x10aux/debug.h>
#include <stdio.h>

#if defined(DEBUG_SUPPORT)
// Hook to trigger loading of FD2 component of debugger.

#if defined (__linux__) && !defined(__bgp__) 
class FDFDLoader {
public:
    FDFDLoader();
};

static FDFDLoader dummy;

#include <dlfcn.h>

FDFDLoader::FDFDLoader() {
    void *handle;

    handle = dlopen("libderdFD2.so", RTLD_NOW);
    if (!handle) {
        fprintf(stderr, "FD2 lib not loaded (continuing): %s\n", dlerror());
    }
}

#endif // __linux__ && !__bgp__

#endif // DEBUG_SUPPORT


// vim:tabstop=4:shiftwidth=4:expandtab
