/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: array.tcc,v 1.2 2007-04-28 07:03:45 ganeshvb Exp $ */

#include "array.h"
#include "x10/gas.h"

using namespace x10lib;

template<typename T, int RANK>
Array<T, RANK>*
Array<T, RANK> :: makeArray (const Region<RANK>* region, const Dist<RANK>* dist)
{  
  T* data = (T*) mallocSMGlobal (region->totalCard() * sizeof (T));
  
  Array<T, RANK>* ret = new Array<T, RANK> (region, dist, data);

  return ret;
}

template <typename T, int RANK>
void 
Array <T, RANK> :: putScalarAt (const Point<RANK>& p, const T& val) 
{
  RectangularRegion<RANK>* r = region_->regionAt (p);

  const Point<RANK> index = region_->indexOf (p);

  assert (r->contains(p));

  assert (this->dist_->place(index) == here());
  
  int n = r->ord(p);
  
  putScalarAt (n, val);
}

template <typename T, int RANK>
void 
Array <T, RANK> :: putScalarAt (const int n, const T& val)
{
  assert (data_ != NULL);

  assert (n >=0 && n < this->region_->totalCard());
  
  data_[n] = val;
}

template <typename T, int RANK>
T& 
Array <T, RANK> :: getScalarAt (const Point<RANK>& p) const
{
  RectangularRegion<RANK>* r = region_->regionAt (p);

  const Point<RANK> index = region_->indexOf (p);

  assert (r->contains(p));

  assert (this->dist_->place(index) == here());
  
  int n = r->ord(p);
  
  return getScalarAt (n);
}

template <typename T, int RANK>
T& 
Array <T, RANK> :: getScalarAt (const int n) const
{
  assert (data_ != NULL);

  assert (n >=0 && n < this->region_->card());
  
  return data_[n];
}

template <typename T, int RANK>
Array<T, RANK>& 
Array <T, RANK> :: view (const Region<RANK>& subRegion) const
{
  assert (false);
}


//initialization routines
template <typename T, int RANK, typename CONST_INIT>
void 
initialize (Array<T, RANK>& arr, CONST_INIT op)
{
  assert (false);
}

template <typename T, int RANK, template <int RANK> class POINT_INIT> 
void initialize (Array<T, RANK>& arr, POINT_INIT<RANK> op);

//pointwise routines for standard operators
template <typename T, int RANK, int N, typename SCALAR_OP>
void iterate (Array<T, RANK> (&args) [N], order_t order,  const SCALAR_OP& op)
{
  assert (false);
}

template <typename T, int RANK, int N, typename SCALAR_OP>
void iterate (Array<T, RANK> (&args) [N], order_t order,  const SCALAR_OP (&op)[N-1])
{
  assert (false);
}

//reduce
template <typename T, int RANK, typename SCALAR_OP>
T reduce (Array<T, RANK> &arg, int dim, const SCALAR_OP& op)
{
  assert (false);
}

//scan
template <typename T, int RANK, typename SCALAR_OP>
Array<T, RANK> scan (Array<T, RANK> &arg, int dim, const SCALAR_OP& op)
{
  assert (false);
}

//restriction
template <typename T, int RANK>
Array<T, RANK>& restriction (const Dist<RANK>& R)
{
  assert (false);
}


//assembling
template <typename T, int RANK>
Array<T, RANK>& assemble (const Array<T, RANK>& a1, const Array<T, RANK>& a2)
{
  assert (false);
}

template <typename T, int RANK>
Array<T, RANK>& overlay (const Array<T, RANK>& a1, const Array<T, RANK>& a2)
{
  assert (false);
}

template <typename T, int RANK>
Array<T, RANK>& update (const Array<T, RANK>& a1, const Array<T, RANK>& a2)
{
  assert (false);
}

// Local Variables:
// mode: C++
// End:
