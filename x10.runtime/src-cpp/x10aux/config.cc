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

#include <cstdlib>
#include <cstring>

#include <x10aux/config.h>

const bool x10aux::trace_ansi_colors = getenv("X10_TRACE_ANSI_COLORS");
const bool x10aux::trace_init = getenv("X10_TRACE_INIT") || getenv("X10_TRACE_ALL");
const bool x10aux::trace_static_init = getenv("X10_TRACE_STATIC_INIT") || getenv("X10_TRACE_ALL");
const bool x10aux::trace_x10rt = getenv("X10_TRACE_X10RT") || getenv("X10_TRACE_NET") || getenv("X10_TRACE_ALL");
const bool x10aux::trace_ser = getenv("X10_TRACE_SER") || getenv("X10_TRACE_NET") || getenv("X10_TRACE_ALL");
const bool x10aux::trace_rxtx = getenv("X10_TRACE_RXTX") || getenv("X10_TRACE_NET") || getenv("X10_TRACE_ALL");

const bool x10aux::disable_dealloc = getenv("X10_DISABLE_DEALLOC");

const bool x10aux::x10__assertions_enabled = !getenv("X10_DISABLE_ASSERTIONS");

char* x10aux::get_congruent_base() {
    return getenv(ENV_CONGRUENT_BASE);
}

char* x10aux::get_congruent_size() {
    return getenv(ENV_CONGRUENT_SIZE);
}
