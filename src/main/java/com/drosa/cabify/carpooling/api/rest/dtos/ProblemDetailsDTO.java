package com.drosa.cabify.carpooling.api.rest.dtos;

import java.io.Serializable;
import java.util.Objects;

public class ProblemDetailsDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String type;

  private final String title;

  public ProblemDetailsDTO(final String type, final String title) {
    this.type = type;
    this.title = title;
  }

  public String getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProblemDetailsDTO problemDetails = (ProblemDetailsDTO) o;
    return Objects.equals(this.type, problemDetails.type) &&
        Objects.equals(this.title, problemDetails.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProblemDetailsDTO {\n");

    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
