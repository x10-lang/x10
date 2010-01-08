#include <x10aux/config.h>
#include <x10aux/alloc.h>

#include <x10/lang/Object.h>
#include <x10/lang/Ref.h>

using namespace x10::lang;
using namespace x10aux;

x10aux::RuntimeType x10::lang::SomeObject::rtt;

void SomeObject::_initRTT() {
    rtt.init(&rtt, "x10.lang.Object", 0, NULL, 0, NULL, NULL);
}


itable_entry SomeObject::_itables[1] = { itable_entry(NULL,  (void*)x10aux::getRTT<SomeObject>()) };


// vim:tabstop=4:shiftwidth=4:expandtab
