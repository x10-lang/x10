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

#include <x10/lang/Arithmetic.h>

x10aux::RuntimeType x10::lang::Arithmetic<void>::rtt;

#define ARITHMETIC_RTT_CODE(PRIM) \
x10aux::RuntimeType x10::lang::Arithmetic<PRIM>::rtt; \
void x10::lang::Arithmetic<PRIM>::_initRTT() { \
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Arithmetic<void> >(); \
    if (rtt.initStageOne(canonical)) return; \
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()}; \
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<PRIM>()}; \
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant}; \
    const char *baseName = "x10.lang.Arithmetic"; \
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances); \
}

ARITHMETIC_RTT_CODE(x10_byte)
ARITHMETIC_RTT_CODE(x10_ubyte)
ARITHMETIC_RTT_CODE(x10_short)
ARITHMETIC_RTT_CODE(x10_ushort)
ARITHMETIC_RTT_CODE(x10_int)
ARITHMETIC_RTT_CODE(x10_uint)
ARITHMETIC_RTT_CODE(x10_long)
ARITHMETIC_RTT_CODE(x10_ulong)
ARITHMETIC_RTT_CODE(x10_float)
ARITHMETIC_RTT_CODE(x10_double)
ARITHMETIC_RTT_CODE(x10_complex)
