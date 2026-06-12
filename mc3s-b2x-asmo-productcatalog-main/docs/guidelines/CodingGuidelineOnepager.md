As a Java developer you want to create a Junit test that has a full test coverage and verify that
the code is follwing all instructions from Coding Guide.

# Coding Guide

The following code snippet shows a best practice approach.

# Code style

Please use Google Code Style. File is located in idea project

```java
package com.mindcurv.b2x.commons.services.impl;

import static com.mindcurv.b2x.commons.AcceleratorSettings.PROPERTIES_KEY_API_URL_SUFFIX;

import com.mindcurv.b2x.commons.services.SpecialService;

@Component
public class DefaultSpecialService implements SpecialService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultSpecialService.class);

  @NotNull
  private final ApplicationContext applicationContext;

  @Automwired
  public DefaultSpecialService(@NotNull final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @PostConstruct
  private void postConstruct() {
    // code execution
    LOG.info("postConstruct :: prepared {} configurations", TYPE_CONFIGURATION_MAP.size());
  }

  @Override
  @NotNull
  public Optional<Reference> doSomething(@Nullable final Reference reference) {
    LOG.debug("doSomething :: processing reference '{}'", reference.getId());
    try {
      final var resultOptional = service.process(reference, PROPERTIES_KEY_API_URL_SUFFIX);
      if (resultOptional.isPresent()) {
        final var result = resultOptional.get();
        LOG.info("doSomething :: success '{}'", result.getId(), e);
        return result;
      }
      LOG.warn("doSomething :: failed to process '{}' and '{}'", reference.getId(),
          PROPERTIES_KEY_API_URL_SUFFIX);
    } catch (final Exception e) {
      LOG.warn("doSomething :: failure for '{}'", reference.getId(), e);
    }
    return Optional.ofNullable(reference);
  }
}
```

The details are explained below.

Pre-Requisite: Lombok is used.

## Basic requirements

- Make use of annotations (`@Data, @Builder, @NotNull, @Nullable, @Valid`)
- Use always `final` and `var` where applicable
- Use static imports for any static methods or constants
- Methods should not have more than *50* lines
- Classes should not have more than *750* lines
- Controllers must not contain business logic
- Exceptions are always using the variable `e`.
- Use `final` for every method argument
- Make use of `Optional`. They allow easy ways to process values in a single line using `ifPresent`,
  `map` or `flatMap`.
- Make use of interfaces.

## Unit Testing

- The test class must be final and annotated with @Tag("UnitTest")
- The test class is named <CLassUnderTest>Test and in same package as the class under test
- All tests for methods under test should be in a Nested class with same name as the method under
  test.
- the @Nested class should be named as the method under test.
    - the positive case test method is
      always named "defaults",
    - the first negative test "failed".
- Every class must have a junit test.
    - Exceptions are: Generated code, Interfaces without default
      implementation, Models (only lombok @Data annotation)
    - Every method should be tested with a positive and negative case at least.

### Mocking

- use static imported openMocks(this)
- Use Mockito (when(). thenReturn)  to create test objects .
- Use @Mock annotation for variables. Setup the mocked arguments in a befroeEach methd and change
  only the required mock values for different scenarios to keep the test code as small as possible.
- Don't define variables in each method / Nested class. Use only global mocks

### Assertions

- assertNotNull for the method return value is always outside the assertAll block. This is only
  required if method is returning null values.
    - NotNull annotated methods don't need this check.
- use then assertAll for all assertions to structure the code after initial check for non null.
- Prefer to use concrete assertions like assertEquals(comparison, result, "message"), assertNull(
  comparison, result, "message"), assertNotNull( result, "message") instead of assertThat(result)
  .is.

### Unit Test metrics

- Expectation for a module is a coverage *> 85%*
- Expectation for new code is 100%
- Verify the results in local test runner and local mutation tests.
- A code change must never lower the coverage. Verify that in PMD result
- Mutation tests must be executed to avoid nonsense tests which only provide good coverage results
  but are not testing business logic. Verify the results in local

## Naming

- Implementation classes are not using `Impl` as suffix and reside in a package `impl`
- The Implementation class has a meaningful Prefix, e.g. `Default<InterfaceName>`
- Helper classes reside in a package helper and are using suffix `Helper`

## Code Annotations

### Spring

- Services are annotated as `@Service`
- Components are annotated as `@Component`

### RequiredArgs / PostConstruct

- do not use `@RequiredArgsConstructor` for as it's not supported in spring 4
- `@Autowired` in case of Spring requirements
- Add `@PostConstruct` on initial setup after spring bean construction.

### Methods

- Add expected `@Nullable` or `@NotNull` annotation to methods and arguments
- use `final` for every argument
- try to return `@NotNull Optional<Object>` instead of `@Nullable Object`

## Logging

Do not use Slf4 annotation from lombok as it's not supported consistently in spring 4.
Use private static final Logger LOG = LoggerFactory.getLogger(CLASS.class);

### LOG Level

- Use `LOG.warn()` as maximum log level
- The error log level `LOG.error()` should not be used as we can then identify not processed errors
  in monitoring. they should not appear within our code.
- Use Log info only for relevant process steps no avoid too many logging statements. They should
  help to see
  the flow. More details can be logged a s LOG.debug

### Message

- Follow the pattern `LOG.info("<methodname> :: <message>")`
- Log exception as additional argument , `LOG.warn("<methodname> :: <message>", e)`
- use single quites to check for correct values. Otherwise you wouldn't see spaces.
  `LOG.warn("<methodname> :: <message> '{}'", value)`
- Full Exceptions need to be logged at least once in a catch block .
  e.getMessage is not sufficient.

### Data security & personal data

- Do not use toString in logging to avoid PID logging
- Bad Example LOG.info("<methodname> :: {}", customer)
- Good Example LOG.info("<methodname> :: ‘{}’", customer.getKey())


- good : openMocks(this); and MockitoAnnotations.openMocks statically imported
- not good: MockitoAnnotations.openMocks(this);

10. add a message to each assertion if possible which explains what went wrong

use the java class from prompt as input 