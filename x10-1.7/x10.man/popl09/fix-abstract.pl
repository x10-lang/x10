$in = 1;
while (<>) {
  chomp;
  $in = 0 if /\\documentclass/;
  do { $in = 1; next } if /\\begin{abstract}/;
  $in = 0 if /\\end{abstract}/;
  next unless $in;
  s/\\Xten{}|{\\Xten}/X10/g;
  s/\\Xcd{(.*?)}|/$1/g;
  s/\\xcd"(.*?)"|/$1/g;
  s/\$\\cal C\$/C/g;
  s/C\\{c\\}/C{c}/g;
  s/{\\em\s*(.*)?}/$1/g;
  s/{\\tt\s*(.*)?}/$1/g;
  s/\\emph{\s*(.*)?}/$1/g;
  s/\$(.*?)\$/$1/g;
  print "$_\n";
}
