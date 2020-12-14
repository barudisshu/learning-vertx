package info.galudisu.local;

import info.galudisu.HeatSensor;
import info.galudisu.HttpServer;
import info.galudisu.Listener;
import info.galudisu.SensorData;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(HeatSensor.class, new DeploymentOptions().setInstances(4));
    vertx.deployVerticle(new Listener());
    vertx.deployVerticle(new SensorData());
    vertx.deployVerticle(new HttpServer());
  }
}
