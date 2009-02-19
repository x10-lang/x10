#include "TwoMainOne.h"
using namespace x10;
static x10::ref<Exception> EXCEPTION = NULL;
#include "TwoMainOne.inc"
//#line 2 "TwoMainOne.x10"
void x10__TwoMainOne::foo()
{
 {
    //#line 2 "TwoMainOne.x10"
    System::x10__out->println(String("Hello, world foo!"));
}
}
//#line 3 "TwoMainOne.x10"
/* template:Main { */
extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            x10__TwoMainOne::main(args);
        } catch(int exitCode) {
            x10::exitCode = exitCode;
        } catch(x10::__ref& e) {
            fprintf(stderr, "%d: ", (int)__here__);
            //fprintf(stderr, "Caught %p\n", ((const x10::ref<Exception>&)e)._val);
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
/* } */void x10__TwoMainOne::main(x10::ref<x10::array<x10::ref<String> > > x10__s)
{
if (__here__ != 0) goto SKIP_s1;
 {
    //#line 4 "TwoMainOne.x10"
    SKIP_s1: ;
    { // Inlined x10__TwoMainOne.foo()
        x10::ref<x10__TwoMainOne> __var1__ = (x10::ref<x10__TwoMainOne>)(new
                                                                         (x10::alloc<x10__TwoMainOne>())
                                                                         x10__TwoMainOne(
                                                                           ));
        x10::ref<x10__TwoMainOne> saved_this = __var1__;
        {
            //#line 2 "TwoMainOne.x10"
            System::x10__out->println(String("Hello, world foo!"));
        }
    }
    RETURN_0: ;
    if (__here__ != 0) goto SKIP_s2;
    ;
    //#line 5 "TwoMainOne.x10"
    ((x10::ref<x10__TwoMainTwo>)(new (x10::alloc<x10__TwoMainTwo>())
                                 x10__TwoMainTwo(
                                   )))->bar();
}
SKIP_s2: ;
}
//#line 1 "TwoMainOne.x10"
x10__TwoMainOne::x10__TwoMainOne() : x10::lang::Object()
{
    
}
struct x10__TwoMainOne::_GLOBAL_STATE x10__TwoMainOne::GLOBAL_STATE;
void* ArrayCopySwitch(x10_async_handler_t h, void* __arg) {
    switch (h) {
        
    }
return NULL;
}
void AsyncSwitch(x10_async_handler_t h, void* arg, int niter) {
    switch (h) {
        
    }
}
