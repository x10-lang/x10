#ifndef __X10_LANG_COMPARABLE_H
#define __X10_LANG_COMPARABLE_H

#include <x10rt.h>

#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 {
    namespace lang {

        template<class TPMGL(T)> class Comparable   {
        public:
            RTT_H_DECLS_INTERFACE
    
          template <class I> struct itable {
              itable(x10_int (I::*compareTo) (TPMGL(T)),
                     x10_boolean (I::*equals) (x10::lang::Any*),
                     x10_int (I::*hashCode) (),
                     x10::lang::String* (I::*toString) (),
                     x10::lang::String* (I::*typeName) ()) : compareTo(compareTo), equals(equals), hashCode(hashCode), toString(toString), typeName(typeName) {}
              x10_int (I::*compareTo) (TPMGL(T));
              x10_boolean (I::*equals) (x10::lang::Any*);
              x10_int (I::*hashCode) ();
              x10::lang::String* (I::*toString) ();
              x10::lang::String* (I::*typeName) ();
          };
    
          static x10_int compareTo(x10::lang::Comparable<TPMGL(T)>* _recv, TPMGL(T) arg0) {
              x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
              return (_refRecv->*(x10aux::findITable<Comparable<TPMGL(T)> >(_refRecv->_getITables())->compareTo))(arg0);
          }     
          template <class R> static x10_int compareTo(R _recv, TPMGL(T) arg0) {
              return _recv->compareTo(arg0);
          }     

          static x10_boolean equals(x10::lang::Comparable<TPMGL(T)>* _recv, x10::lang::Any* arg0) {
              x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv);
              return (recv->*(x10aux::findITable<x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->equals))(arg0);
          }
          static x10_int hashCode(x10::lang::Comparable<TPMGL(T)>* _recv) {
              x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv);
              return (recv->*(x10aux::findITable<x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->hashCode))();
          }
          static x10::lang::String* toString(x10::lang::Comparable<TPMGL(T)>* _recv) {
              x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv);
              return (recv->*(x10aux::findITable<x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->toString))();
          }
          static x10::lang::String* typeName(x10::lang::Comparable<TPMGL(T)>* _recv) {
              x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv);
              return (recv->*(x10aux::findITable<x10::lang::Comparable<TPMGL(T)> >(recv->_getITables())->typeName))();
          }
        };

        template <> class Comparable<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return & rtt; }
    
        };


#define COMPARABLE_PRIM_DECL(PRIM,UTILS)              \
        template<> class Comparable<PRIM>   {    \
        public:                                     \
        RTT_H_DECLS_INTERFACE                       \
            template <class I> struct itable {                       \
                itable(x10_int (I::*compareTo) (PRIM),               \
                       x10_boolean (I::*equals) (x10::lang::Any*), \
                       x10_int (I::*hashCode) (),                       \
                       x10::lang::String* (I::*toString) (), \
                       x10::lang::String* (I::*typeName) ()) : compareTo(compareTo), equals(equals), hashCode(hashCode), toString(toString), typeName(typeName) {} \
                x10_int (I::*compareTo) (PRIM);                      \
                x10_boolean (I::*equals) (x10::lang::Any*); \
                x10_int (I::*hashCode) ();                              \
                x10::lang::String* (I::*toString) ();       \
                x10::lang::String* (I::*typeName) ();       \
            };                                                          \
            static inline x10_int compareTo(PRIM recv, PRIM arg0) {     \
                return x10aux::UTILS::compareTo(recv, arg0);            \
            }                                                           \
            static x10_int compareTo(Comparable<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::lang::Comparable<PRIM> >(recv->_getITables())->compareTo))(arg0); \
            }                                                           \
            static x10_boolean equals(PRIM recv, PRIM arg0) { return x10aux::equals(recv, arg0); } \
            static x10_int hashCode(PRIM recv) { return x10aux::hash_code(recv); } \
            static x10::lang::String* toString(PRIM recv) { return x10aux::to_string(recv); } \
            static x10::lang::String* typeName(PRIM recv) { return x10aux::type_name(recv); } \
        };
        
COMPARABLE_PRIM_DECL(x10_boolean, boolean_utils)
COMPARABLE_PRIM_DECL(x10_byte, byte_utils)
COMPARABLE_PRIM_DECL(x10_ubyte, byte_utils)
COMPARABLE_PRIM_DECL(x10_short, short_utils)
COMPARABLE_PRIM_DECL(x10_ushort, short_utils)
COMPARABLE_PRIM_DECL(x10_char, char_utils)
COMPARABLE_PRIM_DECL(x10_int, int_utils)
COMPARABLE_PRIM_DECL(x10_uint, int_utils)
COMPARABLE_PRIM_DECL(x10_float, float_utils)
COMPARABLE_PRIM_DECL(x10_long, long_utils)
COMPARABLE_PRIM_DECL(x10_ulong, long_utils)
COMPARABLE_PRIM_DECL(x10_double, double_utils)

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

template<class TPMGL(T)> x10aux::RuntimeType x10::lang::Comparable<TPMGL(T)>::rtt;
template<class TPMGL(T)> void x10::lang::Comparable<TPMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Comparable<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<TPMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.lang.Comparable";
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances);
}
#endif // X10_LANG_COMPARABLE_H_IMPLEMENTATION
#endif // __X10_LANG_COMPARABLE_H_NODEPS
