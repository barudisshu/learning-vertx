package info.galudisu.cluster;

import info.galudisu.HeatSensor;
import info.galudisu.HttpServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstInstance {
  private static final Logger LOGGER = LoggerFactory.getLogger(FirstInstance.class);

  public static void main(String[] args) {
    Vertx.clusteredVertx(
        new VertxOptions(),
        ar -> {
          if (ar.succeeded()) {
            LOGGER.info("First instance has been started");
            Vertx vertx = ar.result();
            vertx.deployVerticle(HeatSensor.class, new DeploymentOptions().setInstances(4));
            vertx.deployVerticle(new HttpServer());
          } else {
            LOGGER.error("Could not start", ar.cause());
          }
        });
  }
}
