	{ print "    public void test" $1 "() {"; 
          print "        run(\"" SRCDIR "/" $1 ".x10\",\"" $1 "\");";
          print "    }";
          print "";
         }
