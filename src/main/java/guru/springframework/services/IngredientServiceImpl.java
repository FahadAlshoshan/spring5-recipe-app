package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {
  private final RecipeRepository recipeRepository;
  private final IngredientToIngredientCommand ingredientToIngredientCommand;
  private final UnitOfMeasureRepository unitOfMeasureRepository;
  private final IngredientCommandToIngredient ingredientCommandToIngredient;

  public IngredientServiceImpl(
          RecipeRepository recipeRepository,
          IngredientToIngredientCommand ingredientToIngredientCommand,
          UnitOfMeasureRepository unitOfMeasureRepository, IngredientCommandToIngredient ingredientCommandToIngredient) {
    this.recipeRepository = recipeRepository;
    this.ingredientToIngredientCommand = ingredientToIngredientCommand;
    this.unitOfMeasureRepository = unitOfMeasureRepository;
    this.ingredientCommandToIngredient = ingredientCommandToIngredient;
  }

  @Override
  public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
    Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(RuntimeException::new);

    Optional<IngredientCommand> ingredientCommand =
        recipe.getIngredients().stream()
            .filter(ingredient -> ingredient.getId().equals(ingredientId))
            .map(ingredient -> ingredientToIngredientCommand.convert(ingredient))
            .findFirst();
    return ingredientCommand.get();
  }

  @Override
  @Transactional
  public IngredientCommand saveIngredientCommand(IngredientCommand command) {
    Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());

    if (!recipeOptional.isPresent()) {

      // todo toss error if not found!
      log.error("Recipe not found for id: " + command.getRecipeId());
      return new IngredientCommand();
    } else {
      Recipe recipe = recipeOptional.get();

      Optional<Ingredient> ingredientOptional =
          recipe.getIngredients().stream()
              .filter(ingredient -> ingredient.getId().equals(command.getId()))
              .findFirst();

      if (ingredientOptional.isPresent()) {
        Ingredient ingredientFound = ingredientOptional.get();
        ingredientFound.setDescription(command.getDescription());
        ingredientFound.setAmount(command.getAmount());
        ingredientFound.setUnitOfMeasure(
            unitOfMeasureRepository
                .findById(command.getUnitOfMeasure().getId())
                .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"))); // todo address this
      } else {
        // add new Ingredient
        recipe.addIngredient(ingredientCommandToIngredient.convert(command));
      }

      Recipe savedRecipe = recipeRepository.save(recipe);

      // to do check for fail
      return ingredientToIngredientCommand.convert(
          savedRecipe.getIngredients().stream()
              .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
              .findFirst()
              .get());
    }
  }
}
