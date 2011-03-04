#ifndef __PRIORITYQUEUE_H
#define __PRIORITYQUEUE_H

#include <x10rt.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
namespace x10 { namespace lang { 
class Boolean;
} } 
#include <x10/lang/Boolean.struct_h>
namespace x10 { namespace lang { 
class Int;
} } 
#include <x10/lang/Int.struct_h>
namespace x10 { namespace lang { 
class NullPointerException;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10 { namespace compiler { 
class Native;
} } 
namespace x10 { namespace compiler { 
class NonEscaping;
} } 
#include <PriorityQueue.struct_h>

template<class FMGL(T)> class PriorityQueue_methods  {
    public:
    static void _instance_init(PriorityQueue<FMGL(T)>& this_);
    
    static void _constructor(PriorityQueue<FMGL(T)>& this_) {
        {
         
        }
        
    }
    inline static PriorityQueue<FMGL(T)> _make() {
        PriorityQueue<FMGL(T)> this_; 
        _constructor(this_);
        return this_;
    }
    
    static void _constructor(PriorityQueue<FMGL(T)>& this_, PriorityQueue<FMGL(T)> x)
    {
        {
         
        }
        
    }
    inline static PriorityQueue<FMGL(T)> _make(
             PriorityQueue<FMGL(T)> x)
    {
        PriorityQueue<FMGL(T)> this_; 
        _constructor(this_, x);
        return this_;
    }
    
    static x10_boolean
      empty(
      PriorityQueue<FMGL(T)> this_);
    static x10_int
      size(
      PriorityQueue<FMGL(T)> this_);
    static FMGL(T)
      top(
      PriorityQueue<FMGL(T)> this_);
    static void
      push(
      PriorityQueue<FMGL(T)> this_, FMGL(T) element);
    static void
      pop(
      PriorityQueue<FMGL(T)> this_);
    static x10aux::ref<x10::lang::String>
      toString(
      PriorityQueue<FMGL(T)> this_);
    static x10_int
      hashCode(
      PriorityQueue<FMGL(T)> this_);
    static x10_boolean
      equals(
      PriorityQueue<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other);
    static x10_boolean
      equals(
      PriorityQueue<FMGL(T)> this_, PriorityQueue<FMGL(T)> other);
    static x10_boolean
      _struct_equals(
      PriorityQueue<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other);
    static x10_boolean
      _struct_equals(
      PriorityQueue<FMGL(T)> this_, PriorityQueue<FMGL(T)> other);
    
};
template <> class PriorityQueue_methods<void> {
    public:
    
};
#endif // PRIORITYQUEUE_H

template<class FMGL(T)> class PriorityQueue;

#ifndef PRIORITYQUEUE_H_NODEPS
#define PRIORITYQUEUE_H_NODEPS
#include <x10/lang/Any.h>
#include <x10/lang/Any.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Int.h>
#include <x10/lang/NullPointerException.h>
#include <x10/lang/String.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#ifndef PRIORITYQUEUE_H_GENERICS
#define PRIORITYQUEUE_H_GENERICS
#endif // PRIORITYQUEUE_H_GENERICS
#ifndef PRIORITYQUEUE_H_IMPLEMENTATION
#define PRIORITYQUEUE_H_IMPLEMENTATION
#include <PriorityQueue.h>


#include "PriorityQueue.inc"

template<class FMGL(T)> class PriorityQueue_ithunk0 : public PriorityQueue<FMGL(T)> {
public:
    static x10::lang::Any::itable<PriorityQueue_ithunk0<FMGL(T)> > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return PriorityQueue_methods<FMGL(T)>::equals(*this, arg0);
    }
    x10_int hashCode() {
        return PriorityQueue_methods<FMGL(T)>::hashCode(*this);
    }
    x10aux::ref<x10::lang::String> toString() {
        return PriorityQueue_methods<FMGL(T)>::toString(*this);
    }
    x10aux::ref<x10::lang::String> typeName() {
        return x10aux::type_name(*this);
    }
    
};
template<class FMGL(T)> x10::lang::Any::itable<PriorityQueue_ithunk0<FMGL(T)> >  PriorityQueue_ithunk0<FMGL(T)>::itable(&PriorityQueue_ithunk0<FMGL(T)>::equals, &PriorityQueue_ithunk0<FMGL(T)>::hashCode, &PriorityQueue_ithunk0<FMGL(T)>::toString, &PriorityQueue_ithunk0<FMGL(T)>::typeName);
template<class FMGL(T)> class PriorityQueue_iboxithunk0 : public x10::lang::IBox<PriorityQueue<FMGL(T)> > {
public:
    static x10::lang::Any::itable<PriorityQueue_iboxithunk0<FMGL(T)> > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return PriorityQueue_methods<FMGL(T)>::equals(this->value, arg0);
    }
    x10_int hashCode() {
        return PriorityQueue_methods<FMGL(T)>::hashCode(this->value);
    }
    x10aux::ref<x10::lang::String> toString() {
        return PriorityQueue_methods<FMGL(T)>::toString(this->value);
    }
    x10aux::ref<x10::lang::String> typeName() {
        return x10aux::type_name(this->value);
    }
    
};
template<class FMGL(T)> x10::lang::Any::itable<PriorityQueue_iboxithunk0<FMGL(T)> >  PriorityQueue_iboxithunk0<FMGL(T)>::itable(&PriorityQueue_iboxithunk0<FMGL(T)>::equals, &PriorityQueue_iboxithunk0<FMGL(T)>::hashCode, &PriorityQueue_iboxithunk0<FMGL(T)>::toString, &PriorityQueue_iboxithunk0<FMGL(T)>::typeName);
template<class FMGL(T)> x10aux::itable_entry PriorityQueue<FMGL(T)>::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &PriorityQueue_ithunk0<FMGL(T)>::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<PriorityQueue<FMGL(T)> >())};
template<class FMGL(T)> x10aux::itable_entry PriorityQueue<FMGL(T)>::_iboxitables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &PriorityQueue_iboxithunk0<FMGL(T)>::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<PriorityQueue<FMGL(T)> >())};
template<class FMGL(T)> void PriorityQueue_methods<FMGL(T)>::_instance_init(PriorityQueue<FMGL(T)>& this_) {
    _I_("Doing initialisation for class: PriorityQueue<FMGL(T)>");
    
}


//#line 12 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10ConstructorDecl_c


//#line 15 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10ConstructorDecl_c


//#line 23 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10_boolean PriorityQueue_methods<FMGL(T)>::empty(
  PriorityQueue<FMGL(T)> this_) {
    
    //#line 23 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return false;
    
}

//#line 27 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10_int PriorityQueue_methods<FMGL(T)>::size(PriorityQueue<FMGL(T)> this_) {
    
    //#line 27 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return ((x10_int)0);
    
}

//#line 31 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> FMGL(T) PriorityQueue_methods<FMGL(T)>::top(PriorityQueue<FMGL(T)> this_) {
    
    //#line 31 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::NullPointerException::_make()));
}

//#line 35 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> void PriorityQueue_methods<FMGL(T)>::push(PriorityQueue<FMGL(T)> this_, FMGL(T) element) {
 
}

//#line 39 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> void PriorityQueue_methods<FMGL(T)>::pop(PriorityQueue<FMGL(T)> this_) {
 
}

//#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c

//#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10aux::ref<x10::lang::String> PriorityQueue_methods<FMGL(T)>::toString(
  PriorityQueue<FMGL(T)> this_) {
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return x10aux::string_utils::lit("struct PriorityQueue");
    
}

//#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10_int PriorityQueue_methods<FMGL(T)>::hashCode(
  PriorityQueue<FMGL(T)> this_) {
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10LocalDecl_c
    x10_int result = ((x10_int)0);
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return result;
    
}

//#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10_boolean PriorityQueue_methods<FMGL(T)>::equals(
  PriorityQueue<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other) {
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10If_c
    if ((!(x10aux::instanceof<PriorityQueue<FMGL(T)> >(other)))) {
        
        //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return PriorityQueue_methods<FMGL(T)>::equals(this_, x10aux::class_cast<PriorityQueue<FMGL(T)> >(other));
    
}

//#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10_boolean PriorityQueue_methods<FMGL(T)>::equals(
  PriorityQueue<FMGL(T)> this_, PriorityQueue<FMGL(T)> other) {
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10_boolean PriorityQueue_methods<FMGL(T)>::_struct_equals(
  PriorityQueue<FMGL(T)> this_, x10aux::ref<x10::lang::Any> other) {
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10If_c
    if ((!(x10aux::instanceof<PriorityQueue<FMGL(T)> >(other))))
    {
        
        //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return PriorityQueue_methods<FMGL(T)>::_struct_equals(this_, 
             x10aux::class_cast<PriorityQueue<FMGL(T)> >(other));
    
}

//#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10MethodDecl_c
template<class FMGL(T)> x10_boolean PriorityQueue_methods<FMGL(T)>::_struct_equals(
  PriorityQueue<FMGL(T)> this_, PriorityQueue<FMGL(T)> other) {
    
    //#line 10 "/Users/pkambadu/Projects/BC/src/PriorityQueue.x10": x10.ast.X10Return_c
    return true;
    
}
template<class FMGL(T)> void PriorityQueue<FMGL(T)>::_serialize(PriorityQueue<FMGL(T)> this_, x10aux::serialization_buffer& buf) {
    
}

template<class FMGL(T)> void PriorityQueue<FMGL(T)>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    
}


template<class FMGL(T)> x10_boolean PriorityQueue<FMGL(T)>::equals(x10aux::ref<x10::lang::Any> that) {
    return PriorityQueue_methods<FMGL(T)>::equals(*this, that);
}
template<class FMGL(T)> x10_boolean PriorityQueue<FMGL(T)>::equals(PriorityQueue<FMGL(T)> that) {
    return PriorityQueue_methods<FMGL(T)>::equals(*this, that);
}
template<class FMGL(T)> x10_boolean PriorityQueue<FMGL(T)>::_struct_equals(x10aux::ref<x10::lang::Any> that) {
    return PriorityQueue_methods<FMGL(T)>::_struct_equals(*this, that);
}
template<class FMGL(T)> x10_boolean PriorityQueue<FMGL(T)>::_struct_equals(PriorityQueue<FMGL(T)> that) {
    return PriorityQueue_methods<FMGL(T)>::_struct_equals(*this, that);
}
template<class FMGL(T)> x10aux::ref<x10::lang::String> PriorityQueue<FMGL(T)>::toString() {
    return PriorityQueue_methods<FMGL(T)>::toString(*this);
}
template<class FMGL(T)> x10_int PriorityQueue<FMGL(T)>::hashCode() {
    return PriorityQueue_methods<FMGL(T)>::hashCode(*this);
}
template<class FMGL(T)> x10aux::RuntimeType PriorityQueue<FMGL(T)>::rtt;
template<class FMGL(T)> void PriorityQueue<FMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<PriorityQueue<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<FMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "PriorityQueue";
    rtt.initStageTwo(baseName, 2, parents, 1, params, variances);
}
#endif // PRIORITYQUEUE_H_IMPLEMENTATION
#endif // __PRIORITYQUEUE_H_NODEPS
