#include "HWExtern.h"
#include "HWExtern_x10stub.c"

using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;

struct __init__0_args : public x10::closure_args {
    __init__0_args() { }
};
static x10_double __init__0(void *arg, x10::ref<point> p) {
    __init__0_args* args = (__init__0_args*)arg; // boilerplate
//    cerr << "__init__0: applying to " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
    x10_int i = p->operator[](0); // from exploded vars
//    cerr << "__init__0: got " << i << endl;
    return (x10_double)i;
}

x10::ref<x10::x10array<x10_double> > HWExtern::x10__arr;

void HWExtern::processArray(x10::ref<x10::x10array<x10_double> > a, x10_int i) {
    HWExtern_processArray(a->raw(), NULL, i);
}

extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWExtern::main(args);
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
void HWExtern::main(x10::ref<x10::array<x10::ref<String> > > s) {
//    cerr << "arr[0] = " << arr->operator[](0) << endl;
//    cerr << "arr[1] = " << arr->operator[](1) << endl;
    processArray(x10__arr, 0);
//    cerr << "arr[0] = " << arr->operator[](0) << endl;
//    cerr << "arr[1] = " << arr->operator[](1) << endl;
    System::x10__out->println(x10__arr->operator[](0));
}

HWExtern::HWExtern() : x10::lang::Object() { }

void* HWExtern::__static_init() {
    x10__arr = x10::x10newArray<x10_double>(((x10::ref<_region<1> >)(new (x10::alloc<_region<1> >())
	    _region<1>(0,1)))->toDistribution(),
	x10::__init_closure<x10_double>(__init__0, __init__0_args().ptr()).ptr());
    return NULL;
}
static void* __init__ = HWExtern::__static_init();

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
