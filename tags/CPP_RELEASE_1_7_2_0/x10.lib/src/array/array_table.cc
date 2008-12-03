/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_table.cc,v 1.3 2007-12-10 13:15:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for array table interface. **/

#include <iostream>
#include <x10/array_table.h>
#include <x10/xassert.h>
#include <lapi.h>

using namespace std;

#define X10_MAX_ARRAYS 128

static void* table[X10_MAX_ARRAYS];

namespace x10lib 
{  
  extern lapi_handle_t __x10_hndl;
  extern int __x10_my_place;
  extern int __x10_num_places;

  x10_err_t registerLocalSection (void* a, const int handle)
  {
    assert (handle >= 0);
    
    assert (table[handle] == NULL);
    
    assert (handle >=0 && handle < X10_MAX_ARRAYS);
    
    table[handle] = a;
    
    return X10_OK; 
  }
  
  void* getLocalSection (int handle)
  {
    assert (handle >=0 && handle < X10_MAX_ARRAYS);
    
    return table[handle];
  }

  x10_err_t freeLocalSection (int handle)
  {
    assert (handle >=0 && handle < X10_MAX_ARRAYS);
    
    table[handle] = NULL;

    return X10_OK;
  }
  
}
