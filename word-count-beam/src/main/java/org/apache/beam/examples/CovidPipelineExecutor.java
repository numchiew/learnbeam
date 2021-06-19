package org.apache.beam.examples;

import org.apache.beam.examples.WordCount.CountWords;
import org.apache.beam.examples.WordCount.FormatAsTextFn;
import org.apache.beam.examples.WordCount.WordCountOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.MapElements;

public class CovidPipelineExecutor {

  public PipelineResult run(WordCountOptions options) {
    System.out.println(options.getInputFile());
    System.out.println(options.getOutput());

    Pipeline p = Pipeline.create(options);

    p.apply("ReadLines", TextIO.read().from(options.getInputFile()))
        .apply("Group per key and state then sum", new SumCovidCase())
        .apply("write to file", TextIO.write().to(options.getOutput()).withoutSharding());

    return p.run();
  }

}
