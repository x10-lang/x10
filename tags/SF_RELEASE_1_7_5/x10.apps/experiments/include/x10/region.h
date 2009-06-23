/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: region.h,v 1.1 2007-08-02 11:22:44 srkodali Exp $ */

#ifndef __X10_REGION_H__
#define __X10_REGION_H__

#include "point.h"
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
    virtual uint64_t ord(const Point<RANK>& x) const = 0;
  
    /** Returns the point in the region whose ordinal is ord.
     * What if ord is out of range?
     */
    virtual Point<RANK> coord(int ord) const = 0;

    virtual const int card() const = 0;
    virtual const int totalCard() const = 0;
    virtual const int* size() const = 0; 

    virtual const Region<RANK>* regionAt (const Point<RANK>& p) const = 0;
    virtual const Point<RANK> indexOf (const Point<RANK>& p) const = 0;
    
    
    virtual bool isEqual(const Region<RANK>& x) const = 0;
    virtual bool isConvex() const = 0;
    virtual bool isDisjoint(const Region<RANK>& r) const = 0;

  };
  
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
      //assert (false); //strided regoins not yet implemented
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
    void translate (const Point<RANK>& newOrigin);
  
    /** Reset the diagonal to newDiagonal.
     */
    void reshape (const Point<RANK>& newDiagonal);
  
    /** Return true if the point lies in the given region, otherwise false.
     */ 
    bool contains(const Point<RANK>& x) const ;
  
    uint64_t ord(const Point<RANK>& x) const;
    
    virtual const RectangularRegion<RANK>* regionAt (const Point<RANK>& p) const 
    {
      return this;
    }

    virtual const Point<RANK> indexOf (const Point<RANK>& p) const
    {
      return p;
    }
  
    Point<RANK> coord(int ord) const;
  
    bool isEqual(const Region<RANK>& x) const;
    bool isConvex() const;
    bool isDisjoint(const Region<RANK>& r) const;
  
    // TBD: add various operations on regions, such as intersection, union, set difference.
  
    const int card() const
    {
      return card_;
    }

    const int* size() const 
    {
      return size_;
    }

    const int totalCard() const
    {
      return card_;
    }    
  
  protected :
  
    Point<RANK> origin_;
    Point<RANK> diagonal_;
    Point<RANK> stride_;

    int size_[RANK];
    int linearStep_[RANK]; //for efficiency, cache all the computations involved in ord in constructor

    int card_; //cardinality of the region

  private:
    
    void init_();
  }; 
  
  /** A tiled region is a partitioning of a base region into a collection of subregions, one for
   * each point in a given indexing region. For instance take base region to be [0:7,0:7] and
   * indexing region to be [0:1,0:1] can be tiled as: [0,0] -> [0:3,0:3], [0,1]->[0:3,3:7], 
   * [1,0] -> [3:7,0:3], [1,1] -> [3:7,3:7].
   */ 
  template<int RANK>
  class TiledRegion : public RectangularRegion<RANK> 
  {

  public:
 
    /** A general constructor that permits the user to supply an array of regions.
     * A very restricted implementation that works ONLY if all the
     * sub-regions are IDENTICAL (in size and shape) and 
     * they are stored in the LEXICOGRAPHIC order in the bases. 
     * Additionally, for i < j, bases[i] << bases[j], where a << b
     * mean region a LEXICOGRAPHICALLY preceeds region b.
     * EG: bases = {[0:3, 0:3], [0:3, 4:7], [4:7, 0:3], [4:7, 4:7]} is CORRECT
     * EG: bases =  {[0:3, 0:3], [4:7, 0:3], [0:3, 4:7], [4:7, 4:7]} is INCORRECT
     */

    TiledRegion () {} 

    TiledRegion (const Point<RANK>& diagonal, const RectangularRegion<RANK>** bases) : 
      RectangularRegion<RANK> (diagonal)
    {
      bases_ = new const RectangularRegion<RANK>*[this->card()];
      memcpy (bases_, bases, sizeof(RectangularRegion<RANK>*) * this->card());
      totalCard_ = 0;
      for (int i = 0; i < this->card(); i++)
	totalCard_ += bases_[i]->totalCard();
    }

    TiledRegion (const TiledRegion<RANK>& other) :
      RectangularRegion<RANK> (other.origin_, other.diagonal_, other.stride_),
      totalCard_ (other.totalCard_)
    {
      bases_ = new const RectangularRegion<RANK>*[this->card()];
      memcpy (bases_, other.bases_, sizeof(RectangularRegion<RANK>*) * this->card());
    } 

    TiledRegion<RANK>* clone () const 
    {
       return new TiledRegion<RANK>(*this);
    }

    
    /** Return the underlying array of regions.
     */
    RectangularRegion<RANK>** bases()
    {
      return bases_;
    }
        
    const RectangularRegion<RANK>* regionAt (const Point<RANK>& p) const;
    const Point<RANK> indexOf (const Point<RANK>& p) const;

    /** Returns a tiled region obtained by block dividing the given region across
     * grid. The tiles are obtained by blocking in each dimension. Note the rank
     * of the grid has nothing to do with the rank of region.
     */

    template <int RANK2>
    static TiledRegion<RANK>* makeBlock(const Region<RANK>* base, const Region<RANK2>* grid)
    {   
        assert (RANK == 1);
        const int* gridSize = grid->size();      
        const int* baseSize = base->size();      
        int newSize[RANK];
        const RectangularRegion<RANK>* regions[grid->card()];
        newSize[0] = baseSize[0] / gridSize[0]; 
 
        int offset[1];
        offset[0] = 0;
        for (int i = 0; i < gridSize[0]; i++) {
          regions[i] =  new RectangularRegion<RANK>(Point<RANK>(offset[0]),
                               Point<RANK>(newSize[0] + offset[0]-1));
          offset[0] += newSize[0];
       }

       return new TiledRegion<RANK> (Point<RANK>(gridSize[0]-1), regions);       
    }

    /** As in makeBlock, except that the region is blocked in only one dimension, as specified by 
     * dim. 0 <= dim <= RANK-1.
     */
 
    template <int RANK2>
    static TiledRegion<RANK> makeBlock1d(const Region<RANK> region, int dim, const Region<RANK2> grid)
    {
      assert (false);
    }

    const int totalCard() const
    {
      return totalCard_;
    }
   
    ~TiledRegion () {
      delete [] bases_;
    }
  private:
    
    const RectangularRegion<RANK>** bases_;

    int totalCard_;

    /** A region transformation is a tiled region whose indexing region is identical to its base region.
     * Thus it maps each point in its index region to a region containing a single point. For instance
     * the tiled region with base region = index region = [0:1,0:1] that maps [i,j] to [j,i] is given by:
     * [0,0] -> [0:0,0:0], [0,1] -> [1:1,0:0], [1,0] -> [0:0,1:1], [1,1] -> [1:1,1:1].
     */
  };

}

#include "region.tcc"

#endif /*X10REGION_H_*/

// Local Variables:
// mode: C++
// End:
