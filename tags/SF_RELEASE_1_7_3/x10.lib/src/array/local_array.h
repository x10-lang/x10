/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: local_array.h,v 1.4 2007-12-10 16:44:39 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_LOCAL_ARRAY_H__
#define __X10_LOCAL_ARRAY_H__

#include <x10/dist.h>

#include <lapi.h>

#define X10_MAX_RANK 7

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
    
  /** Generic Arrays - arrays with no rank and type .
   *  When the array creation is truly one-sided the type and
   *  rank is known only at the "creation" site, not at "participants" side
   *
   * A Generic Array is "readily" serializable.
   * That is, it has no pointers (except data) and no base type.
   * Should not have any virtual functions too.
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
    T* raw (Point<RANK> offset) const
    {
      return _data + _region->ord (offset);
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

} /* closing brace for namespace x10lib */ 
#endif
  
#endif /* __X10_LOCAL_ARRAY_H */
