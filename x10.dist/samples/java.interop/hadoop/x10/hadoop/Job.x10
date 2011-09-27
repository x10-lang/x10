package x10.hadoop;

import x10.interop.Java;
import x10.interop.java.Throws;

import org.apache.hadoop.conf.Configuration;

public class Job[APPLICATION, MAPPER, COMBINER, REDUCER, KEYOUT, VALUEOUT] {
    val job : org.apache.hadoop.mapreduce.Job;

    @Throws[java.io.IOException]
    def this(conf: Configuration, name: String) {
        job = new org.apache.hadoop.mapreduce.Job(conf, name);
        job.setJarByClass(Java.javaClass[APPLICATION]());
        job.setMapperClass(Java.javaClass[MAPPER]());
        job.setCombinerClass(Java.javaClass[COMBINER]());
        job.setReducerClass(Java.javaClass[REDUCER]());
        job.setOutputKeyClass(Java.javaClass[KEYOUT]());
        job.setOutputValueClass(Java.javaClass[VALUEOUT]());
    }

    //public def operator this() : org.apache.hadoop.mapreduce.Job = job;      // does not compile (?!)
    public def get() : org.apache.hadoop.mapreduce.Job = job;
}
