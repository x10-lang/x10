#include <stdlib.h>
#include <stdio.h>
#include <time.h>

#define errorTolerance  1.0e-12

long long nano_time() {
    long long retVal;
    struct timespec tp;

    int rc = clock_gettime(CLOCK_REALTIME, &tp);
    retVal = (((long long) tp.tv_sec) * 1000000000) + tp.tv_nsec;

    return retVal;
}

static double computeFunction(double x)  {
  return (x * x + 1.0) * x;
}

static double recEval(double l, double r, double fl, double fr, double a) {
  double h = (r - l) * 0.5;
  double c = l + h;
  double fc = (c * c + 1.0) * c; 
  double hh = h * 0.5;
  double al = (fl + fc) * hh; 
  double ar = (fr + fc) * hh;
  double alr = al + ar;
  double diff = alr - a;
  if (diff < 0) diff = -diff;
  if (diff <= errorTolerance)
    return alr;
  ar = recEval(c, r, fc, fr, ar);
  return ar + recEval(l, c, fl, fc, al);
}

// accumulating version
static double accEval(double l, double r, double fl, double fr, double a) {
  double accum = 0.0;
  double h = (r - l) * 0.5;
  for (;;) {
    double c = l + h;
    double fc = (c * c + 1.0) * c; 
    h *= 0.5;
    double al = (fl + fc) * h; 
    double ar = (fr + fc) * h;
    double alr = al + ar;
    double diff = alr - a;
    if (diff < 0) diff = -diff;
    if (diff <= errorTolerance)
      return accum + alr;
    accum += accEval(c, r, fc, fr, ar);
    r = c;
    a = al;
    fr = fc;
  }
}


int main(int argc, char *argv[]) {
  double start = 0.0;
  double end =  1536.0;
  double fLower = computeFunction(start);
  double fUpper = computeFunction(end);
  double area =  0.5 * (end-start) * (fUpper+fLower);
  for (int i = 0; i < 10; ++i) {
    long long startTime = nano_time();
    //    double result = accEval(start, end, fLower, fUpper, area);
    double result = recEval(start, end, fLower, fUpper, area);
    long long endTime = nano_time();
    double secs = (endTime - startTime) / 1e9;
    printf("Time:  %7.3F  Result: %F\n", secs, result);
  }
  return 0;
}

