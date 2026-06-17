package com.course.devops.red.load;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MemoryLoadThread extends Thread {
  private int loadMb;
  private int durationSecond;
  private static final int ONE_MEGABYTE = 1048576;

  public MemoryLoadThread(String name, int loadMb, int durationSecond) {
    super(name);
    this.loadMb = loadMb;
    this.durationSecond = durationSecond;
  }

  @Override
  public void run() {
    var byteHolder = new ArrayList<byte[]>();
    var pauseMillis = ((durationSecond - 1) * 1000) / loadMb;

    System.out.println("Start load memory " + this.getName());

    for (int i = 0; i < loadMb; i++) {
      try {
        byteHolder.add(new byte[ONE_MEGABYTE]);
        TimeUnit.MILLISECONDS.sleep(pauseMillis);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      TimeUnit.SECONDS.sleep(5);
      byteHolder.clear();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Done load memory " + this.getName());
  }
}
