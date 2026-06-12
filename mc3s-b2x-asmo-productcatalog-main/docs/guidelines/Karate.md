# API tests using karate
The APIs can be tested with karate.

## Configuration
| Environment variable | Purpose                                                                                                                    |
|----------------------|----------------------------------------------------------------------------------------------------------------------------|
| `TEST_PASSWORD`      | Define password for execution, see 1 password.                                                                             |
| `ENVIRONMENT`        | provide environment , default = 'dev'                                                                                      |
| `XSSFILTER_ENABLED`  | Disable XSS filter . In case you need to disable XSS filters: set the environment variable `XSSFILTER_ENABLED` to `false`. |

## Execution
![runkarate](images/RunKarate.png)

It will expect a running aggregator as well. If you want to start it automatically: add the SpringBootTest annotation in Test Runner (KarateApiTest).

You can run the test against local or DEV environment by setting the karate environment.

```java
@Tag("KarateTest")
@SpringBootTest(webEnvironment = DEFINED_PORT, classes = {Mc3sRunnerApp.class})
class KarateApiTest {

    @BeforeEach
  void setup() {
    // provide empty value as environemtn variable to run against local
    System.setProperty("karate.env", toRootLowerCase(Optional.ofNullable(System.getenv("ENVIRONMENT")).orElse("dev")));
  }

}
```

## Expected result

![result](images/KarateResult.png)
