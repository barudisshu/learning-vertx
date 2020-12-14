package info.galudisu.streamapis;

import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

public class VertxStreams {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    OpenOptions opts = new OpenOptions().setRead(true);
    // Opening a file is an asynchronous operation.
    vertx
        .fileSystem()
        .open(
            "data.txt",
            opts,
            ar -> {
              if (ar.succeeded()) {
                // AsyncFile is the interface for vert.x asynchronous files.
                AsyncFile file = ar.result();
                // The callback for new buffer data
                file.handler(System.out::println)
                    // The callback when an exception arises
                    .exceptionHandler(Throwable::printStackTrace)
                    // The callback when the stream ends
                    .endHandler(
                        done -> {
                          System.out.println("\n--- DONE");
                          vertx.close();
                        });
              } else {
                ar.cause().printStackTrace();
              }
            });
  }
}
