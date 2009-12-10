#ifndef __RC7_COMM_H
#define __RC7_COMM_H

#include <x10rt.h>

#ifndef TRANSPORT
#error "Native transport has not been defined"
#endif

#define mpi 901

#if TRANSPORT == mpi
#undef SEEK_SET
#undef SEEK_CUR
#undef SEEK_END
#include <mpi.h>
#include <nbc.h>
#else
#include <pgasrt.h>
#endif

#include <x10/runtime/Runtime.h>

#define X10_LANG_VALUE_H_NODEPS
#include <x10/lang/Closure.h>
#undef X10_LANG_VALUE_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
namespace rc7 { 

class Comm : public x10::lang::Closure  {
    public:
    RTT_H_DECLS_CLASS
    
    void _instance_init();
    
    static x10_int FMGL(next_id);
    
#if TRANSPORT == mpi
    MPI_Comm    FMGL(my_id);
#else
    x10_int FMGL(my_id);
#endif
    
    static x10aux::ref<rc7::Comm> _make() {
        x10aux::ref<rc7::Comm> this_ = new (x10aux::alloc<rc7::Comm>()) rc7::Comm();
        this_->_constructor();
        return this_;
    }
    void _constructor();
  
     public: Comm() {FMGL(my_id) = 0;} 
     public: Comm (const Comm& other) { FMGL(my_id) = other.FMGL(my_id);}
 
    static x10aux::ref<rc7::Comm> _make(x10_int id) {
        x10aux::ref<rc7::Comm> this_ = new (x10aux::alloc<rc7::Comm>()) rc7::Comm();
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
        x10aux::ref<rc7::Comm> this_ = new (x10aux::alloc<rc7::Comm >()) rc7::Comm();
        this_->_deserialize_body(buf);
        return this_;
    }

#if TRANSPORT == mpi
    public: static const MPI_Op ADD_OP = MPI_SUM;
    public: static const MPI_Op MIN_OP = MPI_MIN;
    public: static const MPI_Op MAX_OP = MPI_MAX;
    public: static const MPI_Datatype DT_INT = MPI_INT;
    public: static const MPI_Datatype DT_DBL = MPI_DOUBLE;
    public: static const MPI_Datatype DT_DBLINT = MPI_BYTE;
#else
    public: static const int ADD_OP = PGASRT_OP_ADD;
    public: static const int MIN_OP = PGASRT_OP_MIN;
    public: static const int MAX_OP = PGASRT_OP_MAX;
    public: static const int DT_INT = PGASRT_DT_int;
    public: static const int DT_DBL = PGASRT_DT_dbl;
    public: static const int DT_DBLINT = PGASRT_DT_dblint;
#endif
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
   
      void barrier();

    public: static x10aux::ref<rc7::Comm> world() {return _make(0);}

    template<typename T>
#if TRANSPORT == mpi
        void broadcast (T* buf, int root, signed int len)
        {
            NBC_Handle hndl;
            if (NBC_SUCCESS != NBC_Ibcast((void*) buf, len*sizeof(T), 
                        MPI_BYTE, root, (MPI_Comm)FMGL(my_id), &hndl)) {
                fprintf(stderr, "Error in NBC_Iallreduce\n");
                abort();
            }
            x10::runtime::Runtime::increaseParallelism();
            while (NBC_CONTINUE != NBC_Test(&hndl)) x10rt_probe();
            x10::runtime::Runtime::decreaseParallelism(1);
        }
#else
    void broadcast (T* buf, int root, signed int len)
    {
    
     void *r = __pgasrt_tspcoll_ibcast((unsigned)FMGL(my_id),  (unsigned)root, (void*)buf, (void*)buf, len*sizeof(T));
     x10::runtime::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
    }
#endif

#if TRANSPORT == mpi
   template<typename T>
    T reduce(T val, MPI_Op OP, MPI_Datatype TYPE) {
    T val2;
    NBC_Handle hndl;
    if (NBC_SUCCESS != NBC_Iallreduce(&val, &val2, 1, TYPE, OP, (MPI_Comm)FMGL(my_id), &hndl)) {
        fprintf(stderr, "Error in NBC_Iallreduce\n");
        abort();
    }
     x10::runtime::Runtime::increaseParallelism();
     while (NBC_CONTINUE != NBC_Test(&hndl)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
          return val2;
   }
#else
   template<typename T>
    T reduce(T val, int OP,  int TYPE) {
    T val2;
    void *r = __pgasrt_tspcoll_iallreduce((unsigned)FMGL(my_id),  
            &val, &val2, (__pgasrt_ops_t) OP, (__pgasrt_dtypes_t) TYPE, 1);
    x10::runtime::Runtime::increaseParallelism();
    while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
    x10::runtime::Runtime::decreaseParallelism(1);
         return val2;
   }
#endif

    int reduce_di(const double val, int index, int OP, int TYPE);

   x10aux::ref<Comm> split(signed int color, signed int new_rank);
 
};

} 
#endif // RC7_COMM_H

namespace rc7 { 
class Comm;
} 

#ifndef RC7_COMM_H_NODEPS
#define RC7_COMM_H_NODEPS
#include <x10/lang/Closure.h>
#include <x10/lang/Int.h>
#ifndef RC7_COMM_H_GENERICS
#define RC7_COMM_H_GENERICS
#endif // RC7_COMM_H_GENERICS
#endif // __RC7_COMM_H_NODEPS
