package blackbird.spring;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String[] greeting(String message) throws Exception {

        System.out.println("message:" + message);
        String[] a = {"answer to " + message};
        return a;

    }

}