#include <sstream>
#include <iostream>
using std::cout;
using std::endl;
using namespace std;

#include "x10base.h"
#include "ActivityMaker.h"
#include "ActivityManager.h"
#include "Activity.h"
//#include "FinishScopeManager.h"

#include <stdlib.h>
#include <mpi.h>

#include <boost/serialization/access.hpp>
#include <boost/archive/binary_oarchive.hpp>
#include <boost/archive/binary_iarchive.hpp>
//export and base_object should be after all archives
#include <boost/serialization/export.hpp>
#include <boost/serialization/base_object.hpp>


using x10lib::ActivityMaker;
using x10lib::Activity;
using x10lib::FinishRecord;
using x10lib::ActivityManager;
using x10lib::FinishScopeManager;

enum NextChoiceT { NEXT_SELF,
		   NEXT_CYCLE,
		   NEXT_RANDOM};

class TreeActivity : public Activity
{
public:
  TreeActivity(FinishRecord&    fr,
	       Place            spawner,
	       int              count,
	       int              nextChoice,
	       int              nChildren,
	       int              childId)
    : Activity(fr, spawner),
      mCount(count),
      mNextChoice(nextChoice),
      mSpawner(spawner),
      mNumChildren(nChildren),
      mChildId(childId)
  {
  }

  x10lib::ProbeStatusType run();

private:
  int           mNextChoice;
  int           mCount;
  int           mSpawner;
  int           mNumChildren;
  int           mChildId;
}; //class TreeActivity

class TreeActivityMaker : public ActivityMaker
{
public:
  TreeActivityMaker(int count=0,
		    int nextChoice=-1,
		    int nChildren=0,
		    int childId=0) 
    : ActivityMaker(x10lib::Here()),
      mCount(count),
      mNextChoice(nextChoice),
      mNumChildren(nChildren),
      mChildId(childId)
  {}

  TreeActivity* Make(FinishRecord& fr,
		     Place         spawner) {
    return new TreeActivity(fr, spawner, mCount, 
			    mNextChoice, mNumChildren,
			    mChildId);
  }
private:
  int mCount;
  int mNextChoice;
  int mNumChildren;
  int mChildId;

  friend class boost::serialization::access;
  template<class Archive>
  void serialize(Archive& ar, unsigned int version) {
    ar & boost::serialization::base_object<ActivityMaker>(*this);
    ar & mCount & mNextChoice & mNumChildren & mChildId;
  }
}; //class TreeActivityMaker

BOOST_CLASS_EXPORT(TreeActivityMaker);

x10lib::ProbeStatusType TreeActivity::run()
{
  cout<<"TreeActivity at "<<x10lib::Here()<<" from "<<mSpawner
      <<". count="<<mCount<<" childId="<<mChildId<<endl;

  if(mCount) {
    Place next;
    switch(mNextChoice) {
    case NEXT_SELF:
      next = x10lib::Here();
      break;
    case NEXT_CYCLE:
      next = (x10lib::Here()+1)%x10lib::MaxPlaces();
      break;
    case NEXT_RANDOM:
      next = rand()%x10lib::MaxPlaces();
      break;
    default:
      assert(0);
    }

    --mCount;
    for(int i=0; i<mNumChildren; i++) {
      TreeActivityMaker maker(mCount, mNextChoice, mNumChildren, i);
      spawn(maker, (next+i)%x10lib::MaxPlaces());
    }
  }
  return x10lib::ACTIVITY_DONE;
}




int main(int argc, char *argv[]) 
{
  int count=10;
  x10lib::Init(argc,argv);

  //  TreeActivityMaker selfMaker(2,NEXT_SELF, 2, 0);
  // ActivityManager::rInstance().Process(selfMaker);
  
  TreeActivityMaker cycleMaker(3,NEXT_CYCLE, 2, 0);
  ActivityManager::rInstance().Process(cycleMaker);

//   TreeActivityMaker randomMaker(10,NEXT_RANDOM);
//   ActivityManager::rInstance().Process(randomMaker);


  MPI_Barrier(MPI_COMM_WORLD);
  x10lib::Finalize();
  return 0;
}
