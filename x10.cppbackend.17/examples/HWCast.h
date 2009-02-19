#ifndef __HWCAST_H
#define __HWCAST_H
#include <x10lang.h>
using namespace x10::lang;
class HWCast : public Object {
    class S : public Object {
        public : S();
	public : virtual void id();
    };
    class C : public S {
        public : C();
	public : virtual void id();
    };
    public : static void foo(x10::ref<S> y);
    public : static void bar(x10::ref<C> x);
    public : static void main(x10::ref<x10::array<x10::ref<String> > > args);
    public : HWCast();
};
#endif //HWCAST_H
