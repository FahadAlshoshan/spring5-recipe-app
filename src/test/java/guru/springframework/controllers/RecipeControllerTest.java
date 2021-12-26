package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeControllerTest {
  @Mock RecipeService recipeService;
  RecipeController recipeController;
  MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    recipeController = new RecipeController(recipeService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(recipeController)
            .setControllerAdvice(new ControllerExceptionHandler())
            .build();
  }

  @Test
  public void testGetRecipeNotFound() throws Exception {
    Recipe recipe = new Recipe();
    recipe.setId(1L);

    when(recipeService.findById(anyLong())).thenThrow(NotFoundException.class);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/show"))
        .andExpect(status().isNotFound())
        .andExpect(view().name("404error"));
  }

  @Test
  public void testGetRecipeNumberFormat() throws Exception {
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/s/show"))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("400error"));
  }

  @Test
  public void testGetRecipe() throws Exception {
    Recipe recipe = new Recipe();
    recipe.setId(1L);

    when(recipeService.findById(anyLong())).thenReturn(recipe);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/show"))
        .andExpect(status().isOk())
        .andExpect(view().name("/recipe/show"))
        .andExpect(model().attributeExists("recipe"));
  }

  @Test
  public void testGetNewRecipeForm() throws Exception {
    RecipeCommand command = new RecipeCommand();

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("recipe/recipeform"))
        .andExpect(model().attributeExists("recipe"));
  }

  @Test
  public void testUpdateRecipeForm() throws Exception {
    RecipeCommand command = new RecipeCommand();
    command.setId(2L);

    when(recipeService.findRecipeCommandById(anyLong())).thenReturn(command);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/update"))
        .andExpect(status().isOk())
        .andExpect(view().name("recipe/recipeform"))
        .andExpect(model().attributeExists("recipe"));
  }

  @Test
  public void testPostNewRecipeForm() throws Exception {
    RecipeCommand command = new RecipeCommand();
    command.setId(2L);

    when(recipeService.saveRecipeCommand(any())).thenReturn(command);

    mockMvc.perform(MockMvcRequestBuilders.post("/recipe")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("id", "")
                    .param("description", "some string")
                    .param("directions", "some directions")
                    .param("url", "https://someurl.com")

            )
            .andExpect(view().name("redirect:/recipe/2/show"));
  }

  @Test
  public void testDeleteRecipe() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/delete"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"));
    verify(recipeService, times(1)).deleteById(anyLong());
  }
}
