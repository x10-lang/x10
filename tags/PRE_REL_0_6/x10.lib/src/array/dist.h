#ifndef __X10_DIST_H__
#define __X10_DIST_H__

#include "types.h"
#include "region.h"
#include "rectangular_region.h"

namespace x10lib {

  template<int RANK>
  class UniqueDist;

  template<int RANK>
  class ConstDist;

  /**
   * Distribution class (ABSTRACT)
   *
   */
  template <int RANK>
  class Dist
  {
  public:
    
    static const Dist<RANK>* makeUnique () {
      
      return new UniqueDist<1>();
    }
    
    static const Dist<RANK>* makeConst (const Region<RANK>* r, x10_place_t p) {
      
      return new ConstDist<1>(r, p);
    }
    

  public:
    
    Dist () {} 
    /** The index region for places and region should be the same. For each point in this
     * region, region(p) is mapped to places(p). Note: An array of places corresponds to
     * ARMCI's notion of a domain.
     */
    
    Dist (const Region<RANK>* region) :
      region_ (region),
      nplaces_ (__x10_num_places)
    {
      places_ = new x10_place_t [nplaces_];
      for (int i = 0; i < nplaces_; i++)
	places_[i] = i;
    }
    
    Dist (const Region<RANK>* region, x10_place_t* places, int nplaces) :
      region_ (region),
      nplaces_ (nplaces)
    {
      places_ = new x10_place_t [nplaces_];
      memcpy (places_, places, sizeof(x10_place_t) * nplaces_);
    }
    
    Dist (const Dist<RANK>& other) :
      region_ (other.region_),
      nplaces_ (other.nplaces_)
    {
      places_ = new x10_place_t [nplaces_];
      memcpy (places_, other.places, sizeof(x10_place_t) * nplaces_);
    }

    virtual Dist<RANK>* clone() const = 0;
 
    ~Dist () 
    {
      delete [] places_;
    }
    
    virtual const x10_place_t place (const Point<RANK>& p) const = 0;

    const Region<RANK>* region () const{

      return region_;
    }

    const int* places() const{

      return  places_;

    }

    virtual int card() const = 0;

  protected:
    
    const Region<RANK>* region_;
    x10_place_t* places_;
    int nplaces_;
  };

  /** A constant distribution maps every point in its underlying region to the same place.
   */ 
  template <int RANK>
  class ConstDist : public Dist<RANK>
  {
  public:
 
    ConstDist (const Region<RANK>* region, x10_place_t* p) :
      Dist<RANK> (region, p, 1) {}

    ConstDist (const ConstDist<RANK>& other) :
      Dist<RANK> (other.region_, other.places_, 1) 
    {

    } 

    virtual ConstDist<RANK>* clone() const 
    {
      return new ConstDist<RANK> (*this);
    }
    const x10_place_t place (const Point<RANK>& p) const
    { 
      return this->places_[0];
    }

    virtual int card() const {
      return this->region_->card();
    }

  };


  template <int RANK>
  class UniqueDist : public Dist<RANK>
  {

  public:

    UniqueDist () :
      Dist<RANK>(new RectangularRegion<RANK>(Point<RANK>(x10lib::__x10_num_places-1)))
    {
    } 
    
    UniqueDist (const Region<RANK>* region, x10_place_t* places) :
      Dist<RANK> (region, places, __x10_num_places)
    {
      
    }

    UniqueDist (const UniqueDist<RANK>& other) :
      Dist<RANK> (other.region_, other.places_, __x10_num_places) 
    {

    } 

    virtual UniqueDist<RANK>* clone() const 
    {
      return new UniqueDist<RANK> (*this);
    }
  
    const x10_place_t place (const Point<RANK>& p) const 
    {
      return this->places_[this->region_->ord (p)];
    }
    
    virtual int card() const {
      return  1;
    }
  };
  
}
#endif /*X10DIST_H_*/

// Local Variables:
// mode: C++
// End:
