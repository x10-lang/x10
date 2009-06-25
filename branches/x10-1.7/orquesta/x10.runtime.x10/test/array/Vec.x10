import x10/*new*/.lang.*;

import x10.util.Iterator_Scanner;


value class Vec {
    
    //region(:rank==1) R;
    final Region R;
    final Array_double data;
    
    Vec(final Array_double d) {
        this.R = d.region;
        //this.data = d; // XXX fails if d isn't value - why does it pass typechecking??
        class Init implements Indexable_double {
            public double get(Point p) {
                return d.get(p);
            }
        }
	this.data = Array_double.make(R, new Init(), true);
    }

    Vec(final double [] d) {
        this.R = Region.makeRectangular(0, d.length-1);
        class Init implements Indexable_double {
            public double get(Point p) {
                return d[p.get(0)];
            }
        }
        this.data = Array_double.make(R, new Init(), true);
    }

    double dot(Vec that) {
        double sum = 0.0;
	// for (point p : data)
	// for (point [i] : data) <- faster in current impl
        Iterator_Scanner it = R.scanners();
        while (it.hasNext()) {
            Region.Scanner s = (Region.Scanner) it.next();
            int min0 = s.min(0);
            int max0 = s.max(0);
            for (int p=min0; p<=max0; p++)
                sum += this.data.get(p)*that.data.get(p);
        }
        return sum;
    }
    
    Vec times(final double that) {
        class Init implements Indexable_double {
            public double get(Point p) {
                return /*this.*/data.get(p)*that;
            }
        }
        return new Vec(Array_double.make(R, new Init()));
    }
        
    Vec plus(final Vec that) {
        class Init implements Indexable_double {
            public double get(Point p) {
                return /*this.*/data.get(p) + that.data.get(p);
            }
        }
        return new Vec(Array_double.make(R, new Init()));
    }

    Vec minus(final Vec that) {
        class Init implements Indexable_double {
            public double get(Point p) {
                return /*this.*/data.get(p) - that.data.get(p);
            }
        }
        return new Vec(Array_double.make(R, new Init()));
    }

    public String toString() {
        String s = "[";
        Iterator_Scanner it = R.scanners();
        while (it.hasNext()) {
            Region.Scanner sc = (Region.Scanner) it.next();
            int min0 = sc.min(0);
            int max0 = sc.max(0);
            for (int i=0; i<=max0; i++) {
                if (i>0) s += ", ";
                s += data.get(i);
            }
        }
        s += "]";
        return s;
    }

}
