echo "Geometric average"
for ((i = 1; i <= 64; i=i*2))
do
awk 'BEGIN {p = 1} {p *= $5 } END {print  19, "Geometric-Mean", 1, 1,  (p^(1/18.0))}' azulw$i.dat >> azulw$i.dat 
done
