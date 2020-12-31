package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 桥接Vert.x 与 non-Vert.x 的线程模型 某些场景下，可能不需要关心Vert.x
 * context的运行情况。譬如使用到第三方代码它有自身的线程模型，希望可以适配Vert.x一起使用。
 */
public class MixedThreading extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(MixedThreading.class);

  @Override
  public void start() throws Exception {
    // We get the context of the verticle because start is running on an event-loop thread.
    Context context = vertx.getOrCreateContext();
    new Thread(
            () -> {
              try {
                run(context);
              } catch (InterruptedException e) {
                logger.error("Woops", e);
                Thread.currentThread().interrupt();
              }
            })
        .start();
  }

  private void run(Context context) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    logger.info("I am in a non-Vert.x thread");
    context.runOnContext(
        v -> {
          logger.info("I am on the event-loop");
          vertx.setTimer(
              1_000,
              id -> {
                logger.info("This is the final countdown");
                latch.countDown();
              });
        });

    logger.info("Waiting on the countdown latch...");
    latch.await();
    logger.info("Bye!");
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MixedThreading());
  }
}
