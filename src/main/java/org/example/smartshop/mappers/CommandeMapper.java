package org.example.smartshop.mappers;


import org.example.smartshop.entities.DTO.commandes.CommandeCreateDto;
import org.example.smartshop.entities.DTO.commandes.CommandeResponseDto;
import org.example.smartshop.entities.DTO.commandes.CommandeUpdateDto;
import org.example.smartshop.entities.DTO.orderItem.OrderItemResponseDto;
import org.example.smartshop.entities.models.Commande;
import org.example.smartshop.entities.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommandeMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.nom")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "paiements", source = "paiements")
    CommandeResponseDto toDto(Commande commande);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "totalLine", expression =
            "java(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    OrderItemResponseDto toItemDto(OrderItem item);
}



