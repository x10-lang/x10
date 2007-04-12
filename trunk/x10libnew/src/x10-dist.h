#ifndef X10DIST_H_
#define X10DIST_H_

/**
 * Update dist to be the distribution obtained by pointing the i'th region to the i'th place.
 */
extern int X10_dist_make(X10_region_t* regions, X10_place_t* places, int num, X10_dist_t* dist);

/**
  * Distribution class (ABSTRACT)
  *
  */
template <int RANK>
class X10_dist_t
{
public:

 /** The index region for places and region should be the same. For each point in this
  * region, region(p) is mapped to places(p). Note: An array of places corresponds to
  * ARMCI's notion of a domain.
  */
  X10_dist_t (const X10_tiled_region_t<Rank>& region, X10_array_t<X10_place_t>& places);
  
  virtual X10_place_t place (const X10_point_t<RANK>& p) const = 0;

protected:

   X10_region_t<RANK> region_;
   X10_place_t* places_;
  
};

/** A constant distribution maps every point in its underlying region to the same place.
 */ 
template <int RANK>
class X10_const_dist_t : private X10_dist_t<RANK>
{
public:
 
  X10_const_dist_t (const X10_region_t<Rank>& region, const X10_place_t& p);

   X10_place_t place (const X10_point_t<RANK>& p);

};



template <int RANK>
class X10_unique_dist_t : private X10_dist_t<RANK>
{

public:
 
  X10_unique_dist_t(const X10_region_t<Rank>& region, X10_place_t places);
  
  X10_place_t place (const X10_point_t<RANK>& p) const;
};

#endif /*X10DIST_H_*/
