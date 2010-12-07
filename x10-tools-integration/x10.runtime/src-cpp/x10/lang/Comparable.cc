#include <x10/lang/Comparable.h>

x10aux::RuntimeType x10::lang::Comparable<void>::rtt;

#define COMPARABLE_RTT_CODE(PRIM) \
x10aux::RuntimeType x10::lang::Comparable<PRIM>::rtt; \
void x10::lang::Comparable<PRIM>::_initRTT() { \
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Comparable<void> >(); \
    if (rtt.initStageOne(canonical)) return; \
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()}; \
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<PRIM>()}; \
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant}; \
    const char *baseName = "x10.lang.Comparable"; \
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances); \
}

COMPARABLE_RTT_CODE(x10_boolean)
COMPARABLE_RTT_CODE(x10_byte)
COMPARABLE_RTT_CODE(x10_ubyte)
COMPARABLE_RTT_CODE(x10_short)
COMPARABLE_RTT_CODE(x10_ushort)
COMPARABLE_RTT_CODE(x10_char)
COMPARABLE_RTT_CODE(x10_int)
COMPARABLE_RTT_CODE(x10_uint)
COMPARABLE_RTT_CODE(x10_float)
COMPARABLE_RTT_CODE(x10_long)
COMPARABLE_RTT_CODE(x10_ulong)
COMPARABLE_RTT_CODE(x10_double)
