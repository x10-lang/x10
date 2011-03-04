% Octave file for generating the recursive matrix.
% SCALE = the actual number of nodes in the graph is 2^SCALE
% a + b + c + d = 1.0 (probabilities)
%
function graph = rmat (SCALE,a,b,c,d)
  if ((a+b+c+d) != 1.0)
    usage ("The probabilities should add up to 1.0")
  endif

  % Set the number of vertices
  N = 2^SCALE;

  % Set the number of edges
  M = 8*N;

  % Set the random number seed
  rand ("seed", 2.0);

  % Create index arrays
  ii = ones (M,1);
  jj = ones (M,1);

  %Loop over each order of bit
  ab = a+b;
  c_norm = c/(c+d);
  a_norm = a/(a+b);

  for ib = 1:SCALE
    % Compare with probabilities and set bits of indices
    ii_bit = rand(M,1) > ab;
    jj_bit = rand(M,1) > (c_norm.*ii_bit + a_norm.*not(ii_bit));
    ii = ii + ((2^(ib-1)).*ii_bit);
    jj = jj + ((2^(ib-1)).*jj_bit);
  end

  ii
  jj

  % Create adjacency matrix for veiwing purposes
  answer = sparse (ii, jj, ones(M,1))

endfunction 
