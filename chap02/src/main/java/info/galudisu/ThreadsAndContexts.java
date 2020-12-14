package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一个verticle对象本质上是下面两个对象的组合体：
 *
 * <p>
 *
 * <ul>
 *   <li>一个verticle的引用实例Vertx instance
 *   <li>一个专门的上下文实例，允许事件被派遣和处理
 * </ul>
 *
 * <p><img alt="Figure 2.5" src=../../../../../../img/Figure_02_05.png" />
 *
 * <p>Vert.x instance 熬了了声明event handler的核心API。譬如<code>setTimer</code>，<code>setPeriodic</code>，
 * <code>createHttpServer</code>，<code>deployVerticle</code>等等。该Vert.x
 * instance会被多个其它verticle共享，因此每个JVM进程通常仅有一个{@link Vertx}实例。
 *
 * <p>context instance装载了对执行handler的线程访问。event则源自于各种各样的资源。譬如timers, database drivers，HTTP
 * servers等等。就其本身而言，多半是其它线程触发，比如Netty接收线程(accepting threads)或时间线程(timer threads)。
 *
 * <p>事件的处理发生于context中的自定义回调(user-defined callbacks)。context instance允许我们在verticle event-loop
 * 线程调用回调，因此需要慎重对待Vert.x的线程模型。
 *
 * <p><img alt="Figure 2.6" src=../../../../../../img/Figure_02_06.png" />
 *
 * <p>对应worker verticle的情况则不大一样，handler的执行使用了worker thread pool中的一个worker
 * thread。可以假设代码被单一线程方法，对于一个worker thread用来处理一个worker thread的事件是没有稳定性可言。
 *
 *
 * <strong>关于context对象的方法，只需要明白两点即可：</strong>
 * <ul>
 *   <ol>从一个包含context thread的verticle调用{@link Vertx#getOrCreateContext()}返回该context</ol>
 *   <ol>从一个不包含context thread的verticle调用{@link Vertx#getOrCreateContext()}返回新创建的context</ol>
 * </ul>
 */
public class ThreadsAndContexts extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadsAndContexts.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    // 打印的"ABC"和"123"被单独分配到一个event loop

    vertx.getOrCreateContext().runOnContext(v -> LOGGER.info("ABC"));

    vertx.getOrCreateContext().runOnContext(v -> LOGGER.info("123"));

    // 重载context的exception对自定义操作非常有帮助
    Context ctx = vertx.getOrCreateContext();
    ctx.put("foo", "bar");

    ctx.exceptionHandler(
        t -> {
          if ("Tada".equals(t.getMessage())) {
            LOGGER.info("Got a _Tada_ exception");
          } else {
            LOGGER.error("Woops", t);
          }
        });

    ctx.runOnContext(
        v -> {
          throw new RuntimeException("Tada");
        });

    ctx.runOnContext(v -> LOGGER.info("foo - {}", (String) ctx.get("foo")));
  }
}
