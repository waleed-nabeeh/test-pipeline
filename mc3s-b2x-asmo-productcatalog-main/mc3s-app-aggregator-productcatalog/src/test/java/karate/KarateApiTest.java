package karate;

import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.ACCESS_TOKEN_ADMIN;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.ACCESS_TOKEN_BUYER;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.ACCESS_TOKEN_USER;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.TEST_ADMIN;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.TEST_BUYER;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.TEST_USER;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.executeKarateTests;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.getEnvironment;
import static com.mindcurv.b2x.archunit.karate.helper.KarateTestHelper.getStore;
import static com.mindcurv.b2x.archunit.karate.helper.SlackHelper.slackAlerts;
import static com.mindcurv.b2x.archunit.karate.helper.TeamsHelper.teamsAlerts;
import static com.mindcurv.b2x.aws.core.cognito.helper.OAuthHelper.getAccessTokenForRole;
import static java.lang.System.getenv;
import static java.lang.System.setProperty;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("KarateTest")
final class KarateApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(KarateApiTest.class);

  @Karate.Test
  Karate productcatalog() {
    if (executeKarateTests()) {
      LOG.info("productcatalog :: executing tests");
      setProperty(ACCESS_TOKEN_ADMIN, getAccessTokenForRole(getenv(TEST_ADMIN)));
      setProperty(ACCESS_TOKEN_BUYER, getAccessTokenForRole(getenv(TEST_BUYER)));
      setProperty(ACCESS_TOKEN_USER, getAccessTokenForRole(getenv(TEST_USER)));
      setProperty("karate.env", getEnvironment());
      setProperty("storeIdentifier", getStore());

      return Karate.run(
          "classpath:karate/healthCheck.feature",
          "classpath:karate/product.feature",
          "classpath:karate/attributeGroup.feature",
          "classpath:karate/catalog.feature",
          "classpath:karate/productInStore.feature",
          "classpath:karate/category.feature",
          "classpath:karate/listing.feature",
          "classpath:karate/productTypes.feature");
    }
    LOG.info("productcatalog :: not enabled, executing empty suite");
    return Karate.run("classpath:karate/empty.feature");
  }

  @AfterAll
  static void report() {
    if (executeKarateTests()) {
      slackAlerts();
      teamsAlerts();
    }
    LOG.info("report :: execution finished");
  }
}