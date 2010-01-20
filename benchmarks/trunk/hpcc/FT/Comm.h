#ifndef __FT_COMM_H
#define __FT_COMM_H

#include <x10rt.h>
#include <pgasrt.h>
#include <x10/lang/Runtime.h>

#define X10_LANG_OBJECT_H_NODEPS
#include <x10/lang/Object.h>
#undef X10_LANG_OBJECT_H_NODEPS
#define X10_LANG_INT_STRUCT_H_NODEPS
#include <x10/lang/Int.struct_h>
#undef X10_LANG_INT_STRUCT_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
#include <x10/lang/Int.struct_h>
namespace FT { 

class Comm : public x10::lang::Object   {
    public:
    RTT_H_DECLS_CLASS
    
    void _instance_init();
    
    void _constructor(x10_int id);
    
    static x10aux::ref<FT::Comm> _make(x10_int id);
    
    x10_int FMGL(my_id);
    
    x10_int FMGL(next_id);
    
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: static void _serialize(x10aux::ref<FT::Comm> this_,
                                   x10aux::serialization_buffer& buf);
    
    public: x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: void _serialize_body(x10aux::serialization_buffer& buf);
    
    public: template<class __T> static x10aux::ref<__T> _deserializer(x10aux::deserialization_buffer& buf);
    
    public: template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer& buf);
    
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

    /* template<typename T>
    void alltoall (T* sbuf, T* rbuf, signed int len)
    {

     void *r = __pgasrt_tspcoll_ialltoall((unsigned)FMGL(my_id),  (void*)sbuf, (void*)rbuf,
             len*sizeof(T));
     x10::runtime::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
    } */

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
    void alltoall(const double* in, double* out, size_t size);

   x10aux::ref<Comm> split(signed int color, signed int new_rank);

 
};

} 
#endif // FT_COMM_H

namespace FT { 
class Comm;
} 

#ifndef FT_COMM_H_NODEPS
#define FT_COMM_H_NODEPS
#include <x10/lang/Object.h>
#include <x10/lang/Int.h>
#ifndef FT_COMM_H_GENERICS
#define FT_COMM_H_GENERICS
template<class __T> x10aux::ref<__T> FT::Comm::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<FT::Comm> this_ = new (x10aux::alloc_remote<FT::Comm>()) FT::Comm();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

template<class __T> x10aux::ref<__T> FT::Comm::_deserialize(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_reference_state rr = x10::lang::Object::_deserialize_reference_state(buf);
    x10aux::ref<FT::Comm> this_;
    if (rr.ref != 0) {
        this_ = FT::Comm::_deserializer<FT::Comm>(buf);
    }
    return x10::lang::Object::_finalize_reference<__T>(this_, rr);
}

#endif // FT_COMM_H_GENERICS
#endif // __FT_COMM_H_NODEPS
