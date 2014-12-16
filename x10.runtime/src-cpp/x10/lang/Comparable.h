#ifndef __X10_LANG_COMPARABLE_H
#define __X10_LANG_COMPARABLE_H

#include <x10rt.h>

#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

#include <x10/lang/Boolean.h>
#include <x10/lang/Byte.h>
#include <x10/lang/Char.h>
#include <x10/lang/Double.h>
#include <x10/lang/Int.h>
#include <x10/lang/Float.h>
#include <x10/lang/Long.h>
#include <x10/lang/Short.h>
#include <x10/lang/UByte.h>
#include <x10/lang/UInt.h>
#include <x10/lang/ULong.h>
#include <x10/lang/UShort.h>

namespace x10 {
    namespace lang {

        template<class TPMGL(T)> class Comparable   {
        public:
            RTT_H_DECLS_INTERFACE
    
          template <class Iface> struct itable {
              itable(x10_int (Iface::*compareTo) (TPMGL(T)),
                     x10_boolean (Iface::*equals) (::x10::lang::Any*),
                     x10_int (Iface::*hashCode) (),
                     ::x10::lang::String* (Iface::*toString) (),
                     ::x10::lang::String* (Iface::*typeName) ()) : compareTo(compareTo), equals(equals), hashCode(hashCode), toString(toString), typeName(typeName) {}
              x10_int (Iface::*compareTo) (TPMGL(T));
              x10_boolean (Iface::*equals) (::x10::lang::Any*);
              x10_int (Iface::*hashCode) ();
              ::x10::lang::String* (Iface::*toString) ();
              ::x10::lang::String* (Iface::*typeName) ();
          };
    
          template <class R> static x10_int compareTo(R* _recv, TPMGL(T) arg0) {
              ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
              ::x10aux::nullCheck(_refRecv);
              return (_refRecv->*(::x10aux::findITable<Comparable<TPMGL(T)> >(_refRecv->_getITables())->compareTo))(arg0);
          }     
          template <class R> static x10_int compareTo(R _recv, TPMGL(T) arg0) {
              return _recv->compareTo(arg0);
          }     

          template <class R> static x10_boolean equals(R* _recv, ::x10::lang::Any* arg0) {
              ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
              ::x10aux::nullCheck(_recv);
              return (recv->*(::x10aux::findITable< ::x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->equals))(arg0);
          }
          template <class R> static x10_int hashCode(R* _recv) {
              ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
              ::x10aux::nullCheck(_recv);
              return (recv->*(::x10aux::findITable< ::x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->hashCode))();
          }
          template <class R> static ::x10::lang::String* toString(R* _recv) {
              ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
              ::x10aux::nullCheck(_recv);
              return (recv->*(::x10aux::findITable< ::x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->toString))();
          }
          template <class R> static ::x10::lang::String* typeName(R* _recv) {
              ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
              ::x10aux::nullCheck(_recv);
              return (recv->*(::x10aux::findITable< ::x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->typeName))();
          }
        };

        template <> class Comparable<void> {
        public:
            static ::x10aux::RuntimeType rtt;
            static const ::x10aux::RuntimeType* getRTT() { return & rtt; }
    
        };

#define COMPARABLE_PRIM_DECL(PRIM,UTILS)              \
        template<> class Comparable<PRIM>   {    \
        public:                                     \
        RTT_H_DECLS_INTERFACE                       \
            template <class Iface> struct itable {                       \
                itable(x10_int (Iface::*compareTo) (PRIM),               \
                       x10_boolean (Iface::*equals) (::x10::lang::Any*), \
                       x10_int (Iface::*hashCode) (),                       \
                       ::x10::lang::String* (Iface::*toString) (), \
                       ::x10::lang::String* (Iface::*typeName) ()) : compareTo(compareTo), equals(equals), hashCode(hashCode), toString(toString), typeName(typeName) {} \
                x10_int (Iface::*compareTo) (PRIM);                      \
                x10_boolean (Iface::*equals) (::x10::lang::Any*); \
                x10_int (Iface::*hashCode) ();                              \
                ::x10::lang::String* (Iface::*toString) ();       \
                ::x10::lang::String* (Iface::*typeName) ();       \
            };                                                          \
            static inline x10_int compareTo(PRIM recv, PRIM arg0) {     \
                return UTILS::compareTo(recv, arg0);            \
            }                                                           \
            template <class R> static x10_int compareTo(R* _recv, PRIM arg0) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Comparable<PRIM> >(recv->_getITables())->compareTo))(arg0); \
            }                                                           \
            static x10_boolean equals(PRIM recv, PRIM arg0) { return ::x10aux::equals(recv, arg0); } \
            static x10_int hashCode(PRIM recv) { return ::x10aux::hash_code(recv); } \
            static ::x10::lang::String* toString(PRIM recv) { return ::x10aux::to_string(recv); } \
            static ::x10::lang::String* typeName(PRIM recv) { return ::x10aux::type_name(recv); } \
        };
        
COMPARABLE_PRIM_DECL(x10_boolean, BooleanNatives)
COMPARABLE_PRIM_DECL(x10_byte, ByteNatives)
COMPARABLE_PRIM_DECL(x10_ubyte, UByteNatives)
COMPARABLE_PRIM_DECL(x10_short, ShortNatives)
COMPARABLE_PRIM_DECL(x10_ushort, UShortNatives)
COMPARABLE_PRIM_DECL(x10_char, CharNatives)
COMPARABLE_PRIM_DECL(x10_int, IntNatives)
COMPARABLE_PRIM_DECL(x10_uint, UIntNatives)
COMPARABLE_PRIM_DECL(x10_float, FloatNatives)
COMPARABLE_PRIM_DECL(x10_long, LongNatives)
COMPARABLE_PRIM_DECL(x10_ulong, ULongNatives)
COMPARABLE_PRIM_DECL(x10_double, DoubleNatives)

    }
} 
#endif // X10_LANG_COMPARABLE_H

#ifndef X10_LANG_COMPARABLE_H_NODEPS
#define X10_LANG_COMPARABLE_H_NODEPS
#include <x10/lang/Any.h>
#ifndef X10_LANG_COMPARABLE_H_GENERICS
#define X10_LANG_COMPARABLE_H_GENERICS
#endif // X10_LANG_COMPARABLE_H_GENERICS
#ifndef X10_LANG_COMPARABLE_H_IMPLEMENTATION
#define X10_LANG_COMPARABLE_H_IMPLEMENTATION
#include <x10/lang/Comparable.h>

template<class TPMGL(T)> ::x10aux::RuntimeType x10::lang::Comparable<TPMGL(T)>::rtt;
template<class TPMGL(T)> void x10::lang::Comparable<TPMGL(T)>::_initRTT() {
    const ::x10aux::RuntimeType *canonical = ::x10aux::getRTT< ::x10::lang::Comparable<void> >();
    if (rtt.initStageOne(canonical)) return;
    const ::x10aux::RuntimeType* parents[1] = { ::x10aux::getRTT< ::x10::lang::Any>()};
    const ::x10aux::RuntimeType* params[1] = { ::x10aux::getRTT<TPMGL(T)>()};
    ::x10aux::RuntimeType::Variance variances[1] = { ::x10aux::RuntimeType::invariant};
    const char *baseName = "x10.lang.Comparable";
    rtt.initStageTwo(baseName, ::x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances);
}
#endif // X10_LANG_COMPARABLE_H_IMPLEMENTATION
#endif // __X10_LANG_COMPARABLE_H_NODEPS
