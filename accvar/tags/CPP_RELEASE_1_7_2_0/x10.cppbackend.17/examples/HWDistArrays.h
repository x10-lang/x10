#ifndef __HWDISTARRAYS_H
#define __HWDISTARRAYS_H

#include <x10lang.h>
using namespace x10::lang;
class HWDistArrays : public Object {
    public: static void main(x10::ref<x10::array<x10::ref<String> > > s);
    public: HWDistArrays();
    public: static struct _GLOBAL_STATE {
                x10::ref<dist> D;
                x10::ref<x10::x10array<x10_double> > a;
            } GLOBAL_STATE;
    public: static x10_double __init__0(void*, x10::ref<point>);
};
extern "C" {
    extern int main(int ac, char **av);
}

#endif
