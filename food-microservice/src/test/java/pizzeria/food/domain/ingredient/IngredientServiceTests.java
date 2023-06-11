package pizzeria.food.domain.ingredient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.food.models.prices.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class IngredientServiceTests {

    @Autowired
    private transient IngredientRepository repo;

    @Autowired
    private transient IngredientService ingredientService;

    @Test
    void registerIngredient1() {
        Ingredient ingredient = new Ingredient("test", 1.0, new ArrayList<>());
        try {
            Ingredient ing = ingredientService.registerIngredient(ingredient);
            assertTrue(repo.existsById(ing.getId()));
            List<Ingredient> test = repo.findAll();
            assertThat(test.size()).isEqualTo(1);
            assertThat(test.get(0).getName()).isEqualTo("test");
        } catch (IngredientAlreadyInUseException | InvalidIngredientException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    void registerIngredient2(){
        Ingredient ingredient = new Ingredient("test", 1.0, new ArrayList<>());
        assertThrows(IngredientAlreadyInUseException.class, () -> {
            Ingredient ing = ingredientService.registerIngredient(ingredient);
            ingredientService.registerIngredient(ing);
        });
    }

    @Test
    void registerIngredient3(){
        Ingredient ingredient = new Ingredient("test", 1.0, new ArrayList<>());
        Ingredient ingredient1 = new Ingredient("test", 1.0, new ArrayList<>());
        assertThrows(IngredientAlreadyInUseException.class, () -> {
            ingredientService.registerIngredient(ingredient);
            ingredientService.registerIngredient(ingredient1);
        });
    }

    @Test
    void updateIngredient() {
        Ingredient ingredient = new Ingredient("test", 1.0, new ArrayList<>());
        try {
            Ingredient ing = repo.save(ingredient);
            ing.setName("test1");
            ingredientService.updateIngredient(ing, ing.getId());
            assertTrue(repo.existsById(ing.getId()));
            List<Ingredient> test = repo.findAll();
            assertThat(test.size()).isEqualTo(1);
            assertThat(test.get(0).getName()).isEqualTo("test1");
        } catch (IngredientNotFoundException | InvalidIngredientException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void updateIngredientThrowsException(){
        Ingredient ingredient = new Ingredient("test", 1.0, new ArrayList<>());
        assertThrows(IngredientNotFoundException.class, () -> {
            ingredientService.updateIngredient(ingredient, 1);
        });
    }


    @Test
    void deleteIngredient() {
        Ingredient ingredient = new Ingredient("test", 1.0, new ArrayList<>());
        try {
            Ingredient ing = repo.save(ingredient);
            ingredientService.deleteIngredient(ing.getId());
            assertTrue(!repo.existsById(ing.getId()));
            List<Ingredient> test = repo.findAll();
            assertThat(test.size()).isEqualTo(0);
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void deleteIngredientThrowsException(){
        assertThrows(IngredientNotFoundException.class, () -> {
            ingredientService.deleteIngredient(1);
        });
    }

    @Test
    void getDetails() {
        // create 7 ingredients and save them in the database
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Ingredient ingredient = new Ingredient("test" + i, 10 - i, new ArrayList<>());
            ids.add(repo.save(ingredient).getId());
        }
        try {
            Map<Long, Tuple> map = ingredientService.getDetails(ids);
            assertThat(map.size()).isEqualTo(7);
            for (int i = 0; i < 7; i++) {
                assertThat(map.get(ids.get(i)).getPrice()).isEqualTo(10 - i);
                assertThat(map.get(ids.get(i)).getName()).isEqualTo("test" + i);
            }
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void getDetailsThrowsException(){
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertThrows(IngredientNotFoundException.class, () -> {
            ingredientService.getDetails(ids);
        });
    }

    @Test
    void getDetailsThrowsException2(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        repo.save(new Ingredient("test1", 2.0, new ArrayList<>()));
        repo.save(new Ingredient("test2", 3.0, new ArrayList<>()));
        repo.save(new Ingredient("test3", 4.0, new ArrayList<>()));
        assertThrows(IngredientNotFoundException.class, () -> {
            ingredientService.getDetails(List.of(4L, 2L, 7L));
        });
    }

    @Test
    void getDetails2() {
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        repo.save(new Ingredient("test1", 2.0, new ArrayList<>()));
        repo.save(new Ingredient("test2", 3.0, new ArrayList<>()));
        repo.save(new Ingredient("test3", 4.0, new ArrayList<>()));
        repo.save(new Ingredient("test4", 5.0, new ArrayList<>()));
        repo.save(new Ingredient("test5", 6.0, new ArrayList<>()));

        List<Long> ids = List.of(3L, 6L, 4L, 2L);

        try {
            Map<Long, Tuple> map = ingredientService.getDetails(ids);
            assertThat(map.size()).isEqualTo(4);
            assertThat(map.get(3L).getPrice()).isEqualTo(3.0);
            assertThat(map.get(3L).getName()).isEqualTo("test2");
            assertThat(map.get(6L).getPrice()).isEqualTo(6.0);
            assertThat(map.get(6L).getName()).isEqualTo("test5");
            assertThat(map.get(4L).getPrice()).isEqualTo(4.0);
            assertThat(map.get(4L).getName()).isEqualTo("test3");
            assertThat(map.get(2L).getPrice()).isEqualTo(2.0);
            assertThat(map.get(2L).getName()).isEqualTo("test1");
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void getDetails3(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        repo.save(new Ingredient("test1", 2.0, new ArrayList<>()));
        repo.save(new Ingredient("test2", 3.0, new ArrayList<>()));
        repo.save(new Ingredient("test3", 4.0, new ArrayList<>()));

        try {
            Map<Long, Tuple> map = ingredientService.getDetails(List.of());
            assertThat(map.size()).isEqualTo(0);
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testGetDetails() {
        for (int i = 0; i < 7; i++) {
            Ingredient ingredient = new Ingredient("test" + i, 10 - i, new ArrayList<>());
            repo.save(ingredient);
        }
        List<Long> ids = List.of(4L, 2L, 5L, 12L, 7L, 3L);
        assertThrows(IngredientNotFoundException.class, () -> {
            ingredientService.getDetails(ids);
        });
    }

    @Test
    void getToppingsList() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Ingredient ingredient = new Ingredient("test" + i, 10 - i, new ArrayList<>());
            ingredients.add(repo.save(ingredient));
        }
        List<Ingredient> list = ingredientService.getToppingsList();
        assertThat(list.size()).isEqualTo(25);
        for (int i = 0; i < 25; i++) {
            assertThat(list.get(i).getId()).isEqualTo(ingredients.get(i).getId());
            assertThat(list.get(i).getName()).isEqualTo(ingredients.get(i).getName());
            assertThat(list.get(i).getPrice()).isEqualTo(ingredients.get(i).getPrice());
        }

    }

    @Test
    void testGetDetailsWithNullInput(){
        try {
            Map<Long, Tuple> map = ingredientService.getDetails(null);
            assertThat(map).isEqualTo(new HashMap<>());
        } catch (IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void testInputValidationRegister1(){
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.registerIngredient(null);
        });
    }

    @Test
    void testInputValidationRegister2(){
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.registerIngredient(new Ingredient(null, 1.0, new ArrayList<>()));
        });
    }

    @Test
    void testInputValidationRegister3(){
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.registerIngredient(new Ingredient("", 1.0, new ArrayList<>()));
        });
    }

    @Test
    void testInputValidationRegister4(){
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.registerIngredient(new Ingredient("test", -1, new ArrayList<>()));
        });
    }

    @Test
    void registerIngredientInputValidationCorrect() {
        Ingredient ingredient = new Ingredient("test", 0.0, new ArrayList<>());
        try {
            Ingredient ing = ingredientService.registerIngredient(ingredient);
            assertTrue(repo.existsById(ing.getId()));
            List<Ingredient> test = repo.findAll();
            assertThat(test.size()).isEqualTo(1);
            assertThat(test.get(0).getName()).isEqualTo("test");
        } catch (IngredientAlreadyInUseException | InvalidIngredientException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    void testRegisterInputValidationCorrect(){
        Ingredient ingredient = new Ingredient("t", 10, new ArrayList<>());
        try {
            Ingredient ing = ingredientService.registerIngredient(ingredient);
            assertTrue(repo.existsById(ing.getId()));
            List<Ingredient> test = repo.findAll();
            assertThat(test.size()).isEqualTo(1);
            assertThat(test.get(0).getName()).isEqualTo("t");
        } catch (IngredientAlreadyInUseException | InvalidIngredientException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    void testUserInputValidationRegister5(){
        Ingredient ingredient = new Ingredient("test", 0.0, null);
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.registerIngredient(ingredient);
        });
    }

    @Test
    void testUserInputValidationUpdate1(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.updateIngredient(null, 1L);
        });
    }

    @Test
    void testUserInputValidationUpdate2(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.updateIngredient(new Ingredient(null, 1.0, new ArrayList<>()), 1L);
        });
    }

    @Test
    void testUserInputValidationUpdate3(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.updateIngredient(new Ingredient("", 1.0, new ArrayList<>()), 1L);
        });
    }

    @Test
    void testUserInputValidationUpdate4(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.updateIngredient(new Ingredient("test", -1.0, new ArrayList<>()), 1L);
        });
    }

    @Test
    void testUserInputValidationUpdate5(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        assertThrows(InvalidIngredientException.class, () -> {
            ingredientService.updateIngredient(new Ingredient("test", 1.0, null), 1L);
        });
    }

    @Test
    void testUserInputValidationIsCorrectUpdating(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        Ingredient ingredient = new Ingredient("test", 0.0, new ArrayList<>());
        try {
            Ingredient ing = ingredientService.updateIngredient(ingredient, 1L);
            assertTrue(repo.existsById(ing.getId()));
            List<Ingredient> test = repo.findAll();
            assertThat(test.size()).isEqualTo(1);
            assertThat(test.get(0).getName()).isEqualTo("test");
            assertThat(test.get(0).getPrice()).isEqualTo(0.0);
        } catch (InvalidIngredientException | IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    void testUserInputValidationIsCorrectUpdating2(){
        repo.save(new Ingredient("test", 1.0, new ArrayList<>()));
        Ingredient ingredient = new Ingredient("t", 0.0, new ArrayList<>());
        try {
            Ingredient ing = ingredientService.updateIngredient(ingredient, 1L);
            assertTrue(repo.existsById(ing.getId()));
            List<Ingredient> test = repo.findAll();
            assertThat(test.size()).isEqualTo(1);
            assertThat(test.get(0).getName()).isEqualTo("t");
            assertThat(test.get(0).getPrice()).isEqualTo(0.0);
        } catch (InvalidIngredientException | IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    void testUserInputValidationMethod1(){
        assertFalse(ingredientService.validateIngredient(null));
    }

    @Test
    void testUserInputValidationMethod2(){
        assertFalse(ingredientService.validateIngredient(new Ingredient(null, 1.0, new ArrayList<>())));
    }

    @Test
    void testUserInputValidationMethod3(){
        assertFalse(ingredientService.validateIngredient(new Ingredient("", 1.0, new ArrayList<>())));
    }

    @Test
    void testUserInputValidationMethod4(){
        assertFalse(ingredientService.validateIngredient(new Ingredient("test", -1.0, new ArrayList<>())));
    }

    @Test
    void testUserInputValidationMethod5(){
        assertFalse(ingredientService.validateIngredient(new Ingredient("test", 1.0, null)));
    }

    @Test
    void testUserInputValidationMthod6(){
        assertTrue(ingredientService.validateIngredient(new Ingredient("t", 1.0, new ArrayList<>())));
    }

    @Test
    void testUserInputValidationMthod7(){
        assertTrue(ingredientService.validateIngredient(new Ingredient("test", 0.0, new ArrayList<>())));
    }

}
