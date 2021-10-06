#include <HelloWholeWorld.h>
#include <x10aux/bootstrap.h>
extern "C" { int main(int ac, char **av) { return ::x10aux::template_main< ::HelloWholeWorld>(ac,av); } }
