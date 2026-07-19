package logiroute.logiroute_menu.service;

import logiroute.logiroute_menu.domain.*;
import logiroute.logiroute_menu.dto.*;
import logiroute.logiroute_menu.exception.ResourceNotFoundException;
import logiroute.logiroute_menu.mapper.MenuMapper;
import logiroute.logiroute_menu.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private MenuCategoryRepository categoryRepository;
    @Mock
    private MenuItemRepository itemRepository;
    @Mock
    private OptionGroupRepository groupRepository;
    @Mock
    private ItemOptionRepository optionRepository;
    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuServiceImpl menuService;

    private UUID restaurantId, categoryId, itemId, groupId, optionId;
    private Restaurant restaurant;
    private MenuCategory category;
    private MenuItem item;
    private OptionGroup group;
    private ItemOption option;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        optionId = UUID.randomUUID();

        restaurant = Restaurant.builder().id(restaurantId).build();
        category = MenuCategory.builder().id(categoryId).restaurant(restaurant).name("Cat").build();
        item = MenuItem.builder().id(itemId).category(category).name("Item").build();
        group = OptionGroup.builder().id(groupId).menuItem(item).name("Group").build();
        option = ItemOption.builder().id(optionId).optionGroup(group).name("Opt").build();
    }

    // --- Category Tests ---
    @Test
    void createCategory_Success() {
        CreateCategoryRequest request = new CreateCategoryRequest("New Cat", 1);
        CategoryResponse response = new CategoryResponse(categoryId, restaurantId, "New Cat", 1, Instant.now(), Instant.now());

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(any(MenuCategory.class))).thenReturn(category);
        when(menuMapper.toCategoryResponse(category)).thenReturn(response);

        CategoryResponse result = menuService.createCategory(restaurantId, request);
        
        assertThat(result).isNotNull();
        verify(categoryRepository).save(category);
        assertThat(category.getRestaurant()).isEqualTo(restaurant);
    }

    @Test
    void createCategory_RestaurantNotFound() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());
        CreateCategoryRequest request = new CreateCategoryRequest("New Cat", 1);
        
        assertThatThrownBy(() -> menuService.createCategory(restaurantId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void updateCategory_Success() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Updated Cat", 2);
        CategoryResponse response = new CategoryResponse(categoryId, restaurantId, "Updated Cat", 2, Instant.now(), Instant.now());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(menuMapper).updateEntity(category, request);
        when(categoryRepository.save(category)).thenReturn(category);
        when(menuMapper.toCategoryResponse(category)).thenReturn(response);

        CategoryResponse result = menuService.updateCategory(categoryId, request);
        
        assertThat(result).isNotNull();
        verify(menuMapper).updateEntity(category, request);
        verify(categoryRepository).save(category);
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        menuService.deleteCategory(categoryId);
        verify(categoryRepository).delete(category);
    }

    @Test
    void getCategoriesByRestaurant_Success() {
        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        when(categoryRepository.findByRestaurantIdOrderBySortOrderAsc(restaurantId)).thenReturn(List.of(category));
        when(menuMapper.toCategoryResponse(category)).thenReturn(new CategoryResponse(categoryId, restaurantId, "Cat", 1, Instant.now(), Instant.now()));

        List<CategoryResponse> results = menuService.getCategoriesByRestaurant(restaurantId);
        assertThat(results).hasSize(1);
    }

    // --- Menu Item Tests ---
    @Test
    void createMenuItem_Success() {
        CreateMenuItemRequest request = new CreateMenuItemRequest("Item", "Desc", 100, false, false, true);
        MenuItemResponse response = new MenuItemResponse(itemId, categoryId, "Item", "Desc", 100, false, false, true, Instant.now(), Instant.now());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(menuMapper.toEntity(request)).thenReturn(item);
        when(itemRepository.save(any(MenuItem.class))).thenReturn(item);
        when(menuMapper.toItemResponse(item)).thenReturn(response);

        MenuItemResponse result = menuService.createMenuItem(categoryId, request);
        
        assertThat(result).isNotNull();
        verify(itemRepository).save(item);
        assertThat(item.getCategory()).isEqualTo(category);
    }

    @Test
    void updateMenuItem_Success() {
        UpdateMenuItemRequest request = new UpdateMenuItemRequest("Updated Item", null, 150, false, false, true);
        MenuItemResponse response = new MenuItemResponse(itemId, categoryId, "Updated Item", "Desc", 150, false, false, true, Instant.now(), Instant.now());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(menuMapper).updateEntity(item, request);
        when(itemRepository.save(item)).thenReturn(item);
        when(menuMapper.toItemResponse(item)).thenReturn(response);

        MenuItemResponse result = menuService.updateMenuItem(itemId, request);
        
        assertThat(result).isNotNull();
        verify(menuMapper).updateEntity(item, request);
        verify(itemRepository).save(item);
    }

    @Test
    void deleteMenuItem_Success() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        menuService.deleteMenuItem(itemId);
        verify(itemRepository).delete(item);
    }

    @Test
    void getMenuItemsByCategory_Success() {
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(itemRepository.findByCategoryId(categoryId)).thenReturn(List.of(item));
        when(menuMapper.toItemResponse(item)).thenReturn(new MenuItemResponse(itemId, categoryId, "Item", "Desc", 100, false, false, true, Instant.now(), Instant.now()));

        List<MenuItemResponse> results = menuService.getMenuItemsByCategory(categoryId);
        assertThat(results).hasSize(1);
    }

    // --- Option Group Tests ---
    @Test
    void createOptionGroup_Success() {
        CreateOptionGroupRequest request = new CreateOptionGroupRequest("Group", 0, 1);
        OptionGroupResponse response = new OptionGroupResponse(groupId, itemId, "Group", 0, 1, Instant.now(), Instant.now());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(menuMapper.toEntity(request)).thenReturn(group);
        when(groupRepository.save(any(OptionGroup.class))).thenReturn(group);
        when(menuMapper.toOptionGroupResponse(group)).thenReturn(response);

        OptionGroupResponse result = menuService.createOptionGroup(itemId, request);
        
        assertThat(result).isNotNull();
        verify(groupRepository).save(group);
        assertThat(group.getMenuItem()).isEqualTo(item);
    }

    @Test
    void updateOptionGroup_Success() {
        UpdateOptionGroupRequest request = new UpdateOptionGroupRequest("Updated Group", 0, 2);
        OptionGroupResponse response = new OptionGroupResponse(groupId, itemId, "Updated Group", 0, 2, Instant.now(), Instant.now());

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        doNothing().when(menuMapper).updateEntity(group, request);
        when(groupRepository.save(group)).thenReturn(group);
        when(menuMapper.toOptionGroupResponse(group)).thenReturn(response);

        OptionGroupResponse result = menuService.updateOptionGroup(groupId, request);
        
        assertThat(result).isNotNull();
        verify(menuMapper).updateEntity(group, request);
        verify(groupRepository).save(group);
    }

    @Test
    void deleteOptionGroup_Success() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        
        menuService.deleteOptionGroup(groupId);
        
        verify(groupRepository).delete(group);
    }

    @Test
    void deleteOptionGroup_NotFound() {
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> menuService.deleteOptionGroup(groupId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getOptionGroupsByItem_Success() {
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(groupRepository.findByMenuItemId(itemId)).thenReturn(List.of(group));
        when(menuMapper.toOptionGroupResponse(group)).thenReturn(new OptionGroupResponse(groupId, itemId, "Group", 0, 1, Instant.now(), Instant.now()));

        List<OptionGroupResponse> results = menuService.getOptionGroupsByItem(itemId);
        assertThat(results).hasSize(1);
    }

    // --- Item Option Tests ---
    @Test
    void createItemOption_Success() {
        CreateItemOptionRequest request = new CreateItemOptionRequest("Opt", 50, true);
        ItemOptionResponse response = new ItemOptionResponse(optionId, groupId, "Opt", 50, true, Instant.now(), Instant.now());

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(menuMapper.toEntity(request)).thenReturn(option);
        when(optionRepository.save(any(ItemOption.class))).thenReturn(option);
        when(menuMapper.toItemOptionResponse(option)).thenReturn(response);

        ItemOptionResponse result = menuService.createItemOption(groupId, request);
        
        assertThat(result).isNotNull();
        verify(optionRepository).save(option);
        assertThat(option.getOptionGroup()).isEqualTo(group);
    }

    @Test
    void updateItemOption_Success() {
        UpdateItemOptionRequest request = new UpdateItemOptionRequest("Updated Opt", 60, false);
        ItemOptionResponse response = new ItemOptionResponse(optionId, groupId, "Updated Opt", 60, false, Instant.now(), Instant.now());

        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        doNothing().when(menuMapper).updateEntity(option, request);
        when(optionRepository.save(option)).thenReturn(option);
        when(menuMapper.toItemOptionResponse(option)).thenReturn(response);

        ItemOptionResponse result = menuService.updateItemOption(optionId, request);
        
        assertThat(result).isNotNull();
        verify(menuMapper).updateEntity(option, request);
        verify(optionRepository).save(option);
    }

    @Test
    void deleteItemOption_Success() {
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        menuService.deleteItemOption(optionId);
        verify(optionRepository).delete(option);
    }

    @Test
    void getItemOptionsByGroup_Success() {
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(optionRepository.findByOptionGroupId(groupId)).thenReturn(List.of(option));
        when(menuMapper.toItemOptionResponse(option)).thenReturn(new ItemOptionResponse(optionId, groupId, "Opt", 50, true, Instant.now(), Instant.now()));

        List<ItemOptionResponse> results = menuService.getItemOptionsByGroup(groupId);
        assertThat(results).hasSize(1);
    }
}
