/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: alloc.h,v 1.1 2007-08-02 11:22:37 srkodali Exp $ */

#ifndef __ALLOC_H__
#define __ALLOC_H__

#include <iostream>
#include <sys/types.h>

#include  <x10/xassert.h>
#include  <x10/gas.h> 
#include  <lapi.h>

using namespace std;

namespace x10lib{

  extern lapi_handle_t __x10_hndl;
  extern int __x10_num_places;
  extern int __x10_my_place;

  class Allocator
  {  
  public:

    Allocator(size_t size)  :
      offset_(0),
      size_(size)
    { 
      pointer_ = new char[size];
      addrTable_ = new void*[__x10_num_places];
      LAPI_Address_init (__x10_hndl, pointer_, addrTable_);
    }

    char* addr () const 
    {
      return pointer_;
    }  

    char* chunk (size_t size)
    {
      char* ret =  pointer_ + offset_;
      offset_ += size;
    
      return ret;
    }
 
    void* addrTable (int i)
    {
      return addrTable_[i];
    }  

    const uint64_t offset () const 
    {
      return offset_;
    }

    ~Allocator()
    {
      delete [] pointer_;
      delete [] addrTable_;
    }
  
  private:

    char* pointer_;
    void** addrTable_;
    uint64_t offset_;
    size_t size_;
  };
}

#endif

// Local Variables:
// mode: C++
// End:
