package helloworld;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static String template = "Hello, %s!";
    private AtomicLong counter = new AtomicLong();
    HelloJsonClient helloJsonClient = new HelloJsonClient("localhost", 50051);
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        helloJsonClient.greet(name);
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}
