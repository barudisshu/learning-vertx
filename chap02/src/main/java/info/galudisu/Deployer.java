package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deployer extends AbstractVerticle {
  private final Logger logger = LoggerFactory.getLogger(Deployer.class);

  @Override
  public void start() {
    long delay = 1000;
    for (int i = 0; i < 50; i++) {
      // We deploy a new instance of EmptyVerticle every second.
      vertx.setTimer(delay, id -> deploy());
      delay = delay + 1000;
    }
  }

  private void deploy() {
    // Deploying a verticle is an asynchronous operation, and there is a variant of the deploy
    // method that supports an asynchronous result.
    vertx.deployVerticle(
        new EmptyVerticle(),
        ar -> {
          if (ar.succeeded()) {
            String id = ar.result();
            logger.info("Successfully deployed {}", id);
            // We will undeploy a verticle after five seconds.
            vertx.setTimer(5000, tid -> undeployLater(id));
          } else {
            logger.error("Error while deploying", ar.cause());
          }
        });
  }

  private void undeployLater(String id) {
    // Undeploying is very similar to deploying.
    vertx.undeploy(
        id,
        ar -> {
          if (ar.succeeded()) {
            logger.info("{} was undeployed", id);
          } else {
            logger.error("{} could not be undeployed", id);
          }
        });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Deployer());
  }
}
