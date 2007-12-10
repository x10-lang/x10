/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: array_table.h,v 1.5 2007-12-10 13:15:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for array table interface. **/

#ifndef __X10_ARRAY_TABLE_H
#define __X10_ARRAY_TABLE_H

#include <x10/types.h>
#include <x10/err.h>

//Following the FORTRAN convention...
#define X10_MAX_RANK 7 

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib{
  
  x10_err_t registerLocalSection (void*  a, const int handle);
  
  void* getLocalSection (int handle);  

  x10_err_t freeLocalSection (int handle);

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_ARRAY_TABLE_H */
