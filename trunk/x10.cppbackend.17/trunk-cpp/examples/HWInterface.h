#ifndef __HWINTERFACE_H
#define __HWINTERFACE_H

#include <x10lang.h>
#include "HWI.h"
using namespace x10::lang;
class HWInterface : public Object {
    class HWIImp : public Object, public HWI {
    public:
        virtual x10::ref<String> hw();
    };
public:
    static void main(x10::ref<x10::array<x10::ref<String> > > args);
};
extern "C" {
    extern int main(int ac, char **av);
}

#endif
