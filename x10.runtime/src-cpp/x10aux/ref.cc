#include <x10aux/config.h>

#include <x10aux/ref.h>
#include <x10aux/network.h>
#include <x10aux/throw.h>
#include <x10/lang/NullPointerException.h>
#include <x10/lang/BadPlaceException.h>
#include <x10aux/reference_logger.h>


using namespace x10aux;
using namespace x10::lang;

// do not call this if NO_EXCEPTIONS is defined
// defined here because it depends on NullPointerException and we don't want a header cycle
void x10aux::throwNPE() { throwException<NullPointerException>(); }

void x10aux::throwBPE() { throwException<BadPlaceException>(); }

x10aux::place x10aux::location(x10aux::ref<x10::lang::Object> obj) {
    return obj->location;
}

//remote_ref remote_ref::make (void *ptr, bool immortalize) {
//    if (remote_ref::is_remote(ptr)) return *strip(ptr);
//    #if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
//    if (immortalize) {
//        ReferenceLogger::log(ptr);
//    }
//    #endif
//    remote_ref r = { x10aux::here, (size_t)ptr };
//    return r;
//}

//void *remote_ref::take (remote_ref r) {
//    if (r.loc==x10aux::here) return (void*)(size_t)r.addr;
//    if (r.addr==0) return NULL;
//    return mask(new remote_ref(r));
//}

//x10_int x10aux::location (void *ptr) {
//    if (remote_ref::is_remote(ptr)) return remote_ref::strip(ptr)->loc;
//    return x10aux::here;
//}

//bool remote_ref::equals (void *ptr1, void *ptr2) {
//    if (!remote_ref::is_remote(ptr1) || !remote_ref::is_remote(ptr2))
//        return false;
//    remote_ref *r1 = remote_ref::strip(ptr1);
//    remote_ref *r2 = remote_ref::strip(ptr2);
//    return r1->loc==r2->loc && r1->addr==r2->addr;
//}

// vim:tabstop=4:shiftwidth=4:expandtab
