package team05.excel;

import java.io.IOException;

// Author Justin Babineau jbabine1
public interface DataReader {

  public abstract void read(String path) throws IOException;
  
}
