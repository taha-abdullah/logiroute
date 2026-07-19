package logiroute.logiroute_menu.controller;

import jakarta.validation.Valid;
import logiroute.logiroute_menu.dto.CreateRestaurantRequest;
import logiroute.logiroute_menu.dto.RestaurantResponse;
import logiroute.logiroute_menu.dto.UpdateRestaurantRequest;
import logiroute.logiroute_menu.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return new ResponseEntity<>(restaurantService.createRestaurant(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> getAllRestaurants(Pageable pageable) {
        return ResponseEntity.ok(restaurantService.getAllRestaurants(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.getRestaurant(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable UUID id, @Valid @RequestBody UpdateRestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable UUID id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
