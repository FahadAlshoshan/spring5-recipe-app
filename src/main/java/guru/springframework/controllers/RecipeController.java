package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class RecipeController {
  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }


  @GetMapping("/recipe/{id}/show")
  public String showById(@PathVariable String id, Model model) {
    model.addAttribute("recipe", recipeService.findById(new Long(id)));
    return "/recipe/show";
  }


  @GetMapping("/recipe/{id}/update")
  public String newRecipe(@PathVariable String id, Model model) {
    model.addAttribute("recipe", recipeService.findRecipeCommandById(new Long(id)));
    return "recipe/recipeform";
  }


  @GetMapping("/recipe/new")
  public String newRecipe(Model model) {
    model.addAttribute("recipe", new RecipeCommand());
    return "recipe/recipeform";
  }

  @PostMapping("/recipe")
  public String saveOrUpdate(@ModelAttribute RecipeCommand command) {
    RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);
    return "redirect:/recipe/" + savedCommand.getId() + "/show";
  }

  @GetMapping("/recipe/{id}/delete")
  public String deleteRecipe(@PathVariable Long id, Model model) {
    log.debug("Deleting id: " + id);
    recipeService.deleteById(new Long(id));
    return "redirect:/";
  }
}