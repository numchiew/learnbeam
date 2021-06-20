package org.apache.beam.examples;

import avro.shaded.com.google.common.collect.ImmutableList;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import org.apache.beam.examples.WordCount.CountWords;
import org.apache.beam.examples.WordCount.FormatAsTextFn;
import org.apache.beam.examples.WordCount.WordCountOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.CreateDisposition;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;

public class CovidPipelineExecutor {

  public PipelineResult run(WordCountOptions options) {
    System.out.println(options.getInputFile());
    System.out.println(options.getOutput());

    Pipeline p = Pipeline.create(options);

    p.apply("ReadLines", TextIO.read().from(options.getInputFile()))
        .apply("Group per key and state then sum", new SumCovidCase())
        .apply("map to string", MapElements.via(new formatAsText()))
//        .apply("map to bigquery row", MapElements.via(new mapToBqRow()))
//        .apply("ingest to bq", BigQueryIO.writeTableRows()
//            .to(options.getOutput())
//            .withSchema(getTableSchema())
//            .withCreateDisposition(CreateDisposition.CREATE_IF_NEEDED)
//            .withWriteDisposition(WriteDisposition.WRITE_TRUNCATE));
        .apply("write to file", TextIO.write().to(options.getOutput()).withoutSharding());

    return p.run();
  }

  public static TableSchema getTableSchema() {
    return new TableSchema()
        .setFields(
            ImmutableList.of(
                new TableFieldSchema()
                    .setName("date")
                    .setType("Date_Time")
                    .setMode("NULLABLE"),
                new TableFieldSchema()
                    .setName("state_name")
                    .setType("STRING")
                    .setMode("NULLABLE"),
                new TableFieldSchema()
                    .setName("confirm_case")
                    .setType("INTEGER")
                    .setMode("NULLABLE")
            )
        );
  }

  public static class formatAsText extends SimpleFunction<CovidSummary, String> {

    @Override
    public String apply(CovidSummary covidSummary) {
      return covidSummary.getDate().toString() +
          " " + covidSummary.getStateName() +
          " " + covidSummary.getConfirmCase();
    }
  }

  public static class mapToBqRow extends SimpleFunction<CovidSummary, TableRow> {

    @Override
    public TableRow apply(CovidSummary covidSummary) {
      TableRow row = new TableRow();
      row.set("date", covidSummary.getDate().toString());
      row.set("state_name", covidSummary.getStateName());
      row.set("confirm_case", covidSummary.getConfirmCase());

      return row;
    }
  }

}
