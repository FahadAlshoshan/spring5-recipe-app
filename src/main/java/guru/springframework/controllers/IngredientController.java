package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class IngredientController {
  private final IngredientService ingredientService;
  private final RecipeService recipeService;
  private final UnitOfMeasureService unitOfMeasureService;

  public IngredientController(
      IngredientService ingredientService,
      RecipeService recipeService,
      UnitOfMeasureService unitOfMeasureService) {
    this.ingredientService = ingredientService;
    this.recipeService = recipeService;
    this.unitOfMeasureService = unitOfMeasureService;
  }


  @GetMapping("/recipe/{id}/ingredients")
  public String getIngredients(@PathVariable String id, Model model) {
    log.debug("Getting Recipe Ingredients for Recipe id: " + id);
    model.addAttribute("recipe", recipeService.findRecipeCommandById(new Long(id)));
    return "recipe/ingredient/list";
  }

  @GetMapping("/recipe/{RecipeId}/ingredient/{IngredientId}/show")
  public String getIngredients(
      @PathVariable String RecipeId, @PathVariable String IngredientId, Model model) {
    log.debug(
        "Getting Recipe Ingredients for Recipe id: "
            + RecipeId
            + " And Ingredient id: "
            + IngredientId);
    model.addAttribute(
        "ingredient",
        ingredientService.findByRecipeIdAndIngredientId(
            new Long(RecipeId), new Long(IngredientId)));
    return "recipe/ingredient/show";
  }


  @GetMapping("/recipe/{RecipeId}/ingredient/{IngredientId}/update")
  public String updateIngredient(
      @PathVariable String RecipeId, @PathVariable String IngredientId, Model model) {
    model.addAttribute(
        "ingredient",
        ingredientService.findByRecipeIdAndIngredientId(
            Long.valueOf(RecipeId), Long.valueOf(IngredientId)));
    model.addAttribute("uomList", unitOfMeasureService.listAllUoms());
    return "recipe/ingredient/ingredientform";
  }


  @GetMapping("/recipe/{recipeId}/ingredient/new")
  public String newIngredient(@PathVariable String recipeId, Model model) {
    RecipeCommand recipeCommand = recipeService.findRecipeCommandById(Long.valueOf(recipeId));
    IngredientCommand ingredientCommand = new IngredientCommand();
    ingredientCommand.setRecipeId(Long.valueOf(recipeId));

    model.addAttribute("ingredient", ingredientCommand);

    ingredientCommand.setUnitOfMeasure(new UnitOfMeasureCommand());

    model.addAttribute("uomList", unitOfMeasureService.listAllUoms());
    return "recipe/ingredient/ingredientform";
  }


  @PostMapping("/recipe/{recipeId}/ingredient")
  public String saveOrUpdate(@ModelAttribute IngredientCommand command) {
    IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);

    log.debug("saved recipe id:" + savedCommand.getRecipeId());
    log.debug("saved ingredient id:" + savedCommand.getId());

    return "redirect:/recipe/"
        + savedCommand.getRecipeId()
        + "/ingredient/"
        + savedCommand.getId()
        + "/show";
  }


  @GetMapping("/recipe/{recipeId}/ingredient/{ingredientId}/delete")
  public String deleteIngredient(
      @PathVariable String recipeId, @PathVariable String ingredientId, Model model) {
    ingredientService.deleteByRecipeIdAndIngredientId(
        Long.valueOf(recipeId), Long.valueOf(ingredientId));
    return "redirect:/recipe/"+ recipeId +"/ingredients";
  }
}
