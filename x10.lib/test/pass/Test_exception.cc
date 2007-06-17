#include <iostream>
#include <x10/x10lib.h>

using namespace x10lib;
using namespace std;

class MathException  : public Exception
{
  public:
  MathException (place_t _p) : p(_p) {}

  size_t size () { return sizeof (MathException); };

  MathException* actual () {return this;}
  void print () { cout << "Math exception " << p << endl; }

  private:
  place_t p;
};

class NullPointerException  : public Exception
{
  public:
  NullPointerException (place_t _p) : p(_p) {}

  size_t size () { return sizeof (NullPointerException); };

  NullPointerException* actual () {return this;}
  void print () { cout << "Null Pointer exception " << p << endl; }

  private:
  place_t p;
};

int main ()
{
  Init(NULL, 0);

  Exception* exception  = NULL;
  int cs = 0; 
  cs = finishStart (cs);
  try {
   if (here() % 2 == 1) throw new MathException (here());
  } catch (Exception* e) {
    exception = e;
  }

  try {
    finishEnd (exception);
  } catch (MultiException me) {
    me.print();
  }

  exception = NULL; 
  cs = 0;
  cs = finishStart (cs);
  try {
    if (here() % 2 == 0) throw new NullPointerException (here());
  } catch (Exception* e ) {
    exception = e;  
  }

  try {
    finishEnd (exception);
  } catch (MultiException me) {
    me.print();
  }
  //cout <<"after finish end 2" << endl;

  cs = 0;
  cs = finishStart (cs);

  Finalize();

  return 0;
}
