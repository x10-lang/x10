#include<iostream>
#include<fstream>
#include<math.h>

#define delta 1e-5

#define ABS(x) x 

using namespace std;

int main(int argc, char**argv) {

   ifstream refs(argv[1], ifstream::in); 
   ifstream cmp(argv[2], ifstream::in); 

  int ref_i, cmp_i;
  double ref_bc, cmp_bc;

  double sum = 0.0;
   while (refs.good()) {
 
      char a,b, c;
      refs >> a >> b >> c >> ref_i >>  ref_bc ;
      cmp >>  a  >> b >> c >> cmp_i >>  cmp_bc ;

       double diff = ref_bc - cmp_bc;
       diff = diff < 0 ? -diff:diff;
       if ((ABS(diff)) > delta) cout << ABS(ref_bc-cmp_bc) << " " << ref_i << " " << cmp_i << endl;

       sum += (ref_bc-cmp_bc)*(ref_bc-cmp_bc);
    }

    double l2norm = sqrt(sum);

    if (l2norm > delta) {

        cout << "error.. L2NORM: " << l2norm << endl;
    } else {
        cout << "success.. L2NORM: " << l2norm << endl;

    }
}
