package guru.springframework.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
public class Recipe {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String description;
  private Integer prepTime;
  private Integer CookTime;
  private String source;
  private String url;
  private Integer servings;

  @Lob private String directions;

  @Lob private Byte[] image;

  @Enumerated(value = EnumType.STRING)
  private Difficulty difficulty;

  @OneToOne(cascade = CascadeType.ALL)
  private Note note;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
  private Set<Ingredient> ingredients = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "recipe_category",
      joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories = new HashSet<>();

  public void setNote(Note note) {
    if (note != null) {
      this.note = note;
      note.setRecipe(this);
    }
  }

  public Recipe addIngredient(Ingredient ingredient) {
    ingredient.setRecipe(this);
    this.ingredients.add(ingredient);
    return this;
  }
}
