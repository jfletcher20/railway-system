package edu.unizg.foi.uzdiz.jfletcher20.models;

public class IncorrectColumnCountException extends Exception {
  private static final long serialVersionUID = -5007696035554692545L;
  private final int expectedColumns;
  private final int actualColumns;
  private final int rowNumber;

  public IncorrectColumnCountException(int expectedColumns, int actualColumns, int rowNumber) {
    super(String.format("Red %d ima krivi broj stupaca. Ocekivano: %d, otkriveno: %d", rowNumber,
        expectedColumns, actualColumns));
    this.expectedColumns = expectedColumns;
    this.actualColumns = actualColumns;
    this.rowNumber = rowNumber;
  }

  public int getExpectedColumns() {
    return expectedColumns;
  }

  public int getActualColumns() {
    return actualColumns;
  }

  public int getRowNumber() {
    return rowNumber;
  }
}
