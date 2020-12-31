package info.galudisu.reactivex.intro;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;

import java.util.concurrent.TimeUnit;

public class VertxIntro extends AbstractVerticle {

  // rsStart notifies of deployment success using a Completable rather than a Future.
  @Override
  public Completable rxStart() {
    Observable
        // The scheduler enforces the Vert.x threading model.
        .interval(1, TimeUnit.SECONDS, RxHelper.scheduler(vertx))
        .subscribe(n -> System.out.println("tick"));

    return vertx
        .createHttpServer()
        .requestHandler(r -> r.response().end("Ok"))
        // This is an RxJava variant of listen(port, callback).
        .rxListen(8080)
        // This returns  Completable from a Single.
        .ignoreElement();
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new VertxIntro());
  }
}
