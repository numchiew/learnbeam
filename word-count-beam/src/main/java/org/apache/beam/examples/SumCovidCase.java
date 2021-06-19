package org.apache.beam.examples;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.DateTime;

public class SumCovidCase extends PTransform<PCollection<String>, PCollection<CovidSummary>> {

  @Override
  public PCollection<CovidSummary> expand(PCollection<String> input) {
    //        .apply("Sum count", MapElements.via(new SumPerKey()))

    return input
        .apply("Convert row to Key pair", ParDo.of(new ConvertCovidObjectFn()))
        .apply("Group row by Date + State", GroupByKey.create())
        .apply("sum", ParDo.of(new sumPerKey()));

  }

  public static class sumPerKey extends DoFn<KV<String, Iterable<Covid>>, CovidSummary> {
    @ProcessElement
    public void processElement(@Element KV<String, Iterable<Covid>> element, OutputReceiver<CovidSummary> output) {
      System.out.println(element.getKey());
      Iterable<Covid> covidList = element.getValue();
      AtomicInteger sumTotal = new AtomicInteger();
      covidList.forEach(covid -> {
        sumTotal.addAndGet(covid.getConfirmCase());
      });
      String[] outputVal = element.getKey().split("_");

      output.output(new CovidSummary(new DateTime(outputVal[0]), outputVal[1], sumTotal.intValue()));
    }

  }
}
