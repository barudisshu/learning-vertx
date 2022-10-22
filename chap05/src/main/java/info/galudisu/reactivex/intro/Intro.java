package info.galudisu.reactivex.intro;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.concurrent.TimeUnit;

/**
 * <img alt="Figure 5.3" src=../../../../../../../../img/Figure_05_03.png" />
 *
 * @author galudisu
 */
public class Intro {

  public static void main(String[] args) throws InterruptedException {
    // This is an observable of a predefined sequence.
    Observable.just(1, 2, 3)
        // We map to a string.
        .map(Object::toString)
        // We transform the string.
        .map(s -> "@" + s)
        // For each item, we print to the standard output.
        .subscribe(System.out::println);

    // Error handling with RxJava
    // The observable emits one error.
    Observable.<String>error(() -> new RuntimeException("Woops"))
        // This is never called.
        .map(String::toUpperCase)
        // The stack trace will be printed.
        .subscribe(System.out::println, Throwable::printStackTrace);

    Single<String> s1 = Single.just("foo");
    Single<String> s2 = Single.just("bar");
    Flowable<String> m = Single.merge(s1, s2);
    m.subscribe(System.out::println);

    // Dealing with all life-cycle events in RxJava
    Observable.just("--", "this", "is", "--", "a", "sequence", "of", "items", "1")
        // Actions can be inserted, such as when a subscription happens.
        .doOnSubscribe(d -> System.out.println("Subscribed!"))
        // This delays emitting events by five seconds.
        .delay(5, TimeUnit.SECONDS)
        .filter(s -> !s.startsWith("--"))
        // Another action, here called for each item flowing in the stream
        .doOnNext(System.out::println)
        .map(String::toUpperCase)
        // This groups events 2 by 2.
        .buffer(2)
        .subscribe(
            pair -> System.out.println("next: " + pair),
            Throwable::printStackTrace,
            // Called when the stream has completed
            () -> System.out.println(">>> Done"));

    Thread.sleep(10_000);
  }
}
