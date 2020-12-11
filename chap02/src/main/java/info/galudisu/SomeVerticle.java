package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * 带有<code>listen</code> handler和promise的时序图。
 *
 * <p><img alt="Figure 2.2" src=../../../../../../img/Figure_02_02.png" />
 *
 * <p>不带<code>listen</code> handler和promise的时序图。
 *
 * <p><img alt="Figure 2.3" src=../../../../../../img/Figure_02_03.png" />
 *
 * <p>从图中可以看出，要想在<code>deployer</code>获得通知错误消息，仅能通过{@link
 * AbstractVerticle#start(Promise)}带参数的方法实现。不管{@link Promise}出现在哪个实现方法，在实现语句最后都应该显式返回{@link
 * Promise#complete()}或{@link Promise#fail(String)}
 *
 * <p>另外也有一个{@link AbstractVerticle#start()} 不带参数的方法。
 */
public class SomeVerticle extends AbstractVerticle {

  // The Promise is of type void because Vert.x is only interested in the deployment completion, and
  // there is no value to carry along.
  @Override
  public void start(Promise<Void> promise) throws Exception {
    vertx
        .createHttpServer()
        .requestHandler(req -> req.response().end("OK"))
        // listen 提供一种异步结果的指示器
        .listen(
            8080,
            ar -> {
              if (ar.succeeded()) {
                // complete() 用于标识Promise是完成
                promise.complete();
              } else {
                // 如果监听操作失败，标识Promise为失败，并传递error信息
                promise.fail(ar.cause());
              }
            });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SomeVerticle());
  }
}
