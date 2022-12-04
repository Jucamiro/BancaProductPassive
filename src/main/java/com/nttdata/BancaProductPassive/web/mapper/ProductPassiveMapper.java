package com.nttdata.BancaProductPassive.web.mapper;

import com.nttdata.BancaProductPassive.domain.ProductPassive;
import com.nttdata.BancaProductPassive.web.model.ProductPassiveModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductPassiveMapper {

    ProductPassive modelToEntity (ProductPassiveModel model);

    ProductPassiveModel entityToModel (ProductPassive event);

    @Mapping(target="idProductPassive", ignore = true)
    void update(@MappingTarget ProductPassive entity, ProductPassive updateEntity);

}
