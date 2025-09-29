package auto;

import java.util.Random;

public class ApiButtonsService {

    public String getCountButtons(String type) {
        try {
            Thread.sleep(13000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (type.equals("row")) {
            return "{\"count\": \"5\"}";
        } else {
            return "{\"count\": \"%d\"}".formatted(new Random().nextInt(10));
        }
    }
}
