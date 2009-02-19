#ifndef __HWEXCEPTION_H
#define __HWEXCEPTION_H

#include <x10lang.h>
using namespace x10::lang;
class HWException : public Object {
public:
    static void main(x10::ref<x10::array<x10::ref<String> > > args);
};
extern "C" {
    extern int main(int ac, char **av);
}

#endif

