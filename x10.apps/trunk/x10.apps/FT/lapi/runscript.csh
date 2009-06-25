#!/usr/bin/csh

# 4,5,6 with largest.list
# 1,2,3 with 5nodes.list

foreach r (1 2 3)
foreach p (64 32)
foreach t (mpi lapi-slabs mpi-slabs lapi-pencils mpi-pencils)
 echo "Doing DD $t $p"
 /usr/bin/time poe ./ft-$t.DD -procs $p -hostfile ./lists/largest.list |& tee output.DD.$t.$p.$r
end
end
end
