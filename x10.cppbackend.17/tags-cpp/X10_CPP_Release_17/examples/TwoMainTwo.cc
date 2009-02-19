#include "TwoMainTwo.h"
using namespace x10;
static x10::ref<Exception> EXCEPTION = NULL;
#include "TwoMainTwo.inc"
//#line 2 "TwoMainTwo.x10"
void x10__TwoMainTwo::bar()
{
 {
    //#line 2 "TwoMainTwo.x10"
    System::x10__out->println(String("Hello, world bar!"));
}
}
//#line 3 "TwoMainTwo.x10"
void x10__TwoMainTwo::main(x10::ref<x10::array<x10::ref<String> > > x10__s)
{
 {
    //#line 4 "TwoMainTwo.x10"
    ((x10::ref<x10__TwoMainTwo>)(new (x10::alloc<x10__TwoMainTwo>()) x10__TwoMainTwo(
                                   )))->bar();
    //#line 5 "TwoMainTwo.x10"
    ((x10::ref<x10__TwoMainOne>)(new (x10::alloc<x10__TwoMainOne>())
                                 x10__TwoMainOne(
                                   )))->foo();
}
}
//#line 1 "TwoMainTwo.x10"
x10__TwoMainTwo::x10__TwoMainTwo() : x10::lang::Object()
{
    
}
struct x10__TwoMainTwo::_GLOBAL_STATE x10__TwoMainTwo::GLOBAL_STATE;
