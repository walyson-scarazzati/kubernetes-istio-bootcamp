package com.course.devops.blue.load;

import org.apache.commons.lang3.RandomUtils;

public class CpuLoadThread extends Thread {

  private int durationSecond;

  public CpuLoadThread(String name, int durationSecond) {
    super(name);
    this.durationSecond = durationSecond;
  }

  @Override
  public void run() {
    long startTime = System.currentTimeMillis();
    long durationMillis = this.durationSecond * 1000;
    long result = 0l;

    System.out.println("Start load cpu " + this.getName());

    while ((System.currentTimeMillis() - startTime) < durationMillis) {
      var i = RandomUtils.nextDouble();
      result += Math.atan(i) * Math.tan(i);
    }

    System.out.println("Done load cpu " + result + " " + this.getName());
  }
}
