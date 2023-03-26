package fr.judo.shiai.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudokaLoader {

    private JudokaRepository judokaRepository;

    public JudokaLoader (@Autowired final JudokaRepository judokaRepository) {
        this.judokaRepository = judokaRepository;
       // judokaRepository.createJudoka();
    }
}
