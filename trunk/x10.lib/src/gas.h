/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: gas.h,v 1.2 2007-05-09 07:04:29 ganeshvb Exp $ */

#ifndef __X10_GAS_H__
#define __X10_GAS_H__

#include "types.h"

namespace x10lib {
  
  char* mallocSMGlobal (size_t nbytes);
  
  bool gas_islocal(gas_ref_t* ref);
  
  /**
   * Return the node id for the current node.
   */
  place_t here();

  int numPlaces();

  extern lapi_handle_t GetHandle();

  class Allocator;
  Allocator* GlobalSMAlloc;

  func_t* handlerTable;
}

#endif /*X10GAS_H_*/
