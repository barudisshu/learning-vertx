package info.galudisu.cluster;

import info.galudisu.HeatSensor;
import info.galudisu.HttpServer;
import info.galudisu.Listener;
import info.galudisu.SensorData;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecondInstance {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecondInstance.class);

  public static void main(String[] args) {
    Vertx.clusteredVertx(
        new VertxOptions(),
        ar -> {
          if (ar.succeeded()) {
            LOGGER.info("Second instance has been started");
            Vertx vertx = ar.result();
            vertx.deployVerticle(HeatSensor.class, new DeploymentOptions().setInstances(4));
            vertx.deployVerticle(new Listener());
            vertx.deployVerticle(new SensorData());

            JsonObject conf = new JsonObject().put("port", 8081);
            vertx.deployVerticle(HttpServer.class, new DeploymentOptions().setConfig(conf));
          } else {
            LOGGER.error("Could not start", ar.cause());
          }
        });
  }
}
