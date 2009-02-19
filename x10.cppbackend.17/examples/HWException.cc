#include "HWException.h"
using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;
extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWException::main(args);
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
void HWException::main(x10::ref<x10::array<x10::ref<String> > > args) {
    try {
        throw (x10::ref<RuntimeException>)(new (x10::alloc<RuntimeException>()) RuntimeException(String("Hello World")));
    }
    catch (x10::__ref& __ref__0) {
        x10::ref<Exception>& __exc__ref__0 = (x10::ref<Exception>&)__ref__0;
        if (INSTANCEOF(__exc__ref__0, x10::ref<RuntimeException>)) {
            x10::ref<RuntimeException> e = (x10::ref<RuntimeException>) __exc__ref__0;
            System::x10__out->println(e->getMessage());
            e->printStackTrace(System::x10__out);
        } else
            throw;
    }
}

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
