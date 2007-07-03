#!/usr/bin/perl

`rm host.list`;
for ($i = 0; $i < 16; $i = $i + 1) {
  for ($j = $ARGV[1]; $j <= $ARGV[2]; $j = $j+1) {
      `echo $ARGV[0]$j >> host.list`;
   }
}

