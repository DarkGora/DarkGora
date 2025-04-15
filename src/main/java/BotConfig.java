import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public final class BotConfig {
    private BotConfig() {} // Запрещаем создание экземпляров

    public static final String USER_NAME = "Gora321_bot";
    public static final String TOKEN = "7753540504:AAF6PE6BC8WlrrsIQUHOpO30zcLmqAovII8";
    public static final long GROUP_ID = -1002474189401L;
    public static final String CORRECT_IMAGE_PATH = "images/EZpNyk3XYAA7kv0.jpg";
    public static final String WRONG_IMAGE_PATH = "images/scale_1200.jpg";

    // Другие константы конфигурации
}