package org.apache.beam.examples;

import com.google.auto.value.AutoValue.Builder;
import java.io.Serializable;
import lombok.*;
import org.joda.time.DateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CovidSummary implements Serializable {
  DateTime date;
  String stateName;
  int confirmCase;
}
