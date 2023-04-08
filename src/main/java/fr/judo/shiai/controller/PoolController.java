package fr.judo.shiai.controller;

import fr.judo.shiai.domain.*;
import fr.judo.shiai.dto.PoolDto;
import fr.judo.shiai.mappers.PoolMapper;
import fr.judo.shiai.repository.CategoryRepository;
import fr.judo.shiai.repository.JudokaRepository;
import fr.judo.shiai.solver.SenshiSolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin
@RestController
public class PoolController {

    private static final int MAX_JUDOKAS_PER_POOL = 4;

    @Autowired
    private JudokaRepository judokaRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SenshiSolver senshiSolver;

    @Autowired
    private PoolMapper poolMapper;

    @PostMapping("/pools")
    public Iterable<PoolDto> computeAllPools() {
        PoolDispatchingSolution poolDispatchingSolution = new PoolDispatchingSolution();
        try {
            // Retrieve judokas
            List<Judoka> judokas = judokaRepository.findAllPresentAndWeightedJudoka();
            log.info("Judokas available : " + judokas.size());
            // Declare pools
            List<Pool> pools = computePoolsList(judokas.size());
            // Declare problem/solution container
            poolDispatchingSolution.setPoolList(pools);
            poolDispatchingSolution.setJudokaList(judokas);
            poolDispatchingSolution = senshiSolver.solve(poolDispatchingSolution);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return poolMapper.toDto(poolDispatchingSolution.getPoolList());
    }

    @PostMapping("/pools-fallback")
    public Iterable<PoolDto> computeAllPoolsFallback() {

        List<Pool> pools = new ArrayList<>();
        final AtomicLong poolIndexCounter = new AtomicLong();

        // Retrieve judokas
        List<Judoka> judokas = judokaRepository.findAllPresentAndWeightedJudoka();
        Iterable<Category> categories = categoryRepository.findAll();

        // create sub-groups of category and sex among the judokas present and weighted
        categories.forEach(category -> {

            Collection<Judoka> judokaInCategory = judokas.stream()
                    .filter(judoka -> judoka.getCategory().getName().equals(category.getName()))
                    .toList();

            Collection<Judoka> boys = judokaInCategory.stream()
                    .filter(judoka -> judoka.getGender().equals(Gender.MALE))
                    .sorted(Comparator.comparing(Judoka::getWeight))
                    .toList();
            Collection<Judoka> girls = judokaInCategory.stream()
                    .filter(judoka -> judoka.getGender().equals(Gender.FEMALE))
                    .sorted(Comparator.comparing(Judoka::getWeight))
                    .toList();

            pools.addAll(buildPools(boys, poolIndexCounter));
            pools.addAll(buildPools(girls, poolIndexCounter));
        });

        return poolMapper.toDto(pools);
    }

    private Collection<? extends Pool> buildPools(Collection<Judoka> judokas, AtomicLong poolIndexCounter) {
        final AtomicInteger counter = new AtomicInteger();
        return new ArrayList<>(
                judokas.stream()
                        .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / MAX_JUDOKAS_PER_POOL))
                        .values())
                .stream().map(judokasSubSet -> {
                    Pool pool = new Pool();
                    pool.setId(poolIndexCounter.getAndIncrement());
                    pool.setJudokaList(judokasSubSet);
                    return pool;
                }).toList();
    }

    /**
     * @param judokasCount
     * @return
     */
    private List<Pool> computePoolsList(final int judokasCount) {
        List<Pool> poolList = new ArrayList<>();
        for (int i = 0; i < judokasCount / MAX_JUDOKAS_PER_POOL + 1; i++) {
            Pool pool = new Pool();
            pool.setId(Long.valueOf(i));
            poolList.add(pool);
        }
        return poolList;
    }
}
