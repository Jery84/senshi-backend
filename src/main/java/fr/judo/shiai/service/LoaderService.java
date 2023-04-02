package fr.judo.shiai.service;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import fr.judo.shiai.domain.Category;
import fr.judo.shiai.domain.Club;
import fr.judo.shiai.domain.Gender;
import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.repository.CategoryRepository;
import fr.judo.shiai.repository.ClubRepository;
import fr.judo.shiai.repository.JudokaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

@Service
@Slf4j
public class LoaderService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private JudokaRepository judokaRepository;

    private static final String CATEGORY_FILE_PATH = "data/categories.csv";
    private static final String CLUBS_FILE_PATH = "data/clubs.csv";
    private static final String JUDOKAS_FILE_PATH = "data/judokas.csv";

    public void load() {
        log.info("Loading data");

        // load the categories
        genericLoad(CATEGORY_FILE_PATH, Category.class, categoryRepository, new BiConsumer<String[], Collection<Category>>() {
            @Override
            public void accept(String[] ligne, Collection<Category> categories) {
                categories.add(Category.builder()
                        .name(ligne[0])
                        .yearMin(Integer.parseInt(ligne[1]))
                        .yearMax(Integer.parseInt(ligne[2]))
                        .build());
            }
        });

        // load the clubs
        genericLoad(CLUBS_FILE_PATH, Club.class, clubRepository, new BiConsumer<String[], Collection<Club>>() {
            @Override
            public void accept(String[] ligne, Collection<Club> clubs) {
                clubs.add(Club.builder().name(ligne[0]).build());
            }
        });

        // load the judokas
        genericLoad(JUDOKAS_FILE_PATH, Judoka.class, judokaRepository, new BiConsumer<String[], Collection<Judoka>>() {
            @Override
            public void accept(String[] ligne, Collection<Judoka> judokas) {
                judokas.add(Judoka.builder()
                        .id(Integer.parseInt(ligne[0]))
                        .gender(Gender.valueOf(ligne[1]))
                        .firstName(ligne[2])
                        .lastName(ligne[3])
                        .club(clubRepository.findById(Long.parseLong(ligne[4])).get())
                        .birthDate(parseDate(ligne[5]))
                        .weight(Strings.isEmpty(ligne[6]) ? 0 : Double.parseDouble(ligne[6]))
                        .license(ligne[7])
                        .category(categoryRepository.findById(ligne[8]).get())
                        .present(ligne[9].equals("Y"))
                        .build());
            }
        });
    }



    private <T> void genericLoad(String filePath, Class<T> categoryClass, CrudRepository repository, BiConsumer<String[], Collection<T>> lambda) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream inputStream = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            CSVReader csvReader = new CSVReaderBuilder(bufferedReader)
                    .withSkipLines(1)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                    .build();

            String[] ligne;
            Collection<T> records = new ArrayList<>();
            while ((ligne = csvReader.readNext()) != null) {
                lambda.accept(ligne, records);
            }
            repository.saveAll(records);
            log.info(records.size() + " '" + categoryClass.getSimpleName() + "' loaded");
            csvReader.close();
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private LocalDate parseDate(String date) {
        String[] dateParts = date.split("/");
        return LocalDate.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[0]));
    }
}
