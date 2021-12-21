package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
  private final RecipeRepository recipeRepository;
  private final RecipeToRecipeCommand recipeToRecipeCommand;
  private final RecipeCommandToRecipe recipeCommandToRecipe;

  public RecipeServiceImpl(
      RecipeRepository recipeRepository,
      RecipeToRecipeCommand recipeToRecipeCommand,
      RecipeCommandToRecipe recipeCommandToRecipe) {
    this.recipeRepository = recipeRepository;
    this.recipeToRecipeCommand = recipeToRecipeCommand;
    this.recipeCommandToRecipe = recipeCommandToRecipe;
  }

  @Override
  public Set<Recipe> getRecipes() {
    log.debug("I'm in the service");
    Set<Recipe> all = new HashSet<>();
    recipeRepository.findAll().iterator().forEachRemaining(all::add);
    return all;
  }

  @Override
  public Recipe findById(Long id) {
    return recipeRepository.findById(id).orElse(null);
  }

  @Override
  @Transactional
  public RecipeCommand saveRecipeCommand(RecipeCommand command) {
    Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

    Recipe savedRecipe = recipeRepository.save(detachedRecipe);
    log.debug("Saved RecipeId: " + savedRecipe.getId());
    return recipeToRecipeCommand.convert(savedRecipe);
  }

  @Override
  @Transactional
  public RecipeCommand findRecipeCommandById(Long id) {
    return recipeToRecipeCommand.convert(findById(id));
  }

  @Override
  public void deleteById(Long id){
    recipeRepository.deleteById(id);
  }
}
