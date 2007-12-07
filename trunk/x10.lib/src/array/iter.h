/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: iter.h,v 1.2 2007-12-07 14:08:58 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ITER_H__
#define __X10_ITER_H__

#include "point.h"
#include <math.h>


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
   for (int I = R.origin().value(0), J = R.origin().value(1); I <= R.diagonal().value(0) ||  J <= R.diagonal().value(1); I += R.stride().value(0), J += R.stride().value(1)) \

#endif
