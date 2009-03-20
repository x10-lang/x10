/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "ActivityMaker.h"  // class implemented

#include "FinishRecordManager.h"
#include "ActivityManager.h"
#include "DsFinishRecord.h"

#include <assert.h>
#include <iostream>

#if 0
#include <boost/archive/binary_iacrhive.hpp>
#include <boost/archive/binary_oarchive.hpp>
#include <boost/serialization/is_abstract.hpp> //to declare as abstract
#include <boost/serialization/export.hpp>
#endif

/**Declare this class as abstract*/
BOOST_IS_ABSTRACT(x10lib::ActivityMaker);

BOOST_CLASS_EXPORT(x10lib::ActivityMaker);

namespace x10lib
{

  /////////////////////////////// PUBLIC ///////////////////////////////////////

  //============================= LIFECYCLE ====================================

  ActivityMaker::ActivityMaker(Place spawner)
    : mSpawner(spawner)
  { //VOID
  }

  ActivityMaker::~ActivityMaker()
  { //VOID
  }
  
  //============================= OPERATORS ====================================
  //============================= OPERATIONS ===================================
  void
  ActivityMaker::Scope(const FinishScope& rScope)
  {
    mScope = rScope;
  }

  FinishScope
  ActivityMaker::Scope() const
  {
    return mScope;
  }

  
  /**The invocation (from the main thread where non-thread-safe
   *  functions are allowed, creates an activity, and enqueues it into
   * the list of activities in the current place.
   */
  /*virtual*/ void
  ActivityMaker::Invoke()
  {
    if(mSpawner<0) {
      std::cerr<<"ActivityMaker not originally constructed with the spawning place ID"<<std::endl;
      assert(mSpawner >= 0); //We have a valid spawner
    }
    
    FinishRecordManager& frm = FinishRecordManager::rInstance();

    FinishRecord *fr = frm.pLookup(mScope);
    if(fr == NULL) {
      fr = frm.Insert(mScope, 
		      new DsFinishRecord(mScope,
					 mSpawner));
    }
    
    Activity* p_act = Make(*fr, mSpawner);
    ActivityManager::rInstance().AddActivity(p_act);
  }

  //============================= ACCESS     ===================================
  //============================= INQUIRY    ===================================
  /////////////////////////////// PROTECTED  ///////////////////////////////////

  /////////////////////////////// PRIVATE    ///////////////////////////////////
  ActivityMaker::ActivityMaker()
    : mSpawner(-1)
  { //VOID
  }

} //namespace x10lib

