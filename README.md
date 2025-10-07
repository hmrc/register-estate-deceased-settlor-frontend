# Register estate deceased settlor

This service is responsible collecting information about the deceased settlor for the estate that is being registered.

To run locally using the micro-service provided by the service manager:

***sm2 --start ESTATES_ALL***

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8824 but is defaulted to that in build.sbt).

```bash
  sbt run
```

## Testing the service
Run unit tests before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests and integration tests and checks for dependency updates:

```bash
 ./run_all_tests.sh
```
### License


This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
