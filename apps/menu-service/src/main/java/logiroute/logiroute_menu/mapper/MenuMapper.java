package logiroute.logiroute_menu.mapper;

import logiroute.logiroute_menu.domain.MenuCategory;
import logiroute.logiroute_menu.domain.MenuItem;
import logiroute.logiroute_menu.domain.OptionGroup;
import logiroute.logiroute_menu.domain.ItemOption;
import logiroute.logiroute_menu.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper {

    @Mapping(source = "restaurant.id", target = "restaurantId")
    CategoryResponse toCategoryResponse(MenuCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    MenuCategory toEntity(CreateCategoryRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget MenuCategory category, UpdateCategoryRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    MenuItemResponse toItemResponse(MenuItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    MenuItem toEntity(CreateMenuItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget MenuItem item, UpdateMenuItemRequest request);

    @Mapping(source = "menuItem.id", target = "menuItemId")
    OptionGroupResponse toOptionGroupResponse(OptionGroup group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    OptionGroup toEntity(CreateOptionGroupRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget OptionGroup group, UpdateOptionGroupRequest request);

    @Mapping(source = "optionGroup.id", target = "optionGroupId")
    ItemOptionResponse toItemOptionResponse(ItemOption option);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "optionGroup", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    ItemOption toEntity(CreateItemOptionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "optionGroup", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget ItemOption option, UpdateItemOptionRequest request);
}
