/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: local_array.h,v 1.2 2007-12-10 05:59:33 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_LOCAL_ARRAY_H__
#define __X10_LOCAL_ARRAY_H__

#include "dist.h"
#include <x10/alloc.h>

#include "lapi.h"

#define X10_MAX_RANK 7

namespace x10lib{
    
  /* A Generic Array that is "readily" serializable.
   * That is, it has no pointers (except data) and no base type.
   * Should not have any virtual functions too.
   * This is useful to represent arrays whose type and rank is not known
   * For eg, when the array creation is truly one-sided the type and
   * rank is known only at the "creation" site, not at "participants" side
   */
  
  class GenericArray
  {    
  public:

    GenericArray () {}
    
    ~GenericArray() 
    {
      delete [] _data;
    }

  public:

    char* _data; 
    int _nelements; 
    size_t _elsize;
    
    /* TODO: all these should belong to Rankless Region;
     * Since there is no Rankless Region now, this is just a temp. hack
     */    
    int _rank;
    int _origin[X10_MAX_RANK];
    int _diagonal[X10_MAX_RANK];
    int _stride[X10_MAX_RANK];
    

    char* raw() const
    {
      return _data;
    }

    /* TODO: add all the array methods
     * TODO: make this the base class of other special arrays 
     */    
  };
  
  
  /** 
   * Fragmented Arrays,
   * but have a globally valid "handle"
   */
  
  template <typename T, int RANK>
  class LocalArray
  {   
  public:   
    
    LocalArray (RectangularRegion<RANK>* region, int handle) : 
      _region (region->clone()),
      _data (new T [region->card()]),
      _handle (handle)
    {}
     
    operator T* () const 
    {
      return _data;
    }
      
    const Region<RANK>* region() const
    {
      return _region; 
    }
    
    /** A pointer to the local chunk of memory used to store the elements of the array.
     */
    T* raw() const
    {
      return _data;
    }
       
	     
    T& elementAt (const Point<RANK> p)
    {
      return _data[_region->ord(p)];
    }
	     

    int handle ()
    {
      return _handle;
    }

    ~LocalArray () 
    {     
      delete _region;
      delete [] _data;
    }
    
  private:
    
    const Region<RANK> * _region;
    
    T* _data;

    int _handle;
    
  };
} 
  
  
#endif /*X10ARRAY_H*/

// Local Variables:
// mode: C++
// End:
