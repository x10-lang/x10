#include "HWCast.h"
using namespace x10;
using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;
HWCast::S::S() : x10::lang::Object() { }
void HWCast::S::id() {
    System::x10__out->println(String("S"));
}
HWCast::C::C() : HWCast::S() { }
void HWCast::C::id() {
    System::x10__out->println(String("C"));
}
void HWCast::foo(x10::ref<HWCast::S> y) {
    x10::ref<HWCast::C> x = (x10::ref<HWCast::C>) y;
    x->id();
}
void HWCast::bar(x10::ref<HWCast::C> x) {
    x10::ref<HWCast::S> y = x;
    y = (x10::ref<HWCast::S>) x;
    y->id();
}
extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWCast::main(args);
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
void HWCast::main(x10::ref<x10::array<x10::ref<String> > > args) {
    foo((x10::ref<C>)(new (x10::alloc<C>()) C()));
    bar((x10::ref<C>)(new (x10::alloc<C>()) C()));
    System::x10__out->println(INSTANCEOF((x10::ref<C>)(new (x10::alloc<C>()) C()), x10::ref<S>));
    System::x10__out->println(INSTANCEOF((x10::ref<S>)(new (x10::alloc<S>()) S()), x10::ref<C>));
    x10::ref<C> c = (x10::ref<C>)(new (x10::alloc<C>()) C());
    System::x10__out->println(INSTANCEOF(c, x10::ref<S>));
    return;
}
HWCast::HWCast() : x10::lang::Object() { }

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
