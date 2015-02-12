/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef __X10_LANG_GLOBALREF
#define __X10_LANG_GLOBALREF

#include <x10rt.h>

#include <x10/lang/Any.h>

namespace x10 {
    namespace lang { 
        x10_long globalref_getInitialEpoch();

        template<class T> class GlobalRef  {
            
          public:
            RTT_H_DECLS_STRUCT

            static ::x10aux::itable_entry _itables[2];
            static ::x10::lang::Any::itable<GlobalRef<T> > itable;

            static ::x10aux::itable_entry _iboxitables[2];

            ::x10aux::itable_entry* _getITables() { return _itables; }
            ::x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
            x10_ulong value; 
            x10_long epoch;
            ::x10aux::place location;	

            GlobalRef(T obj = NULL) : value((size_t)(obj)), epoch(globalref_getInitialEpoch()), location(::x10aux::here) { }
            GlobalRef(::x10aux::place p, x10_ulong obj = 0) : value(obj), epoch(globalref_getInitialEpoch()), location(p) { }
	
            static inline GlobalRef<T> _make(T obj) { return GlobalRef<T>(obj); }

            static GlobalRef<T> _alloc () {GlobalRef<T> t; return t;} // Note: no need to zero t

            void _constructor (T t) {
                value = (size_t)t;
            }
            
            // we are assuming T is always a pointer type, becasue of the isRef constraint on GlobalRef
            inline T __apply() { return (T)(size_t)value; }

            GlobalRef<T>* operator->() { return this; }

            void forget();
            
            static void _serialize(GlobalRef<T> this_, ::x10aux::serialization_buffer& buf);
    
            static GlobalRef<T> _deserialize(::x10aux::deserialization_buffer& buf) {
                GlobalRef<T> this_;
                this_->_deserialize_body(buf);
                return this_;
            }
    
            void _deserialize_body(::x10aux::deserialization_buffer& buf);
            
            x10_boolean equals(::x10::lang::Any* that) { return _struct_equals(that); }
    
            x10_boolean equals(GlobalRef<T> that) { return _struct_equals(that); }
    
            x10_boolean _struct_equals(::x10::lang::Any*);
    
            x10_boolean _struct_equals(GlobalRef<T> that);
    
            x10_boolean isNull();
    
            ::x10::lang::String* toString();
    
            x10_int hashCode();

            ::x10::lang::String* typeName();
        };

        template <> class GlobalRef<void> {
          public:
            static ::x10aux::RuntimeType rtt;
            static const ::x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
} 

#endif // X10_LANG_GLOBALREF

#ifndef X10_LANG_GLOBALREF_H_NODEPS
#define X10_LANG_GLOBALREF_H_NODEPS
#include <x10/lang/Any.h>
#include <x10/lang/String.h>
#ifndef X10_LANG_GLOBALREF_GENERICS
#define X10_LANG_GLOBALREF_GENERICS
#endif // X10_LANG_GLOBALREF_GENERICS
#ifndef X10_LANG_GLOBALREF_IMPLEMENTATION
#define X10_LANG_GLOBALREF_IMPLEMENTATION
#include <x10/lang/GlobalRef.h>
#include <x10/lang/GlobalRef__LocalEval.h>

// ITable junk, both for GlobalRef and IBox<GlobalRef>
namespace x10 {
    namespace lang { 

        template<class T> ::x10::lang::Any::itable<GlobalRef<T> >
            GlobalRef<T>::itable(&GlobalRef<T>::equals,
                                 &GlobalRef<T>::hashCode,
                                 &GlobalRef<T>::toString,
                                 &GlobalRef<T>::typeName);

        template<class T> class GlobalRef_iboxithunk0 : public ::x10::lang::IBox< ::x10::lang::GlobalRef<T> > {
        public:
            static ::x10::lang::Any::itable<GlobalRef_iboxithunk0<T> > itable;
            x10_boolean equals(::x10::lang::Any* arg0) {
                return this->value->equals(arg0);
            }
            x10_int hashCode() {
                return this->value->hashCode();
            }
            ::x10::lang::String* toString() {
                return this->value->toString();
            }
            ::x10::lang::String* typeName() {
                return this->value->typeName();
            }
        };

        template<class T> ::x10::lang::Any::itable<GlobalRef_iboxithunk0<T> >
            GlobalRef_iboxithunk0<T>::itable(&GlobalRef_iboxithunk0<T>::equals,
                                             &GlobalRef_iboxithunk0<T>::hashCode,
                                             &GlobalRef_iboxithunk0<T>::toString,
                                             &GlobalRef_iboxithunk0<T>::typeName);
    }
}

namespace x10 {
    namespace lang {
        void logGlobalReference(::x10::lang::Reference* obj);
        void forgetGlobalReference(::x10::lang::Reference* obj);
    }
}

template<class T> void x10::lang::GlobalRef<T>::forget() {
    if (NULL != __apply()) {
        forgetGlobalReference(reinterpret_cast< ::x10::lang::Reference*>(__apply()));
    }
}

template<class T> void x10::lang::GlobalRef<T>::_serialize(::x10::lang::GlobalRef<T> this_,
                                                           ::x10aux::serialization_buffer& buf) {
    buf.write(this_->location);
    buf.write(this_->epoch);
    buf.write(this_->value);
    #if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
    if (this_->location == ::x10aux::here) {
        if (NULL != this_->__apply()) logGlobalReference(reinterpret_cast< ::x10::lang::Reference*>(this_->__apply()));
    }
    #endif
}

template<class T> void x10::lang::GlobalRef<T>::_deserialize_body(::x10aux::deserialization_buffer& buf) {
    location = buf.read< ::x10aux::place>();
    epoch = buf.read<x10_long>();
    value = buf.read<x10_ulong>();
}


template<class T> x10_boolean x10::lang::GlobalRef<T>::_struct_equals(::x10::lang::Any* that) {
    if ((!(::x10aux::instanceof< ::x10::lang::GlobalRef<T> >(that)))) {
        return false;
    }
    return _struct_equals(::x10aux::class_cast< ::x10::lang::GlobalRef<T> >(that));
}

template<class T> x10_boolean x10::lang::GlobalRef<T>::_struct_equals(::x10::lang::GlobalRef<T> that) { 
    return (location == that->location) && ::x10aux::struct_equals(value, that->value);
}

template<class T> x10_boolean x10::lang::GlobalRef<T>::isNull() {
    return value == 0;
}

template<class T> ::x10::lang::String* x10::lang::GlobalRef<T>::toString() {
    char* tmp = ::x10aux::alloc_printf("GlobalRef[%s](%lld, 0x%llx)", ::x10aux::getRTT<T>()->name(), (long long) location, (long long)value);
    return ::x10::lang::String::Steal(tmp);
}

template<class T> x10_int x10::lang::GlobalRef<T>::hashCode() {
    // TODO: match this implementation with the java GlobalRef.hashCode
    return (x10_int)value;
}

template<class T> ::x10::lang::String* x10::lang::GlobalRef<T>::typeName() {
    char* tmp = ::x10aux::alloc_printf("x10.lang.GlobalRef[%s]", ::x10aux::getRTT<T>()->name());
    return ::x10::lang::String::Steal(tmp);
}

template<class T> ::x10aux::RuntimeType x10::lang::GlobalRef<T>::rtt;

template<class T> ::x10aux::itable_entry x10::lang::GlobalRef<T>::_itables[2] = {::x10aux::itable_entry(&::x10aux::getRTT< ::x10::lang::Any>, &GlobalRef<T>::itable),
                                                                               ::x10aux::itable_entry(NULL, (void*)"x10.lang.GlobalRef[T]")};

template<class T> ::x10aux::itable_entry x10::lang::GlobalRef<T>::_iboxitables[2] = {::x10aux::itable_entry(&::x10aux::getRTT< ::x10::lang::Any>, &GlobalRef_iboxithunk0<T>::itable),
                                                                                   ::x10aux::itable_entry(NULL, (void*)"x10.lang.GlobalRef[T]")};

template<class T> void x10::lang::GlobalRef<T>::_initRTT() {
    const ::x10aux::RuntimeType *canonical = ::x10aux::getRTT< ::x10::lang::GlobalRef<void> >();
    if (rtt.initStageOne(canonical)) return;
    const ::x10aux::RuntimeType* parents[2] = { ::x10aux::getRTT< ::x10::lang::Any>(), ::x10aux::getRTT< ::x10::lang::Any>()};
    const ::x10aux::RuntimeType* params[1] = { ::x10aux::getRTT<T>()};
    ::x10aux::RuntimeType::Variance variances[1] = { ::x10aux::RuntimeType::invariant};
    const char *baseName = "x10.lang.GlobalRef";
    rtt.initStageTwo(baseName, ::x10aux::RuntimeType::struct_kind, 2, parents, 1, params, variances);
}
#endif // X10_LANG_GLOBALREF_IMPLEMENTATION
#endif // __X10_LANG_GLOBALREF_NODEPS
