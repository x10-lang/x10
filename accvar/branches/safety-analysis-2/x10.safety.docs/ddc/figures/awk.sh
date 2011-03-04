echo "Geometric average"
awk 'BEGIN {p = 1} {p *= $5 } END {print  19, "Geometric-Mean", 1, 1,  (p^(1/18.0))}' java.dat >> java.dat 
awk 'BEGIN {p = 1} {p *= $5 } END {print  19, "Geometric-Mean", 1, 1,  (p^(1/18.0))}' cpp.dat >> cpp.dat 
