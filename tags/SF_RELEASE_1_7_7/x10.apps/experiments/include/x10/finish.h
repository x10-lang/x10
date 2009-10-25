/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: finish.h,v 1.1 2007-08-02 11:22:43 srkodali Exp $ */

#ifndef __FINISH_H__
#define __FINISH_H__

#include <iostream>
#include <x10/err.h>
#include <x10/types.h>
#include <x10/gas.h>
#include <lapi.h>

namespace x10lib {

extern int __x10_inited;
extern lapi_handle_t __x10_hndl;
extern lapi_thread_func_t __x10_tf;
extern lapi_cntr_t __x10_wait_cntr;

extern int __x10_num_places;
extern int __x10_my_place;
extern int __x10_addr_hndl;
extern int __x10_addrtbl_sz;

class Exception{
    public:
      Exception () {}
      virtual size_t size() = 0;
      virtual void print() = 0;
  };

  class MultiException {
    public:
      MultiException (Exception** e, int n)
       : exceptions_(e), total_(n) {}

       void print () {
         for (int i = 0; i < total_; i++)
           exceptions_[i]->print();
       }

       int size() const { return total_; }
       Exception **const exceptions() const { return exceptions_; }

      ~MultiException () {
        //x10lib takes care of garbage collection/managing
        // each of the exceptions_ objects. So just
        //delete the exceptions_ array only.

        delete [] exceptions_; 
       } 
    private:
      int  total_;
      Exception** exceptions_; 
  };

  void finishEnd (Exception* a);
 
  int finishStart (int cs);
};


#endif
