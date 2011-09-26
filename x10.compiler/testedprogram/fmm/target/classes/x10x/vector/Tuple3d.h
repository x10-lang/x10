#ifndef __X10X_VECTOR_TUPLE3D_H
#define __X10X_VECTOR_TUPLE3D_H

#include <x10rt.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10x { namespace vector { 

class Tuple3d   {
    public:
    RTT_H_DECLS_INTERFACE
    
    template <class I> struct itable {
        itable(x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>), x10_int (I::*hashCode) (), x10_double (I::*i) (), x10_double (I::*j) (), x10_double (I::*k) (), x10aux::ref<x10::lang::String> (I::*toString) (), x10aux::ref<x10::lang::String> (I::*typeName) ()) : equals(equals), hashCode(hashCode), i(i), j(j), k(k), toString(toString), typeName(typeName) {}
        x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>);
        x10_int (I::*hashCode) ();
        x10_double (I::*i) ();
        x10_double (I::*j) ();
        x10_double (I::*k) ();
        x10aux::ref<x10::lang::String> (I::*toString) ();
        x10aux::ref<x10::lang::String> (I::*typeName) ();
    };
    
    template <class R> static x10_boolean equals(x10aux::ref<R> _recv, x10aux::ref<x10::lang::Any> arg0) {
        x10aux::ref<x10::lang::Reference> _refRecv(_recv);
        return (_refRecv.operator->()->*(x10aux::findITable<x10x::vector::Tuple3d>(_refRecv->_getITables())->equals))(arg0);
    }
    template <class R> static x10_boolean equals(R _recv, x10aux::ref<x10::lang::Any> arg0) {
        return _recv->equals(arg0);
    }
    template <class R> static x10_int hashCode(x10aux::ref<R> _recv) {
        x10aux::ref<x10::lang::Reference> _refRecv(_recv);
        return (_refRecv.operator->()->*(x10aux::findITable<x10x::vector::Tuple3d>(_refRecv->_getITables())->hashCode))();
    }
    template <class R> static x10_int hashCode(R _recv) {
        return _recv->hashCode();
    }
    template <class R> static x10_double i(x10aux::ref<R> _recv) {
        x10aux::ref<x10::lang::Reference> _refRecv(_recv);
        return (_refRecv.operator->()->*(x10aux::findITable<x10x::vector::Tuple3d>(_refRecv->_getITables())->i))();
    }
    template <class R> static x10_double i(R _recv) {
        return _recv->i();
    }
    template <class R> static x10_double j(x10aux::ref<R> _recv) {
        x10aux::ref<x10::lang::Reference> _refRecv(_recv);
        return (_refRecv.operator->()->*(x10aux::findITable<x10x::vector::Tuple3d>(_refRecv->_getITables())->j))();
    }
    template <class R> static x10_double j(R _recv) {
        return _recv->j();
    }
    template <class R> static x10_double k(x10aux::ref<R> _recv) {
        x10aux::ref<x10::lang::Reference> _refRecv(_recv);
        return (_refRecv.operator->()->*(x10aux::findITable<x10x::vector::Tuple3d>(_refRecv->_getITables())->k))();
    }
    template <class R> static x10_double k(R _recv) {
        return _recv->k();
    }
    template <class R> static x10aux::ref<x10::lang::String> toString(x10aux::ref<R> _recv) {
        x10aux::ref<x10::lang::Reference> _refRecv(_recv);
        return (_refRecv.operator->()->*(x10aux::findITable<x10x::vector::Tuple3d>(_refRecv->_getITables())->toString))();
    }
    template <class R> static x10aux::ref<x10::lang::String> toString(R _recv) {
        return _recv->toString();
    }
    template <class R> static x10aux::ref<x10::lang::String> typeName(x10aux::ref<R> _recv) {
        x10aux::ref<x10::lang::Reference> _refRecv(_recv);
        return (_refRecv.operator->()->*(x10aux::findITable<x10x::vector::Tuple3d>(_refRecv->_getITables())->typeName))();
    }
    template <class R> static x10aux::ref<x10::lang::String> typeName(R _recv) {
        return _recv->typeName();
    }
    
};

} } 
#endif // X10X_VECTOR_TUPLE3D_H

namespace x10x { namespace vector { 
class Tuple3d;
} } 

#ifndef X10X_VECTOR_TUPLE3D_H_NODEPS
#define X10X_VECTOR_TUPLE3D_H_NODEPS
#ifndef X10X_VECTOR_TUPLE3D_H_GENERICS
#define X10X_VECTOR_TUPLE3D_H_GENERICS
#endif // X10X_VECTOR_TUPLE3D_H_GENERICS
#endif // __X10X_VECTOR_TUPLE3D_H_NODEPS
