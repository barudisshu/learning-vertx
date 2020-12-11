package info.galudisu;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(SampleVerticle.class);

  @Override
  public void start() throws Exception {
    logger.info("n = {}", config().getInteger("n", -1));
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    for (int n = 0; n < 4; n++) {
      // We create a JSON object and put an integer value for key "n"
      JsonObject conf = new JsonObject().put("n", n);
      // The DeploymentOption allows more control on a verticle, including passing configuration
      // data.
      // We can deploy multiple instances at once.
      DeploymentOptions opts = new DeploymentOptions().setConfig(conf).setInstances(n);
      // Since we deploy multiple instances, we need to point to the verticle using its fully
      // qualified class name (FQCN) rather than using the new operator. For deploying just one
      // instance, you can elect either an instance created with new or using a FQCN.
      vertx.deployVerticle("info.galudisu.SampleVerticle", opts);
      // vertx.deployVerticle(SampleVerticle.class, opts);
    }
  }
}
