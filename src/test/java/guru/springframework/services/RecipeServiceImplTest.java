package guru.springframework.services;

import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

public class RecipeServiceImplTest extends TestCase {
  RecipeServiceImpl recipeService;

  @Mock RecipeRepository recipeRepository;
  @Mock RecipeToRecipeCommand recipeToRecipeCommand;
  @Mock RecipeCommandToRecipe recipeCommandToRecipe;

  @Override
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    recipeService =
        new RecipeServiceImpl(recipeRepository, recipeToRecipeCommand, recipeCommandToRecipe);
  }

  @Test
  public void testGetRecipes() {
    Recipe recipe = new Recipe();
    HashSet recipesData = new HashSet();
    recipesData.add(recipe);

    when(recipeRepository.findAll()).thenReturn(recipesData);

    Set<Recipe> recipeSet = recipeService.getRecipes();

    assertEquals(recipeSet.size(), 1);
    verify(recipeRepository, times(1)).findAll();
  }

  @Test
  public void testGetRecipeById() {
    Recipe recipe = new Recipe();
    recipe.setId(1L);
    Optional<Recipe> recipeOptional = Optional.of(recipe);

    when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);
    Recipe recipeReturned = recipeService.findById(1L);

    assertNotNull(recipeReturned);
    verify(recipeRepository, times(1)).findById(anyLong());
    verify(recipeRepository, never()).findAll();
  }

  @Test
  public void testDeleteById() {
    recipeService.deleteById(2L);
    verify(recipeRepository,times(1)).deleteById(2L);
  }
}
