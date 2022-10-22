package info.galudisu.kotlin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.jackson.DatabindCodec
import org.slf4j.LoggerFactory

/**
 *
 * Main vertx entry.
 *
 * @author galudisu
 */
class MainVerticle : AbstractVerticle() {

  companion object {
    private val LOGGER = LoggerFactory.getLogger(MainVerticle::class.java)

    init {
      LOGGER.info("Customizing the built-in jackson ObjectMapper...")
      val objectMapper = DatabindCodec.mapper()
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
      objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
      val module = JavaTimeModule()
      objectMapper.registerModule(module)
    }
  }

  @Throws(Exception::class)
  override fun start(startPromise: Promise<Void>?) {
    LOGGER.info("Starting UDP server...")
    super.start(startPromise)
  }
}
