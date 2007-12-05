/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_table.h,v 1.3 2007-11-12 07:56:58 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for array table interface. **/

#ifndef __X10_ARRAY_TABLE_H
#define __X10_ARRAY_TABLE_H


#include <x10/types.h>
#include <x10/err.h>

#define X10_MAX_RANK 7 //Following the FORTRAN convention.

#ifdef __cplusplus
namespace x10lib {
  
  struct array_info
  {
    void* data; 
    int nelements; 
    size_t elementSize;
    int rank;
    int sizes[X10_MAX_RANK];
    int stride[X10_MAX_RANK];
    int lda[X10_MAX_RANK];
  };
}


typedef x10lib::array_info* array_info_t;

namespace x10lib{
  
  x10_err_t registerArray (const array_info_t a, const int handle);
  
  array_info_t getLocalAddress (int handle);  
}
#endif

#endif
