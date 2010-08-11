const NORTH: point{rank==2} = [1,0];
const WEST:  point{rank==2} = [0,1];

def sor(omega: double,
        G: Array[double]{rank==2},
        iter: int): void {
  outer: Region{self==G.region, rank==2} = G.region;
  inner: Region{G.region.contains(self),
                rank==G.region.rank}
          = outer & (outer-NORTH) & (outer+NORTH)
                  & (outer-WEST)  & (outer+WEST);

  d0: Region = inner.rank(0); // {i | (i,j) in inner}
  d1: Region = inner.rank(1); // {j | (i,j) in inner}
  
  if (d1.size() == 0) return;
  
  d1min: int = d1.low();
  d1max: int = d1.high();
  
  for (var off: int = 1; off <= iter*2; off++)
    finish foreach ((i): point in d0)
      if (i % 2 == off % 2)
        for (ij: point in inner & [i..i,d1min..d1max])
          G(ij) = omega / 4.
                * (G(ij-NORTH) + G(ij+NORTH)
                 + G(ij-WEST)  + G(ij+WEST))
                * (1. - omega) * G(ij);
}
