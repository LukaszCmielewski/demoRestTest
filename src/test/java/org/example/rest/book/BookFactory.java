package org.example.rest.book;

import com.github.javafaker.Faker;
import org.example.rest.author.AuthorDTO;
import org.example.rest.author.AuthorFactory;
import org.example.rest.category.CategoryDTO;
import org.example.rest.category.CategoryFactory;
import org.example.rest.publisher.PublisherFactory;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BookFactory {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    //private final AuthorFactory authorFactory;
    public static BookDTO create(BookDTO dto) {
        return new BookDTO(
                dto.getTitle(),
                dto.getIsbn(),
                dto.getPublicationYear(),
                dto.getEdition(),
                dto.getSummary(),
                dto.getCoverImageUrl(),
                dto.getLanguage(),
                dto.getPublisher(),
                dto.getAuthors(),
                dto.getCategories(),
                dto.isAvailable()
        );
    }

    public static BookDTO createRandom() {
        return BookDTO.builder()
                .title(faker.book().title())
                .isbn(generateISBN13())
                .publicationYear(generateYear())
                .edition(generateEdition())
                .summary(generateSummary())
                .coverImageUrl(faker.internet().image())
                .language(generateLanguage())
                .publisher(PublisherFactory.createRandomPublisher())
                .authors(generateAuthors())
                .categories(generateCategories())
                .available(true).build();
    }

    public static List<CategoryDTO> generateCategories() {
        int count = 1 + random.nextInt(4); // liczba autorów od 1 do 4

        List<CategoryDTO> categories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            categories.add(CategoryFactory.createWithRandomName(null));
        }
        return categories;
    }

    public static List<AuthorDTO> generateAuthors() {
        int count = 1 + random.nextInt(4); // liczba autorów od 1 do 4

        List<AuthorDTO> authors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            authors.add(AuthorFactory.createRandomAuthor());
        }
        return authors;
    }

    public static String generateLanguage() {
        String[] languages = Locale.getISOLanguages();
        String languageCode = languages[random.nextInt(languages.length)];
        Locale locale = new Locale(languageCode);
        return locale.getDisplayLanguage(Locale.ENGLISH); // nazwa języka po angielsku
    }

    public static String generateSummary() {
        return faker.lorem().sentences(3).stream()
                .reduce((s1, s2) -> s1 + " " + s2)
                .orElse("");
    }

    public static String generateEdition() {
        int editionNumber = 1 + random.nextInt(10); // np. 1 do 10
        String suffix;
        if (editionNumber % 100 >= 11 && editionNumber % 100 <= 13) {
            suffix = "th";
        } else {
            switch (editionNumber % 10) {
                case 1:
                    suffix = "st";
                    break;
                case 2:
                    suffix = "nd";
                    break;
                case 3:
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
                    break;
            }
        }
        return editionNumber + suffix + " Edition";
    }

    public static int generateYear() {
        int currentYear = Year.now().getValue();
        int recentStart = currentYear - 30;
        if (random.nextDouble() < 0.8) {
            return recentStart + random.nextInt(currentYear - recentStart + 1);
        } else {
            return 1500 + random.nextInt(recentStart - 1500);
        }
    }

    public static String generateISBN13() {
        String prefix = "978";
        StringBuilder isbnBuilder = new StringBuilder(prefix);
        for (int i = 0; i < 9; i++) {
            isbnBuilder.append(random.nextInt(10)); // 0-9
        }
        String isbnWithoutCheck = isbnBuilder.toString();
        int checkDigit = calculateISBN13CheckDigit(isbnWithoutCheck);
        isbnBuilder.append(checkDigit);
        return isbnBuilder.toString();
    }

    private static int calculateISBN13CheckDigit(String isbnWithoutCheckDigit) {
        int sum = 0;
        for (int i = 0; i < isbnWithoutCheckDigit.length(); i++) {
            int digit = Character.getNumericValue(isbnWithoutCheckDigit.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int remainder = sum % 10;
        return (remainder == 0) ? 0 : 10 - remainder;
    }

    // Test
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println(generateISBN13());
        }
    }
}
