/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: gas.h,v 1.1 2007-04-28 07:03:45 ganeshvb Exp $ */

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

}

#endif /*X10GAS_H_*/
