#ifndef __X10_LANG_COMPARABLE_H
#define __X10_LANG_COMPARABLE_H

#include <x10rt.h>

#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 {
    namespace lang {

        template<class FMGL(T)> class Comparable   {
        public:
            RTT_H_DECLS_INTERFACE
    
          template <class I> struct itable {
              itable(x10_int (I::*compareTo) (FMGL(T)),
                     x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>),
                     x10_int (I::*hashCode) (),
                     x10aux::ref<x10::lang::String> (I::*toString) (),
                     x10aux::ref<x10::lang::String> (I::*typeName) ()) : compareTo(compareTo), equals(equals), hashCode(hashCode), toString(toString), typeName(typeName) {}
              x10_int (I::*compareTo) (FMGL(T));
              x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>);
              x10_int (I::*hashCode) ();
              x10aux::ref<x10::lang::String> (I::*toString) ();
              x10aux::ref<x10::lang::String> (I::*typeName) ();
          };
    
          static x10_int compareTo(x10aux::ref<x10::lang::Reference> recv, FMGL(T) arg0) {
              return (recv.operator->()->*(x10aux::findITable<x10::lang::Comparable<FMGL(T)> >(recv->_getITables())->compareTo))(arg0);
          }
          static x10_boolean equals(x10aux::ref<x10::lang::Reference> recv, x10aux::ref<x10::lang::Any> arg0) {
              return (recv.operator->()->*(x10aux::findITable<x10::lang::Comparable<FMGL(T)> >(recv->_getITables())->equals))(arg0);
          }
          static x10_int hashCode(x10aux::ref<x10::lang::Reference> recv) {
              return (recv.operator->()->*(x10aux::findITable<x10::lang::Comparable<FMGL(T)> >(recv->_getITables())->hashCode))();
          }
          static x10aux::ref<x10::lang::String> toString(x10aux::ref<x10::lang::Reference> recv) {
              return (recv.operator->()->*(x10aux::findITable<x10::lang::Comparable<FMGL(T)> >(recv->_getITables())->toString))();
          }
          static x10aux::ref<x10::lang::String> typeName(x10aux::ref<x10::lang::Reference> recv) {
              return (recv.operator->()->*(x10aux::findITable<x10::lang::Comparable<FMGL(T)> >(recv->_getITables())->typeName))();
          }
          static void _serialize(x10aux::ref<x10::lang::Comparable<FMGL(T)> > this_,
                                 x10aux::serialization_buffer& buf) {
              x10::lang::Reference::_serialize(this_, buf);
          }
    
          template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer& buf) {
              return x10::lang::Reference::_deserialize<__T>(buf);
          }
          
          void _instance_init();
        };

        template <> class Comparable<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return & rtt; }
    
        };


/*




        template<> class Comparable<x10_int>   {
        public:
          RTT_H_DECLS_INTERFACE
    
          static x10_int compareTo(x10_int recv, x10_int arg0) {
              return x10aux::int_utils::compareTo(recv, arg0);
          }
          static x10_boolean equals(x10_int recv, x10_int arg0) {
              return x10aux::equals(recv, arg0);
          }
          static x10_int hashCode(x10_int recv) {
              return x10aux::hash_code(recv);
          }
          static x10aux::ref<x10::lang::String> toString(x10_int recv) {
              return x10aux::to_string(recv);
          }
          static x10aux::ref<x10::lang::String> typeName(x10_int recv) {
              return x10aux::type_name(recv);
          }
        };

*/

















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

template<class FMGL(T)> void x10::lang::Comparable<FMGL(T)>::_instance_init() {
    _I_("Doing initialisation for class: x10::lang::Comparable<FMGL(T)>");
}

template<class FMGL(T)> x10aux::RuntimeType x10::lang::Comparable<FMGL(T)>::rtt;
template<class FMGL(T)> void x10::lang::Comparable<FMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Comparable<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<FMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.lang.Comparable";
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances);
}
#endif // X10_LANG_COMPARABLE_H_IMPLEMENTATION
#endif // __X10_LANG_COMPARABLE_H_NODEPS
