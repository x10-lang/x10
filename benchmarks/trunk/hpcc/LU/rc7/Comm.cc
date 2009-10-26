#include <rc7/Comm.h>



void rc7::Comm::_instance_init() {
    _I_("Doing initialisation for class: rc7::Comm");
    
}


//#line 3 "/gsa/yktgsa-h1/01/bikshand/fresh/x10.demo.sc08/src/x10-20/HPL/scartch/Comm.x10"
x10_int rc7::Comm::FMGL(next_id) = ((x10_int)0);


//#line 4 "/gsa/yktgsa-h1/01/bikshand/fresh/x10.demo.sc08/src/x10-20/HPL/scartch/Comm.x10"

//#line 5 "/gsa/yktgsa-h1/01/bikshand/fresh/x10.demo.sc08/src/x10-20/HPL/scartch/Comm.x10"
void rc7::Comm::_constructor() {
    this->x10::lang::Value::_constructor();
    
    //#line 5 "/gsa/yktgsa-h1/01/bikshand/fresh/x10.demo.sc08/src/x10-20/HPL/scartch/Comm.x10"
    x10aux::placeCheck(x10aux::nullCheck(((x10aux::ref<rc7::Comm>)this)))->
      FMGL(my_id) = ((x10_int)0);
    
}


//#line 6 "/gsa/yktgsa-h1/01/bikshand/fresh/x10.demo.sc08/src/x10-20/HPL/scartch/Comm.x10"
void rc7::Comm::_constructor(x10_int id) {
    this->x10::lang::Value::_constructor();
    
    //#line 6 "/gsa/yktgsa-h1/01/bikshand/fresh/x10.demo.sc08/src/x10-20/HPL/scartch/Comm.x10"
    x10aux::placeCheck(x10aux::nullCheck(((x10aux::ref<rc7::Comm>)this)))->
      FMGL(my_id) = id;
    
}

    void rc7::Comm::barrier() {
     void *r = __pgasrt_tspcoll_ibarrier(FMGL(my_id));
     x10::runtime::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
    }


    int rc7::Comm::reduce_di(const double d, const int idx, int OP, int TYPE) {
     struct {double d; int i;} val = {d, idx};
     void *r = __pgasrt_tspcoll_iallreduce((unsigned)FMGL(my_id),  (void*) &val, (void*) &val, PGASRT_OP_MAX, PGASRT_DT_dblint, 1);
     x10::runtime::Runtime::increaseParallelism();
     while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();
     x10::runtime::Runtime::decreaseParallelism(1);
      return val.i;
   }

   x10aux::ref<rc7::Comm> rc7::Comm::split(signed int color, signed int new_rank) {
    signed int new_id = ++FMGL(next_id);
    //std::cout << "Comm id " << FMGL(my_id) << std::endl;
    __pgasrt_tspcoll_comm_split((unsigned)FMGL(my_id), (unsigned)new_id, (unsigned)color, (unsigned)new_rank);
    return rc7::Comm::_make(new_id);
  }


x10_boolean rc7::Comm::_struct_equals(x10aux::ref<x10::lang::Object> p0) {
    if (p0.operator->() == this) return true; // short-circuit trivial equality
    if (!this->x10::lang::Value::_struct_equals(p0))
        return false;
    x10aux::ref<rc7::Comm> that = (x10aux::ref<rc7::Comm>) p0;
    if (!x10aux::struct_equals(this->FMGL(my_id), that->FMGL(my_id)))
        return false;
    return true;
}

const x10aux::serialization_id_t rc7::Comm::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(rc7::Comm::_deserializer<x10::lang::Object>);

void rc7::Comm::_serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    x10::lang::Value::_serialize_body(buf, m);
    buf.write(this->FMGL(my_id),m);
    
}

void rc7::Comm::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Value::_deserialize_body(buf);
    FMGL(my_id) = buf.read<x10_int >();
}

x10aux::RuntimeType rc7::Comm::rtt;
void rc7::Comm::_initRTT() {
    rtt.canonical = &rtt;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Value>()};
    rtt.init(&rtt, "rc7.Comm", 1, parents, 0, NULL, NULL);
}

extern "C" const char* LNMAP_rc7_Comm_cc = "N{\"rc7/Comm.cc\"} F{0:\"/gsa/yktgsa-h1/01/bikshand/fresh/x10.demo.sc08/src/x10-20/HPL/scartch/Comm.x10\",1:\"rc7.Comm\",2:\"this\",3:\"\",4:\"rc7::Comm\",5:\"_constructor\",6:\"void\",7:\"x10.lang.Int\",8:\"x10_int\",} L{17->0:4,34->0:6,19->0:5,23->0:5,13->0:3,30->0:6,} M{6 4.5(8)->3 1.2(7);6 4.5()->3 1.2();}";
