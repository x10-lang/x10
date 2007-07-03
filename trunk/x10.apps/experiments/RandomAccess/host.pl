#!/usr/bin/perl

`rm host.list`;
for ($i = 0; $i < 16; $i = $i + 1) {
  for ($j = $ARGV[0]; $j <= $ARGV[1]; $j = $j+1) {
      `echo v80n$j >> host.list`;
   }
}

