#ifndef X10RT_H
#define X10RT_H

// include everything from this file

#include <x10aux/config.h>

// has to be first to ensure initialisation of pgas occurs before uses of x10aux::alloc
#include <x10aux/network.h>

#include <x10aux/class_cast.h>
#include <x10aux/captured_lval.h>
#include <x10aux/reference_logger.h>
#include <x10aux/alloc.h>
#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/throw.h>
#include <x10aux/RTT.h>
#include <x10aux/assert.h>
#include <x10aux/static_init.h>
#include <x10aux/basic_functions.h>

#include <x10aux/itables.h>

#include <x10aux/array_utils.h>

#include <x10aux/math_utils.h>
#include <x10aux/system_utils.h>
#include <x10aux/processes.h>

#include <x10aux/place_local.h>

#include <x10aux/cuda_kernel.h>

#include <x10aux/vec_decl.h>

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
