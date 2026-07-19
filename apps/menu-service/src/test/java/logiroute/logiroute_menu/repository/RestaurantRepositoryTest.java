package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveRestaurant() {
        // Arrange
        Restaurant restaurant = Restaurant.builder()
                .name("Pizza Palace")
                .description("Best pizza in town")
                .cuisineTags(List.of("Italian", "Pizza"))
                .latitude(new BigDecimal("40.712800"))
                .longitude(new BigDecimal("-74.006000"))
                .isOpen(true)
                .build();

        // Act
        Restaurant saved = restaurantRepository.saveAndFlush(restaurant);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        
        Optional<Restaurant> found = restaurantRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Pizza Palace");
        assertThat(found.get().getCuisineTags()).containsExactly("Italian", "Pizza");
    }

    @Test
    void shouldSoftDeleteRestaurant() {
        // Arrange
        Restaurant restaurant = Restaurant.builder()
                .name("Burger Joint")
                .latitude(new BigDecimal("34.052200"))
                .longitude(new BigDecimal("-118.243700"))
                .build();
        Restaurant saved = restaurantRepository.saveAndFlush(restaurant);

        // Act
        restaurantRepository.delete(saved);
        restaurantRepository.flush(); // Force the delete query execution
        entityManager.clear(); // Clear persistence context to hit DB

        // Assert
        // 1. Shouldn't be found via repository because of @SQLRestriction
        Optional<Restaurant> found = restaurantRepository.findById(saved.getId());
        assertThat(found).isEmpty();

        // 2. Should still exist in the database with deleted_at set
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM restaurants WHERE id = ? AND deleted_at IS NOT NULL",
                Long.class,
                saved.getId()
        );
        assertThat(count).isEqualTo(1L);
    }
}
