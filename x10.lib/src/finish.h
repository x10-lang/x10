/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: finish.h,v 1.6 2007-06-21 14:24:26 ganeshvb Exp $ */

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
