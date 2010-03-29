#include <FT/Comm.h>


void FT::Comm::_instance_init() {
    _I_("Doing initialisation for class: FT::Comm");
    
}


//#line 3 "/vol/x10/users/ganesh/work-17/hpcc/hpcc/FT/Comm.x10"
void FT::Comm::_constructor(x10_int id) {
    this->x10::lang::Object::_constructor();
    
    //#line 3 "/vol/x10/users/ganesh/work-17/hpcc/hpcc/FT/Comm.x10"
    ((x10aux::ref<FT::Comm>)this)->FMGL(my_id) = id;
    
}
x10aux::ref<FT::Comm> FT::Comm::_make(x10_int id) {
    x10aux::ref<FT::Comm> this_ = new (x10aux::alloc<FT::Comm>()) FT::Comm();
    this_->_constructor(id);
    return this_;
}



//#line 4 "/vol/x10/users/ganesh/work-17/hpcc/hpcc/FT/Comm.x10"

//#line 5 "/vol/x10/users/ganesh/work-17/hpcc/hpcc/FT/Comm.x10"
const x10aux::serialization_id_t FT::Comm::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FT::Comm::_deserializer<x10::lang::Object>);

void FT::Comm::_serialize(x10aux::ref<FT::Comm> this_,
                          x10aux::serialization_buffer& buf) {
    _serialize_reference(this_, buf);
    if (this_ != x10aux::null) {
        this_->_serialize_body(buf);
    }
}

void FT::Comm::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    
}

void FT::Comm::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    
}

 void FT::Comm::barrier() {
     void *r = __pgasrt_tspcoll_ibarrier(FMGL(my_id));
     x10::lang::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::lang::Runtime::decreaseParallelism(1);
    }

    void FT::Comm::alltoall(const double* in, double* out, size_t size) {
        /* printf ("size: %d %d %f %f\n", size, FMGL(my_id), in[0], in[1]);
     __pgasrt_tspcoll_alltoall((unsigned)FMGL(my_id), in, out,size);
        printf ("after: %f %f\n", out[0], out[1]);*/
     void *r = __pgasrt_tspcoll_ialltoall((unsigned)FMGL(my_id), in, out,size);
     x10::lang::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::lang::Runtime::decreaseParallelism(1);
   }

  int FT::Comm::reduce_di(const double d, const int idx, int OP, int TYPE) {
     struct {double d; int i;} val = {d, idx};
     void *r = __pgasrt_tspcoll_iallreduce((unsigned)FMGL(my_id),  (void*) &val, (void*) &val, PGASRT_OP_MAX, PGASRT_DT_dblint, 1);
     x10::lang::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::lang::Runtime::decreaseParallelism(1);
      return val.i;
   }

   x10aux::ref<FT::Comm> FT::Comm::split(signed int color, signed int new_rank) {
    signed int new_id = ++FMGL(next_id);
    //std::cout << "FT::Comm id " << FMGL(my_id) << std::endl;
    __pgasrt_tspcoll_comm_split((unsigned)FMGL(my_id), (unsigned)new_id, (unsigned)color, (unsigned)new_rank);
    return FT::Comm::_make(new_id);
  }


x10aux::RuntimeType FT::Comm::rtt;
void FT::Comm::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
    rtt.initStageTwo("FT.Comm", 1, parents, 0, NULL, NULL);
}

extern "C" { const char* LNMAP_FT_Comm_cc = "N{\"FT/Comm.cc\"} F{0:\"/vol/x10/users/ganesh/work-17/hpcc/hpcc/FT/Comm.x10\",1:\"FT.Comm\",2:\"this\",3:\"\",4:\"x10.lang.Int\",5:\"FT::Comm\",6:\"_constructor\",7:\"void\",8:\"x10_int\",} L{17->0:3,29->0:4,13->0:3,31->0:5,} M{7 5.6(8)->3 1.2(4);}"; }
