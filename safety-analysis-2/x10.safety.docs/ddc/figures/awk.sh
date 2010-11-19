echo "Java version"
awk '{ s += $5 } END { print "sum: ", s, " arithmetic average of ratios: ", s/18, " samples: ", 18 }' java.dat
awk 'BEGIN {p = 1} {p *= $5 } END { print "product: ", p, " geometric average of ratios: ", (p^(1/18.0)), " samples: ", 18 }' java.dat

echo "";
echo "C++ version"
awk '{ s += $5 } END { print "sum: ", s, " arithmetic average of ratios: ", s/18, " samples: ", 18 }' cpp.dat
awk 'BEGIN {p = 1} {p *= $5 } END { print "product: ", p, " geometric average of ratios: ", (p^(1/18.0)), " samples: ", 18 }' cpp.dat

echo "";
echo "Ratio of C++ and Java (C++/Java)"
 awk 'NR==FNR{a[NR]=$5; next} {s += a[FNR]/$5} END  { print "sum: ", s, " arithmetic average of ratios: ", s/18, " samples: ", 18 }' cpp.dat java.dat
 awk -v p=1 'NR==FNR{a[NR]=$5; next} {p *= a[FNR]/$5} END  { print "product: ", p, " geometric average of ratios: ", p^(1/18.0), " samples: ", 18 }' cpp.dat java.dat
