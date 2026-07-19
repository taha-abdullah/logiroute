package logiroute.logiroute_menu.service;

import logiroute.logiroute_menu.domain.Restaurant;
import logiroute.logiroute_menu.dto.CreateRestaurantRequest;
import logiroute.logiroute_menu.dto.RestaurantResponse;
import logiroute.logiroute_menu.dto.UpdateRestaurantRequest;
import logiroute.logiroute_menu.exception.ResourceNotFoundException;
import logiroute.logiroute_menu.mapper.RestaurantMapper;
import logiroute.logiroute_menu.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Override
    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponse(restaurant);
    }

    @Override
    public RestaurantResponse getRestaurant(UUID id) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        return restaurantMapper.toResponse(restaurant);
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(UUID id, UpdateRestaurantRequest request) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        restaurantMapper.updateEntity(restaurant, request);
        restaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponse(restaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(UUID id) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        restaurantRepository.delete(restaurant);
    }

    @Override
    public Page<RestaurantResponse> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable)
                .map(restaurantMapper::toResponse);
    }

    private Restaurant findRestaurantOrThrow(UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }
}
