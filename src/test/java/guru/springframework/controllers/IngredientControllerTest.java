package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IngredientControllerTest {
  @Mock RecipeService recipeService;
  @Mock IngredientService ingredientService;
  @Mock UnitOfMeasureService unitOfMeasureService;
  IngredientController ingredientController;
  MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ingredientController =
        new IngredientController(ingredientService, recipeService, unitOfMeasureService);
    mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();
  }

  @Test
  public void testListIngredients() throws Exception {
    RecipeCommand recipeCommand = new RecipeCommand();
    when(recipeService.findRecipeCommandById(anyLong())).thenReturn(recipeCommand);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/ingredients"))
        .andExpect(status().isOk())
        .andExpect(view().name("recipe/ingredient/list"))
        .andExpect(model().attributeExists("recipe"));

    verify(recipeService, times(1)).findRecipeCommandById(anyLong());
  }

  @Test
  public void testShowIngredients() throws Exception {
    IngredientCommand ingredientCommand = new IngredientCommand();
    when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong()))
        .thenReturn(ingredientCommand);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/ingredient/2/show"))
        .andExpect(status().isOk())
        .andExpect(view().name("recipe/ingredient/show"))
        .andExpect(model().attributeExists("ingredient"));

    verify(ingredientService, times(1)).findByRecipeIdAndIngredientId(anyLong(), anyLong());
  }
}
