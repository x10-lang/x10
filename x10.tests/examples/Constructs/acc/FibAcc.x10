import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers
import x10.util.*;
import x10.lang.annotations.*;
import harness.x10Test;



struct IntReducer implements Reducible[Int] 
{
	public def zero():Int = 0 ;
	public operator this( x:Int,y:Int ):Int = x+y ;
}

class FibAcc extends x10Test
{
	public def run() : boolean = true ;
	public static def main( Array[String] ) 
	{
		f:Int = 7 ;
		val res0 = new FibAcc().fib( f ) ;
		
		Console.OUT.println( "Fibonacci for " + f + " is: " + res0 ) ;
	}
	def fib( n:Int ):Int 
	{
		acc x:Int = new IntReducer() ;
		
		finish fib1( n, x ) ; // fib1 may write into x.
		return x ;
	}
	def fib1( n:Int, acc z:Int )
	{
		if ( n < 2 )
		{
			z = n;  // calling acc.supply
			return ; 
		}
		async fib1( n-1, z ) ;
		fib1( n-2, z ) ;
	}
}
