#include <x10aux/config.h>
#include <x10aux/alloc.h>

#include <x10/lang/Any.h>
#include <x10/lang/Ref.h>

x10aux::RuntimeType x10::lang::Any::rtt;

void x10::lang::Any::_initRTT() {
    rtt.canonical = &(Any::rtt);
    rtt.init(&rtt, "x10.lang.Any", 0, NULL, 0, NULL, NULL);
}

// vim:tabstop=4:shiftwidth=4:expandtab
