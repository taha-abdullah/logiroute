package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.MenuCategory;
import logiroute.logiroute_menu.domain.MenuItem;
import logiroute.logiroute_menu.domain.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MenuItemRepositoryTest {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    private MenuCategory testCategory;

    @BeforeEach
    void setUp() {
        Restaurant restaurant = Restaurant.builder()
                .name("Burger Stand")
                .latitude(new BigDecimal("34.052200"))
                .longitude(new BigDecimal("-118.243700"))
                .build();
        Restaurant savedRestaurant = restaurantRepository.saveAndFlush(restaurant);

        MenuCategory category = MenuCategory.builder()
                .restaurant(savedRestaurant)
                .name("Mains")
                .build();
        testCategory = menuCategoryRepository.saveAndFlush(category);
    }

    @Test
    void shouldSaveAndRetrieveMenuItem() {
        // Arrange
        MenuItem item = MenuItem.builder()
                .category(testCategory)
                .name("Cheeseburger")
                .description("A delicious cheeseburger")
                .basePriceCents(899)
                .isVegan(false)
                .isGlutenFree(false)
                .build();

        // Act
        MenuItem saved = menuItemRepository.saveAndFlush(item);

        // Assert
        assertThat(saved.getId()).isNotNull();
        
        Optional<MenuItem> found = menuItemRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Cheeseburger");
        assertThat(found.get().getBasePriceCents()).isEqualTo(899);
        assertThat(found.get().getCategory().getId()).isEqualTo(testCategory.getId());
    }

    @Test
    void shouldFindItemsByCategoryIdAndRespectSoftDelete() {
        // Arrange
        MenuItem item1 = MenuItem.builder().category(testCategory).name("Burger").basePriceCents(799).build();
        MenuItem item2 = MenuItem.builder().category(testCategory).name("Fries").basePriceCents(399).isVegan(true).build();
        
        MenuItem savedItem1 = menuItemRepository.save(item1);
        MenuItem savedItem2 = menuItemRepository.save(item2);
        menuItemRepository.flush();

        // Act - Verify both are found
        List<MenuItem> items = menuItemRepository.findByCategoryId(testCategory.getId());
        assertThat(items).hasSize(2);
        assertThat(items).extracting(MenuItem::getName).containsExactlyInAnyOrder("Burger", "Fries");

        // Act - Soft delete the first item
        menuItemRepository.delete(savedItem1);
        menuItemRepository.flush();
        entityManager.clear();

        // Assert - Only the second item should be found
        List<MenuItem> remainingItems = menuItemRepository.findByCategoryId(testCategory.getId());
        assertThat(remainingItems).hasSize(1);
        assertThat(remainingItems.get(0).getName()).isEqualTo("Fries");

        // Verify the deleted item still exists in the DB physically
        Long dbCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM menu_items WHERE id = ? AND deleted_at IS NOT NULL",
                Long.class,
                savedItem1.getId()
        );
        assertThat(dbCount).isEqualTo(1L);
    }
}
