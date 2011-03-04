/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: iter.h,v 1.6 2008-01-19 18:20:18 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ITER_H__
#define __X10_ITER_H__

#include <x10/region.h>
#include <math.h>

/* C++ Lang Interface */
#ifdef __cplusplus

/* For each iterator */

template <int RANK, void F (x10lib::Point<RANK>)>
void foreach (x10lib::Region<RANK>* r)
{
  for (int i = 0; i < r->card(); ++i)
    F (r->coord(i)); 
} 

/* specialization */
template <void F (x10lib::Point<1>)>
void foreach (x10lib::Region<1>* r)
{
  int start_i = r->origin().value(0);
  int end_i = r->diagonal().value(0);
  int step_i = r->stride().value(0);
  for (int i = start_i; i < end_i; i += step_i)
    F (x10lib::Point<1>(i)); 
}

/* specialization */
template <void F (x10lib::Point<2>)>
void foreach (x10lib::Region<2>* r)
{
  int start_i = r->origin().value(0);
  int end_i = r->diagonal().value(0);
  int step_i = r->stride().value(0);

  int start_j = r->origin().value(1);
  int end_j = r->diagonal().value(1);
  int step_j = r->stride().value(1);
  
  for (int i = start_i; i < end_i; i += step_i) 
    for (int j = start_j; j < end_j; j += step_j) 
      F (x10lib::Point<2>(i, j));   
}

/* rectangular 1-d, 2-d and 3-d iterators */

/* Row-major order */

/* rectangular 1-d, 2-d and 3-d iterators */

/* Row-major order */

#define for_local_1d(I, _, R) \
   for (int I = R.origin().value(0); I <= R.diagonal().value(0); I += R.stride().value(0)) \


#define for_local_2d(I, J, _, R) \
   for (int I = R.origin().value(0); I <= R.diagonal().value(0); I += R.stride().value(0)) \
     for (int J = R.origin().value(1); J <= R.diagonal().value(1); J += R.stride().value(1)) 

#define for_local_3d(I, J, K,  _, R) \
   for (int I = R.origin().value(0); I <= R.diagonal().value(0); I += R.stride().value(0)) \
     for (int J = R.origin().value(1); J <= R.diagonal().value(1); J += R.stride().value(1)) \
       for (int K = R.origin().value(2); K < R.diagonal().value(2); K += R.stride().value(2)) 
     

/* strict upper triangular iterator (only for 2D and SQUARE region) */

#define for_local_2d_upper(I, J, _, R) \
   for (int I = R.origin().value(0), off = R.origin().value(1) - R.origin().value(0); I <= R.diagonal().value(0); I += R.stride().value(0)) \
     for (int J = I +  1 + off; J <= R.diagonal().value(1); J += R.stride().value(1)) 

/* strict lower triangular iterator (only for 2D and SQUARE region) */

#define for_local_2d_lower(I, J, _, R) \
   for (int I = R.origin().value(0), off = R.origin().value(1) - R.origin().value(0); I <= R.diagonal().value(0); I += R.stride().value(0)) \
     for (int J = R.origin().value(1); J < I + off; J += R.stride().value(1)) 

/* diagonal iterator (only for 2D and SQUARE region)*/

#define for_local_2d_diag(I, J, _, R) \
   for (int I = R.origin().value(0), J = R.origin().value(1); I <= R.diagonal().value(0) ||  J <= R.diagonal().value(1); I += R.stride().value(0), J += R.stride().value(1)) 

#endif

#endif /* __X10_ITER_H */
