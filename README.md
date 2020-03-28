# feign-advanced
A demo application to showcase some more advanced configurations with Feign, like MA-TLS and customised logging.

## Tips
Create a new [Feign client](https://cloud.spring.io/spring-cloud-openfeign/reference/html) for the service you want to consume.
You can do this by creating a new interface (implementation class is provided for you by the framework) under your `service.client` package for example, and annotating it:
```java
@FeignClient(name = "${myclient.name}", url = "${myclient.url}", fallback = YourClientInterface.YourFallbackInnerClass.class)
public interface MyClient { }
```
This will tell Spring how to initialise your client.
In order to use the placeholder values for the `name` and `url` parameters, you of course need to provide the values -with a more appropriate name- in the `application.yml` file.
The `name` parameter does not play a significant role in our context, but the `url` parameter needs to be the host name of the service you want to consume, e.g. `http://consumable-service` in your cluster.

Next, you can create a method on your interface for each endpoint you want to consume.
It should look exactly the same as a Controller method would, with SpringMVC Mapping annotations and return types of model POJO-s, void or even String.
For example:
```java
@GetMapping("/consumable-resources")
ConsumableResource getConsumableResourcesByResourceId(@RequestParam String resourceId);
```
Be aware that any error response you may receive from the consumed service will trigger a `HystrixRuntimeException` that wraps a domain specific `ServiceInvocationException` containing the response status and some of the original request details.
You may want to catch the Hystrix exception, or better yet create an ExceptionHandler method (in case you have an API-driven service) that handles this exception type and extracts the information from the wrapped exception.

If you **don't** want error responses to trigger exceptions, you can use the `feign.Response` class as a return type.
This object will wrap the full HTTP response and you can decide what to do with the body depending on the received status.
This can be especially useful if you are implementing a gateway or an orchestrator service for example.

The last thing you need to add to your interface is the fallback class that appears as the final parameter in the annotation above.
The most convenient way is to create it as an inner class implementing the interface *in the interface itself*.
Hystrix -the circuit breaker configured for your Feign clients- will use the method implementations you provide in this class in case a request times out.
If you have a default response as a fallback, you can return it here.
Otherwise you could throw the above mentioned `ServiceInvocationException` with an appropriate message to leverage the same mechanism you used to handle exceptions triggered by error responses.
Be aware though that in this case the `HystrixRuntimeException` will contain a `fallbackException` member that returns an `AssetionError` instance which finally wraps our `ServiceInvocationException`.
Following the previous examples:
```java
@Component
class MyClientFallback implements MyClient {
    @Override
    ConsumableResource getConsumableResourcesByResourceId(String resourceId) {
        throw new ServiceInvocationException("boo-hoo");
    }
}
```
If you want to customize the default circuit breaker configuration provided by Hystrix, you can do so in the `application.yml` file.
You can read [this documentation](https://github.com/Netflix/Hystrix/wiki/Configuration) as a reference, but here is an example you could use as a starting point:
```yaml
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000
      circuitBreaker:
        requestVolumeThreshold: 2
        sleepWindowInMilliseconds: 60000
```
This will create a shared configuration for all your clients with a request timeout of 3 seconds.
If two requests time out within a rolling window 10 seconds (default), the circuit will open and any new requests will be rejected for the next minute.
You can change these values according to the load of your service.
In case of a fairly low load, and a small system, you probably don't need to rely on circuit breaking at all.
You can also define these properties per endpoint that you want to invoke -i.e. per method on your Feign client interface- by duplicating everything starting from `default:`, and replacing the `default:` line with `YourClientInterface.getConsumableResourcesByResourceId(String):` (following the example names from above).  

This bedrock comes with some built in logging, error handling and optional TLS capabilities that will be applied to any Feign client you create.
You can find this in the `FeignClientConfiguration.java` class.
If you want to configure a trust store containing the certificate(s) of the consumed service, or even a trust store and a keystore (containing your own private key and certificate) for mutual authentication, you need to provide these as resources.
You can then wire these into your application via the following properties:
```yaml
feign:
  hystrix:
    enabled: true
  client:
    withTls: true
  secrets:
    trustStoreResource: <file_name>
    keyStoreResource: <file_name>
    keyStorePassword: <password>
```
So basically: enable the TLS by flipping the boolean flag, and then provide the file names and the key store password (don't use this manner of providing passwords in production code!).