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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;

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

  @Test
  public void testUpdateIngredientFrom() throws Exception {
    IngredientCommand ingredientCommand = new IngredientCommand();
    when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong()))
        .thenReturn(ingredientCommand);
    when(unitOfMeasureService.listAllUoms()).thenReturn(new HashSet<>());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/ingredient/1/update"))
        .andExpect(status().isOk())
        .andExpect(view().name("recipe/ingredient/ingredientform"))
        .andExpect(model().attributeExists("ingredient"))
        .andExpect(model().attributeExists("uomList"));
  }

  @Test
  public void testNewIngredientFrom() throws Exception {
    RecipeCommand recipeCommand = new RecipeCommand();
    recipeCommand.setId(1L);
    when(recipeService.findRecipeCommandById(anyLong())).thenReturn(recipeCommand);
    when(unitOfMeasureService.listAllUoms()).thenReturn(new HashSet<>());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/ingredient/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("recipe/ingredient/ingredientform"))
        .andExpect(model().attributeExists("ingredient"))
        .andExpect(model().attributeExists("uomList"));

    verify((recipeService) , times(1)).findRecipeCommandById(anyLong());
  }

  @Test
  public void testSaveIngredientForm() throws Exception {
    IngredientCommand ingredientCommand = new IngredientCommand();
    ingredientCommand.setRecipeId(1L);
    ingredientCommand.setId(1L);

    when(ingredientService.saveIngredientCommand(any())).thenReturn(ingredientCommand);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/recipe/1/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "anything"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/recipe/1/ingredient/1/show"));
  }

  @Test
  public void testDeleteIngredient() throws Exception {
    mockMvc
            .perform(MockMvcRequestBuilders.get("/recipe/1/ingredient/1/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/recipe/1/ingredients"));
    verify(ingredientService, times(1)).deleteByRecipeIdAndIngredientId(anyLong(),anyLong());
  }
}
