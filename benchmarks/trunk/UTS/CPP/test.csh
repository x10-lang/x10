#/bin/tcsh
foreach k (1 2 4 8 16 32 64 128 256 512 1024)
  echo "--------------------------------" |& tee -a results/par_uts.${k}.log
  foreach nodes (1 2 4 8 16 32)
    mpirun -np ${nodes} --bynode --hostfile hostfile \
          ./par_uts -r 42 -b0 2000 -m 8 -q 0.124875 -k ${k} \
          |& tee -a results/par_uts.${k}.log
  end
end
