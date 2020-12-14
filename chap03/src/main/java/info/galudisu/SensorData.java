package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SensorData extends AbstractVerticle {
  private final HashMap<String, Double> lastValues = new HashMap<>();

  @Override
  public void start() throws Exception {
    EventBus bus = vertx.eventBus();

    // The start method only declares two event-bus destination handlers.
    bus.consumer("sensor.updates", this::update);
    bus.consumer("sensor.average", this::average);
  }

  // When a new measurement is being received, we extract the data from the JSON body.
  private void update(Message<JsonObject> message) {
    JsonObject json = message.body();
    lastValues.put(json.getString("id"), json.getDouble("temp"));
  }

  // The incoming message for average requests is not used, so it can just contain an empty JSON
  // document.
  private void average(Message<JsonObject> message) {
    double avg =
        lastValues.values().stream().collect(Collectors.averagingDouble(Double::doubleValue));
    JsonObject json = new JsonObject().put("average", avg);
    // The reply method is used to reply to a message.
    message.reply(json);
  }
}
