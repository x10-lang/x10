#ifndef X10REGION_H_
#define X10REGION_H_

#include "x10-point.h"
#include <assert.h>

namespace x10lib {

template<int RANK>
class Region {

public:
	Region () {} 
	
	/** Return true if the point lies in the given region, otherwise false.
  	*/ 
	virtual bool contains(const Point<RANK>& x) const = 0;
	
	/** Returns the ordinal number for this point in the canonical 
	 * (lexicographic) ordering of points in this region. Returns -1 if
	 * the point does not lie in the region.
	 */
	virtual int ord(const Point<RANK>& x) const = 0;
	
	/** Returns the point in the region whose ordinal is ord.
	 * What if ord is out of range?
	 */
	virtual Point<RANK> coord(int ord) const = 0;
	
	virtual bool isEqual(const Region<RANK>& x) const = 0;
	virtual bool isConvex() const = 0;
	virtual bool isDisjoint(const Region<RANK>& r) const = 0;
};

template<int RANK>
class RectangularRegion : public Region<RANK>
{
  public:

  RectangularRegion() {}; //useful for declaring array of regions.
 
 /** Return the rectangular region whose origin is [0,...,0] and with the given diagonal.
  */
  RectangularRegion (const Point<RANK>& diagonal);
 
  RectangularRegion (const Point<RANK>& origin, const Point<RANK>& diagonal);

  /** Reset the origin to newOrigin.
   */
  void translate (const Point<RANK>& newOrigin);
  
  /** Reset the diagonal to newDiagonal.
   */
  void reshape (const Point<RANK>& newDiagonal);

   /** Return true if the point lies in the given region, otherwise false.
   */ 
   bool contains(const Point<RANK>& x) const ;
	  
    int ord(const Point<RANK>& x) const;
	
    Point<RANK> coord(int ord) const;
	
    bool isEqual(const Region<RANK>& x) const;
    bool isConvex() const;
    bool isDisjoint(const Region<RANK>& r) const;
   
  // TBD: add various operations on regions, such as intersection, union, set difference.
  // TBD: Permit strided regions.

}; 

template <typename T, int RANK> class Array;

/** A tiled region is a partitioning of a base region into a collection of subregions, one for
 * each point in a given indexing region. For instance take base region to be [0:7,0:7] and
 * indexing region to be [0:1,0:1] can be tiled as: [0,0] -> [0:3,0:3], [0,1]->[0:3,3:7], 
 * [1,0] -> [3:7,0:3], [1,1] -> [3:7,3:7].
 */ 
template<int RANK>
class TiledRegion : private Region<RANK> 
{

public:
 
  /** A general constructor that permits the user to supply an array of regions.
   */
  TiledRegion (const Array<Region<RANK>, RANK>& regionArray);
  
  /** Return the underlying array of regions.
   */
  Array<Region<RANK>,RANK> mapping();
  
  /** Returns a tiled region obtained by block dividing the given region across
   * grid. The tiles are obtained by blocking in each dimension. Note the rank
   * of the grid has nothing to do with the rank of region.
   */

  template <int RANK2>
  static TiledRegion<RANK> makeBlock(const Region<RANK> region, const Region<RANK2> grid)
  {
       assert (false);
  }
  /** As in makeBlock, except that the region is blocked in only one dimension, as specified by 
   * dim. 0 <= dim <= RANK-1.
   */
 
  template <int RANK2>
  static TiledRegion<RANK> makeBlock1d(const Region<RANK> region, int dim, const Region<RANK2> grid)
   {
      assert (false);
   }
 
 /** A region transformation is a tiled region whose indexing region is identical to its base region.
  * Thus it maps each point in its index region to a region containing a single point. For instance
  * the tiled region with base region = index region = [0:1,0:1] that maps [i,j] to [j,i] is given by:
  * [0,0] -> [0:0,0:0], [0,1] -> [1:1,0:0], [1,0] -> [0:0,1:1], [1,1] -> [1:1,1:1].
  */
};

}
#endif /*X10REGION_H_*/
