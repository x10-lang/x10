/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef x10aux_GL_h
#define x10aux_GL_h

#include <GL/glew.h>

#if defined(__APPLE__) || defined(MACOSX)
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

namespace x10 { namespace gl {

    template<class T> void array_hack (::x10::lang::Rail<T> arr, void *ptr)
    {
        // go straight to hell, do not pass go, do not collect $200
        arr->FMGL(raw)->data = (x10_ulong)(ptr);
    }

} }

#endif
