#include "FinishScopeManager.h"  // class implemented


namespace x10lib 
{
  /////////////////////////////// PUBLIC ///////////////////////////////////////
  
  //============================= LIFECYCLE ====================================
  
  /*static*/ void
  FinishScopeManager::Init()
  {
    msScopeMgr = new FinishScopeManager();
  } //Init

  /*static*/ void
  FinishScopeManager::Finalize()
  {
  } //Finalize


  //============================= OPERATORS ====================================
  //============================= OPERATIONS ===================================

  FinishScope
  FinishScopeManager::CreateFinishScope()
  {
    return FinishScope(mScopeCtr++);
  }

  //============================= ACCESS      ===================================

  /*static*/ FinishScopeManager&
  FinishScopeManager::rInstance() 
  {
    return *msScopeMgr;
  }


  //============================= INQUIRY    ===================================
  /////////////////////////////// PROTECTED  ///////////////////////////////////

  /////////////////////////////// PRIVATE    ///////////////////////////////////

  FinishScopeManager::FinishScopeManager()
  {
    mScopeCtr = 0;
  }

  FinishScopeManager* FinishScopeManager::msScopeMgr = NULL;

} //namespace x10lib

