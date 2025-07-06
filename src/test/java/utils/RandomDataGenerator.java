package utils;

import java.util.List;
import java.util.UUID;

public class RandomDataGenerator {
    public static String generateRandomEmail() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static String generateRandomPassword() {
        return "pass_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateRandomName() {
        return "name_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static List<String> generateRandomIngredients(List<String> availableIds, int count) {
        if (availableIds.size() < count) {
            count = availableIds.size();
        }
        return availableIds.subList(0, count);
    }
}