#include "HelloWorld.h"
//static x10::ref<x10::lang::Exception> EXCEPTION = NULL;
//extern "C" {
//    int main(int ac, char **av) {
//        x10::array<x10::ref<x10::lang::String> >* args = x10::convert_args(ac, av);
//        try {
//            HelloWorld::main(args);
//        } catch(int exitCode) {
//            x10::exitCode = exitCode;
//        } catch(x10::__ref& e) {
//            fprintf(stderr, "%d: ", (int)__here__);
//            //fprintf(stderr, "Caught %p\n", e._val);
//            ((const x10::ref<x10::lang::Exception>&)e)->printStackTrace(System::x10__out);
//            x10::exitCode = 1;
//        } catch(...) {
//            fprintf(stderr, "%d: Caught exception\n", (int)__here__);
//            x10::exitCode = 1;
//        }
//        x10::free_args(args);
//        return x10::exitCode;
//    }
//}
//// the original app-main method
//void HelloWorld::main(x10::ref<x10::array<x10::ref<x10::lang::String> > > args) {
//    x10::lang::System::x10__out->println(x10::lang::String("Hello World"));
//    for (x10_int i = 0; i < args->x10__length; i++) {
//        x10::lang::System::x10__out->println(x10::lang::String("Arg ")+i+x10::lang::String(": ")+args[i]);
//    }
//    x10::ref<x10::lang::String> prefix = x10::lang::String("3 = ");
//    x10_double three = 3;
//    x10::lang::System::x10__out->println(prefix + three);
//}
using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;
extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HelloWorld::main(args);
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
void HelloWorld::main(x10::ref<x10::array<x10::ref<String> > > args) {
    System::x10__out->println(String("Hello World"));
    for (x10_int i = 0; i < args->x10__length; i++)
        System::x10__out->println(String("Arg ") + i + String(": ") + args->operator[](i));
    x10::ref<String> prefix = String("3 = ");
    x10_double three = 3;
    System::x10__out->println(prefix + three);
}

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
