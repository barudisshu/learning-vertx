package info.galudisu.future;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Intro {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    // Create a promise.
    Promise<String> promise = Promise.promise();
    // Asynchronous operation
    vertx.setTimer(
        5_000,
        id -> {
          if (System.currentTimeMillis() % 2L == 0L) {
            promise.complete("Ok!");
          } else {
            promise.fail(new RuntimeException("Bad luck..."));
          }
        });

    // Derive a future from a promise, and then return it.
    Future<String> future = promise.future();

    future
        // Callback for when the promise is completed.
        .onSuccess(System.out::println)
        // Callback for when the future is failed.
        .onFailure(err -> System.out.println(err.getMessage()));

    // Advanced future composition operations
    promise
        .future()
        // Recover from an error with another value.
        .recover(err -> Future.succeededFuture("Let's say it's ok!"))
        // Map a value to another value.
        .map(String::toUpperCase)
        .flatMap(
            str -> {
              // Compose with another asynchronous operation.
              Promise<String> next = Promise.promise();
              vertx.setTimer(3_000, id -> next.complete(">>> " + str));
              return next.future();
            })
        .onSuccess(System.out::println);

    // Converts a Future to a CompletionStage
    CompletionStage<String> cs = promise.future().toCompletionStage();
    cs.thenApply(String::toUpperCase)
        .thenApply(str -> "~~~ " + str)
        .whenComplete(
            (str, err) -> {
              if (err == null) {
                System.out.println(str);
              } else {
                System.out.println("Oh... " + err.getMessage());
              }
            });

    // From a CompletionStage to Vert.x Future
    CompletableFuture<String> cf =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                Thread.sleep(5_000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return "5 seconds have elapsed";
            });

    // Convert to a Vert.x future, and dispatch on a Vert.x context.
    Future.fromCompletionStage(cf, vertx.getOrCreateContext())
        .onSuccess(System.out::println)
        .onFailure(Throwable::printStackTrace);
  }
}
