package org.apache.beam.examples;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ConvertCovidObjectFn extends DoFn<String, KV<String, Covid>> {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
  private static final String DELIMITOR = ",";
  @ProcessElement
  public void processElement(@Element String element, OutputReceiver<KV<String, Covid>> output) {
    System.out.println(element);
    String[] columns = element.split(DELIMITOR);
    DateTime date = DateTime.parse(columns[0].trim(), DATE_TIME_FORMATTER);
    System.out.println(date.toString());
    String state = columns[1].trim();
    int confirmCase = Integer.parseInt(columns[2].trim());
    int death = Integer.parseInt(columns[4].trim());
    output.output(KV.of(date.toString() + "_" + state, new Covid(date, state, confirmCase, death)));

  }

}
