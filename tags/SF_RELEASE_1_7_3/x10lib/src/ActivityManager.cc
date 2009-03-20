/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "ActivityManager.h"  // class implemented

#include <sstream>
#include <string>
#include <list>
#include <iostream>
using namespace std; //:KLUDGE: General one as I am not sure which
		     //namespace contains ios::in 

#include <mpi.h>    //FIXME: For mpi_barrier. for now
#include <assert.h>

#include <boost/archive/binary_oarchive.hpp>
#include <boost/archive/binary_iarchive.hpp>

#include "DsFinishRecord.h"
#include "FinishScope.h"
#include "FinishScopeManager.h"
#include "FinishRecordManager.h"
#include "ActivityMaker.h"
#include "Activity.h"
#include "system.h"
#include "BaseMutex.h"
#include "NonThreadSafeInvokable.h"

namespace x10lib
{

  /////////////////////////////// PUBLIC ///////////////////////////////////////

  //============================= LIFECYCLE ====================================

  /*static*/ void
  ActivityManager::Init()
  {
    system::RegisterRemoteMethod(NotifyTerminationAm);
    system::RegisterRemoteMethod(SpawnActivityAm);
    mpObject = new ActivityManager();
  } //Init

  /*static*/ void
  ActivityManager::Finalize()
  {
    system::DeregisterRemoteMethod(NotifyTerminationAm);
    system::DeregisterRemoteMethod(SpawnActivityAm);
    delete mpObject;
    mpObject = NULL;
  } //Finalize

  /*static*/ ActivityManager&
  ActivityManager::rInstance()
  {
    return *mpObject;
  } //rInstance
  

  //============================= OPERATORS ====================================
  //============================= OPERATIONS ===================================


  /**FIXME: The resetting of mHasTerminated at the start of the
   * function is not thread-safe. It is also set in the active method
   * that signals termination. Try to think of an alternative method
   * of resetting it without locks. Barrier?
   */
  void
  ActivityManager::Process(ActivityMaker& maker) 
  {
    mHasTerminated = false; //Reset termination

    FinishRecord *root_finish_record=NULL;
    FinishScopeManager&  fsm = FinishScopeManager::rInstance();
    FinishRecordManager& frm = FinishRecordManager::rInstance();
    
    if(Here()==0) {
      FinishScope fs = fsm.CreateFinishScope();
      root_finish_record = new DsFinishRecord(fs, Here());

      frm.Insert(fs, root_finish_record);
      maker.Scope(fs);

      Activity *act = maker.Make(*root_finish_record, Here());
      mReadyList.push_back(act);
    }

    MPI_Barrier(MPI_COMM_WORLD);
//     cerr<<Here()<<"::Entering process() main loop"<<endl;

    while(!HasTerminated() &&
	  !((Here()==0) && root_finish_record->HasTerminated())) {

      //      cerr<<Here()<<"::process(). Starting activity running"<<endl;

      /**Erasing an element at an iterator invalidates that
       * iterator. So working around it this way.
       */
      for(list<Activity *>::iterator itr= mReadyList.begin();
	  itr != mReadyList.end(); ) {
	Activity* act = *itr;
	if(act->run() == ACTIVITY_DONE) {
	  list<Activity*>::iterator itr1=itr;
	  itr++;
	  mReadyList.erase(itr1);
	  delete act;
	}
	else {
	  itr++;
	}
      }

      /**Flip the list being added to from the active methods. This
       * keeps mutex contention low
       */
      mpNtsListMutex->Lock();
      mCurNtsListIdx ^= 1;
      mpNtsListMutex->Unlock();

      int curIdx = mCurNtsListIdx^1;
      for(list<NonThreadSafeInvokable*>::iterator itr= mNtsLists[curIdx].begin();
	  itr != mNtsLists[curIdx].end(); itr++) {
	(*itr)->Invoke();
	delete (*itr);
      }
      mNtsLists[curIdx].clear();
      //      cerr<<Here()<<"::process(). Wrapping around"<<endl;
    }
//     cerr<<Here()<<"::Out of process() main loop"<<endl;
    if(Here()==0) {
      assert(root_finish_record!=NULL);
      delete root_finish_record;
      SignalGlobalTermination();
    }
  } //Process

  /**Note that it is important to serialize a pointer. I think things
   * don't work when you just serialize the reference. In particular,
   * there will be problem when deserializing with a pointer to the
   * base class (I think it expects to be serialized using a pointer
   * as well). 
   */
  void
  ActivityManager::InvokeSpawnActivity(ActivityMaker& maker,
				       Place          dest)
  {
    if(dest == Here()) {
      /*We are in the main thread. Can directly call Invoke()*/
      maker.Invoke();
    }
    else {
      ostringstream oss;
      {
	boost::archive::binary_oarchive toa(oss);
	ActivityMaker *const p_maker = &maker;
	toa & p_maker;
      }

      system::InvokeRemoteMethod(SpawnActivityAm, 
				 (void *)oss.str().c_str(),
				 oss.str().length(),
				 dest);
    }
  } //InvokeSpawnActivity

  void
  ActivityManager::AddActivity(Activity* act)
  {
    mReadyList.push_back(act);
  } //AddActivity

  void
  ActivityManager::AddNtsInvokableObject(NonThreadSafeInvokable* obj)
  {
    mpNtsListMutex->Lock();
    mNtsLists[mCurNtsListIdx].push_back(obj);
    mpNtsListMutex->Unlock();
  } //AddNtsInvokableObject

  //============================= ACCESS     ===================================
  //============================= INQUIRY    ===================================
  /////////////////////////////// PROTECTED  ///////////////////////////////////

  /////////////////////////////// PRIVATE    ///////////////////////////////////

  ActivityManager::ActivityManager()
  {
    mpNtsListMutex   = system::CreateMutex();
    mHasTerminated   = false;
    mCurNtsListIdx   = 0;
  } //ActivityManager

  ActivityManager::~ActivityManager()
  {
    delete mpNtsListMutex;
  }

  /*static*/ void
  ActivityManager::NotifyTerminationAm(void*, int, Place)
  {
    ActivityManager::rInstance().mHasTerminated=true;
  }

  /*static*/ void
  ActivityManager::SpawnActivityAm(void *buf,
				   int   nbytes,
				   Place from) 
  {
    ActivityMaker *maker;
    {
      std::istringstream iss(std::string((char *)buf, nbytes), ios::in);
      boost::archive::binary_iarchive bia(iss);
      bia & maker;
    }
    ActivityManager::rInstance().AddNtsInvokableObject(maker);
  }

  void
  ActivityManager::SignalGlobalTermination()
  {
    assert(Here()==0);
    mHasTerminated = true;
    for(int i=0; i<MaxPlaces(); i++) {
      if(i != Here()) {
	system::InvokeRemoteMethod(NotifyTerminationAm,NULL,0,i);
      }
    }
  } //SignalGlobalTermination

  bool
  ActivityManager::HasTerminated() const
  {
    return mHasTerminated;
  } //HasTerminated

  ActivityManager* ActivityManager::mpObject = NULL;
  
} //namespace x10lib

