package com.course.devops.red.load;

import org.apache.commons.lang3.RandomUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CpuLoadThread extends Thread {

  private final int durationSecond;
  private volatile boolean shouldStop = false;

  public CpuLoadThread(String name, int durationSecond) {
    super(name);
    this.durationSecond = durationSecond;
  }

  @Override
  public void run() {
    long startTime = System.currentTimeMillis();
    long durationMillis = (long) this.durationSecond * 1000;
    long result = 0L;

    log.info("Start load cpu {}", this.getName());

    while ((System.currentTimeMillis() - startTime) < durationMillis && !shouldStop) {
      double i = RandomUtils.insecure().randomDouble();
      result += Math.atan(i) * Math.tan(i);
    }

    log.info("Done load cpu {} {}", result, this.getName());
  }

  public void stopLoad() {
    this.shouldStop = true;
  }
}