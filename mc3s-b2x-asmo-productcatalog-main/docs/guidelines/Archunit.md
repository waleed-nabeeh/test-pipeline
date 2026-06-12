# Archunit 

The classes are also tested with Architecture Junit Tests using [ArchUnit](https://www.archunit.org/)

The tests are located in `mc3s-b2x-commons` project and module `mc3s-b2x-archunit` 

## Tests

- naming conventions 
- package conventions 
- expected annotations
- classes line count
- junit test classes rules
- helper expectations 
- layered architecture
- logging guidelines

The checks are automatically executed in a standard build 

``` 
./mvnw clean install
```

Failure Example during build 

```
[ERROR] Failures:
[ERROR]   Architecture Violation [Priority: MEDIUM] - Rule 'use logger.warn as max' was violated (1 times):
Method <com.mindcurv.b2x.aws.core.rekognition.impl.DefaultAwsRekognitionFaceDetectionService.detectFacesSimilarity(java.util.List, float)> calls method <org.slf4j.Logger.error(java.lang.String, java.lang.Object)> in (DefaultAwsRekognitionFaceDetectionService.java:52)
```