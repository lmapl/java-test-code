package io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class Tes {

  public static void main(String[] args) throws IOException {

    ByteArrayOutputStream output = new ByteArrayOutputStream();

    output.write("This text is converted to bytes".getBytes("UTF-8"));

    byte[] bytes = output.toByteArray();


  }

  public static void main1(String[] args) throws IOException {

    PipedOutputStream pipedOutputStream = new PipedOutputStream();
    PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);

    Thread thread1 = new Thread(() -> {
      try {
        pipedOutputStream.write("Hello pipi,aaaaa".getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    Thread thread2 = new Thread(() -> {
      try {
        int data = pipedInputStream.read();
        while (data != -1){
          System.out.print((char) data);
          data = pipedInputStream.read();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    thread1.start();
    thread2.start();

  }
}
