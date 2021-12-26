package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ImageControllerTest {
  @Mock ImageService imageService;
  @Mock RecipeService recipeService;
  ImageController imageController;
  MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    imageController = new ImageController(imageService, recipeService);
    mockMvc =
        MockMvcBuilders.standaloneSetup(imageController)
            .setControllerAdvice(new ControllerExceptionHandler())
            .build();
  }

  @Test
  public void getImageForm() throws Exception {
    // given
    RecipeCommand command = new RecipeCommand();
    command.setId(1L);

    when(recipeService.findRecipeCommandById(anyLong())).thenReturn(command);

    // when
    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/1/image"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("recipe"));

    verify(recipeService, times(1)).findRecipeCommandById(anyLong());
  }

  @Test
  public void testPostImageForm() throws Exception {
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "imagefile", "testing.txt", "test/plain", "Spreing Framework Guru".getBytes());

    mockMvc
        .perform(multipart("/recipe/1/image").file(multipartFile))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "/recipe/1/show"));
    verify(imageService, times(1)).saveImageFile(anyLong(), any());
  }

  @Test
  public void renderImageFromDB() throws Exception {

    // given
    RecipeCommand command = new RecipeCommand();
    command.setId(1L);

    String s = "fake image text";
    Byte[] bytesBoxed = new Byte[s.getBytes().length];

    int i = 0;

    for (byte primByte : s.getBytes()) {
      bytesBoxed[i++] = primByte;
    }

    command.setImage(bytesBoxed);

    when(recipeService.findRecipeCommandById(anyLong())).thenReturn(command);

    // when
    MockHttpServletResponse response =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/recipe/1/recipeimage"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    byte[] reponseBytes = response.getContentAsByteArray();

    assertEquals(s.getBytes().length, reponseBytes.length);
  }

  @Test
  public void testGetImageNumberFormatException() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/recipe/asdf/image"))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("400error"));
  }
}
