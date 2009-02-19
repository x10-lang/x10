#include "HWInterface.h"
using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;
x10::ref<String> HWInterface::HWIImp::hw() {
//    String str = String("Hello, World!");
////    cerr << "str = " << (const string&)(str) << endl;
//    x10::ref<String> res = str;
////    cerr << "res = " << (void*)(res.operator->()) << " .value = " << (const string&)(*res) << endl;
//    return res;
    return String("Hello, World!");
}
extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWInterface::main(args);
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
void HWInterface::main(x10::ref<x10::array<x10::ref<String> > > a) {
    // TODO: GC (ref-counting?)
//    x10::ref<HWIImp> hwi = (x10::ref<HWIImp>)(new (x10::alloc<HWIImp>()) HWIImp());
////    cerr << "hwi = " << (void*)(hwi.operator->()) << endl;
//    x10::ref<String> res = hwi->hw();
////    cerr << "res = " << (void*)(res.operator->()) << " .value = " << (const string&)(*res) << endl;
//    System::x10__out->println(res);
    System::x10__out->println((x10::ref<HWIImp>)(new (x10::alloc<HWIImp>()) HWIImp())->hw());
}

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) { }
void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
