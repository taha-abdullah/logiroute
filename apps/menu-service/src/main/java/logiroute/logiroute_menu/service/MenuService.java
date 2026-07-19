package logiroute.logiroute_menu.service;

import logiroute.logiroute_menu.dto.*;
import java.util.List;
import java.util.UUID;

public interface MenuService {
    // Categories
    CategoryResponse createCategory(UUID restaurantId, CreateCategoryRequest request);
    CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest request);
    void deleteCategory(UUID categoryId);
    List<CategoryResponse> getCategoriesByRestaurant(UUID restaurantId);

    // Menu Items
    MenuItemResponse createMenuItem(UUID categoryId, CreateMenuItemRequest request);
    MenuItemResponse updateMenuItem(UUID itemId, UpdateMenuItemRequest request);
    void deleteMenuItem(UUID itemId);
    List<MenuItemResponse> getMenuItemsByCategory(UUID categoryId);

    // Option Groups
    OptionGroupResponse createOptionGroup(UUID itemId, CreateOptionGroupRequest request);
    OptionGroupResponse updateOptionGroup(UUID groupId, UpdateOptionGroupRequest request);
    void deleteOptionGroup(UUID groupId);
    List<OptionGroupResponse> getOptionGroupsByItem(UUID itemId);

    // Item Options
    ItemOptionResponse createItemOption(UUID groupId, CreateItemOptionRequest request);
    ItemOptionResponse updateItemOption(UUID optionId, UpdateItemOptionRequest request);
    void deleteItemOption(UUID optionId);
    List<ItemOptionResponse> getItemOptionsByGroup(UUID groupId);
}
