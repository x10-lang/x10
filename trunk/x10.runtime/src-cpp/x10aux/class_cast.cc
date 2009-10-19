#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/class_cast.h>
#include <x10aux/throw.h>

#include <x10/lang/ClassCastException.h>

using namespace x10aux;
using namespace x10::lang;

void x10aux::throwClassCastException() {
    throwException<x10::lang::ClassCastException>();
}

// Box primitives on casting to interfaces
#define PRIMITIVE_INTERFACE_CAST(T,F) \
  GPUSAFE ref<T> ClassCastNotBothRef<ref<T>,F>::_ (F obj, bool checked) { \
    _CAST_(TYPENAME(F) <<" converted to "<<TYPENAME(ref<T>)); \
    return class_cast<ref<T> >(box(obj), checked); \
  }
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_boolean)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_byte)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_char)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_short)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_int)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_long)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_ubyte)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_ushort)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_uint)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_ulong)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_float)
PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_double)

PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_boolean)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_byte)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_char)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_short)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_int)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_long)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_ubyte)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_ushort)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_uint)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_ulong)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_float)
PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_double)

// Unbox primitives on down-casting from interfaces
#define INTERFACE_PRIMITIVE_CAST(T,F) \
  GPUSAFE T ClassCastNotBothRef<T,ref<F> >::_ (ref<F> obj, bool checked) { \
    _CAST_(TYPENAME(ref<F>) <<" converted to "<<TYPENAME(T)); \
    return unbox(class_cast<ref<x10::lang::Box<T> > >(obj, checked)); \
  }
INTERFACE_PRIMITIVE_CAST(x10_boolean, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_byte, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_char, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_short, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_int, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_long, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_float, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_double, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_ubyte, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_ushort, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_uint, x10::lang::Object)
INTERFACE_PRIMITIVE_CAST(x10_ulong, x10::lang::Object)

#define PRIMITIVE_VALUE_CAST(P) \
ref<x10::lang::Value> ClassCastNotBothRef<ref<x10::lang::Value>,P>::_(P obj, bool checked) { \
    _CAST_("converted to value: "<<CAST_TRACER<P>(obj)<<" of type "<<TYPENAME(P)); \
    return ref<x10::lang::Value>(new (x10aux::alloc<ValueBox<P> >()) ValueBox<P>(obj)); \
}
#define VALUE_PRIMITIVE_CAST(P) \
P ClassCastNotBothRef<P,ref<x10::lang::Value> >::_(ref<x10::lang::Value> obj, bool checked) { \
    _CAST_("converted from value: "<<CAST_TRACER<ref<x10::lang::Value> >(obj)<<" of type "<<TYPENAME(P)); \
    return ref<ValueBox<P> >(obj)->v; \
}
#define PRIMITIVE_VALUE_CAST2(P) \
    PRIMITIVE_VALUE_CAST(P) \
    VALUE_PRIMITIVE_CAST(P)
PRIMITIVE_VALUE_CAST2(x10_boolean)
PRIMITIVE_VALUE_CAST2(x10_byte)
PRIMITIVE_VALUE_CAST2(x10_char)
PRIMITIVE_VALUE_CAST2(x10_short)
PRIMITIVE_VALUE_CAST2(x10_int)
PRIMITIVE_VALUE_CAST2(x10_long)
PRIMITIVE_VALUE_CAST2(x10_float)
PRIMITIVE_VALUE_CAST2(x10_double)
PRIMITIVE_VALUE_CAST2(x10_ubyte)
PRIMITIVE_VALUE_CAST2(x10_ushort)
PRIMITIVE_VALUE_CAST2(x10_uint)
PRIMITIVE_VALUE_CAST2(x10_ulong)

// vim:tabstop=4:shiftwidth=4:expandtab
