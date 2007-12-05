/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_table.cc,v 1.1 2007-10-19 16:04:29 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for array table interface. **/

#include <iostream>
#include "array_table.h"
#include <x10/xassert.h>
#include <lapi.h>

using namespace std;

#define X10_MAX_ARRAYS 128

static array_info_t table[X10_MAX_ARRAYS];

namespace x10lib 
{  
  extern lapi_handle_t __x10_hndl;
  extern int __x10_my_place;
  extern int __x10_num_places;

  x10_err_t registerArray (const array_info_t a, const int handle)
  {
    assert (handle >= 0);
    
    assert (table[handle] == NULL);
    
    assert (handle >=0 && handle < X10_MAX_ARRAYS);
    
    table[handle] = (array_info_t) a;
    
    return X10_OK; 
  }
  
  array_info_t getLocalAddress (int handle)
  {
    assert (handle >=0 && handle < X10_MAX_ARRAYS);
    
    return table[handle];
  }
  
}
