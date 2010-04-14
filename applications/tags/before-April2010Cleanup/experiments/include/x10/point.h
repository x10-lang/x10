/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: point.h,v 1.1 2007-08-02 11:22:44 srkodali Exp $ */

#ifndef __X10_POINT_H__
#define __X10_POINT_H__

#include <x10/xassert.h>
#include <x10/gas.h>
#include <iostream>

using namespace std;

namespace x10lib{

  extern int __x10_my_place;
  extern int __x10_num_places;
  
  template<int RANK>
  class Point {
    
  public:
    
    Point (const int k) 
    {
      for (int i = 0; i < RANK; i++)
	values_[i] = k;
    }
    
    Point (const int* values) 
    {
      for (int i = 0; i < RANK; i++)
	values_[i] = values[i];
    }
    
    Point (const Point<RANK>& other)
    {
      for (int i = 0; i < RANK; i++)
	values_[i] = other.values_[i];
    }
    
    const int value (const int x) const
    {
      assert (x < RANK);
      return values_[x];
    }
    
  private:
    
    int values_[RANK]; 

  };
  
  template<>
  class Point<1>
  {
  public:
      
    Point (const int i) : 
      i_(i) 
    { }

    Point (const int* values) 
      :      i_ (values[0])
    {}


    Point (const Point<1>& other) :
      i_(other.i_) {}
	
    const int value (const int x) const
    {
      assert (x == 0);	  
      return i_;
    }

    const int valueI () const
    {
      return i_;
    }
	
  private:
	
    const int i_;
  };
  
  template<>
  class Point<2>
  {
  public:
    
    Point (const int k) :
      i_(k), j_(k) 
    {}

    Point (int i, int j) : 
      i_(i),j_(j) 
    {}

    Point (const int* values) : 
      i_(values[0]),
      j_ (values[1])
    {}

    Point (const Point<2>& other) :
      i_(other.i_),
      j_(other.j_) {}
    
    const int value (const int x) const
    {
      assert (x <= 1);
      return x == 0 ? i_ : j_;
    }
    
    const int valueI () const
    {
      return i_;
    }
    
    const int valueJ () const
    {
      return j_;
    }
    
  private:
    const int i_;
    const int j_;
  };
  

  template<>
  class Point<3>
  {
  public:
    Point (int i, int j, int k) : i_(i), j_(j), k_(k) {}
    
  private:
    const int i_;
    const int j_;
    const int k_;
  };
  
}

#endif /* __X10__POINT_H__*/


// Local Variables:
// mode: C++
// End:
