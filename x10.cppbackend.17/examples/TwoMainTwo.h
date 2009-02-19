#ifndef __TWOMAINTWO_H
#define __TWOMAINTWO_H
#include <x10lang.h>
#include "TwoMainOne.h"
using namespace x10::lang;
class x10__TwoMainTwo : public x10::lang::Object {
    /*public */ public : virtual void bar();
    /*public */ public : static void main(x10::ref<x10::array<x10::ref<String> > > x10__s);
    /*public */ public : x10__TwoMainTwo();
    public : static void* ArrayCopySwitch(x10_async_handler_t h, void* __arg);
    public : static void AsyncSwitch(x10_async_handler_t h, void* arg, int niter);
    public: static struct _GLOBAL_STATE {
    
        
    } GLOBAL_STATE;
    
};
#endif // __TWOMAINTWO_H
class x10__TwoMainTwo;
