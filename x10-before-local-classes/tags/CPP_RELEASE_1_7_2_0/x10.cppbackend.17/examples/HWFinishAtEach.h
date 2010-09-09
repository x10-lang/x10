#ifndef __HWFINISHATEACH_H
#define __HWFINISHATEACH_H

#include <x10lang.h>
using namespace x10::lang;
class HWFinishAtEach : public x10::lang::Object {
    private : static x10::ref<dist> x10__UNIQUE;
    public : static x10_int foo();
    public : static void main(x10::ref<x10::array<x10::ref<String> > > args);
    public : HWFinishAtEach();
    public: static void* __static_init();
};
extern "C" {
    extern int main(int ac, char **av);
}

struct async__0_args : public x10::closure_args {
    async__0_args() { }
};

void async__0();

#endif

