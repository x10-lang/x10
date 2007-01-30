/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "PthreadMutex.h"  // class implemented

namespace x10lib
{
  /////////////////////////////// PUBLIC ///////////////////////////////////////
  
  //============================= LIFECYCLE ====================================
  
  PthreadMutex::PthreadMutex()
  {
    pthread_mutex_init(&mMutex,NULL);
  }// PthreadMutex

  PthreadMutex::~PthreadMutex()
  {
    assert(pthread_mutex_destroy(&mMutex)==0);
  }// ~PthreadMutex


//============================= OPERATORS ====================================
//============================= OPERATIONS ===================================

  /*virtual*/ void 
  PthreadMutex::Lock() 
  {
    assert(pthread_mutex_lock(&mMutex) == 0);
  }
  
  /*virtual*/ bool
  PthreadMutex::TryLock()
  {
    return (pthread_mutex_trylock(&mMutex) != EBUSY);
  }

  /*virtual*/ void 
  PthreadMutex::Unlock()
  {
    assert(pthread_mutex_unlock(&mMutex)==0);
  }

//============================= ACCESS     ===================================
//============================= INQUIRY    ===================================
/////////////////////////////// PROTECTED  ///////////////////////////////////

/////////////////////////////// PRIVATE    ///////////////////////////////////


  namespace system
  {
    BaseMutex* CreateMutex()
    {
      return new PthreadMutex;
    }
  }
} //namespace x10lib

