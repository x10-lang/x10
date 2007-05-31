/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: gas.h,v 1.4 2007-05-31 11:25:57 ganeshvb Exp $ */

#ifndef __X10_GAS_H__
#define __X10_GAS_H__

#include "types.h"

#ifdef __cplusplus
namespace x10lib {
  

  place_t ID;
  
  int MAX_PLACES;
  
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
#endif

#ifdef __cplusplus
extern "C" 
{
#endif
  place_t x10_here();

  int x10_num_places();

#ifdef __cplusplus
}
#endif

#endif /*X10GAS_H_*/
