/*************************************************/
/* START of SeqRail1 */
#include <SeqRail1.h>

#include <Benchmark.h>
#include <x10/lang/Int.h>
#include <x10/lang/Double.h>
#include <x10/lang/Rail.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Long.h>
#include <x10/lang/String.h>
#include <harness/x10Test.h>

//#line 22 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10FieldDecl_c

//#line 24 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10MethodDecl_c
x10_double SeqRail1::expected() {
    
    //#line 24 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10Return_c
    return ((((x10_double) (this->FMGL(N)))) * (((x10_double) (this->FMGL(M)))));
    
}

//#line 25 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10MethodDecl_c
x10_double SeqRail1::operations() {
    
    //#line 25 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10Return_c
    return ((((x10_double) (this->FMGL(N)))) * (((x10_double) (this->FMGL(M)))));
    
}

//#line 32 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10FieldDecl_c

//#line 34 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10MethodDecl_c
x10_double SeqRail1::once() {
    
    //#line 35 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10LocalDecl_c
    x10_double sum = 0.0;
    
    //#line 36 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": polyglot.ast.For_c
    {
        x10_int k;
        for (
             //#line 36 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10LocalDecl_c
             k = ((x10_int)0); ((k) < (this->FMGL(M))); 
                                                        //#line 36 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10LocalAssign_c
                                                        k = ((x10_int) ((k) + (((x10_int)1)))))
        {
            
            //#line 37 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": polyglot.ast.For_c
            {
                x10_int i;
                for (
                     //#line 37 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10LocalDecl_c
                     i = ((x10_int)0); ((i) < (this->FMGL(N))); 
                                                                //#line 37 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10LocalAssign_c
                                                                i =
                                                                  ((x10_int) ((i) + (((x10_int)1)))))
                {
                    
                    //#line 38 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10LocalAssign_c
                    sum = ((sum) + (this->FMGL(a)->x10::lang::Rail<x10_double >::__apply(
                                      ((x10_long) (((x10_int) ((i) + (k))))))));
                }
            }
            
        }
    }
    
    //#line 39 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10Return_c
    return sum;
    
}

//#line 46 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10MethodDecl_c
void SeqRail1::main(x10::lang::Rail<x10::lang::String* >* id__0) {
    
    //#line 47 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10Call_c
    (SeqRail1::_make())->execute();
}

//#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10MethodDecl_c
SeqRail1* SeqRail1::SeqRail1____this__SeqRail1() {
    
    //#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10Return_c
    return this;
    
}

//#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10ConstructorDecl_c
void SeqRail1::_constructor() {
    
    //#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10ConstructorCall_c
    (this)->::Benchmark::_constructor();
    
    //#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.AssignPropertyCall_c
    
    //#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10Call_c
    this->SeqRail1::__fieldInitializers2032();
}
SeqRail1* SeqRail1::_make() {
    SeqRail1* this_ = new (memset(x10aux::alloc<SeqRail1>(), 0, sizeof(SeqRail1))) SeqRail1();
    this_->_constructor();
    return this_;
}



//#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": x10.ast.X10MethodDecl_c
void SeqRail1::__fieldInitializers2032() {
    
    //#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(N) = ((x10_int)1000000);
    
    //#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(M) = ((x10_int)20);
    
    //#line 16 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/SeqRail1.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(a) = x10::lang::Rail<x10_double >::_make(((x10_long) (((x10_int) ((this->
                                                                                    FMGL(N)) + (this->
                                                                                                  FMGL(M)))))),
                                                        1.0);
}
const x10aux::serialization_id_t SeqRail1::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(SeqRail1::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void SeqRail1::_serialize_body(x10aux::serialization_buffer& buf) {
    Benchmark::_serialize_body(buf);
    buf.write(this->FMGL(N));
    buf.write(this->FMGL(M));
    buf.write(this->FMGL(a));
    
}

x10::lang::Reference* SeqRail1::_deserializer(x10aux::deserialization_buffer& buf) {
    SeqRail1* this_ = new (memset(x10aux::alloc<SeqRail1>(), 0, sizeof(SeqRail1))) SeqRail1();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void SeqRail1::_deserialize_body(x10aux::deserialization_buffer& buf) {
    Benchmark::_deserialize_body(buf);
    FMGL(N) = buf.read<x10_int>();
    FMGL(M) = buf.read<x10_int>();
    FMGL(a) = buf.read<x10::lang::Rail<x10_double >*>();
}

x10aux::RuntimeType SeqRail1::rtt;
void SeqRail1::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<Benchmark>()};
    rtt.initStageTwo("SeqRail1",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}

/* END of SeqRail1 */
/*************************************************/
