#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/class_cast.h>
#include <x10aux/throw.h>

#include <x10/lang/ClassCastException.h>

using namespace x10aux;
using namespace x10::lang;

void x10aux::throwClassCastException() {
    throwException<x10::lang::ClassCastException>();
}


