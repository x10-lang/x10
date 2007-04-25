#ifndef __X10_ASSERT__H__
#define __X10_ASSERT_H__


#ifdef WARN
#include <iostream>
using namespace std;

#define stringize(a) #a

#define assert(cond) \
  do {									\
    if ((cond) == 0)							\
      cout << "assert " << stringize(cond) << " failed: " << __FILE__  << ", line " \
      << __LINE__ <<  endl;						\
  }while(0) 

#else

#include <cassert>

#endif

#endif /* __X10_ASSERT__H__ */
