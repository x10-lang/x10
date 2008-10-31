/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: region.h,v 1.6 2007-12-10 13:15:45 srkodali Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_REGION_H__
#define __X10_REGION_H__

#include <x10/point.h>
#include <math.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
  
  template<int RANK>
  class Region {
    
  public:

    Region () {} 
  
    /** Return true if the point lies in the given region, otherwise false.
     */ 
    virtual bool contains(const Point<RANK>& x) const = 0;

    virtual Region<RANK>* clone() const = 0;
  
    /** Returns the ordinal number for this point in the canonical 
     * (lexicographic) ordering of points in this region. Returns -1 if
     * the point does not lie in the region.
     */
    virtual long ord(const Point<RANK>& x) const = 0;
  
    /** Returns the point in the region whose ordinal is ord.
     * What if ord is out of range?
     */
    virtual Point<RANK> coord(long ord) const = 0;

    virtual const int card() const = 0;
    virtual const int* size() const = 0; 
        
    virtual bool isEqual(const Region<RANK>& x) const = 0;
    virtual bool isConvex() const = 0;
    virtual bool isDisjoint(const Region<RANK>& r) const = 0;
    
    virtual Point<RANK> origin() const = 0;
    virtual Point<RANK> diagonal() const = 0;
    virtual Point<RANK> stride() const = 0;

  };

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_REGION_H */
