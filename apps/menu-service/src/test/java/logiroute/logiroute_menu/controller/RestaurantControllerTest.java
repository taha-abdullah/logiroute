package logiroute.logiroute_menu.controller;

import tools.jackson.databind.ObjectMapper;
import logiroute.logiroute_menu.dto.CreateRestaurantRequest;
import logiroute.logiroute_menu.dto.RestaurantResponse;
import logiroute.logiroute_menu.dto.UpdateRestaurantRequest;
import logiroute.logiroute_menu.exception.ResourceNotFoundException;
import logiroute.logiroute_menu.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    private final UUID restaurantId = UUID.randomUUID();
    private final RestaurantResponse response = new RestaurantResponse(
            restaurantId, "My Restaurant", "Desc", List.of("Pizza"), true, BigDecimal.valueOf(40.7), BigDecimal.valueOf(-74.0), Instant.now(), Instant.now()
    );

    @Test
    void createRestaurant_Success() throws Exception {
        CreateRestaurantRequest request = new CreateRestaurantRequest("My Restaurant", "Desc", List.of("Pizza"), true, BigDecimal.valueOf(40.7), BigDecimal.valueOf(-74.0));
        when(restaurantService.createRestaurant(any(CreateRestaurantRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                .andExpect(jsonPath("$.name").value("My Restaurant"));
    }

    @Test
    void createRestaurant_ValidationFailure() throws Exception {
        // Missing name
        CreateRestaurantRequest request = new CreateRestaurantRequest("", "Desc", List.of("Pizza"), true, BigDecimal.valueOf(40.7), BigDecimal.valueOf(-74.0));

        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void getRestaurant_Success() throws Exception {
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(response);

        mockMvc.perform(get("/api/restaurants/{id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId.toString()));
    }

    @Test
    void getRestaurant_NotFound() throws Exception {
        when(restaurantService.getRestaurant(restaurantId)).thenThrow(new ResourceNotFoundException("Restaurant not found"));

        mockMvc.perform(get("/api/restaurants/{id}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Restaurant not found"));
    }

    @Test
    void getAllRestaurants_Success() throws Exception {
        Page<RestaurantResponse> page = new PageImpl<>(List.of(response));
        when(restaurantService.getAllRestaurants(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/restaurants?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(restaurantId.toString()));
    }

    @Test
    void updateRestaurant_Success() throws Exception {
        UpdateRestaurantRequest request = new UpdateRestaurantRequest("Updated Name", null, null, true);
        RestaurantResponse updatedResponse = new RestaurantResponse(
                restaurantId, "Updated Name", "Desc", List.of("Pizza"), true, BigDecimal.valueOf(40.7), BigDecimal.valueOf(-74.0), Instant.now(), Instant.now()
        );

        when(restaurantService.updateRestaurant(eq(restaurantId), any(UpdateRestaurantRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/restaurants/{id}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteRestaurant_Success() throws Exception {
        doNothing().when(restaurantService).deleteRestaurant(restaurantId);

        mockMvc.perform(delete("/api/restaurants/{id}", restaurantId))
                .andExpect(status().isNoContent());
        
        verify(restaurantService).deleteRestaurant(restaurantId);
    }
}
