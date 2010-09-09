#ifndef __HELLOWORLD_H
#define __HELLOWORLD_H

#include <x10lang.h>
//class HelloWorld : public x10::lang::Object {
//public:
//    static void main(x10::ref<x10::array<x10::ref<x10::lang::String> > > args);
//};
//extern "C" {
//    extern int main(int ac, char **av);
//}
using namespace x10::lang;
class HelloWorld : public Object {
public:
    static void main(x10::ref<x10::array<x10::ref<String> > > args);
};
extern "C" {
    extern int main(int ac, char **av);
}

#endif
