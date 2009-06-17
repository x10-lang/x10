/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: dist.h,v 1.15 2008-01-19 18:20:18 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_DIST_H__
#define __X10_DIST_H__

#include <x10/types.h>
#include <x10/region.h>
#include <x10/rectangular_region.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

  extern int __x10_num_places;

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
    
    static const Dist<1>* makeUnique () {
      
      return new UniqueDist();
    }
    
    static const Dist<RANK>* makeConst (const Region<RANK>* r, x10_place_t p) {
      
      return new ConstDist<RANK>(r, p);
    }
    

  public:
    
    Dist () {} 
    /** The index region for places and region should be the same. For each point in this
     * region, region(p) is mapped to places(p). Note: An array of places corresponds to
     * ARMCI's notion of a domain.
     */
    
    Dist (const Region<RANK>* region) :
      _region (region),
      _nplaces (x10lib::__x10_num_places)
    {
      _places = new x10_place_t [_nplaces];
      for (int i = 0; i < _nplaces; i++)
	_places[i] = i;
    }
    
    Dist (const Region<RANK>* region, x10_place_t* places, int nplaces) :
      _region (region),
      _nplaces (nplaces)
    {
      _places = new x10_place_t [_nplaces];
      memcpy (_places, places, sizeof(x10_place_t) * _nplaces);
    }
    
    Dist (const Dist<RANK>& other) :
      _region (other._region),
      _nplaces (other._nplaces)
    {
      _places = new x10_place_t [_nplaces];
      memcpy (_places, other.places, sizeof(x10_place_t) * _nplaces);
    }

    virtual Dist<RANK>* clone() const = 0;
   
    virtual RectangularRegion<RANK> restriction (int p) const = 0;
      
    virtual ~Dist () 
    {
      delete [] _places;
    }
    
    virtual const x10_place_t place (const Point<RANK>& p) const = 0;

    const Region<RANK>* region () const{

      return _region;
    }

    const int* places() const{

      return  _places;

    }
    
    const int nplaces() const {
      return _nplaces;
    }

    virtual int card() const = 0;

  protected:
    
    const Region<RANK>* _region;
    x10_place_t* _places;
    int _nplaces;
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
      Dist<RANK> (other._region, other._places, 1) 
    {

    } 

    virtual ConstDist<RANK>* clone() const 
    {
      return new ConstDist<RANK> (*this);
    }
    const x10_place_t place (const Point<RANK>& p) const
    { 
      return this->_places[0];
    }

    virtual int card() const {
      return this->_region->card();
    }

    virtual RectangularRegion<RANK> restriction (int p) const {
      assert (p == this->_places[0]);
      return *((RectangularRegion<RANK>*) this->_region);
    }

  };

  class UniqueDist : public Dist<1>
  {
    
  public:
    
    UniqueDist () :
      Dist<1>(new RectangularRegion<1>(Point<1>(__x10_num_places-1)))
    {

    } 
    
    UniqueDist (const Region<1>* region, x10_place_t* places) :
      Dist<1> (region, places, x10lib::__x10_num_places)
    {

    }

    UniqueDist (const UniqueDist & other) :
      Dist<1> (other._region, other._places, x10lib::__x10_num_places) 
    {

    } 

    virtual UniqueDist* clone() const 
    {
      return new UniqueDist (*this);
    }
  
    const x10_place_t place (const Point<1>& p) const 
    {
      return p.value(0);
    }
    
    virtual int card() const {
      return  1;
    }
    
    virtual RectangularRegion<1> restriction (int p) const {
      
      assert (p >= 0  && p < x10lib::__x10_num_places);
      
      return RectangularRegion<1> (Point<1>(p), Point<1>(p));
    }
  };  


  /* Works only for RANK = 1 */
  template <int RANK>
  class BlockDist : public Dist<RANK>
  {
    
  private:
    
    BlockDist (const Region<RANK>* region, x10_place_t* places, int* blkSize) :
      Dist<RANK> (region, places, x10lib::__x10_num_places)
    {
      assert (RANK == 1);
    }    
  };
 
  /* TODO : assumes |region| is perfectly divisible by nplaces */ 
  template <>
  class BlockDist<1> : public Dist<1>
  {
  public:
    
    BlockDist (const Region<1>* region, x10_place_t* places, int nplaces) :
      Dist<1> (region, places, nplaces),
      _blkSize (region->card() / nplaces)
    {
    }
    
    BlockDist (const BlockDist<1>& other) :
      Dist<1> (other._region, other._places, other._nplaces) 
    {
    
    } 

    virtual BlockDist<1>* clone() const 
    {
      return new BlockDist<1> (*this);
    }
  
    const x10_place_t place (const Point<1>& p) const 
    {
      return this->_places[p.value(0) / _blkSize];
    }
    
    virtual int card() const {
      return  _blkSize;
    }
    
    virtual RectangularRegion<1> restriction (int p) const{

      assert (p >= 0  && p < _nplaces);

      return RectangularRegion<1> (Point<1> (p * _blkSize), Point<1> ((p+1) * _blkSize -1));
    }

  private:

    int _blkSize;
  };    

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_DIST_H */
