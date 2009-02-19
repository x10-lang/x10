#include "HWDistArrays.h"
using namespace x10;

using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;

struct __init__0_args : public x10::closure_args {
    __init__0_args() { }
};
x10_double HWDistArrays::__init__0(void *arg, x10::ref<point> p) {
    __init__0_args* args = (__init__0_args*)arg; // boilerplate
//    cerr << "__init__0: applying to " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
    x10_int i = p->operator[](0); // from exploded vars
//    cerr << "__init__0: got " << i << endl;
    return i * (x10_double) i;
}

HWDistArrays::HWDistArrays() : x10::lang::Object() { }

extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWDistArrays::main(args);
        } catch(int exitCode) {
            x10::exitCode = exitCode;
        } catch(x10::__ref& e) {
            fprintf(stderr, "%d: ", (int)__here__);
            //fprintf(stderr, "Caught %p\n", e._val);
            ((const x10::ref<Exception>&)e)->printStackTrace(System::x10__out);
            x10::exitCode = 1;
        } catch(...) {
            fprintf(stderr, "%d: Caught exception\n", (int)__here__);
            x10::exitCode = 1;
        }
        x10::free_args(args);
        return x10::exitCode;
    }
}
// the original app-main method
void HWDistArrays::main(x10::ref<x10::array<x10::ref<String> > > s) {
    x10::ref<x10::lang::dist> D  = (x10::ref<_dist_block>)(new (x10::alloc<_dist_block >()) _dist_block(
                (x10::ref<_region<1> >)(new (x10::alloc<_region<1> >()) _region<1>(1, x10::lang::place::x10__MAX_PLACES*10))));
    GLOBAL_STATE.D = D;
    x10::ref<x10::x10array<x10_double> > a =
        array_init_closure_invocation(0, D, x10_double, ());
    GLOBAL_STATE.a = a;
    if (__here__ == 0) CS = 1;
    CS = x10::finish_start(CS); // finish#1
    if (1 != CS) goto SKIP_1;
    try {
        const x10::ref<point> p = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
        {
            x10::ref<x10::lang::region> __var0__ = (D | (__here__))->x10__region;
            x10_int __var1__ = __var0__->rank(0)->high();
            for (x10_int i = __var0__->rank(0)->low(); i <= __var1__; i++) {
                System::x10__out->println(a->rawRegion()[i]);
            }
        }
    } catch (x10::__ref& z) {
        EXCEPTION = (const x10::ref<Exception>&)z;
    }
    x10::finish_end(EXCEPTION); // finish#1
    CS = 0;
    SKIP_1: ;
}

struct HWDistArrays::_GLOBAL_STATE HWDistArrays::GLOBAL_STATE;

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
