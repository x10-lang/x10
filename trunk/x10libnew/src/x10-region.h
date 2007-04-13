#ifndef X10REGION_H_
#define X10REGION_H_

using namespace X10::lang;
template<int RANK>
class X10_region_t {
	
	/** Return true if the point lies in the given region, otherwise false.
  	*/ 
	bool contains(const X10_point_t<RANK>& x) const = 0;
	
	/** Returns the ordinal number for this point in the canonical 
	 * (lexicographic) ordering of points in this region. Returns -1 if
	 * the point does not lie in the region.
	 */
	virtual int ord(const X10_point_t<RANK>& x) const = 0;
	
	/** Returns the point in the region whose ordinal is ord.
	 * What if ord is out of range?
	 */
	virtual X10_point_t<RANK> coord(int ord) const = 0;
	
	virtual bool isEqual(const X10_region_t<RANK>& x) const = 0;
	virtual bool isConvex() const = 0;
	virtual bool isDisjoint(const Region<RANK>& r) const = 0;
}

template<int RANK>
class X10_rectangular_region_t : private x10_region_t<RANK>
{
  public:
 
 /** Return the rectangular region whose origin is [0,...,0] and with the given diagonal.
  */
  X10_rectangular_region_t (const X10_point_t<RANK>& diagonal);
 
  X10_rectangular_region_t (const X10_point_t<RANK>& origin, const X10_point_t<RANK>& diagonal);

  /** Reset the origin to newOrigin.
   */
  void translate (const X10_point_t<RANK>& newOrigin);
  
  /** Reset the diagonal to newDiagonal.
   */
  void reshape (const X10_point_t<RANK>& newDiagonal);

  // TBD: add various operations on regions, such as intersection, union, set difference.
  // TBD: Permit strided regions.

}; 

/** A tiled region is a partitioning of a base region into a collection of subregions, one for
 * each point in a given indexing region. For instance take base region to be [0:7,0:7] and
 * indexing region to be [0:1,0:1] can be tiled as: [0,0] -> [0:3,0:3], [0,1]->[0:3,3:7], 
 * [1,0] -> [3:7,0:3], [1,1] -> [3:7,3:7].
 */ 
template<int RANK>
class X10_tiled_region_t : private X10_region_t<RANK> 
{
	public:
 
  /** A general constructor that permits the user to supply an array of regions.
   */
  X10_tiled_region_t (const X10_array_t<X10_region_t,RANK>& regionArray);
  
  /** Return the underlying array of regions.
   */
  X10_array_t<X10_region_t,RANK> mapping();
  
  /** Returns a tiled region obtained by block dividing the given region across
   * grid. The tiles are obtained by blocking in each dimension. Note the rank
   * of the grid has nothing to do with the rank of region.
   * Q: Is the code below the right way to say this?
   */
  static X10_tiled_region_t<RANK> makeBlock(const X10_region_t<RANK> region, const X10_region_t grid)
  
  /** As in makeBlock, except that the region is blocked in only one dimension, as specified by 
   * dim. 0 <= dim <= RANK-1.
   */
  static X10_tiled_region_t<RANK> makeBlock1d(const X10_region_t<RANK> region, int dim, const X10_region_t grid)
}
 
 /** A region transformation is a tiled region whose indexing region is identical to its base region.
  * Thus it maps each point in its index region to a region containing a single point. For instance
  * the tiled region with base region = index region = [0:1,0:1] that maps [i,j] to [j,i] is given by:
  * [0,0] -> [0:0,0:0], [0,1] -> [1:1,0:0], [1,0] -> [0:0,1:1], [1,1] -> [1:1,1:1].
  */

#endif /*X10REGION_H_*/
