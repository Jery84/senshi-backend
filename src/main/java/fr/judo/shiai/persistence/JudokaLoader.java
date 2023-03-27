package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Judoka;
import lombok.extern.slf4j.Slf4j;
import org.kie.internal.io.ResourceFactory;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JudokaLoader {

    private JudokaRepository judokaRepository;

    public JudokaLoader(@Autowired final JudokaRepository judokaRepository) {
        this.judokaRepository = judokaRepository;
        List<Judoka> judokas = new ArrayList<>();
        try {
            CsvParser.separator(';')
                    .mapWith(
                            CsvMapperFactory
                                    .newInstance()
                                    .defaultDateFormat("dd/MM/yyyy")
                                    .newMapper(Judoka.class))
                    .stream(ResourceFactory.newClassPathResource("judokas.csv").getReader())
                    .collect(Collectors.toCollection(() -> judokas));
            for (Judoka judoka : judokas) {
                judokaRepository.createJudoka(judoka);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

        }
        log.info(judokas.size() + " judokas have been loaded");
    }
}
