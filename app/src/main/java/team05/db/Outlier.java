package team05.db;

import team05.fft.Table;
import team05.fft.Transaction;

// Author hypnotics-dev devhypnotics@proton.me
/** Outlier */
public class Outlier implements Table {

  private int tid;
  private int bid;

  public Outlier(Transaction tid, Buyer bid) {
    this.tid = tid.getPk();
    this.bid = bid.getPk();
  }

  public int getPk() {
    return tid;
  }

  public int getBuyer() {
    return bid;
  }

  @Override
  public String toValue() {
    return "(" + bid + ")";
  }

  @Override
  public String toString() {
    return tid + " " + bid;
  }
}
