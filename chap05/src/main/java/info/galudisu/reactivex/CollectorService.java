package info.galudisu.reactivex;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectorService extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectorService.class);

  private WebClient webClient;

  @Override
  public Completable rxStart() {
    webClient = WebClient.create(vertx);
    return vertx
        .createHttpServer()
        .requestHandler(this::handleRequest)
        // A Single<HttpServer>
        .rxListen(8080)
        // A Completable
        .ignoreElement();
  }

  private void handleRequest(HttpServerRequest request) {
    Single<JsonObject> data = collectTemperatures();
    sendToSnapshot(data)
        .subscribe(
            json -> request.response().putHeader("Content-Type", "application/json").end(json.encode()),
            err -> {
              LOGGER.error("Something went wrong", err);
              request.response().setStatusCode(500).end();
            });
  }

  // Sending data the the snapshot service with RxJava
  private Single<JsonObject> sendToSnapshot(Single<JsonObject> data) {
    // Once we have the JSON data, we issue an HTTP request.
    return data.flatMap(
        json ->
            webClient
                .post(4000, "localhost", "")
                .expect(ResponsePredicate.SC_SUCCESS)
                // This sends a JSON object, then reports on the HTTP request response.
                .rxSendJsonObject(json)
                // This allows us to give back the JSON object rather than the HTTP request
                // response.
                .flatMap(resp -> Single.just(json)));
  }

  private Single<HttpResponse<JsonObject>> fetchTemperature(int port) {
    return webClient
        .get(port, "localhost", "/")
        .expect(ResponsePredicate.SC_SUCCESS)
        .as(BodyCodec.jsonObject())
        // This returns a Single.
        .rxSend();
  }

  private Single<JsonObject> collectTemperatures() {
    Single<HttpResponse<JsonObject>> r1 = fetchTemperature(3000);
    Single<HttpResponse<JsonObject>> r2 = fetchTemperature(3001);
    Single<HttpResponse<JsonObject>> r3 = fetchTemperature(3002);

    // The zip operator composes three responses.
    return Single.zip(
        r1,
        r2,
        r3,
        (j1, j2, j3) -> {
          JsonArray array = new JsonArray().add(j1.body()).add(j2.body()).add(j3.body());
          // The value is the zip operator response, boxed in a Single.
          return new JsonObject().put("data", array);
        });
  }
}
