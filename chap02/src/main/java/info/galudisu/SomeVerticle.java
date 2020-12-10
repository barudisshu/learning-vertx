package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class SomeVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> promise) throws Exception {
    vertx
        .createHttpServer()
        .requestHandler(req -> req.response().end("OK"))
        .listen(
            8080,
            ar -> {
              if (ar.succeeded()) {
                promise.complete();
              } else {
                promise.fail(ar.cause());
              }
            });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SomeVerticle());
  }
}
