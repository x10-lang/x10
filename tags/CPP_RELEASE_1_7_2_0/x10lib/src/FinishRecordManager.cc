/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "FinishRecordManager.h"  // class implemented

namespace x10lib
{

/////////////////////////////// PUBLIC ///////////////////////////////////////

//============================= LIFECYCLE ====================================

  /*static*/ void 
  FinishRecordManager::Init()
  {
    mpObject = new FinishRecordManager();
  } //Init

  /*static*/ void
  FinishRecordManager::Finalize()
  {
    delete mpObject;
    mpObject = NULL;
  } //Finalize

  /*static*/ FinishRecordManager&
  FinishRecordManager::rInstance()
  {
    return *mpObject;
  }

//============================= OPERATORS ====================================

  FinishRecord*
  FinishRecordManager::Insert(const FinishScope& scope,
			      FinishRecord*      p_fr) 
  {
    mRecordsMap[scope] = p_fr;
    return p_fr;
  }

  void
  FinishRecordManager::Remove(const FinishScope& scope)
  {
    mRecordsMap.erase(scope);
  }


  FinishRecord*
  FinishRecordManager::pLookup(const FinishScope& scope)
  {
    return (*mRecordsMap.find(scope)).second;
  }


//============================= OPERATIONS ===================================
//============================= ACCESS     ===================================
//============================= INQUIRY    ===================================
/////////////////////////////// PROTECTED  ///////////////////////////////////

/////////////////////////////// PRIVATE    ///////////////////////////////////

  FinishRecordManager::FinishRecordManager()
  { //VOID
  }

  FinishRecordManager* FinishRecordManager::mpObject = NULL;

} //namespace x10lib

