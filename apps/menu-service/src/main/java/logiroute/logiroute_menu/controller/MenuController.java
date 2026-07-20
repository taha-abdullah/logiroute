package logiroute.logiroute_menu.controller;

import jakarta.validation.Valid;
import logiroute.logiroute_menu.dto.*;
import logiroute.logiroute_menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // --- Categories ---

    @PostMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<CategoryResponse> createCategory(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateCategoryRequest request) {
        return new ResponseEntity<>(menuService.createCategory(restaurantId, request), HttpStatus.CREATED);
    }

    @GetMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(menuService.getCategoriesByRestaurant(restaurantId));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(menuService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        menuService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    // --- Menu Items ---

    @PostMapping("/categories/{categoryId}/items")
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CreateMenuItemRequest request) {
        return new ResponseEntity<>(menuService.createMenuItem(categoryId, request), HttpStatus.CREATED);
    }

    @GetMapping("/categories/{categoryId}/items")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(menuService.getMenuItemsByCategory(categoryId));
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable UUID itemId) {
        return ResponseEntity.ok(menuService.getMenuItem(itemId));
    }

    @GetMapping("/items")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByIds(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(menuService.getMenuItemsByIds(ids));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(menuService.updateMenuItem(itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID itemId) {
        menuService.deleteMenuItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // --- Option Groups ---

    @PostMapping("/items/{itemId}/option-groups")
    public ResponseEntity<OptionGroupResponse> createOptionGroup(
            @PathVariable UUID itemId,
            @Valid @RequestBody CreateOptionGroupRequest request) {
        return new ResponseEntity<>(menuService.createOptionGroup(itemId, request), HttpStatus.CREATED);
    }

    @GetMapping("/items/{itemId}/option-groups")
    public ResponseEntity<List<OptionGroupResponse>> getOptionGroupsByItem(@PathVariable UUID itemId) {
        return ResponseEntity.ok(menuService.getOptionGroupsByItem(itemId));
    }

    @PutMapping("/option-groups/{groupId}")
    public ResponseEntity<OptionGroupResponse> updateOptionGroup(
            @PathVariable UUID groupId,
            @Valid @RequestBody UpdateOptionGroupRequest request) {
        return ResponseEntity.ok(menuService.updateOptionGroup(groupId, request));
    }

    @DeleteMapping("/option-groups/{groupId}")
    public ResponseEntity<Void> deleteOptionGroup(@PathVariable UUID groupId) {
        menuService.deleteOptionGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    // --- Item Options ---

    @PostMapping("/option-groups/{groupId}/options")
    public ResponseEntity<ItemOptionResponse> createItemOption(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateItemOptionRequest request) {
        return new ResponseEntity<>(menuService.createItemOption(groupId, request), HttpStatus.CREATED);
    }

    @GetMapping("/option-groups/{groupId}/options")
    public ResponseEntity<List<ItemOptionResponse>> getItemOptionsByGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(menuService.getItemOptionsByGroup(groupId));
    }

    @PutMapping("/options/{optionId}")
    public ResponseEntity<ItemOptionResponse> updateItemOption(
            @PathVariable UUID optionId,
            @Valid @RequestBody UpdateItemOptionRequest request) {
        return ResponseEntity.ok(menuService.updateItemOption(optionId, request));
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> deleteItemOption(@PathVariable UUID optionId) {
        menuService.deleteItemOption(optionId);
        return ResponseEntity.noContent().build();
    }
}
