/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An example to show how to use Java logging API in X10.
 * Run as "x10 -J-Djava.util.logging.config.file=logging.properties Logging"
 */
public class Logging {
    public static def main(Rail[String]):void {
        val logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        logger.log(Level.SEVERE, "this is severe");
        logger.log(Level.WARNING, "this is warning");
        logger.log(Level.INFO, "this is info");
        logger.log(Level.CONFIG, "this is config");
        logger.log(Level.FINE, "this is fine");
        logger.log(Level.FINER, "this is finer");
        logger.log(Level.FINEST, "this is finest");
    }
}
