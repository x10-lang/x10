#ifndef X10DIST_H_
#define X10DIST_H_

#include "x10-common.h"
#include "x10-region.h"

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

  Dist (const Region<RANK>& region, place_t* places) {}
  
  virtual place_t place (const Point<RANK>& p) const = 0;

protected:

   Region<RANK> region_;
   place_t* places_;
  
};

/** A constant distribution maps every point in its underlying region to the same place.
 */ 
template <int RANK>
class ConstDist : private Dist<RANK>
{
public:
 
  ConstDist (const Region<RANK>& region, const place_t& p) {}

   place_t place (const Point<RANK>& p) { assert (false); }

};



template <int RANK>
class UniqueDist : private Dist<RANK>
{

public:
 
  UniqueDist(const Region<RANK>& region, place_t places) {}
  
  place_t place (const Region<RANK>& p) const {assert(false);}
};

/**
 * Update dist to be the distribution obtained by pointing the i'th region to the i'th place.
 */
template <int RANK>
extern int make_dist(Region<RANK>* regions, place_t* places, int num, Dist<RANK>* dist);



}
#endif /*X10DIST_H_*/
