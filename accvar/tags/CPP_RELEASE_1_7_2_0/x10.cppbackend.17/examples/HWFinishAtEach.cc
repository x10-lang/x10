#include "HWFinishAtEach.h"
using namespace x10;
using namespace x10::lang;
static x10::ref<Exception> EXCEPTION = NULL;

x10::ref<dist> HWFinishAtEach::x10__UNIQUE;

x10_int HWFinishAtEach::foo() {
    return 0;
}

extern "C" {
    int main(int ac, char **av) {
        x10::array<x10::ref<String> >* args = x10::convert_args(ac, av);
        try {
            HWFinishAtEach::main(args);
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
void HWFinishAtEach::main(x10::ref<x10::array<x10::ref<String> > > args) {
    x10_int i;
    if (__here__ != 0) goto SKIP_s1;
    i = 0;
    i = 2 + 3;
    System::x10__out->println(String("Only at place 0"));
SKIP_s1: ;
    if (__here__ == 0) CS = 1;
    CS = x10::finish_start(CS); // finish#1
    if (1 != CS) goto SKIP_1;
    try {
        const x10::ref<point> p = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
        System::x10__out->println(String("Testing at each node: ") + p->operator[](0));
    } catch (x10::__ref& z) {
        EXCEPTION = (const x10::ref<Exception>&)z;
    }
    x10::finish_end(EXCEPTION); // finish#1
    CS = 0;
SKIP_1: ;
    if (__here__ != 0) goto SKIP_s2;
    if (true) {
        System::x10__out->println(String("Only at place 0"));
        i = i + 1;
    }
SKIP_s2: ;
    bool cond2;
    if (__here__ != 0) goto SKIP_c2;
    cond2 = false;
    CS = cond2 ? 2 : 3; // END_OF_c2=3
    if (!cond2) goto SKIP_TO_END_OF_c2;
SKIP_c2: ;
    {
        x10::ref<x10::array<x10::ref<String> > > s = args;
        if (__here__ != 0) goto SKIP_s3;
        i = i + 1;
SKIP_s3: ;
        CS = x10::finish_start(CS); // finish#2
        if (2 != CS) goto SKIP_2;
        try {
            const x10::ref<point> p = (x10::ref<_point<1> >)(new (x10::alloc<_point<1> >()) _point<1>(__here__));
            System::x10__out->println(String("Testing at each node."));
            for (x10_int q = 0; q < s->x10__length; ++q) {
                System::x10__out->println(s->operator[](q));
            }
//            if (HWFinishAtEach::x10__UNIQUE->get(p) == __here__) async__0();
//            else x10lib::asyncSpawnInlineAgg(HWFinishAtEach::x10__UNIQUE->get(p), 0, async__0_args().ptr(), sizeof(async__0_args));
            async_invocation(0, x10__UNIQUE->operator[](p), ());
            x10lib::asyncFlush(0, sizeof(async__0_args));
        } catch (x10::__ref& z) {
            EXCEPTION = (const x10::ref<Exception>&)z;
        }
        x10lib::asyncFlush(0, sizeof(async__0_args));
        x10::finish_end(EXCEPTION); // finish#2
        CS = 0;
SKIP_2: ;
    }
SKIP_TO_END_OF_c2: ;
    if (__here__ == 0) CS = 3;
    CS = x10::finish_start(CS); // finish#3
    if (3 != CS) goto SKIP_3;
    try {
        if (__here__ != 0) goto SKIP_s5;
        for (i = 0; i < 10; ++i)
            i++;
SKIP_s5: ;
    } catch (x10::__ref& z) {
        EXCEPTION = (const x10::ref<Exception>&)z;
    }
    x10::finish_end(EXCEPTION); // finish#3
    CS = 0;
SKIP_3: ;
}

HWFinishAtEach::HWFinishAtEach() : x10::lang::Object() { }

void async__0() {
    x10_int j = 0;
}

void* HWFinishAtEach::__static_init() {
    x10__UNIQUE = dist::x10__UNIQUE;
    return NULL;
}
static void* __init__ = HWFinishAtEach::__static_init();

void asyncSwitch(x10_async_handler_t h, void* arg, int niter) {
    switch (h) {
    case 0:
        {
        async__0_args* args = (async__0_args*) arg;
        for (int i = 0; i < niter; i++) {
            async__0_args* _arg = args++;
            async__0();
        }
        }
        break;
    }
}

void* arrayCopySwitch(x10_async_handler_t h, void* __arg) { return NULL; }
