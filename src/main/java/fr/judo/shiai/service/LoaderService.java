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
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class LoaderService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private JudokaRepository judokaRepository;

    public void load() {
        log.info("Loading data");
        loadClubs();
        loadCategories();
        loadJudokas();
    }

    private void loadClubs() {
        try {
            ClassPathResource resource = new ClassPathResource("data/clubs.csv");
            InputStream inputStream = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            CSVReader csvReader = new CSVReaderBuilder(bufferedReader)
                    .withSkipLines(1)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                    .build();

            String[] ligne;
            Collection<Club> clubs = new ArrayList<>();
            while ((ligne = csvReader.readNext()) != null) {
                clubs.add(Club.builder().name(ligne[0]).build());
            }
            clubRepository.saveAll(clubs);
            log.info(clubs.size() + " clubs defined");
            csvReader.close();
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            ClassPathResource resource = new ClassPathResource("data/categories.csv");
            InputStream inputStream = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            CSVReader csvReader = new CSVReaderBuilder(bufferedReader)
                    .withSkipLines(1)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                    .build();

            String[] ligne;
            Collection<Category> categories = new ArrayList<>();
            while ((ligne = csvReader.readNext()) != null) {
                categories.add(Category.builder()
                        .name(ligne[0])
                        .yearMin(Integer.parseInt(ligne[1]))
                        .yearMax(Integer.parseInt(ligne[2]))
                        .build());
            }
            categoryRepository.saveAll(categories);
            log.info(categories.size() + " categories defined");
            csvReader.close();
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private void loadJudokas() {
        try {
            ClassPathResource resource = new ClassPathResource("data/judokas.csv");
            InputStream inputStream = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            CSVReader csvReader = new CSVReaderBuilder(bufferedReader)
                    .withSkipLines(1)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                    .build();

            String[] ligne;
            Collection<Judoka> judokas = new ArrayList<>();
            while ((ligne = csvReader.readNext()) != null) {

                //                id;gender;firstName;lastName;club;birthDate;weight;license;category;present

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
            judokaRepository.saveAll(judokas);
            log.info(judokas.size() + " judokas loaded");
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
