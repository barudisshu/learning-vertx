package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker verticle 是Verticle的一种特别形式——它不在event loop上执行。取而代之的是，他们执行在 worker threads 上。该线程从特定的worker
 * pools获取的。你可以定义自己的worker thread pools，不过大多数情况下，直接使用默认的Vert.x worker pool获取线程即可。
 *
 * <string>worker verticle</string>处理event和一个event-loop verticle差不多，不同的是它可以接受任意长时间去处理event。有以下两个要点：
 *
 * <li>一个worker verticle不是绑在一个单一worker thread上的。因此不像一个event-loop verticle，连续的event可能不在同一个thread上执行</li>
 * <li>worker verticle可能仅能有一个单一的worker thread在给定的时间访问</li>
 *
 * <p>
 * 在部署worker verticle时，需要显式调用{@link DeploymentOptions#setWorker(boolean)}为<code>true</code>指明为worker verticle
 */
public class WorkerVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(WorkerVerticle.class);

  @Override
  public void start() {
    vertx.setPeriodic(
        10_000,
        id -> {
          try {
            logger.info("Zzz...");
            // We can block and get no warning!
            Thread.sleep(8_000);
            logger.info("Up1");
          } catch (InterruptedException e) {
            logger.error("Woops", e);
            Thread.currentThread().interrupt();
          }
        });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    // Making a worker verticle is a deployment options flag.
    DeploymentOptions opts = new DeploymentOptions().setInstances(2).setWorker(true);
    vertx.deployVerticle(WorkerVerticle.class, opts);
  }
}
