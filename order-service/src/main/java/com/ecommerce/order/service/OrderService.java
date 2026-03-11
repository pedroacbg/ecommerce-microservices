package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
//    private final UserRepository userRepository;
    private final CartService cartService;

    public Optional<OrderResponse> createOrder(Long userId) {
        // verifica se o carrinho do usuario tem items
        List<CartItem> cartItems = cartService.getCartItemByUser(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        // verifica o usuario passado
//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//        if(userOpt.isEmpty()){
//            return Optional.empty();
//        }
//        User user = userOpt.get();

        // calcula o valor total dos items
        BigDecimal totalPrice = cartItems.stream().map(CartItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        // cria o pedido
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(null, item.getProductId(), item.getQuantity(), item.getPrice(), order))
                .toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // limpa o carrinho
        cartService.clearCart(userId);
        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order savedOrder) {
        return new OrderResponse(savedOrder.getId(), savedOrder.getTotalAmount(),
                savedOrder.getStatus(), savedOrder.getItems().stream()
                    .map(item -> new OrderItemDTO(
                            item.getId(), item.getProductId(),
                            item.getQuantity(), item.getPrice(),
                            item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                    )).toList(), savedOrder.getCreatedAt());

    }
}
