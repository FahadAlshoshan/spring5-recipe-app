package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class RecipeServiceImplTest extends TestCase {
  RecipeServiceImpl recipeService;

  @Mock RecipeRepository recipeRepository;

  @Override
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    recipeService = new RecipeServiceImpl(recipeRepository);
  }

  @Test
  public void testGetRecipes() {
    Recipe recipe = new Recipe();
    HashSet recipesData = new HashSet();
    recipesData.add(recipe);

    when(recipeRepository.findAll()).thenReturn(recipesData);

    Set<Recipe> recipeSet = recipeService.getRecipes();

    assertEquals(recipeSet, recipeService.getRecipes());
    verify(recipeRepository, times(2)).findAll();
  }
}
