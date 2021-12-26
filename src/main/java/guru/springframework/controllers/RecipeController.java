package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Slf4j
@Controller
public class RecipeController {
  public static final String RECIPE_RECIPEFORM = "recipe/recipeform";
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
    return RECIPE_RECIPEFORM;
  }

  @GetMapping("/recipe/new")
  public String newRecipe(Model model) {
    model.addAttribute("recipe", new RecipeCommand());
    return RECIPE_RECIPEFORM;
  }

  @PostMapping("/recipe")
  public String saveOrUpdate(
      @Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
      return RECIPE_RECIPEFORM;
    }
    RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);
    return "redirect:/recipe/" + savedCommand.getId() + "/show";
  }

  @GetMapping("/recipe/{id}/delete")
  public String deleteRecipe(@PathVariable Long id, Model model) {
    log.debug("Deleting id: " + id);
    recipeService.deleteById(new Long(id));
    return "redirect:/";
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public ModelAndView handleNotFound(Exception exception) {
    log.error("Handling Not Found Exception");
    log.error(exception.getMessage());
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.addObject("exception", exception);
    modelAndView.setViewName("404error");
    return modelAndView;
  }

  //  @ExceptionHandler(NotFoundException.class)
  //  public String handleNotFound() {
  //    log.error("Handling not Found Exception");
  //    return "404error";
  //  }
}
