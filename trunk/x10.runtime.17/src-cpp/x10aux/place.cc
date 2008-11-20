#include <x10aux/config.h>

#include <x10aux/place.h>

#include <x10/x10.h>

x10aux::place x10aux::__here__ = x10aux::here();

const x10_int x10aux::place::x10__MAX_PLACES = x10_nplaces();

void x10aux::place::__init__MAX_PLACES() {
    const_cast<x10_int&>(x10__MAX_PLACES) = x10_nplaces();
}

// vim:tabstop=4:shiftwidth=4:expandtab
