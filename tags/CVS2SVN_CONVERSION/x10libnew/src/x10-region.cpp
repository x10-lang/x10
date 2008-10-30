#include "x10-region.h"

using namespace x10lib;

template <int RANK>
 RectangularRegion <RANK>  :: RectangularRegion (const Point<RANK>& diagonal)
{
	assert (false);
}	


template <int RANK>
RectangularRegion<RANK> :: RectangularRegion (const Point<RANK>& origin, const Point<RANK>& diagonal)
{
	assert (false);         
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
   assert(false);
}


template <int RANK>
int RectangularRegion<RANK> :: ord(const Point<RANK>& p) const
{
   assert(false);
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


template <int RANK>
TiledRegion<RANK> :: TiledRegion (const Array<Region<RANK>, RANK>& regionArray)
{
  
  assert(false);
	
} 

template <int RANK>
Array<Region<RANK>, RANK> TiledRegion<RANK> :: mapping()
{
  assert (false);
}



