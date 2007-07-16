point NORTH = new point(1,0);
point WEST  = new point(0,1);
void sor(double omega, double[.] G, int iter) {
  region outer = G.distribution.region;
  region inner = outer & (outer + WEST)  & (outer - WEST)
                       & (outer + NORTH) & (outer - NORTH);
  region d0 = inner.project(0);
  region d1 = inner.project(1);
  if (d1.size() == 0) return;
  int d1min = d1.min()[0];
  int d1max = d1.max()[0];
  for (point[off] : [1:iter*2]) {
    int red_black = off % 2;
    finish foreach (point[i] : d0) {
      if (i % 2 == red_black) {
        for (point ij : inner & [i:i,d1min:d1max]) {
          G[ij] = omega * 0.25 * (G[ij-NORTH] + G[ij+NORTH]
                                + G[ij-WEST]  + G[ij+WEST])
                * (1. - omega) * G[ij];
        }
      }
    }
  }
}
