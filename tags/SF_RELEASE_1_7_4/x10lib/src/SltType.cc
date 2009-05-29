#include "SltType.h"  // class implemented

namespace x10lib
{

/////////////////////////////// PUBLIC ///////////////////////////////////////

//============================= LIFECYCLE ====================================

  SltType::SltType()
    : mRecord(NULL),
      mLabel(0)
  { //VOID
  }
  
//============================= OPERATORS ====================================
//============================= OPERATIONS ===================================

  void 
  SltType::Record(FinishRecord *record) 
  {
    mRecord = record;
  }
  
  void 
  SltType::Scope(FinishScope scope) 
  {
    mScope = scope;
  }
  
  void
  SltType::Label(int label) 
  {
    mLabel = label;
  }

//============================= ACCESS     ===================================

  FinishRecord* 
  SltType::pRecord() 
  {
    return mRecord;
  }
  
  FinishScope 
  SltType::Scope() 
  {
    return mScope;
  }
  
  int 
  SltType::Label() 
  {
    return mLabel;
  }

//============================= INQUIRY    ===================================
/////////////////////////////// PROTECTED  ///////////////////////////////////

/////////////////////////////// PRIVATE    ///////////////////////////////////

} //namespace x10lib



