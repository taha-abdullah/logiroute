package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.MenuCategory;
import logiroute.logiroute_menu.domain.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MenuCategoryRepositoryTest {

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        // We must have a saved Restaurant to link our categories to
        Restaurant restaurant = Restaurant.builder()
                .name("Taco Stand")
                .latitude(new BigDecimal("34.052200"))
                .longitude(new BigDecimal("-118.243700"))
                .build();
        testRestaurant = restaurantRepository.saveAndFlush(restaurant);
    }

    @Test
    void shouldSaveAndRetrieveMenuCategory() {
        // Arrange
        MenuCategory category = MenuCategory.builder()
                .restaurant(testRestaurant)
                .name("Tacos")
                .sortOrder(1)
                .build();

        // Act
        MenuCategory saved = menuCategoryRepository.saveAndFlush(category);

        // Assert
        assertThat(saved.getId()).isNotNull();
        
        Optional<MenuCategory> found = menuCategoryRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Tacos");
        assertThat(found.get().getRestaurant().getId()).isEqualTo(testRestaurant.getId());
    }

    @Test
    void shouldFindCategoriesByRestaurantIdAndRespectSoftDelete() {
        // Arrange
        MenuCategory cat1 = MenuCategory.builder().restaurant(testRestaurant).name("Tacos").sortOrder(1).build();
        MenuCategory cat2 = MenuCategory.builder().restaurant(testRestaurant).name("Drinks").sortOrder(2).build();
        
        MenuCategory savedCat1 = menuCategoryRepository.save(cat1);
        MenuCategory savedCat2 = menuCategoryRepository.save(cat2);
        menuCategoryRepository.flush();

        // Act - Verify both are found
        List<MenuCategory> categories = menuCategoryRepository.findByRestaurantIdOrderBySortOrderAsc(testRestaurant.getId());
        assertThat(categories).hasSize(2);
        assertThat(categories.get(0).getName()).isEqualTo("Tacos");

        // Act - Soft delete the first category
        menuCategoryRepository.delete(savedCat1);
        menuCategoryRepository.flush();

        // Assert - Only the second category should be found
        List<MenuCategory> remainingCategories = menuCategoryRepository.findByRestaurantIdOrderBySortOrderAsc(testRestaurant.getId());
        assertThat(remainingCategories).hasSize(1);
        assertThat(remainingCategories.get(0).getName()).isEqualTo("Drinks");

        // Verify the deleted category still exists in the DB physically
        Integer dbCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM menu_categories WHERE id = ?",
                Integer.class,
                savedCat1.getId()
        );
        assertThat(dbCount).isEqualTo(1);
    }
}
