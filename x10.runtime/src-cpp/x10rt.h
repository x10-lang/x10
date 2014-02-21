#ifndef X10RT_H
#define X10RT_H

// include everything from this file

#include <x10aux/config.h>

// has to be first to ensure initialisation of x10rt occurs before uses of x10aux::alloc
// Dave G: 1/29/14.  The comment above was made about pgas_sockets; still true?
#include <x10aux/network.h>

#include <x10aux/throw.h>
#include <x10aux/RTT.h>
#include <x10aux/itables.h>
#include <x10aux/class_cast.h>
#include <x10aux/captured_lval.h>
#include <x10aux/alloc.h>
#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/network_dispatcher.h>
#include <x10aux/static_init.h>
#include <x10aux/basic_functions.h>

#include <x10aux/assert.h>

#include <x10aux/array_utils.h>

#include <x10aux/cuda_kernel.h>

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
