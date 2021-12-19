package guru.springframework.controllers;

import guru.springframework.domain.Category;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.CategoryRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/** Created by jt on 6/1/17. */
@Slf4j
@Controller
public class IndexController {
  private CategoryRepository categoryRepository;
  private UnitOfMeasureRepository unitOfMeasureRepository;

  public IndexController(
      CategoryRepository categoryRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
    this.categoryRepository = categoryRepository;
    this.unitOfMeasureRepository = unitOfMeasureRepository;
  }

  @RequestMapping({"", "/index", "/"})
  public String getIndexPage() {
    Optional<Category> categoryOptional = categoryRepository.findByDescription("American");
    Optional<UnitOfMeasure> unitOfMeasureOptional =
        unitOfMeasureRepository.findByDescription("Teaspoon");
    System.out.println(
        categoryOptional.get().getDescription()
            + " "
            + unitOfMeasureOptional.get().getDescription());
    return "index";
  }
}

