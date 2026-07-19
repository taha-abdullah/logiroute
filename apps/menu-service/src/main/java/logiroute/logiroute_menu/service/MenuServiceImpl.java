package logiroute.logiroute_menu.service;

import logiroute.logiroute_menu.domain.*;
import logiroute.logiroute_menu.dto.*;
import logiroute.logiroute_menu.exception.ResourceNotFoundException;
import logiroute.logiroute_menu.mapper.MenuMapper;
import logiroute.logiroute_menu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository itemRepository;
    private final OptionGroupRepository groupRepository;
    private final ItemOptionRepository optionRepository;
    private final MenuMapper menuMapper;

    // --- Categories ---

    @Override
    @Transactional
    public CategoryResponse createCategory(UUID restaurantId, CreateCategoryRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
        
        MenuCategory category = menuMapper.toEntity(request);
        category.setRestaurant(restaurant);
        category = categoryRepository.save(category);
        
        return menuMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest request) {
        MenuCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        menuMapper.updateEntity(category, request);
        category = categoryRepository.save(category);
        
        return menuMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        MenuCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponse> getCategoriesByRestaurant(UUID restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return categoryRepository.findByRestaurantIdOrderBySortOrderAsc(restaurantId).stream()
                .map(menuMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    // --- Menu Items ---

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(UUID categoryId, CreateMenuItemRequest request) {
        MenuCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        MenuItem item = menuMapper.toEntity(request);
        item.setCategory(category);
        item = itemRepository.save(item);
        
        return menuMapper.toItemResponse(item);
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(UUID itemId, UpdateMenuItemRequest request) {
        MenuItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + itemId));
        
        menuMapper.updateEntity(item, request);
        item = itemRepository.save(item);
        
        return menuMapper.toItemResponse(item);
    }

    @Override
    @Transactional
    public void deleteMenuItem(UUID itemId) {
        MenuItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + itemId));
        itemRepository.delete(item);
    }

    @Override
    public List<MenuItemResponse> getMenuItemsByCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return itemRepository.findByCategoryId(categoryId).stream()
                .map(menuMapper::toItemResponse)
                .collect(Collectors.toList());
    }

    // --- Option Groups ---

    @Override
    @Transactional
    public OptionGroupResponse createOptionGroup(UUID itemId, CreateOptionGroupRequest request) {
        MenuItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + itemId));

        OptionGroup group = menuMapper.toEntity(request);
        group.setMenuItem(item);
        group = groupRepository.save(group);
        
        return menuMapper.toOptionGroupResponse(group);
    }

    @Override
    @Transactional
    public OptionGroupResponse updateOptionGroup(UUID groupId, UpdateOptionGroupRequest request) {
        OptionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("OptionGroup not found with id: " + groupId));
        
        menuMapper.updateEntity(group, request);
        group = groupRepository.save(group);
        
        return menuMapper.toOptionGroupResponse(group);
    }

    @Override
    @Transactional
    public void deleteOptionGroup(UUID groupId) {
        OptionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("OptionGroup not found with id: " + groupId));
        groupRepository.delete(group);
    }

    @Override
    public List<OptionGroupResponse> getOptionGroupsByItem(UUID itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("MenuItem not found with id: " + itemId);
        }
        return groupRepository.findByMenuItemId(itemId).stream()
                .map(menuMapper::toOptionGroupResponse)
                .collect(Collectors.toList());
    }

    // --- Item Options ---

    @Override
    @Transactional
    public ItemOptionResponse createItemOption(UUID groupId, CreateItemOptionRequest request) {
        OptionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("OptionGroup not found with id: " + groupId));

        ItemOption option = menuMapper.toEntity(request);
        option.setOptionGroup(group);
        option = optionRepository.save(option);
        
        return menuMapper.toItemOptionResponse(option);
    }

    @Override
    @Transactional
    public ItemOptionResponse updateItemOption(UUID optionId, UpdateItemOptionRequest request) {
        ItemOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemOption not found with id: " + optionId));
        
        menuMapper.updateEntity(option, request);
        option = optionRepository.save(option);
        
        return menuMapper.toItemOptionResponse(option);
    }

    @Override
    @Transactional
    public void deleteItemOption(UUID optionId) {
        ItemOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemOption not found with id: " + optionId));
        optionRepository.delete(option);
    }

    @Override
    public List<ItemOptionResponse> getItemOptionsByGroup(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("OptionGroup not found with id: " + groupId);
        }
        return optionRepository.findByOptionGroupId(groupId).stream()
                .map(menuMapper::toItemOptionResponse)
                .collect(Collectors.toList());
    }
}
