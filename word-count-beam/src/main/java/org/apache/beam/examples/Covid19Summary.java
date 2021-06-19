package org.apache.beam.examples;

import org.apache.beam.examples.WordCount.WordCountOptions;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class Covid19Summary {

  public static void main(String[] args) {
    PipelineOptionsFactory.register(WordCountOptions.class);
    WordCountOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(WordCountOptions.class);

    PipelineResult pr = new CovidPipelineExecutor().run(options);
    System.out.println(pr.waitUntilFinish());
  }
}
