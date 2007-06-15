/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: finish.h,v 1.3 2007-06-15 02:06:47 ipeshansky Exp $ */


#ifndef __FINISH_H__
#define __FINISH_H__

#include <iostream>
#include <x10/err.h>
#include <x10/types.h>
#include <x10/gas.h>

namespace x10lib {
  class Exception {
    public:
      Exception () {}
      virtual size_t size() = 0;
      virtual void testMessage() = 0;
  };

  class MultiException {
    public:
      MultiException (Exception** e, int n)
       : exceptions_(e), total_(n) {}

       void print () {
         for (int i = 0; i < total_; i++)
           exceptions_[i]->testMessage();
       }
       int size() const { return total_; }
       Exception **const exceptions() const { return exceptions_; }
      ~MultiException () {
        for (int i = 0; i < total_; i++)
          delete exceptions_[i];
        delete [] exceptions_; 
       } 
    private:
      int  total_;
      Exception** exceptions_; 
  };

  error_t finishEnd (Exception* a);
 
  error_t finishBegin (int* cs);
};


#endif
