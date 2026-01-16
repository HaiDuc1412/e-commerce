package fpt.haidd69.ecommerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import fpt.haidd69.ecommerce.dto.product.ProductResponse;
import fpt.haidd69.ecommerce.dto.product.ProductVariantResponse;
import fpt.haidd69.ecommerce.entities.Product;
import fpt.haidd69.ecommerce.entities.ProductVariant;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToString")
    @Mapping(target = "variants", source = "variants", qualifiedByName = "filterActiveVariants")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "size", source = "size", qualifiedByName = "sizeToString")
    @Mapping(target = "color", source = "color", qualifiedByName = "colorToString")
    ProductVariantResponse toProductVariantResponse(ProductVariant variant);

    List<ProductVariantResponse> toProductVariantResponseList(List<ProductVariant> variants);

    @Named("categoryToString")
    default String categoryToString(fpt.haidd69.ecommerce.enums.ProductCategory category) {
        return category != null ? category.name() : null;
    }

    @Named("sizeToString")
    default String sizeToString(fpt.haidd69.ecommerce.enums.Size size) {
        return size != null ? size.name() : null;
    }

    @Named("colorToString")
    default String colorToString(fpt.haidd69.ecommerce.enums.Color color) {
        return color != null ? color.name() : null;
    }

    @Named("filterActiveVariants")
    default List<ProductVariantResponse> filterActiveVariants(List<ProductVariant> variants) {
        return variants.stream()
                .filter(ProductVariant::getActive)
                .map(this::toProductVariantResponse)
                .toList();
    }
}
