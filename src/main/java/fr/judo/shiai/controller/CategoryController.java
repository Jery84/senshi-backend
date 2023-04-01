package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Category;
import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("category")
    public Iterable<Category> findAll() {
        return categoryRepository.findAll();
    }
}
