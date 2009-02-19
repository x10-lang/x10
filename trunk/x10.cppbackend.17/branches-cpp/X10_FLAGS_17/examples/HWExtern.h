#ifndef __HWEXTERN_H
#define __HWEXTERN_H

#include <x10lang.h>
using namespace x10::lang;
class HWExtern : public Object {
    private: static x10::ref<x10::x10array<x10_double> > x10__arr;
    public: static void processArray(x10::ref<x10::x10array<x10_double> > a, x10_int i);
    public: static void main(x10::ref<x10::array<x10::ref<String> > > args);
    public: HWExtern();
    public: static void* __static_init();
};
extern "C" {
    extern int main(int ac, char **av);
}

#endif
