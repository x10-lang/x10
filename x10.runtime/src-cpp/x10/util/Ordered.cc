/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 *  (C) Copyright Australian National University 2011.
 */

#include <x10/util/Ordered.h>

x10aux::RuntimeType x10::util::Ordered<void>::rtt;

#define ORDERED_RTT_CODE(PRIM) \
x10aux::RuntimeType x10::util::Ordered<PRIM>::rtt; \
void x10::util::Ordered<PRIM>::_initRTT() { \
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::util::Ordered<void> >(); \
    if (rtt.initStageOne(canonical)) return; \
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()}; \
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<PRIM>()}; \
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant}; \
    const char *baseName = "x10.util.Ordered"; \
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances); \
}

ORDERED_RTT_CODE(x10_char)
ORDERED_RTT_CODE(x10_byte)
ORDERED_RTT_CODE(x10_ubyte)
ORDERED_RTT_CODE(x10_short)
ORDERED_RTT_CODE(x10_ushort)
ORDERED_RTT_CODE(x10_int)
ORDERED_RTT_CODE(x10_uint)
ORDERED_RTT_CODE(x10_long)
ORDERED_RTT_CODE(x10_ulong)
ORDERED_RTT_CODE(x10_float)
ORDERED_RTT_CODE(x10_double)
