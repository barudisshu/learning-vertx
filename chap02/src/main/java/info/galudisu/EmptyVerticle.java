package info.galudisu;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmptyVerticle.class);

  @Override
  public void start() throws Exception {
    LOGGER.info("Start");
  }

  @Override
  public void stop() {
    LOGGER.info("Stop");
  }
}
