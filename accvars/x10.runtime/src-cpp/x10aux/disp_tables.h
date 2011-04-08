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

#ifndef X10AUX_DISP_TABLES_H
#define X10AUX_DISP_TABLES_H

#include <x10aux/config.h>

#include <stdlib.h>

namespace x10aux {

    /**
     * This class is use to construct our own lazily-growing dispatch-tables.
     * F is expected to be a pointer to a member function.
     * The add method is only called during C++ static initialziation
     * (which is single threaded), therefore we do not need to synchronize.
     */
    template <typename F> class dispatcher {
        F* _p;
        int _s;
        F& grow(int s) { return ((_s <= s) ? (_p = (F*)x10aux::system_realloc(_p, (_s = s+1)*sizeof(F))) : _p)[s]; }
    public:
    dispatcher() : _p(NULL), _s(0) { }
        F get(int i) const { return _p[i]; }
        template <typename F2> void add(F2 v) { int s = _s; grow(s) = (F)v; }
        int last() { return _s-1; }
    };
}

#endif
