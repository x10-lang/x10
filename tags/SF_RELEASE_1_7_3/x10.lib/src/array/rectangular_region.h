/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: rectangular_region.h,v 1.6 2007-12-10 16:44:39 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_RECTANGULAR_REGION_H__
#define __X10_RECTANGULAR_REGION_H__

#include <x10/point.h>
#include <x10/region.h>

/* C++ Lang Interface **/
#ifdef __cplusplus
namespace x10lib {

  /**
   * Rectangular Region --
   * Represents a region in the closed range [lo, hi, s], where
   * lo is the origin.
   * hi is the diagonal.
   * s is the skip factor (stride).
   */

  template<int RANK>
  class RectangularRegion : public Region<RANK>
  {
  public:
    
    RectangularRegion() :
      origin_(0),
      diagonal_(0),
      stride_(0) {}; //useful for declaring array of regions.
    
    /** Return the rectangular region whose origin is [0,...,0] and with the given diagonal.
     */
    RectangularRegion (const Point<RANK>& diagonal) :
      origin_ (0),
      diagonal_ (diagonal),
      stride_ (1)
    {
      init_ ();
    }
    
    RectangularRegion (const Point<RANK>& origin, const Point<RANK>& diagonal) :
      origin_ (origin),
      diagonal_(diagonal),
      stride_(1)
    {      
#ifdef DEBUG
      for (int i = 0; i < RANK; i++)
	assert (origin_.value(i) <= diagonal_.value(i));
#endif
      init_ ();
    }

    RectangularRegion<RANK>* clone () const 
    {
      return new RectangularRegion<RANK> (*this);
    }
    
    RectangularRegion (const Point<RANK>& origin, const Point<RANK>& diagonal, const Point<RANK>& stride) :
      origin_ (origin),
      diagonal_(diagonal),
      stride_ (stride)
    {
#ifdef DEBUG
      for (int i = 0; i < RANK; i++)
	assert (origin_.value(i) <= diagonal_.value(i));
#endif
      init_ ();
    }
    
    RectangularRegion (const RectangularRegion<RANK>& other):
      origin_ (other.origin_),
      diagonal_ (other.diagonal_),
      stride_ (other.stride_),
      card_ (other.card_)
    {
      for (int i = 0; i < RANK; i++) {	
	linearStep_[i] = other.linearStep_[i];
	size_[i] = other.size_[i];
      }
    }
    
    /** Reset the origin to newOrigin.
     */
    void translate (const Point<RANK>& newOrigin) 
    {
      assert (false);
    }
    
    /** Reset the diagonal to newDiagonal.
     */
    void reshape (const Point<RANK>& newDiagonal)
    {
      assert (false);
    }
  
    /** Return true if the point lies in the given region, otherwise false.
     */ 
    bool contains(const Point<RANK>& p) const 
    {
      //TODO: change the FORMULA for strided regions.  
      for (int i = 0; i < RANK; i++)
	{
	  if (p.value (i) < this->origin_.value(i) ||  
	      p.value (i) > this->diagonal_.value(i) ||
	      (p.value (i) - this->origin_.value(i)) % stride_.value(i)  != 0)
	    {
	      return false;
	    }
	}

      return true;
    }
  
    long ord(const Point<RANK>& p) const 
    {
      assert (this->contains(p));
      
      int ord = 0;
      
      //compute the dot product of two vectors p & linearStep_
      for (int i = 0; i < RANK; i++)
	{
	  ord = ord + linearStep_[i] * (p.value(i) - origin_.value(i)) / stride_.value(i); 
	}
      
      return ord;
    }
    
    Point<RANK> coord(long idx) const
    {
       assert (idx >= 0 && idx < this->card());

       int ret [RANK];
       /* start with most significant dimension (dim 0) */
       for (int d = 0; d < RANK; ++d) {
        ret[d] = idx / linearStep_[d] + origin_.value(d);
        idx %=  linearStep_[d];
       }

      return Point<RANK> (ret);
    }
  
    bool isEqual(const Region<RANK>& x) const
    {
      assert(false);
      return false;
    }

    bool isConvex() const
    {
      assert(false);
      return false;
    }

    bool isDisjoint(const Region<RANK>& r) const
    {
      assert(false);
      return false;
    }
  
    const int card() const
    {
      return card_;
    }

    const int* size() const 
    {
      return size_;
    }
  
    Point<RANK> origin() const 
    {
      return origin_;
    }

 
    Point<RANK> diagonal() const 
    {
      return diagonal_;
    }

 
    Point<RANK> stride() const 
    {
      return stride_;
    }

    const int* lda() const 
    {
      return linearStep_;
    }

  protected :
  
    Point<RANK> origin_;
    Point<RANK> diagonal_;
    Point<RANK> stride_;

    int size_[RANK];
    int linearStep_[RANK]; //for efficiency, cache all the computations involved in ord in constructor

    int card_; //cardinality of the region

  private:
    
    void init_()
    {
      card_ = 1;

      for (int i = 0; i < RANK; i++)
	{
	  size_[i] = (int) (ceil((diagonal_.value(i) - origin_.value(i) + 1) / (double) stride_.value(i))); 
	  card_ *= size_[i];
	}      

      linearStep_[RANK-1] = 1;
      for (int i = RANK-2; i >=0 ; i--)
	linearStep_[i] = size_[i+1] * linearStep_[i+1]; 
    }
  };   

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_RECTANGULAR_REGION */
