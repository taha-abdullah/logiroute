package logiroute.logiroute_menu.service;

import logiroute.logiroute_menu.domain.Restaurant;
import logiroute.logiroute_menu.dto.CreateRestaurantRequest;
import logiroute.logiroute_menu.dto.RestaurantResponse;
import logiroute.logiroute_menu.dto.UpdateRestaurantRequest;
import logiroute.logiroute_menu.exception.ResourceNotFoundException;
import logiroute.logiroute_menu.mapper.RestaurantMapper;
import logiroute.logiroute_menu.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant restaurant;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Test Restaurant")
                .isOpen(true)
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createRestaurant_Success() {
        CreateRestaurantRequest request = new CreateRestaurantRequest(
                "Test Restaurant", "Desc", List.of(), true, BigDecimal.ZERO, BigDecimal.ZERO);
        RestaurantResponse expectedResponse = new RestaurantResponse(
                restaurantId, "Test Restaurant", "Desc", List.of(), true, BigDecimal.ZERO, BigDecimal.ZERO, Instant.now(), Instant.now());

        when(restaurantMapper.toEntity(request)).thenReturn(restaurant);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        when(restaurantMapper.toResponse(restaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.createRestaurant(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(restaurantId);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void getRestaurant_Success() {
        RestaurantResponse expectedResponse = new RestaurantResponse(
                restaurantId, "Test Restaurant", null, List.of(), true, BigDecimal.ZERO, BigDecimal.ZERO, Instant.now(), Instant.now());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toResponse(restaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.getRestaurant(restaurantId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(restaurantId);
    }

    @Test
    void getRestaurant_NotFound_ThrowsException() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getRestaurant(restaurantId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Restaurant not found with id");
    }

    @Test
    void updateRestaurant_Success() {
        UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                "Updated Name", "Updated Desc", List.of(), false);
        RestaurantResponse expectedResponse = new RestaurantResponse(
                restaurantId, "Updated Name", "Updated Desc", List.of(), false, BigDecimal.ZERO, BigDecimal.ZERO, Instant.now(), Instant.now());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantMapper).updateEntity(restaurant, request);
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        when(restaurantMapper.toResponse(restaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.updateRestaurant(restaurantId, request);

        assertThat(result).isNotNull();
        verify(restaurantMapper).updateEntity(restaurant, request);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void deleteRestaurant_Success() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        restaurantService.deleteRestaurant(restaurantId);

        verify(restaurantRepository).delete(restaurant);
    }

    @Test
    void deleteRestaurant_NotFound_ThrowsException() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.deleteRestaurant(restaurantId))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(restaurantRepository, never()).delete(any());
    }

    @Test
    void getAllRestaurants_Success() {
        RestaurantResponse expectedResponse = new RestaurantResponse(
                restaurantId, "Test Restaurant", null, List.of(), true, BigDecimal.ZERO, BigDecimal.ZERO, Instant.now(), Instant.now());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> page = new PageImpl<>(List.of(restaurant));

        when(restaurantRepository.findAll(pageable)).thenReturn(page);
        when(restaurantMapper.toResponse(restaurant)).thenReturn(expectedResponse);

        Page<RestaurantResponse> results = restaurantService.getAllRestaurants(pageable);

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).id()).isEqualTo(restaurantId);
    }
}
