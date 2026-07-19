package logiroute.logiroute_menu.controller;

import tools.jackson.databind.ObjectMapper;
import logiroute.logiroute_menu.dto.*;
import logiroute.logiroute_menu.service.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuService menuService;

    private final UUID restaurantId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID itemId = UUID.randomUUID();
    
    // --- Category Tests ---

    @Test
    void createCategory_Success() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Starters", 1);
        CategoryResponse response = new CategoryResponse(categoryId, restaurantId, "Starters", 1, Instant.now(), Instant.now());
        
        when(menuService.createCategory(eq(restaurantId), any(CreateCategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/{id}/categories", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Starters"));
    }

    @Test
    void getCategoriesByRestaurant_Success() throws Exception {
        CategoryResponse response = new CategoryResponse(categoryId, restaurantId, "Starters", 1, Instant.now(), Instant.now());
        when(menuService.getCategoriesByRestaurant(restaurantId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/restaurants/{id}/categories", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(categoryId.toString()));
    }
    
    @Test
    void updateCategory_Success() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Mains", 2);
        CategoryResponse response = new CategoryResponse(categoryId, restaurantId, "Mains", 1, Instant.now(), Instant.now());
        
        when(menuService.updateCategory(eq(categoryId), any(UpdateCategoryRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mains"));
    }

    @Test
    void deleteCategory_Success() throws Exception {
        doNothing().when(menuService).deleteCategory(categoryId);

        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
        verify(menuService).deleteCategory(categoryId);
    }
}
