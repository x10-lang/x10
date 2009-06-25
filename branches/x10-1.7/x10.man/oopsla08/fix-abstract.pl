while (<>) {
  chomp;
  s/\\Xten{}|{\\Xten}/X10/g;
  s/{\\em\s*//g;
  s/}//g;
  s/\\xcd{//g;
  print "$_\n";
}
