#ifndef __TWOMAINONE_H
#define __TWOMAINONE_H
#include <x10lang.h>
#include "TwoMainTwo.h"
using namespace x10::lang;
class x10__TwoMainOne : public x10::lang::Object {
    /*public */ public : virtual void foo();
    /*public */ public : static void main(x10::ref<x10::array<x10::ref<String> > > x10__s);
    /*public */ public : x10__TwoMainOne();
    public : static void* ArrayCopySwitch(x10_async_handler_t h, void* __arg);
    public : static void AsyncSwitch(x10_async_handler_t h, void* arg, int niter);
    public: static struct _GLOBAL_STATE {
    
        
    } GLOBAL_STATE;
    
};
#endif // __TWOMAINONE_H
class x10__TwoMainOne;
