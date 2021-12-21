package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecipeServiceIT {
  public static final String DESC = "New Description";
  @Autowired RecipeRepository recipeRepository;
  @Autowired RecipeToRecipeCommand recipeToRecipeCommand;
  @Autowired RecipeCommandToRecipe recipeCommandToRecipe;
  @Autowired RecipeService recipeService;

  @Transactional
  @Test
  public void testSaveOfDescription() {
    Iterable<Recipe> recipes = recipeRepository.findAll();
    Recipe testRecipe = recipes.iterator().next();
    RecipeCommand testRecipeCommand = recipeToRecipeCommand.convert(testRecipe);

    testRecipeCommand.setDescription(DESC);
    RecipeCommand savedRecipeCommand = recipeService.saveRecipeCommand(testRecipeCommand);

    assertEquals(DESC, savedRecipeCommand.getDescription());
    assertEquals(testRecipe.getId(), savedRecipeCommand.getId());
    assertEquals(testRecipe.getCategories().size(),savedRecipeCommand.getCategories().size());
    assertEquals(testRecipe.getIngredients().size() , savedRecipeCommand.getIngredients().size());
  }
}
