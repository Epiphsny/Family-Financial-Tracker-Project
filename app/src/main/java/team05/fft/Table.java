package team05.fft;

// Author hypnotics-dev devhypnotics@proton.me
/** Table */
public interface Table {
  /**
   * Creates a {@link String} representation to insert in an SQL VALUES statment
   *
   * @return String represnetaion of it's elements
   */
  public String toValue();

  // TODO: Add different to methods
}
