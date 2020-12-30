package info.galudisu.callbacks;

import info.galudisu.sensor.HeatSensor;
import info.galudisu.snapshot.SnapshotService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    // Each instance can use a different port number.
    vertx.deployVerticle(
        HeatSensor.class,
        new DeploymentOptions().setConfig(new JsonObject().put("http.port", 3000)));

    vertx.deployVerticle(
        HeatSensor.class,
        new DeploymentOptions().setConfig(new JsonObject().put("http.port", 3001)));
    vertx.deployVerticle(
        HeatSensor.class,
        new DeploymentOptions().setConfig(new JsonObject().put("http.port", 3002)));

    vertx.deployVerticle(new SnapshotService());
    vertx.deployVerticle(new CollectorService());
  }
}
