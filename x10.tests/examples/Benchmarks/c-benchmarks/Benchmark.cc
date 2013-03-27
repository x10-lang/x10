/*************************************************/
/* START of Benchmark */
#include <Benchmark.h>


//#line 29 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c

//#line 30 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c

//#line 31 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c

//#line 33 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c
x10_double Benchmark::now() {
    
    //#line 33 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
    return ((((x10_double) (x10::lang::System::nanoTime()))) * (1.0E-9));
    
}

//#line 34 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c

//#line 38 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c

//#line 40 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c
x10_double Benchmark::FMGL(WARMUP);
void Benchmark::FMGL(WARMUP__do_init)() {
    FMGL(WARMUP__status) = x10aux::StaticInitController::INITIALIZING;
    _SI_("Doing static initialization for field: Benchmark.WARMUP");
    x10_double __var10__ = x10aux::class_cast_unchecked<x10_double>(30.0);
    FMGL(WARMUP) = __var10__;
    FMGL(WARMUP__status) = x10aux::StaticInitController::INITIALIZED;
}
void Benchmark::FMGL(WARMUP__init)() {
    x10aux::StaticInitController::initField(&FMGL(WARMUP__status), &FMGL(WARMUP__do_init), &FMGL(WARMUP__exception), "Benchmark.WARMUP");
    
}
volatile x10aux::StaticInitController::status Benchmark::FMGL(WARMUP__status);
x10::lang::CheckedThrowable* Benchmark::FMGL(WARMUP__exception);

//#line 41 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10FieldDecl_c
x10_double Benchmark::FMGL(TIMING);
void Benchmark::FMGL(TIMING__do_init)() {
    FMGL(TIMING__status) = x10aux::StaticInitController::INITIALIZING;
    _SI_("Doing static initialization for field: Benchmark.TIMING");
    x10_double __var11__ = x10aux::class_cast_unchecked<x10_double>(10.0);
    FMGL(TIMING) = __var11__;
    FMGL(TIMING__status) = x10aux::StaticInitController::INITIALIZED;
}
void Benchmark::FMGL(TIMING__init)() {
    x10aux::StaticInitController::initField(&FMGL(TIMING__status), &FMGL(TIMING__do_init), &FMGL(TIMING__exception), "Benchmark.TIMING");
    
}
volatile x10aux::StaticInitController::status Benchmark::FMGL(TIMING__status);
x10::lang::CheckedThrowable* Benchmark::FMGL(TIMING__exception);

//#line 43 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10ConstructorDecl_c
void Benchmark::_constructor() {
    
    //#line 43 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10ConstructorCall_c
    (this)->::harness::x10Test::_constructor();
    
    //#line 43 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.AssignPropertyCall_c
    
    //#line 44 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(out) = x10::io::Console::FMGL(OUT__get)();
}


//#line 47 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c
x10_boolean Benchmark::run() {
    
    //#line 50 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(this->FMGL(out))->x10::io::Printer::println(reinterpret_cast<x10::lang::Any*>(x10aux::makeStringLit("functional check")));
    
    //#line 51 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double warmup = this->now();
    
    //#line 52 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double result = this->once();
    
    //#line 53 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(result, this->expected()))) {
        
        //#line 54 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(this->FMGL(out))->x10::io::Printer::println(reinterpret_cast<x10::lang::Any*>(x10::lang::String::__plus(x10::lang::String::__plus(x10::lang::String::__plus(x10aux::makeStringLit("got "), result), x10aux::makeStringLit("; expected ")), this->expected())));
        
        //#line 55 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 59 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if (x10aux::equals(x10::lang::String::Lit("cpp"),reinterpret_cast<x10::lang::Any*>(x10aux::makeStringLit("java"))))
    {
        
        //#line 60 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(this->FMGL(out))->x10::io::Printer::println(
          reinterpret_cast<x10::lang::Any*>(x10::lang::String::__plus(x10::lang::String::__plus(x10aux::makeStringLit("warmup for >"), Benchmark::
                                                                                                                                         FMGL(WARMUP__get)()), x10aux::makeStringLit("s"))));
        
        //#line 61 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10While_c
        while (((((this->now()) - (warmup))) < (Benchmark::
                                                  FMGL(WARMUP__get)())))
        {
            
            //#line 62 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
            this->once();
        }
        
    }
    
    //#line 66 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(this->FMGL(out))->x10::io::Printer::println(
      reinterpret_cast<x10::lang::Any*>(x10::lang::String::__plus(x10::lang::String::__plus(x10aux::makeStringLit("timing for >"), Benchmark::
                                                                                                                                     FMGL(TIMING__get)()), x10aux::makeStringLit("s"))));
    
    //#line 67 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double avg = 0.0;
    
    //#line 68 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double min = x10::lang::DoubleNatives::fromLongBits(0x7ff0000000000000LL);
    
    //#line 69 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_int count = ((x10_int)0);
    
    //#line 70 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10While_c
    while (((avg) < (Benchmark::FMGL(TIMING__get)()))) {
        
        //#line 71 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
        x10_double start = this->now();
        
        //#line 72 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        this->once();
        
        //#line 73 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
        x10_double t = ((this->now()) - (start));
        
        //#line 74 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
        if (((t) < (min))) {
            
            //#line 75 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
            min = t;
        }
        
        //#line 76 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
        avg = ((avg) + (t));
        
        //#line 77 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
        count = ((x10_int) ((count) + (((x10_int)1))));
    }
    
    //#line 79 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10LocalAssign_c
    avg = ((avg) / (((x10_double) (count))));
    
    //#line 82 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10LocalDecl_c
    x10_double ops = ((this->operations()) / (avg));
    
    //#line 83 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(this->FMGL(out))->printf(x10aux::makeStringLit("time: %.3f; count: %d; min/time: %.2f\n"),
                                               x10aux::class_cast_unchecked<x10::lang::Any*>(avg),
                                               x10aux::class_cast_unchecked<x10::lang::Any*>(count),
                                               x10aux::class_cast_unchecked<x10::lang::Any*>(((min) / (avg))));
    
    //#line 84 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if (((ops) < (1000000.0))) {
        
        //#line 84 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(this->FMGL(out))->printf(x10aux::makeStringLit("%.3g kop/s\n"),
                                                   x10aux::class_cast_unchecked<x10::lang::Any*>(((ops) / (1000.0))));
    } else 
    //#line 85 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10If_c
    if (((ops) < (1.0E9))) {
        
        //#line 85 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(this->FMGL(out))->printf(x10aux::makeStringLit("%.3g Mop/s\n"),
                                                   x10aux::class_cast_unchecked<x10::lang::Any*>(((ops) / (1000000.0))));
    } else {
        
        //#line 86 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
        x10aux::nullCheck(this->FMGL(out))->printf(x10aux::makeStringLit("%.3g Gop/s\n"),
                                                   x10aux::class_cast_unchecked<x10::lang::Any*>(((ops) / (1.0E9))));
    }
    
    //#line 87 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": Eval of x10.ast.X10Call_c
    x10aux::nullCheck(this->FMGL(out))->printf(x10aux::makeStringLit("test=%s lg=x10-%s ops=%g\n"),
                                               reinterpret_cast<x10::lang::Any*>(x10aux::type_name(this)),
                                               reinterpret_cast<x10::lang::Any*>(x10::lang::String::Lit("cpp")),
                                               x10aux::class_cast_unchecked<x10::lang::Any*>(ops));
    
    //#line 90 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 27 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10MethodDecl_c
Benchmark* Benchmark::Benchmark____this__Benchmark() {
    
    //#line 27 "/home/dgrove/x10-trunk/x10.tests/examples/Benchmarks/Benchmark.x10": x10.ast.X10Return_c
    return this;
    
}
void Benchmark::_serialize_body(x10aux::serialization_buffer& buf) {
    harness::x10Test::_serialize_body(buf);
    buf.write(this->FMGL(out));
    
}

void Benchmark::_deserialize_body(x10aux::deserialization_buffer& buf) {
    harness::x10Test::_deserialize_body(buf);
    FMGL(out) = buf.read<x10::io::Printer*>();
    
}

x10aux::RuntimeType Benchmark::rtt;
void Benchmark::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<harness::x10Test>()};
    rtt.initStageTwo("Benchmark",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}

/* END of Benchmark */
/*************************************************/
