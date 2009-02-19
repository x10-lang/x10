#ifndef __HWARRAYS_H
#define __HWARRAYS_H

#include <x10lang.h>
using namespace x10::lang;
class HWArrays : public Object {
    private: void _run_initializers();
    private: static x10::ref<x10::array<x10::ref<String> > > x10__data;
    private: static x10::ref<x10::x10array<x10::ref<HWArrays> > > x10__a;
    public: x10_int x10__value;
    public: x10::ref<x10::array<x10::ref<String> > > x10__z;
    public: x10_int x10__v;
    public: x10::ref<x10::array<x10::ref<String> > > x10__y;
    public: HWArrays(x10_int value);
    public: static void main(x10::ref<x10::array<x10::ref<String> > > s);
    public: static void* __static_init();
};
extern "C" {
    extern int main(int ac, char **av);
}

#endif
