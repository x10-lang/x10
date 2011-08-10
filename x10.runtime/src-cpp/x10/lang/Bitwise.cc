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

#include <x10/lang/Bitwise.h>

x10aux::RuntimeType x10::lang::Bitwise<void>::rtt;

#define BITWISE_RTT_CODE(PRIM) \
x10aux::RuntimeType x10::lang::Bitwise<PRIM>::rtt; \
void x10::lang::Bitwise<PRIM>::_initRTT() { \
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Bitwise<void> >(); \
    if (rtt.initStageOne(canonical)) return; \
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()}; \
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<PRIM>()}; \
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant}; \
    const char *baseName = "x10.lang.Bitwise"; \
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances); \
}

BITWISE_RTT_CODE(x10_byte)
BITWISE_RTT_CODE(x10_ubyte)
BITWISE_RTT_CODE(x10_short)
BITWISE_RTT_CODE(x10_ushort)
BITWISE_RTT_CODE(x10_int)
BITWISE_RTT_CODE(x10_uint)
BITWISE_RTT_CODE(x10_long)
BITWISE_RTT_CODE(x10_ulong)

