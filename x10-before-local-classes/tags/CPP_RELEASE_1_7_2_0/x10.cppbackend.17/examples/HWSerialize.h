#ifndef __HWSERIALIZE_H
#define __HWSERIALIZE_H

#include <x10lang.h>
using namespace x10::lang;
class HWSerialize : public x10::lang::Object {
    /*public */ public : static void main(x10::ref<x10::array<x10::ref<String> > > args);
    public : static void* ArrayCopySwitch(x10_async_handler_t h, void* __arg);
    public : static void AsyncSwitch(x10_async_handler_t h, void* arg, int niter);
    public : static x10_int __init__0(void*, x10::ref<point>);
    public : static x10::ref<place> __init__1(void*, x10::ref<point>);
    public: static struct _GLOBAL_STATE {
        x10::ref<place> h;
        x10::ref<point> q;
        x10::ref<region> r1;
        x10::ref<region> r2;
        x10::ref<dist> dU;
        x10::ref<dist> dU1;
        x10::ref<dist> dl;
        x10::ref<dist> de;
        x10::ref<x10::x10array<x10_int> > ia;
        x10::ref<x10::x10array<x10::ref<place> > > ip;
    } GLOBAL_STATE;
};
#endif //HWSERIALIZE_H

class HWSerialize;
