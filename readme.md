# Concurrent Adder Service
Built with Spring Boot and Tomcat Embedded HTTP Server 

### Usage
* Starting the Adder service

```
    java -jar ./bin/adder.jar
```

* Adding value (e.g. 10)

```
    curl -d 10 http://localhost:1337/
```

* Fetch sum and reset

```
    curl -d end http://localhost:1337/
```

### Concurrency Test
In addition to the multi-thread unit and integration tests in ``./src/test/`` is a [JMeter](https://jmeter.apache.org/) test plan ``./jmeter/AdderTestPlan.jmx``, using a [Thread Group](https://jmeter.apache.org/usermanual/test_plan.html#thread_group) of 25 threads and  [Synchronizing Timer](http://jmeter.apache.org/usermanual/component_reference.html#Synchronizing_Timer) for concurrent requests. 

The test sends POST requests of value ``10`` over ``25 threads`` looped ``400`` times (with 20 requests simultaneous per loop), then sends a POST request with ``end`` value and asserts the response message is ``100000``.




