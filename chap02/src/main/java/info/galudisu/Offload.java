package info.galudisu;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Vertx#executeBlocking(Handler, Handler)} 方法，在每次执行时，会从Worker thread pool里面获取一个线程。然后载入{@link
 * Offload#blockingCode(Promise)}其在该线程上执行，然后将结果作为一个新的event发送到event loop中。如下图：
 *
 * <p><img alt="Figure 2.4" src=../../../../../../img/Figure_02_04.png" />
 *
 * <p>执行(这里指的是 <code>blockingCode()</code>)被卸载(offloaded to)到worker thread，但结果的处理(这里指的是<code>
 * resultHandler</code>)仍然发生在event loop中。
 *
 * <p><strong>Tip</strong> 默认地，顺序的<code>executeBlocking</code>操作产生的结果，也会以相同的顺序被处理。其中{@link
 * Vertx#executeBlocking(Handler, boolean, Handler)}包含一个额外的<code>boolean</code>参数，当设置其为<code>false
 * </code>时，处理的result如果可用立即作为event-loop event被处理，不关心<code>executeBlocking</code>的调用顺序。
 */
public class Offload extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(Offload.class);

  @Override
  public void start() {
    vertx.setPeriodic(
        5000,
        id -> {
          logger.info("Tick");
          // executeBlocking takes two parameters: the code to run and a callback for when it has
          // run.
          // 接收一个阻塞调用，以及一个回调处理
          vertx.executeBlocking(this::blockingCode, this::resultHandler);
        });
  }

  private void blockingCode(Promise<String> promise) {
    logger.info("Blocking code running");
    try {
      Thread.sleep(4_000);
      logger.info("Done!");
      // 无论如何，Promise object最后都要标识complete或fail
      promise.complete("Ok!");
    } catch (InterruptedException e) {
      promise.fail(e);
      Thread.currentThread().interrupt();
    }
  }

  // 记录一下请求情况
  private void resultHandler(AsyncResult<String> ar) {
    if (ar.succeeded()) {
      String result = ar.result();
      logger.info("Blocking code result: {}", result);
    } else {
      logger.error("Woops", ar.cause());
    }
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Offload());
  }
}
