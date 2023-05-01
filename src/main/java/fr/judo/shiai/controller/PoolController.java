package fr.judo.shiai.controller;

import fr.judo.shiai.domain.*;
import fr.judo.shiai.dto.PoolDto;
import fr.judo.shiai.mappers.PoolMapper;
import fr.judo.shiai.repository.CategoryRepository;
import fr.judo.shiai.repository.JudokaRepository;
import fr.judo.shiai.repository.PoolRepository;
import fr.judo.shiai.solver.SenshiSolver;
import fr.judo.shiai.solver.SenshiSolverSecondChoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
    private PoolRepository poolRepository;
    @Autowired
    private JudokaRepository judokaRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PoolMapper poolMapper;

    @Autowired
    private SenshiSolver senshiSolver;

    @Autowired
    private SenshiSolverSecondChoice senshiSolverSecondChoice;

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

            if (!allPoolsAreValid(poolDispatchingSolution.getPoolList())) {
                pools.clear();
                log.info("Recompute pools to try to get a better result");
                poolDispatchingSolution = new PoolDispatchingSolution();
                // Declare pools
                pools = computePoolsList(judokas.size());
                // Declare problem/solution container
                poolDispatchingSolution.setPoolList(pools);
                poolDispatchingSolution.setJudokaList(judokas);
                poolDispatchingSolution = senshiSolverSecondChoice.solve(poolDispatchingSolution);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        poolRepository.deleteAll();
        poolRepository.saveAll(poolDispatchingSolution.getPoolList().stream()
                .filter(pool -> !pool.getJudokaList().isEmpty()).collect(Collectors.toList()));

        // ensure that we return the correct ids
        return poolMapper.toDto(poolRepository.findAll());
    }

    @PostMapping("/pools-fallback")
    public Iterable<PoolDto> computeAllPoolsFallback() {

        List<Pool> pools = new ArrayList<>();

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

            pools.addAll(buildPools(boys));
            pools.addAll(buildPools(girls));
        });

        poolRepository.deleteAll();
        poolRepository.saveAll(pools);

        // ensure that we return the correct ids
        return poolMapper.toDto(poolRepository.findAll());
    }

    @GetMapping("pool")
    public Iterable<PoolDto> findAll() {
        return poolMapper.toDto(poolRepository.findAll());
    }

    @PutMapping(path = "pool/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PoolDto> update(@PathVariable Long id, @RequestBody PoolDto pool) {

        Pool updated = poolRepository.findById(id).get();
        updated.setName(pool.getTitle());
        updated.getJudokaList().clear();
        pool.getJudokas().forEach(judokaDto -> {
            Judoka judoka = judokaRepository.findById(judokaDto.getId()).get();
            judoka.setPool(updated);
            judokaRepository.save(judoka);
            updated.getJudokaList().add(judoka);
        });
        poolRepository.save(updated);

        return new ResponseEntity<>(poolMapper.toDto(updated), HttpStatus.ACCEPTED);
    }

    private Collection<? extends Pool> buildPools(Collection<Judoka> judokas) {
        final AtomicInteger counter = new AtomicInteger();
        return new ArrayList<>(
                judokas.stream()
                        .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / MAX_JUDOKAS_PER_POOL))
                        .values())
                .stream().map(judokasSubSet -> {
                    Pool pool = new Pool();
                    pool.setJudokaList(judokasSubSet);
                    return pool;
                }).toList();
    }

    /**
     * @param judokasCount count all judokas in list
     * @return list of pools according max size
     */
    private List<Pool> computePoolsList(final int judokasCount) {
        List<Pool> poolList = new ArrayList<>();
        for (int i = 0; i < judokasCount / MAX_JUDOKAS_PER_POOL + 1; i++) {
            Pool pool = new Pool();
            pool.setId((long) i);
            pool.setId(Long.valueOf(i + 1));
            poolList.add(pool);
        }
        return poolList;
    }

    private boolean isPoolValid(final Pool pool) {
        double maxWeight = pool.getJudokaList().stream().max(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight();
        double minWeight = pool.getJudokaList().stream().min(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight();
        return (pool.getJudokaList().size() >= 2 && pool.getJudokaList().size() < 5)
                && (maxWeight * 0.1 > maxWeight - minWeight);
    }

    private boolean allPoolsAreValid(final List<Pool> pools) {
        boolean res = true;
        for (Pool pool : pools) {
            res = res && isPoolValid(pool);
        }
        return res;
    }
}
