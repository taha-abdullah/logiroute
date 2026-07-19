package logiroute.logiroute_menu.service;

import logiroute.logiroute_menu.dto.CreateRestaurantRequest;
import logiroute.logiroute_menu.dto.RestaurantResponse;
import logiroute.logiroute_menu.dto.UpdateRestaurantRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RestaurantService {
    RestaurantResponse createRestaurant(CreateRestaurantRequest request);
    RestaurantResponse getRestaurant(UUID id);
    RestaurantResponse updateRestaurant(UUID id, UpdateRestaurantRequest request);
    void deleteRestaurant(UUID id);
    Page<RestaurantResponse> getAllRestaurants(Pageable pageable);
}
