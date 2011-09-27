/*************************************************/
/* START of Benchmark */
#include <Benchmark.h>


//#line 29 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c

//#line 30 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c

//#line 31 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c

//#line 33 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c
x10_double Benchmark::now() {
    
    //#line 33 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
    return ((((x10_double) ((__extension__ ({
        x10aux::system_utils::nanoTime();
    }))
    ))) * (1.0E-9));
    
}

//#line 34 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c

//#line 38 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c
x10aux::ref<x10::lang::String> Benchmark::FMGL(lg);
void Benchmark::FMGL(lg__do_init)() {
    FMGL(lg__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: Benchmark.lg");
    x10aux::ref<x10::lang::String> __var10__ = x10aux::string_utils::lit("");
    FMGL(lg) = __var10__;
    FMGL(lg__status) = x10aux::INITIALIZED;
}
void Benchmark::FMGL(lg__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var11__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(lg__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var11__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(lg__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(lg),
                                                                    FMGL(lg__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(lg__status) != x10aux::INITIALIZED) {
        x10aux::StaticInitBroadcastDispatcher::lock();
        _SI_("WAITING for field: Benchmark.lg to be initialized");
        while (FMGL(lg__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
        _SI_("CONTINUING because field: Benchmark.lg has been initialized");
        x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__12 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(Benchmark::FMGL(lg__init));

volatile x10aux::status Benchmark::FMGL(lg__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> Benchmark::FMGL(lg__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(lg) = buf.read<x10aux::ref<x10::lang::String> >();
    Benchmark::FMGL(lg__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t Benchmark::FMGL(lg__id) = x10aux::StaticInitBroadcastDispatcher::addRoutine(Benchmark::FMGL(lg__deserialize));


//#line 40 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c
x10_double Benchmark::FMGL(WARMUP) = 30.0;


//#line 41 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c
x10_double Benchmark::FMGL(TIMING) = 10.0;


//#line 43 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10ConstructorDecl_c
void Benchmark::_constructor() {
    
    //#line 43 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<harness::x10Test>)this))->::harness::x10Test::_constructor();
    
    //#line 43 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.AssignPropertyCall_c
    
    //#line 44 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10FieldAssign_c
    ((x10aux::ref<Benchmark>)this)->FMGL(out) = x10::io::Console::FMGL(OUT);
}


//#line 47 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c
x10_boolean Benchmark::run() {
    
    //#line 50 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->FMGL(out))->x10::io::Printer::println(
      x10aux::string_utils::lit("functional check"));
    
    //#line 51 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double warmup = ((x10aux::ref<Benchmark>)this)->now();
    
    //#line 52 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double result = ((x10aux::ref<Benchmark>)this)->once();
    
    //#line 53 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(result, ((x10aux::ref<Benchmark>)this)->expected())))
    {
        
        //#line 54 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->
                            FMGL(out))->x10::io::Printer::println(
          ((((((x10aux::string_utils::lit("got ")) + (result))) + (x10aux::string_utils::lit("; expected ")))) + (((x10aux::ref<Benchmark>)this)->expected())));
        
        //#line 55 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 59 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if (x10aux::equals(x10::lang::String::Lit("cpp"),x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(x10aux::string_utils::lit("java"))))
    {
        
        //#line 60 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->
                            FMGL(out))->x10::io::Printer::println(
          ((((x10aux::string_utils::lit("warmup for >")) + (30.0))) + (x10aux::string_utils::lit("s"))));
        
        //#line 61 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10While_c
        while (((((((x10aux::ref<Benchmark>)this)->now()) - (warmup))) < (30.0)))
        {
            
            //#line 62 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
            ((x10aux::ref<Benchmark>)this)->once();
        }
        
    }
    
    //#line 66 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->FMGL(out))->x10::io::Printer::println(
      ((((x10aux::string_utils::lit("timing for >")) + (10.0))) + (x10aux::string_utils::lit("s"))));
    
    //#line 67 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double avg = 0.0;
    
    //#line 68 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double min = x10aux::double_utils::fromLongBits(0x7ff0000000000000LL);
    
    //#line 69 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_int count = ((x10_int)0);
    
    //#line 70 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10While_c
    while (((avg) < (10.0))) {
        
        //#line 71 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
        x10_double start = ((x10aux::ref<Benchmark>)this)->now();
        
        //#line 72 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        ((x10aux::ref<Benchmark>)this)->once();
        
        //#line 73 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
        x10_double t = ((((x10aux::ref<Benchmark>)this)->now()) - (start));
        
        //#line 74 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
        if (((t) < (min))) {
            
            //#line 75 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
            min = t;
        }
        
        //#line 76 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
        avg = ((avg) + (t));
        
        //#line 77 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
        count = ((x10_int) ((count) + (((x10_int)1))));
    }
    
    //#line 79 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
    avg = ((avg) / (((x10_double) (count))));
    
    //#line 82 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double ops = ((((x10aux::ref<Benchmark>)this)->operations()) / (avg));
    
    //#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->FMGL(out))->printf(
      x10aux::string_utils::lit("time: %.3f; count: %d; min/time: %.2f\n"),
      x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(avg),
      x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(count),
      x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(((min) / (avg))));
    
    //#line 84 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if (((ops) < (1000000.0))) {
        
        //#line 84 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->
                            FMGL(out))->printf(x10aux::string_utils::lit("%.3g kop/s\n"),
                                               x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(((ops) / (1000.0))));
    } else 
    //#line 85 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if (((ops) < (1.0E9))) {
        
        //#line 85 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->
                            FMGL(out))->printf(x10aux::string_utils::lit("%.3g Mop/s\n"),
                                               x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(((ops) / (1000000.0))));
    } else {
        
        //#line 86 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->
                            FMGL(out))->printf(x10aux::string_utils::lit("%.3g Gop/s\n"),
                                               x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(((ops) / (1.0E9))));
    }
    
    //#line 87 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(((x10aux::ref<Benchmark>)this)->FMGL(out))->printf(
      x10aux::string_utils::lit("test=%s lg=x10-%s ops=%g\n"),
      x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(x10aux::type_name(((x10aux::ref<Benchmark>)this))),
      x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(x10::lang::String::Lit("cpp")),
      x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(ops));
    
    //#line 90 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 27 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c
x10aux::ref<Benchmark> Benchmark::Benchmark____Benchmark__this(
  ) {
    
    //#line 27 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
    return ((x10aux::ref<Benchmark>)this);
    
}
void Benchmark::_serialize_body(x10aux::serialization_buffer& buf) {
    harness::x10Test::_serialize_body(buf);
    buf.write(this->FMGL(out));
    
}

void Benchmark::_deserialize_body(x10aux::deserialization_buffer& buf) {
    harness::x10Test::_deserialize_body(buf);
    FMGL(out) = buf.read<x10aux::ref<x10::io::Printer> >();
    
}

x10aux::RuntimeType Benchmark::rtt;
void Benchmark::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<harness::x10Test>()};
    rtt.initStageTwo("Benchmark",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
/* END of Benchmark */
/*************************************************/
