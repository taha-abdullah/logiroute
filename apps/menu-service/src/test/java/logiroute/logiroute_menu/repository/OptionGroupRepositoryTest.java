package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.MenuCategory;
import logiroute.logiroute_menu.domain.MenuItem;
import logiroute.logiroute_menu.domain.OptionGroup;
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
class OptionGroupRepositoryTest {

    @Autowired
    private OptionGroupRepository optionGroupRepository;

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

    private MenuItem testMenuItem;

    @BeforeEach
    void setUp() {
        Restaurant restaurant = Restaurant.builder()
                .name("Salad Bar")
                .latitude(new BigDecimal("34.052200"))
                .longitude(new BigDecimal("-118.243700"))
                .build();
        Restaurant savedRestaurant = restaurantRepository.saveAndFlush(restaurant);

        MenuCategory category = MenuCategory.builder()
                .restaurant(savedRestaurant)
                .name("Salads")
                .build();
        MenuCategory savedCategory = menuCategoryRepository.saveAndFlush(category);

        MenuItem item = MenuItem.builder()
                .category(savedCategory)
                .name("Custom Salad")
                .basePriceCents(500)
                .build();
        testMenuItem = menuItemRepository.saveAndFlush(item);
    }

    @Test
    void shouldSaveAndRetrieveOptionGroup() {
        // Arrange
        OptionGroup group = OptionGroup.builder()
                .menuItem(testMenuItem)
                .name("Choose Dressings")
                .minSelectable(1)
                .maxSelectable(3)
                .build();

        // Act
        OptionGroup saved = optionGroupRepository.saveAndFlush(group);

        // Assert
        assertThat(saved.getId()).isNotNull();
        
        Optional<OptionGroup> found = optionGroupRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Choose Dressings");
        assertThat(found.get().getMinSelectable()).isEqualTo(1);
        assertThat(found.get().getMaxSelectable()).isEqualTo(3);
        assertThat(found.get().getMenuItem().getId()).isEqualTo(testMenuItem.getId());
    }

    @Test
    void shouldFindGroupsByMenuItemIdAndRespectSoftDelete() {
        // Arrange
        OptionGroup group1 = OptionGroup.builder()
                .menuItem(testMenuItem)
                .name("Proteins")
                .minSelectable(1)
                .maxSelectable(1)
                .build();
        OptionGroup group2 = OptionGroup.builder()
                .menuItem(testMenuItem)
                .name("Veggies")
                .minSelectable(0)
                .maxSelectable(5)
                .build();
        
        OptionGroup savedGroup1 = optionGroupRepository.save(group1);
        OptionGroup savedGroup2 = optionGroupRepository.save(group2);
        optionGroupRepository.flush();

        // Act - Verify both are found
        List<OptionGroup> groups = optionGroupRepository.findByMenuItemId(testMenuItem.getId());
        assertThat(groups).hasSize(2);
        assertThat(groups).extracting(OptionGroup::getName).containsExactlyInAnyOrder("Proteins", "Veggies");

        // Act - Soft delete the first group
        optionGroupRepository.delete(savedGroup1);
        optionGroupRepository.flush();
        entityManager.clear();

        // Assert - Only the second group should be found
        List<OptionGroup> remainingGroups = optionGroupRepository.findByMenuItemId(testMenuItem.getId());
        assertThat(remainingGroups).hasSize(1);
        assertThat(remainingGroups.get(0).getName()).isEqualTo("Veggies");

        // Verify the deleted group still exists in the DB physically
        Long dbCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM option_groups WHERE id = ? AND deleted_at IS NOT NULL",
                Long.class,
                savedGroup1.getId()
        );
        assertThat(dbCount).isEqualTo(1L);
    }
}
