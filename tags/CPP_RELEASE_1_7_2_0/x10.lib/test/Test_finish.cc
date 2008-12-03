#include <iostream>
#include <x10/x10lib.h>

using namespace x10lib;
using namespace std;

class MathException  : public Exception
{
  public:
  MathException (x10_place_t _p)
    : p (_p) {}
  
  size_t size () { return sizeof (MathException); };

  MathException* actual () {return this;}
  void print () { cout << "Math exception " << p << endl; }
  x10_place_t proc() { return p;}

public:
  x10_place_t p;
  
};

class NullPointerException  : public Exception
{
  public:
  NullPointerException (x10_place_t _p) 
    : p (_p) {}

  size_t size () { return sizeof (NullPointerException); };

  NullPointerException* actual () {return this;}
  void print () { cout << "Null Pointer exception " << p << endl; }
  x10_place_t proc() { return p;}

public:
  int p;
};

int main ()
{
  Init(NULL, 0);

  Exception* exception  = NULL;
  int cs = 0; 
  cs = FinishStart (cs);
  try {
   if (here() % 2 == 1) throw new MathException (here());
  } catch (Exception* e) {
    exception = e;
  }

  try {
    FinishEnd (exception);
  } catch (MultiException me) {
    int sum1 = 0;
    int sum = 0;
    for (int i = 1; i < __x10_num_places; i += 2)      
      sum1 += i;
    for (int i = 0; i < __x10_num_places/2; i++)
      sum += me[i]->proc();
      assert (sum == sum1);
  }

  exception = NULL; 
  cs = 0;
  cs = FinishStart (cs);
  try {
    if (here() % 2 == 0) throw new NullPointerException (here());
  } catch (Exception* e ) {
    exception = e;  
  }

  try {
    FinishEnd (exception);
  } catch (MultiException me) {
    int sum1 = 0;
    int sum = 0;
    for (int i = 0; i < __x10_num_places; i += 2)      
      sum1 += i;
    for (int i = 0; i < __x10_num_places/2; i++)
      sum += me[i]->proc();
    assert (sum == sum1);
  }

  cout <<"Test_finish PASSED" << endl;

  cs = 0;
  cs = FinishStart (cs);
  

  Finalize();

  return 0;
}
