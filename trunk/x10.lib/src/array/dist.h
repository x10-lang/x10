#ifndef __X10_DIST_H__
#define __X10_DIST_H__

#include "types.h"
#include "region.h"

namespace x10lib{

  /**
   * Distribution class (ABSTRACT)
   *
   */
  template <int RANK>
  class Dist
  {
  
  public:
  
    /** The index region for places and region should be the same. For each point in this
     * region, region(p) is mapped to places(p). Note: An array of places corresponds to
     * ARMCI's notion of a domain.
     */

    Dist (const Region<RANK>* region, place_t* places) :
      region_ (region),
      places_ (places) {}
  
    ~Dist () 
    {
    }
    
    virtual const place_t place (const Point<RANK>& p) const = 0;
    
  protected:
    
    const Region<RANK>* region_;
    place_t* places_;
  
  };

  /** A constant distribution maps every point in its underlying region to the same place.
   */ 
  template <int RANK>
  class ConstDist : public Dist<RANK>
  {
  public:
 
    ConstDist (const Region<RANK>* region, const place_t p) :
      Dist<RANK> (region, new place_t (p)) {}

    const place_t place (const Point<RANK>& p) const
    { 
      return this->places_[0];
    }

  };


  template <int RANK>
  class UniqueDist : public Dist<RANK>
  {

  public:
 
    UniqueDist (const Region<RANK>* region, place_t* places) :
      Dist<RANK> (region, places)
    {
    
    }
  
    const place_t place (const Point<RANK>& p) const 
    {
      return this->places_[this->region_->ord (p)];
    }
  };
  
}
#endif /*X10DIST_H_*/

// Local Variables:
// mode: C++
// End:
