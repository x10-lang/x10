#ifndef __X10_UTIL_INDEXEDMEMORYCHUNK_H
#define __X10_UTIL_INDEXEDMEMORYCHUNK_H

#include <x10rt.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 { namespace lang { 
class String;
} } 
#include <x10/util/IndexedMemoryChunk.struct_h>

namespace x10 { namespace util { 

template<class FMGL(T)> class IndexedMemoryChunk_methods  {
    public:
    static void _instance_init(x10::util::IndexedMemoryChunk<FMGL(T)>& this_);
    
    static x10aux::ref<x10::lang::String> toString(x10::util::IndexedMemoryChunk<FMGL(T)> this_);
    static x10_boolean equals(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other);
    static x10_int hashCode(x10::util::IndexedMemoryChunk<FMGL(T)> this_);

    static FMGL(T) apply(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10_int index);
    static FMGL(T) apply(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10_long index);
    static void set(x10::util::IndexedMemoryChunk<FMGL(T)> this_, FMGL(T) value, x10_int index);
    static void set(x10::util::IndexedMemoryChunk<FMGL(T)> this_, FMGL(T) value, x10_long index);
    static x10aux::ref<x10::lang::String> typeName(x10::util::IndexedMemoryChunk<FMGL(T)> this_);
    static x10_boolean _struct_equals(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other);
    static x10_boolean _struct_equals(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10::util::IndexedMemoryChunk<FMGL(T)> other);
    static x10_boolean at(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10aux::ref<x10::lang::Object> obj) { return true; }
    static x10_boolean at(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10::lang::Place place) { return true; }
    static x10::lang::Place home(x10::util::IndexedMemoryChunk<FMGL(T)> this_) {
        /* FIXME: Should probably call Place_methods::make, but don't want to include Place.h */
        x10::lang::Place tmp; tmp->FMGL(id)=x10aux::here; return tmp;
    }
};

template <> class IndexedMemoryChunk_methods<void> {
    public:
    
};

} } 
#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H

namespace x10 { namespace util { 
template<class FMGL(T)> class IndexedMemoryChunk;
} } 

#ifndef X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#include <x10/lang/Any.h>
#include <x10/lang/String.h>
#ifndef X10_UTIL_INDEXEDMEMORYCHUNK_H_GENERICS
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_GENERICS
#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H_GENERICS
#ifndef X10_UTIL_INDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#include <x10/util/IndexedMemoryChunk.h>


namespace x10 { namespace util { 
template<class FMGL(T)> class IndexedMemoryChunk_ithunk0 : public x10::util::IndexedMemoryChunk<FMGL(T)> {
public:
    static x10::lang::Any::itable<IndexedMemoryChunk_ithunk0<FMGL(T)> > itable;
    x10_boolean at(x10aux::ref<x10::lang::Object> arg0) {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::at(*this, arg0);
    }
    x10_boolean at(x10::lang::Place arg0) {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::at(*this, arg0);
    }
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::equals(*this, arg0);
    }
    x10_int hashCode() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::hashCode(*this);
    }
    x10::lang::Place home() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::home(*this);
    }
    x10aux::ref<x10::lang::String> toString() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::toString(*this);
    }
    x10aux::ref<x10::lang::String> typeName() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::typeName(*this);
    }
    
};
template<class FMGL(T)> x10::lang::Any::itable<IndexedMemoryChunk_ithunk0<FMGL(T)> >  IndexedMemoryChunk_ithunk0<FMGL(T)>::itable(&IndexedMemoryChunk_ithunk0<FMGL(T)>::at, &IndexedMemoryChunk_ithunk0<FMGL(T)>::at, &IndexedMemoryChunk_ithunk0<FMGL(T)>::equals, &IndexedMemoryChunk_ithunk0<FMGL(T)>::hashCode, &IndexedMemoryChunk_ithunk0<FMGL(T)>::home, &IndexedMemoryChunk_ithunk0<FMGL(T)>::toString, &IndexedMemoryChunk_ithunk0<FMGL(T)>::typeName);

template<class FMGL(T)> class IndexedMemoryChunk_iboxithunk0 : public x10::lang::IBox<x10::util::IndexedMemoryChunk<FMGL(T)> > {
public:
    static x10::lang::Any::itable<IndexedMemoryChunk_iboxithunk0<FMGL(T)> > itable;
    x10_boolean at(x10aux::ref<x10::lang::Object> arg0) {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::at(this->value, arg0);
    }
    x10_boolean at(x10::lang::Place arg0) {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::at(this->value, arg0);
    }
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::equals(this->value, arg0);
    }
    x10_int hashCode() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::hashCode(this->value);
    }
    x10::lang::Place home() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::home(this->value);
    }
    x10aux::ref<x10::lang::String> toString() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::toString(this->value);
    }
    x10aux::ref<x10::lang::String> typeName() {
        return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::typeName(this->value);
    }
    
};

template<class FMGL(T)> x10::lang::Any::itable<IndexedMemoryChunk_iboxithunk0<FMGL(T)> >  IndexedMemoryChunk_iboxithunk0<FMGL(T)>::itable(&IndexedMemoryChunk_iboxithunk0<FMGL(T)>::at, &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::at, &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::equals, &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::hashCode, &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::home, &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::toString, &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::typeName);
} } 

template<class FMGL(T)> x10aux::itable_entry x10::util::IndexedMemoryChunk<FMGL(T)>::_itables[2] = {x10aux::itable_entry(x10aux::getRTT<x10::lang::Any>(), &IndexedMemoryChunk_ithunk0<FMGL(T)>::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::IndexedMemoryChunk<FMGL(T)> >())};

template<class FMGL(T)> x10aux::itable_entry x10::util::IndexedMemoryChunk<FMGL(T)>::_iboxitables[2] = {x10aux::itable_entry(x10aux::getRTT<x10::lang::Any>(), &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::IndexedMemoryChunk<FMGL(T)> >())};
template<class FMGL(T)> void x10::util::IndexedMemoryChunk_methods<FMGL(T)>::_instance_init(x10::util::IndexedMemoryChunk<FMGL(T)>& this_) {
    _I_("Doing initialisation for class: x10::util::IndexedMemoryChunk<FMGL(T)>");
    
}


template<class FMGL(T)> x10aux::ref<x10::lang::String> x10::util::IndexedMemoryChunk_methods<FMGL(T)>::toString(
  x10::util::IndexedMemoryChunk<FMGL(T)> this_) {
    return x10::lang::String::Lit("IndexedMemoryChunk");
    
}

template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk_methods<FMGL(T)>::equals(
    x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other) {
    return false;
}

template<class FMGL(T)> x10_int x10::util::IndexedMemoryChunk_methods<FMGL(T)>::hashCode(
  x10::util::IndexedMemoryChunk<FMGL(T)> this_) {
    return ((x10_int)0);
}

template<class FMGL(T)> x10aux::ref<x10::lang::String>
  x10::util::IndexedMemoryChunk_methods<FMGL(T)>::typeName(
  x10::util::IndexedMemoryChunk<FMGL(T)> this_) {
    return x10::lang::String::Lit("x10.util.IndexedMemoryChunk");
    
}
template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk_methods<FMGL(T)>::_struct_equals(
  x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other) {
    if ((!(x10aux::instanceof<x10::util::IndexedMemoryChunk<FMGL(T)> >(other))))
    {
        return false;
        
    }
    return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::_struct_equals(this_, 
             x10aux::class_cast<x10::util::IndexedMemoryChunk<FMGL(T)> >(other));
    
}
template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk_methods<FMGL(T)>::_struct_equals(
  x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10::util::IndexedMemoryChunk<FMGL(T)> other) {
    return (x10aux::struct_equals(this_->
                                    FMGL(data),
                                  other->
                                    FMGL(data)));
    
}
template<class FMGL(T)> void x10::util::IndexedMemoryChunk<FMGL(T)>::_serialize(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10aux::serialization_buffer& buf) {
    buf.write(this_->FMGL(data));
    
}

template<class FMGL(T)> void x10::util::IndexedMemoryChunk<FMGL(T)>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(data) = buf.read<FMGL(T)*>();
}


template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk<FMGL(T)>::equals(x10aux::ref<x10::lang::Any> that) {
    return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::equals(*this, that);
}
template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk<FMGL(T)>::equals(x10::util::IndexedMemoryChunk<FMGL(T)> that) {
    return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::equals(*this, that);
}
template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk<FMGL(T)>::_struct_equals(x10aux::ref<x10::lang::Any> that) {
    return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::_struct_equals(*this, that);
}
template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk<FMGL(T)>::_struct_equals(x10::util::IndexedMemoryChunk<FMGL(T)> that) {
    return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::_struct_equals(*this, that);
}
template<class FMGL(T)> x10aux::ref<x10::lang::String> x10::util::IndexedMemoryChunk<FMGL(T)>::toString() {
    return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::toString(*this);
}
template<class FMGL(T)> x10_int x10::util::IndexedMemoryChunk<FMGL(T)>::hashCode() {
    return x10::util::IndexedMemoryChunk_methods<FMGL(T)>::hashCode(*this);
}
template<class FMGL(T)> x10aux::RuntimeType x10::util::IndexedMemoryChunk<FMGL(T)>::rtt;

template<class FMGL(T)> void x10::util::IndexedMemoryChunk<FMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::util::IndexedMemoryChunk<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<FMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.util.IndexedMemoryChunk";
    rtt.initStageTwo(baseName, 2, parents, 1, params, variances);
}
#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#endif // __X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
