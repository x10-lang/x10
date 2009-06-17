#include <sstream>
#include <iostream>
using std::cout;
using std::endl;
using namespace std;

#include "x10base.h"
#include "ActivityMaker.h"
#include "ActivityManager.h"
#include "Activity.h"
#include "Finish.h"
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

class TestActivityMaker;

class TestActivity : public Activity
{
public:
  TestActivity(FinishRecord&    fr,
	       Place            spawner,
	       int              count,
	       int              nextChoice)
    : Activity(fr, spawner),
      mCount(count),
      mNextChoice(nextChoice),
      mSpawner(spawner)
  {
  }

  x10lib::ProbeStatusType run();

private:
  int           mNextChoice;
  int           mCount;
  int           mSpawner;
  //temp variables for run() method
  Place         next;
  TestActivityMaker *maker;
}; //class TestActivity

class TestActivityMaker : public ActivityMaker
{
public:
  TestActivityMaker(int count=0,
		    int nextChoice=-1) 
    : ActivityMaker(x10lib::Here()),
      mCount(count),
      mNextChoice(nextChoice)
  {}

  TestActivity* Make(FinishRecord& fr,
		     Place         spawner) {
    return new TestActivity(fr, spawner, mCount, mNextChoice);
  }
private:
  int mCount;
  int mNextChoice;

  friend class boost::serialization::access;
  template<class Archive>
  void serialize(Archive& ar, unsigned int version) {
    ar & boost::serialization::base_object<ActivityMaker>(*this);
    ar & mCount & mNextChoice;
  }
}; //class TestActivityMaker

BOOST_CLASS_EXPORT(TestActivityMaker);

x10lib::ProbeStatusType TestActivity::run()
{
  ACTIVITY_START;
  cout<<"TestActivity at "<<x10lib::Here()<<" from "<<mSpawner
      <<". count="<<mCount<<endl;

  if(mCount) {
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

    maker = new TestActivityMaker(--mCount, mNextChoice);
    //spawn(maker, next);
    FINISH(*maker, next);
    
  }
  ACTIVITY_END;
  return x10lib::ACTIVITY_DONE;
}




int main(int argc, char *argv[]) 
{
  int count=10;
  x10lib::Init(argc,argv);

//   self_1c(count);
//   cycle_1c(count);
//   random_1c(count);

//  HelloActivityMaker HelloMaker;
//  ActivityManager::rInstance().Process(HelloMaker);

  TestActivityMaker selfMaker(10,NEXT_SELF);
  ActivityManager::rInstance().Process(selfMaker);
  
  TestActivityMaker cycleMaker(10,NEXT_CYCLE);
  ActivityManager::rInstance().Process(cycleMaker);

  TestActivityMaker randomMaker(10,NEXT_RANDOM);
  ActivityManager::rInstance().Process(randomMaker);


#if 0
  MPI_Barrier(MPI_COMM_WORLD);
  if(x10lib::Here()==0) {
    TestActivityMaker maker(0,NEXT_SELF);
    maker.Scope(FinishScopeManager::rInstance().CreateFinishScope());
    ActivityManager::rInstance().InvokeSpawnActivity(maker, 1);
  }
  MPI_Barrier(MPI_COMM_WORLD);
#endif

#if 0 
  TestActivityMaker *omaker = new TestActivityMaker(10, NEXT_SELF);
  ostringstream oss;
  {
    boost::archive::binary_oarchive boa(oss);
    boa & omaker;
  }

  istringstream iss(std::string(oss.str().c_str(), oss.str().length()), 
		    ios::in);
  ActivityMaker *imaker;
  TestActivityMaker *timaker;
  {
    boost::archive::binary_iarchive bia(iss);
    //#if 0
    bia & timaker;
    //#endif
  }
#endif

  MPI_Barrier(MPI_COMM_WORLD);
  x10lib::Finalize();
  return 0;
}
