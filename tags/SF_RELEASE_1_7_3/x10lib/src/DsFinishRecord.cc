/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "DsFinishRecord.h"  // class implemented

#include <sstream>
#include <boost/archive/binary_oarchive.hpp>
#include <boost/archive/binary_iarchive.hpp>
using namespace std;

#include "FinishRecordManager.h"
#include "x10base.h"
#include "system.h"
#include "NonThreadSafeInvokable.h"
#include "ActivityManager.h"

/**The implementation might not be thread-safe. The hash table storing
 * the incoming spawns only keeps incrementing. When local termination
 * is detected with all outgoing spawns acknowledged, the finish
 * record containing the hash table is destroyed. The implementation
 * of Cleanup() requires that all hash tables be allocated on the
 * heap. 
 */

namespace x10lib 
{
  /////////////////////////////// PUBLIC ///////////////////////////////////////

  //============================= LIFECYCLE ====================================

  DsFinishRecord::DsFinishRecord(const FinishScope &scope, 
				 Place              parent)
  {
    mScope             = scope;
    mParent            = parent;
    mLocalActiveCnt    = 0;
    mOutgoingSpawnsCnt = 0;
  } //DsFinishRecord

  /*static*/ void
  DsFinishRecord::Init()
  { 
    system::RegisterRemoteMethod(NotifyActivityCompletionAm);
  }

  /*static*/ void
  DsFinishRecord::Finalize()
  { 
    system::DeregisterRemoteMethod(NotifyActivityCompletionAm);
  }

  DsFinishRecord::~DsFinishRecord()
  {
    FinishRecordManager::rInstance().Remove(mScope);
  }// ~DsFinishRecord


  //============================= OPERATORS ====================================

  //============================= OPERATIONS ===================================

  /*virtual*/ void
  DsFinishRecord::LocalActivitySpawned(Place spawner)
  {
    mLocalActiveCnt += 1;
    if(mIncomingSpawnsMap.find(spawner) == mIncomingSpawnsMap.end()) {
      mIncomingSpawnsMap[spawner] = 1;
    }
    else {
      mIncomingSpawnsMap[spawner] += 1;
    }
  } //LocalActivitySpawned

  /*virtual*/ void
  DsFinishRecord::LocalActivityDestroyed(Place spawner) 
  {
    mLocalActiveCnt -= 1;
    if(mLocalActiveCnt == 0) {
      AckNonParentSpawners();      

      if(mOutgoingSpawnsCnt==0 && !IsRoot()) {
	AckParent();
	Cleanup();
      }
    }
  } //LocalActivityDestroyed

  /*virtual*/ void
  DsFinishRecord::RemoteActivitySpawned(Place target)
  {
    assert(target != Here());
    mOutgoingSpawnsCnt += 1;
  } //RemoteActivitySpawned

  /*virtual*/ FinishScope
  DsFinishRecord::Scope() const
  {
    return mScope;
  } //Scope

  //============================= ACCESS      ===================================
  //============================= INQUIRY    ===================================
  /*virtual*/ bool
  DsFinishRecord::HasTerminated() const
  {
    assert(IsRoot() == true);
    return mLocalActiveCnt==0 && mOutgoingSpawnsCnt==0;
  } //HasTerminated

  /////////////////////////////// PROTECTED  ///////////////////////////////////

  /////////////////////////////// PRIVATE    ///////////////////////////////////


  void
  DsFinishRecord::RemoteActivityAck(Place target,
				    int   count) 
  {
    assert(mOutgoingSpawnsCnt >= count); 
    mOutgoingSpawnsCnt -= count;

    if(mOutgoingSpawnsCnt==0 && mLocalActiveCnt==0 && !IsRoot()) {
      AckParent(); /*non-parents would have been messaged before*/
      Cleanup();
    }    
  } //RemoteActivityAck

  bool
  DsFinishRecord::IsRoot() const 
  {
    return x10lib::Here() == mScope.Root();
  } //IsRoot

  /**Note that this requires that FinishRecord be always allocated on
     the heap*/ 
  void
  DsFinishRecord::Cleanup()
  {
    delete this;
  } //Cleanup

  /**Private class to invoke non thread-safe function to acknowlegde
   * activity completion. It is enqueued as this might involve sending
   * of further messages.  
   */
  class DsActivityCompletion : public NonThreadSafeInvokable 
  {
  public:
    DsActivityCompletion(FinishScope scope,
			 int         count,
			 int         from)
      : mScope(scope), mCount(count), mFrom(from) {}
    
    void Invoke() 
    {
      DsFinishRecord *dfr = dynamic_cast<DsFinishRecord *>
	(FinishRecordManager::rInstance().pLookup(mScope));
      assert(dfr != NULL);
      dfr->RemoteActivityAck(mFrom, mCount);
    }
  private:
    FinishScope mScope;/**<Finish scope for which completion is notified*/ 
    int         mCount;/**<#completions being notified*/
    int         mFrom; /**<The place where the activities completed*/
  }; //class DsActivityCompletion

  /**FIXME: This am invokes RemoteActivityAck() which might end up
   * sending messages out. Communication inside an AM is not
   * allowed. Fix this. 
   */
  /*static*/ void
  DsFinishRecord::NotifyActivityCompletionAm(void* buf,
					     int   nbytes,
					     Place from)
  {
    FinishScope fs;
    int count;
    {
      istringstream iss(string((char *)buf, nbytes), ios::in);
      boost::archive::binary_iarchive tia(iss);
      tia & fs & count;
    }

    DsActivityCompletion *obj = new DsActivityCompletion(fs,count,from);
    ActivityManager::rInstance().AddNtsInvokableObject(obj);
  }

  /*static*/ void
  DsFinishRecord::InvokeNotifyActivityCompletion(FinishScope fs,
						 Place       dest,
						 int         count)
  {
    std::ostringstream oss;
    {
      boost::archive::binary_oarchive toa(oss);
      toa & fs & count;
    }
    system::InvokeRemoteMethod(NotifyActivityCompletionAm,
			       (void *)oss.str().c_str(),
			       oss.str().length(), 
			       dest);
  }
						 

  void
  DsFinishRecord::AckNonParentSpawners()
  {
    using std::map;
    for(map<Place,int>::iterator itr=mIncomingSpawnsMap.begin();
	itr != mIncomingSpawnsMap.end(); itr++) {
      if((*itr).first != mParent) {
	if((*itr).first != Here()) {
	  InvokeNotifyActivityCompletion(mScope, 
					 (*itr).first,
					 (*itr).second);
	}
	(*itr).second=0;
      }
    }
  }

  void
  DsFinishRecord::AckParent()
  {
    assert(mLocalActiveCnt==0 && mOutgoingSpawnsCnt==0);
    InvokeNotifyActivityCompletion(mScope, 
				   mParent, 
				   mIncomingSpawnsMap[mParent]);
    mIncomingSpawnsMap[mParent]=0;
  }

} //namespace x10lib

