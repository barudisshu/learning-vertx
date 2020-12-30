package info.galudisu.callbacks;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectorService extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(CollectorService.class);
  private WebClient webClient;

  @Override
  public void start() {
    // A Vert.x web client needs a vertx context.
    webClient = WebClient.create(vertx);
    vertx.createHttpServer().requestHandler(this::handleRequest).listen(8080);
  }

  private void handleRequest(HttpServerRequest request) {
    // We need a list to collect the JSON responses.
    List<JsonObject> responses = new ArrayList<>();
    // We also need a counter for tracking responses, since the number of responses may be less than
    // the number of requests when there are errors.
    AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < 3; i++) {
      // This issues an HTTP GET request on resource / on localhost and port 3000 +i.
      webClient
          .get(3_000 + i, "localhost", "/")
          // This predicate triggers an error when the HTTP status code is not in the 2xx range.
          .expect(ResponsePredicate.SC_SUCCESS)
          .as(BodyCodec.jsonObject())
          .send(
              ar -> {
                if (ar.succeeded()) {
                  responses.add(ar.result().body());
                } else {
                  LOGGER.error("Sensor down?", ar.cause());
                }
                // When all requests(or errors) have been received, we can move to the next
                // operation.
                if (counter.incrementAndGet() == 3) {
                  JsonObject data = new JsonObject().put("data", new JsonArray(responses));
                  sendToSnapshot(request, data);
                }
              });
    }
  }

  private void sendToSnapshot(HttpServerRequest request, JsonObject data) {
    webClient
        .post(4_000, "localhost", "/")
        .expect(ResponsePredicate.SC_SUCCESS)
        .sendJsonObject(
            data,
            ar -> {
              if (ar.succeeded()) {
                sendResponse(request, data);
              } else {
                LOGGER.error("Snapshot down?", ar.cause());
                // In case of error, we end the
                request.response().setStatusCode(500).end();
              }
            });
  }

  private void sendResponse(HttpServerRequest request, JsonObject data) {
    request
        .response()
        .putHeader("Content-Type", "application/json")
        // Gives a compact JSON text representation
        .end(data.encode());
  }
}
