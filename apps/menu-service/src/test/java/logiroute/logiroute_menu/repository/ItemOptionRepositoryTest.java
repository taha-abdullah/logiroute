package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.*;
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
class ItemOptionRepositoryTest {

    @Autowired
    private ItemOptionRepository itemOptionRepository;

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

    private OptionGroup testOptionGroup;

    @BeforeEach
    void setUp() {
        Restaurant restaurant = Restaurant.builder()
                .name("Pizza Palace")
                .latitude(new BigDecimal("40.712800"))
                .longitude(new BigDecimal("-74.006000"))
                .build();
        Restaurant savedRestaurant = restaurantRepository.saveAndFlush(restaurant);

        MenuCategory category = MenuCategory.builder()
                .restaurant(savedRestaurant)
                .name("Pizzas")
                .build();
        MenuCategory savedCategory = menuCategoryRepository.saveAndFlush(category);

        MenuItem item = MenuItem.builder()
                .category(savedCategory)
                .name("Cheese Pizza")
                .basePriceCents(1000)
                .build();
        MenuItem savedItem = menuItemRepository.saveAndFlush(item);

        OptionGroup group = OptionGroup.builder()
                .menuItem(savedItem)
                .name("Add Toppings")
                .minSelectable(0)
                .maxSelectable(5)
                .build();
        testOptionGroup = optionGroupRepository.saveAndFlush(group);
    }

    @Test
    void shouldSaveAndRetrieveItemOption() {
        // Arrange
        ItemOption option = ItemOption.builder()
                .optionGroup(testOptionGroup)
                .name("Pepperoni")
                .priceCents(150)
                .build();

        // Act
        ItemOption saved = itemOptionRepository.saveAndFlush(option);

        // Assert
        assertThat(saved.getId()).isNotNull();
        
        Optional<ItemOption> found = itemOptionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Pepperoni");
        assertThat(found.get().getPriceCents()).isEqualTo(150);
        assertThat(found.get().getOptionGroup().getId()).isEqualTo(testOptionGroup.getId());
    }

    @Test
    void shouldFindOptionsByOptionGroupIdAndRespectSoftDelete() {
        // Arrange
        ItemOption option1 = ItemOption.builder()
                .optionGroup(testOptionGroup)
                .name("Pepperoni")
                .priceCents(150)
                .build();
        ItemOption option2 = ItemOption.builder()
                .optionGroup(testOptionGroup)
                .name("Mushrooms")
                .priceCents(100)
                .build();
        
        ItemOption savedOption1 = itemOptionRepository.save(option1);
        ItemOption savedOption2 = itemOptionRepository.save(option2);
        itemOptionRepository.flush();

        // Act - Verify both are found
        List<ItemOption> options = itemOptionRepository.findByOptionGroupId(testOptionGroup.getId());
        assertThat(options).hasSize(2);
        assertThat(options).extracting(ItemOption::getName).containsExactlyInAnyOrder("Pepperoni", "Mushrooms");

        // Act - Soft delete the first option
        itemOptionRepository.delete(savedOption1);
        itemOptionRepository.flush();

        // Assert - Only the second option should be found
        List<ItemOption> remainingOptions = itemOptionRepository.findByOptionGroupId(testOptionGroup.getId());
        assertThat(remainingOptions).hasSize(1);
        assertThat(remainingOptions.get(0).getName()).isEqualTo("Mushrooms");

        // Verify the deleted option still exists in the DB physically
        Integer dbCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM item_options WHERE id = ?",
                Integer.class,
                savedOption1.getId()
        );
        assertThat(dbCount).isEqualTo(1);
    }
}
