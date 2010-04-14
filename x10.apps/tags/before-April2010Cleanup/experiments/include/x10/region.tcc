/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: region.tcc,v 1.1 2007-08-02 11:22:44 srkodali Exp $ */

#include "region.h"
#include <iostream>

namespace x10lib {
using namespace std;

template <int RANK>
void RectangularRegion<RANK> :: init_ ()
{
  card_ = 1;
  for (int i = 0; i < RANK; i++)
    {
      size_[i] = (diagonal_.value(i) - origin_.value(i) + 1); //TODO: modify the formula for strided regions
      card_ *= size_[i];
    }
  
  linearStep_[RANK-1] = 1;
  for (int i = RANK-2; i >=0 ; i--)
    linearStep_[i] = size_[i+1] * linearStep_[i+1]; //TODO: modify formula for strided regions
}

template <int RANK>
void RectangularRegion<RANK> :: translate (const Point<RANK>& newOrigin)
{
  assert(false);
}


template <int RANK>
void RectangularRegion<RANK> :: reshape (const Point<RANK>& newDiagonal)
{
  assert(false);
}

template <int RANK>
bool RectangularRegion<RANK> :: contains (const Point<RANK>& p) const
{
  
  //TODO: change the FORMULA for strided regions.
  
  for (int i = 0; i < RANK; i++)
    if (p.value (i) < this->origin_.value(i) ||  
	p.value (i) > this->diagonal_.value(i))
      return false;
  
  return true;
}

template <int RANK>
uint64_t RectangularRegion<RANK> :: ord(const Point<RANK>& p) const
{
  assert (this->contains(p));

  int ord = 0;
  
  //compute the dot product of two vectors p & linearStep_
  for (int i = 0; i < RANK; i++)
    {
      ord = ord + linearStep_[i] * (p.value(i) - origin_.value(i));
    }

  return ord;
}

template <int RANK>
Point<RANK> RectangularRegion<RANK> :: coord (int ord) const
{
  assert(false);
}

template <int RANK>
bool RectangularRegion<RANK> :: isEqual (const Region<RANK>& x) const
{
   assert(false);
}

template <int RANK>
bool RectangularRegion<RANK> :: isConvex () const
{
   assert(false);
}

template <int RANK>
bool RectangularRegion<RANK> :: isDisjoint (const Region<RANK>& x) const
{
   assert(false);
}

//===================== TiledRegion =======================================

//p should be in Index region
template<int RANK>
const RectangularRegion<RANK>* TiledRegion<RANK> :: regionAt (const Point<RANK>& p) const
{
  assert (this->contains(p));
  
  return bases_[this->ord(p)];
}

// p should be in one (ONLY ONE) of the sub-regions 
template<int RANK>
const Point<RANK> TiledRegion<RANK> :: indexOf (const Point<RANK>& p) const 
{
#ifdef DEBUG
  int found = 0;
  for (int i = 0; i < this->card_; i++)
    {
      if (bases_[i]->contains (p))
	found++;	  
    }
  assert (found == 1);
#endif	

  //cout << "Hello " << endl;
  
  int result[RANK];

  assert (bases_[0]);
  const int* bsize = bases_[0]->size();

  for (int i = 0; i < RANK; i++)
    result[i] = p.value (i) / bsize[i];
  
  return Point<RANK> (result);
}
}

// Local Variables:
// mode: C++
// End:
