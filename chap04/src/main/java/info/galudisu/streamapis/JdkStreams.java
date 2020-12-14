package info.galudisu.streamapis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class JdkStreams {

  public static void main(String[] args) throws URISyntaxException {
    File file = Paths.get(JdkStreams.class.getResource("/data.txt").toURI()).toFile();
    byte[] buffer = new byte[1024];
    // Using try-with-resources we ensure that reader.close() is always going to be called, whether
    // the execution completes normally or exceptionally.
    try (FileInputStream in = new FileInputStream(file)) {
      int count = in.read(buffer);
      while (count != -1) {
        System.out.println(new String(buffer, 0, count));
        count = in.read(buffer);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // We insert two lines to the console once reading has finished.
      System.out.println("\n--- DONE");
    }
  }
}
