package co.com.bancolombia.model.franchise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Branch {
  private String id;
  private String name;
  private String franchiseId;
}
