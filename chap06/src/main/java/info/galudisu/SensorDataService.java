package info.galudisu;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

// This annotation is used to generate an event-bus proxy.
@ProxyGen
@VertxGen
public interface SensorDataService {

  // Factory method for creating a service instance
  static SensorDataService create(Vertx vertx) {
    return new SensorDataServiceImpl(vertx);
  }

  // Factory method for creating a proxy
  static SensorDataService createProxy(Vertx vertx, String address) {
    return new SensorDataServiceVertxEBProxy(vertx, address);
  }

  // Operation that takes a parameter and a callback
  void valueFor(String sensorId, Handler<AsyncResult<JsonObject>> handler);

  // Operation that takes no parameter and a callback
  void average(Handler<AsyncResult<JsonObject>> handler);
}
