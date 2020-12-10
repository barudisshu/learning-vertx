package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HelloVerticle.class);
  private long counter = 1;

  @Override
  public void start() throws Exception {
    // This defines a periodic task every five seconds.
    vertx.setPeriodic(5000, id -> LOGGER.info("tick"));

    vertx
        .createHttpServer()
        .requestHandler(
            // The HTTP server calls this handler on every request.
            req -> {
              LOGGER.info("Request #{} from {}", counter++, req.remoteAddress().host());
              req.response().end("Hello!");
            })
        .listen(8080);

    LOGGER.info("Open http://localhost:8080/");
  }

  public static void main(String[] args) {
    // We need a global Vert.x instance.
    Vertx vertx = Vertx.vertx();
    // This is the simplest way to deploy a verticle.
    vertx.deployVerticle(new HelloVerticle());
  }
}
