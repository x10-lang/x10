const point(:rank==2) NORTH = point.factory.point(1,0);
const point(:rank==2) WEST  = point.factory.point(0,1);

void sor(double omega, final double[:rank==2] G, int iter)
{
  region(:self==G.region & rank==2) outer = G.region;
  region(:G.region.contains(self) & rank==G.region.rank)
      inner = outer & (outer-NORTH) & (outer+NORTH)
                    & (outer-WEST)  & (outer+WEST);

  region d0 = inner.rank(0); // { i | (i,j) in inner }
  region d1 = inner.rank(1); // { j | (i,j) in inner }
  
  if (d1.size() == 0) return;
  
  final int d1min = d1.low();
  final int d1max = d1.high();
  
  for (int off = 1; off <= iter*2; off++)
      finish foreach (point[i]: d0)
          if (i % 2 == off % 2)
              for (point ij: inner & [i:i,d1min:d1max])
                  G[ij] = omega / 4.
                        * (G[ij-NORTH] + G[ij+NORTH]
                          + G[ij-WEST]  + G[ij+WEST])
                        * (1. - omega) * G[ij];
}
