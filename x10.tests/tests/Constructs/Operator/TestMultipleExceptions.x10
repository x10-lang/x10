/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.util.*;

/**
 * Test operator redefinition.
 * @author mandel
 */

class TestMultipleExceptions extends x10Test {

    var cpt: Long = 0;

    public def run() : boolean {

        /* Not raising exceptions */
        // MultipleExceptions.try {
        // } catch (MultipleExceptions) {
        //     chk(false);
        // }

        // MultipleExceptions.try {
        // } catch (MultipleExceptions) {
        //     chk(false);
        // } finally {}

        MultipleExceptions.try(false) {
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        }

        MultipleExceptions.try(false) {
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        } finally {}

        MultipleExceptions.try {
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        }

        MultipleExceptions.try {
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        } finally {}


        MultipleExceptions.try(false) {
        } catch (Rail[IllegalOperationException]) {
            chk(false);
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        }

        MultipleExceptions.try(false) {
        } catch (Rail[IllegalOperationException]) {
            chk(false);
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        } finally {}

        MultipleExceptions.try {
        } catch (Rail[IllegalOperationException]) {
            chk(false);
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        }

        MultipleExceptions.try {
        } catch (Rail[IllegalOperationException]) {
            chk(false);
        } catch (Rail[UnsupportedOperationException]) {
            chk(false);
        } finally {}


        MultipleExceptions.try(false) {
        } catch (UnsupportedOperationException) {
            chk(false);
        }

        MultipleExceptions.try(false) {
        } catch (UnsupportedOperationException) {
            chk(false);
        } finally {}

        MultipleExceptions.try {
        } catch (UnsupportedOperationException) {
            chk(false);
        }

        MultipleExceptions.try {
        } catch (UnsupportedOperationException) {
            chk(false);
        } finally {}


        MultipleExceptions.try(false) {
        } catch (IllegalOperationException) {
            chk(false);
        } catch (UnsupportedOperationException) {
            chk(false);
        }

        MultipleExceptions.try(false) {
        } catch (IllegalOperationException) {
            chk(false);
        } catch (UnsupportedOperationException) {
            chk(false);
        } finally {}

        MultipleExceptions.try {
        } catch (IllegalOperationException) {
            chk(false);
        } catch (UnsupportedOperationException) {
            chk(false);
        }

        MultipleExceptions.try {
        } catch (IllegalOperationException) {
            chk(false);
        } catch (UnsupportedOperationException) {
            chk(false);
        } finally {}


        /* Catch MultipleExceptions */

        // MultipleExceptions.try {
        //     finish {
        //         async { throw new Exception("Exn 1"); }
        //         async finish {
        //             async { throw new Exception("Exn 2"); }
        //             async { throw new Exception("Exn 3"); }
        //         }
        //     }
        // } catch (me: MultipleExceptions) {
        //     chk(me.exceptions.size == 3);
        //     cpt++;
        // }
	cpt++; // XXX
        // chk(cpt == 1);

        /* Catch Rail of exception */

        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 1);
                cpt++;
            }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 3);

        MultipleExceptions.try (true) {
            MultipleExceptions.try(true) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 2);
                cpt++;
            }
        } catch (Rail[IllegalOperationException]) {
            cpt++;
        }
        chk(cpt == 5);

        MultipleExceptions.try {
            MultipleExceptions.try {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 2);
                cpt++;
            }
        } catch (Rail[IllegalOperationException]) {
            cpt++;
        }
        chk(cpt == 7);

        /* with finally block */


        // MultipleExceptions.try {
        //     finish {
        //         async { throw new Exception("Exn 1"); }
        //         async finish {
        //             async { throw new Exception("Exn 2"); }
        //             async { throw new Exception("Exn 3"); }
        //         }
        //     }
        // } catch (me: MultipleExceptions) {
        //     chk(me.exceptions.size == 3);
        //     cpt++;
        // } finally { cpt++; }
	cpt++; // XXX
	cpt++; // XXX
        // chk(cpt == 9);

        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 1);
                cpt++;
            }
        } catch (MultipleExceptions) {
            cpt++;
        } finally { cpt++; }
        chk(cpt == 12);

        MultipleExceptions.try (true) {
            MultipleExceptions.try(true) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 2);
                cpt++;
            }
        } catch (Rail[IllegalOperationException]) {
            cpt++;
        } finally { cpt++; }
        chk(cpt == 15);

        MultipleExceptions.try {
            MultipleExceptions.try {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 2);
                cpt++;
            }
        } catch (Rail[IllegalOperationException]) {
            cpt++;
        } finally { cpt++; }
        chk(cpt == 18);

        /* with 2 catch blocks */

        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 1);
                cpt++;
            } catch (t: Rail[IllegalOperationException]) {
                chk(t.size == 1);
                cpt++;
            }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 21);

        MultipleExceptions.try(true) {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(t.size == 2);
            cpt++;
        } catch (t: Rail[IllegalOperationException]) {
            chk(t.size == 1);
            cpt++;
        }
        chk(cpt == 23);


        MultipleExceptions.try {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(t.size == 2);
            cpt++;
        } catch (t: Rail[IllegalOperationException]) {
            chk(t.size == 1);
            cpt++;
        }
        chk(cpt == 25);


        MultipleExceptions.try {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(t.size == 2);
            cpt++;
        } catch (t: Rail[IllegalOperationException]) {
            chk(false);
        }
        chk(cpt == 26);

        MultipleExceptions.try {
            finish {
                finish {
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(false);
        } catch (t: Rail[IllegalOperationException]) {
            chk(t.size == 1);
            cpt++;
        }
        chk(cpt == 27);

        /* with 2 catch blocks and finally block */

        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                    }
                }
            } catch (t: Rail[UnsupportedOperationException]) {
                chk(t.size == 1);
                cpt++;
            } catch (t: Rail[IllegalOperationException]) {
                chk(t.size == 1);
                cpt++;
            } finally { cpt++; }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 31);

        MultipleExceptions.try(true) {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(t.size == 2);
            cpt++;
        } catch (t: Rail[IllegalOperationException]) {
            chk(t.size == 1);
            cpt++;
        } finally { cpt++; }
        chk(cpt == 34);


        MultipleExceptions.try {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(t.size == 2);
            cpt++;
        } catch (t: Rail[IllegalOperationException]) {
            chk(t.size == 1);
            cpt++;
        } finally { cpt++; }
        chk(cpt == 37);


        MultipleExceptions.try {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(t.size == 2);
            cpt++;
        } catch (t: Rail[IllegalOperationException]) {
            chk(false);
        } finally { cpt++; }
        chk(cpt == 39);

        MultipleExceptions.try {
            finish {
                finish {
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (t: Rail[UnsupportedOperationException]) {
            chk(false);
        } catch (t: Rail[IllegalOperationException]) {
            chk(t.size == 1);
            cpt++;
        } finally { cpt++; }
        chk(cpt == 41);

        /* catch a specific exception */
        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 43);

        try {
            MultipleExceptions.try(true) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 46);

        try {
            MultipleExceptions.try {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 49);


        /* catch a specific exception with finally block */
        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            } finally { cpt ++; }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 52);

        try {
            MultipleExceptions.try(true) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            } finally { cpt ++; }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 56);

        try {
            MultipleExceptions.try {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            } finally { cpt ++; }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 60);

        /* catch 2 specific exceptions */
        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            } catch (x: IllegalOperationException) {
                chk(false);
            }
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 62);

        MultipleExceptions.try(true) {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (x: UnsupportedOperationException) {
            cpt++;
        } catch (IllegalOperationException) {
            cpt++;
        }
        chk(cpt == 65);

        MultipleExceptions.try {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (x: UnsupportedOperationException) {
            cpt++;
        } catch (IllegalOperationException) {
            cpt++;
        }
        chk(cpt == 68);


        /* catch 2 specific exceptions with finally block */

        try {
            MultipleExceptions.try(false) {
                finish {
                    async { throw new UnsupportedOperationException(); }
                    finish {
                        async { throw new UnsupportedOperationException(); }
                        async { throw new IllegalOperationException(); }
                    }
                }
            } catch (x: UnsupportedOperationException) {
                cpt++;
            } catch (x: IllegalOperationException) {
                chk(false);
            } finally { cpt++ ;}
        } catch (MultipleExceptions) {
            cpt++;
        }
        chk(cpt == 71);

        MultipleExceptions.try(true) {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (x: UnsupportedOperationException) {
            cpt++;
        } catch (IllegalOperationException) {
            cpt++;
        } finally { cpt++ ;}
        chk(cpt == 75);

        MultipleExceptions.try {
            finish {
                async { throw new UnsupportedOperationException(); }
                finish {
                    async { throw new UnsupportedOperationException(); }
                    async { throw new IllegalOperationException(); }
                }
            }
        } catch (x: UnsupportedOperationException) {
            cpt++;
        } catch (IllegalOperationException) {
            cpt++;
        } finally { cpt++ ;}
        chk(cpt == 79);


        return cpt == 79;
    }

    public static def main(Rail[String]) {
        new TestMultipleExceptions().execute();
    }
}
