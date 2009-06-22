/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "Activity.h"  // class implemented

#include <sstream>
#include <boost/archive/binary_oarchive.hpp>
#include <boost/archive/binary_iarchive.hpp>
#include <assert.h>

#include "ActivityMaker.h"   //FinishScope()
#include "ActivityManager.h" //To spawn activities
#include "FinishRecord.h"


namespace x10lib
{

  /////////////////////////////// PUBLIC ///////////////////////////////////////

  //============================= LIFECYCLE ====================================

  Activity::Activity(FinishRecord&     rFinishRecord,
		     Place             spawner)
    : mrFinishRecord(rFinishRecord),
      mSpawner(spawner) 
  {
    mrFinishRecord.LocalActivitySpawned(spawner);
  }// Activity
  
  /*virtual*/
  Activity::~Activity()
  {
    mrFinishRecord.LocalActivityDestroyed(mSpawner);
  }// ~Activity
  

  //============================= OPERATORS ====================================
  //============================= OPERATIONS ===================================

  void
  Activity::spawn(ActivityMaker& maker,
		  Place          dest)
  {
    maker.Scope(mrFinishRecord.Scope());
    if(dest != Here())
      mrFinishRecord.RemoteActivitySpawned(dest);

    ActivityManager::rInstance().InvokeSpawnActivity(maker, dest);
  } //spawn

  //============================= ACCESS     ===================================
  //============================= INQUIRY    ===================================
  /////////////////////////////// PROTECTED  ///////////////////////////////////
  SltType&
  Activity::Slt()
  {
    return mSlt;
  }
  /////////////////////////////// PRIVATE    ///////////////////////////////////

} //namespace x10lib

