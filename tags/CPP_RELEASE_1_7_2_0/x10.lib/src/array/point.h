/*
 * (c) Copyright IBM Corporation 2007

 * $Id: point.h,v 1.12 2008-03-14 07:19:40 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_POINT_H__
#define __X10_POINT_H__

#include <x10/xassert.h>
#include <x10/gas.h>
#include <iostream>


/* C++ Lang Interface */

/* change the indices to x10_index_t */

#ifdef __cplusplus
namespace x10lib {

  /*  template <>
    class Point<-1> 
    {  
      Point (int rank, int* values) :
	_rank (rank)
	{ 
	  assert (rank < X10_MAX_RANK);
	  
	  for (int i = 0; i < rank; ++i)
	    _values[i] = values[i];	  
	}
      
      
      const int value (const int x) const
	{
	  assert (x < X10_MAX_RANK);
	  return _values[x];
	}
      
    private:
      int _rank;
      int _values [X10_MAX_RANK];
      }; */
  

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
      _i(i) 
    { }

    Point (const int* values) 
      :      _i (values[0])
    {}


    Point (const Point<1>& other) :
      _i(other._i) {}
	
    const int value (const int x) const
    {
      assert (x == 0);	  
      return _i;
    }

    const int valueI () const
    {
      return _i;
    }
	
  private:
	
    const int _i;
  };
  
  template<>
  class Point<2>
  {
  public:
    
    Point (const int k) :
      _i(k), _j(k) 
    {}

    Point (int i, int j) : 
      _i(i),_j(j) 
    {}

    Point (const int* values) : 
      _i(values[0]),
      _j (values[1])
    {}

    Point (const Point<2>& other) :
      _i(other._i),
      _j(other._j) {}
    
    const int value (const int x) const
    {
      assert (x <= 1);
      return x == 0 ? _i : _j;
    }
    
    const int valueI () const
    {
      return _i;
    }
    
    const int valueJ () const
    {
      return _j;
    }
    
  private:
    const int _i;
    const int _j;
  };
  

  template<>
  class Point<3>
  {
  public:

    Point (const int k) :
      _i(k), _j(k), _k(k)
    {}

    Point (int i, int j, int k) : _i(i), _j(j), _k(k) {}

    Point (const int* values) : 
      _i(values[0]),
      _j (values[1]),
      _k (values[2])
    {}
   
    const int value (const int x) const
    {
      assert (x <= 2);
      return x == 0 ? _i : 
	x == 1 ? _j : _k;
    }    
  private:
    const int _i;
    const int _j;
    const int _k;
  };

  template <int RANK> 
  bool isEqual (Point<RANK> a, Point<RANK> b)
   {
      for (int i = 0; i < RANK; i++) {
         if (a.value(i) != b.value(i))
            return false;
      } 
      return true;
   }   
  
} /* closing brace for namespance x10lib */
#endif

#endif /* __X10__POINT_H */
