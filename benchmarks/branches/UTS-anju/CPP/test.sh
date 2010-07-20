#/bin/bash
for k in 1 2 4 8 16 32 64 128 256 512 1024; do
  echo "--------------------------------" 2>&1 | tee -a results/par_uts.${k}.log
  for nodes in 1 2 4 8 16 32; do
    mpirun -np ${nodes} --bynode --hostfile hostfile \
          ./par_uts -r 42 -b0 2000 -m 8 -q 0.124875 -k ${k} \
          2>&1 | tee -a results/par_uts.${k}.log
  done
done
