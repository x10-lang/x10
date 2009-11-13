#ifndef __FT_COMM_H
#define __FT_COMM_H

#include <x10rt17.h>
#include <pgasrt.h>
#include <x10/runtime/Runtime.h>

#define X10_LANG_VALUE_H_NODEPS
#include <x10/lang/Value.h>
#undef X10_LANG_VALUE_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
namespace FT { 

class Comm : public x10::lang::Value  {
    public:
    RTT_H_DECLS_CLASS
    
    void _instance_init();
    
    static x10_int FMGL(next_id);
    
    x10_int FMGL(my_id);
    
    static x10aux::ref<FT::Comm> _make() {
        x10aux::ref<FT::Comm> this_ = new (x10aux::alloc<FT::Comm>()) FT::Comm();
        this_->_constructor();
        return this_;
    }
    void _constructor();
  
     public: Comm() {FMGL(my_id) = 0;} 
     public: Comm (const Comm& other) { FMGL(my_id) = other.FMGL(my_id);}
 
    static x10aux::ref<FT::Comm> _make(x10_int id) {
        x10aux::ref<FT::Comm> this_ = new (x10aux::alloc<FT::Comm>()) FT::Comm();
        this_->_constructor(id);
        return this_;
    }
    void _constructor(x10_int id);
    
    public: virtual x10_boolean _struct_equals(x10aux::ref<x10::lang::Object> p0);
    public : static void _static_init();
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: virtual x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
    
    public: template<class __T> static x10aux::ref<__T> _deserializer(x10aux::deserialization_buffer& buf) {
        x10aux::ref<FT::Comm> this_ = new (x10aux::alloc<FT::Comm >()) FT::Comm();
        this_->_deserialize_body(buf);
        return this_;
    }
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
   
      void barrier();

    public: static x10aux::ref<FT::Comm> world() {return _make(0);}

    template<typename T>
    void broadcast (T* buf, int root, signed int len)
    {
    
     void *r = __pgasrt_tspcoll_ibcast((unsigned)FMGL(my_id),  (unsigned)root, (void*)buf, (void*)buf, len*sizeof(T));
     x10::runtime::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
    }

    template<typename T>
    void alltoall (T* sbuf, T* rbuf, signed int len)
    {
    
     void *r = __pgasrt_tspcoll_ialltoall((unsigned)FMGL(my_id),  (void*)sbuf, (void*)rbuf, 
             len*sizeof(T));
     x10::runtime::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
    }

   template<typename T>
    T reduce(T val, __pgasrt_ops_t OP,  __pgasrt_dtypes_t TYPE) {
    T val2;
     void *r = __pgasrt_tspcoll_iallreduce((unsigned)FMGL(my_id),  &val, &val2, OP, TYPE, 1);
     x10::runtime::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
          return val2;
   }

    int reduce_di(const double val, int index, int OP, int TYPE);

   x10aux::ref<Comm> split(signed int color, signed int new_rank);
 
};

} 
#endif // FT_COMM_H

namespace FT { 
class Comm;
} 

#ifndef FT_COMM_H_NODEPS
#define FT_COMM_H_NODEPS
#include <x10/lang/Value.h>
#include <x10/lang/Int.h>
#ifndef FT_COMM_H_GENERICS
#define FT_COMM_H_GENERICS
#endif // FT_COMM_H_GENERICS
#endif // __FT_COMM_H_NODEPS
