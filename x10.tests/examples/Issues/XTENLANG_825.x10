// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author hhorii 02/2010
 */

public class XTENLANG_825 extends x10Test {
  public def run(): boolean {
    try {
      finish async throw new Exception();
    } catch(ex: x10.lang.MultipleExceptions) { return true; }
    return false;
  }
  
  public static def main(Rail[String]) {
      new XTENLANG_825().execute();
  }
}

