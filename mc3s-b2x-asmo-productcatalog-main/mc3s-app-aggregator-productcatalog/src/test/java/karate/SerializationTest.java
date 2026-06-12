package karate;

import static com.mindcurv.b2x.commons.helper.JsonUtils.readObject;
import static com.mindcurv.b2x.commons.helper.JsonUtils.toJsonString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.commercetools.api.models.common.LocalizedString;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("UnitTest")
final class SerializationTest {

  private static final Logger LOG = LoggerFactory.getLogger(SerializationTest.class);

  @Test
  void serialization() {
    final var localizedString = LocalizedString.builder().addValue("en", "value").build();
    LOG.info("defaults :: {}", localizedString);
    assertNotNull(localizedString, "expected localizedString");
    final var result = toJsonString(localizedString);
    LOG.info("defaults :: {}", result);
    assertNotNull(result, "expected result");
    final var parsed = readObject(result, LocalizedString.class);
    LOG.info("defaults :: {}", parsed);
    assertNotNull(parsed, "expected parsed");
    assertAll(
        () -> assertNotNull(parsed.values(), "expected values")
    );
  }
}
