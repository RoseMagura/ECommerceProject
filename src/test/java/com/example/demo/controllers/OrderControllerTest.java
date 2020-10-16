package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    public void initRepo(){
        User user = new User("test", "testPassword", new Cart());
        when(userRepo.findByUsername("test")).thenReturn(user);
        Cart cart = new Cart();
        cart.setUser(user);
        List<Item> items = new ArrayList<>();
        items.add(new Item("Round Widget", 2.99, "A widget that is round"));
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(2.99));
        user.setCart(cart);
    }

//    @Test
//    public void testSubmitSuccess(){}

    @Test
    public void testSubmitUserError(){
        ResponseEntity<UserOrder> response = orderController.submit("errorUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

//    @Test
//    public void testHistorySuccess(){
//        ResponseEntity<List<UserOrder>> response =
//                orderController.getOrdersForUser("test");
//        assertNotNull(response);
//        System.out.println(response.getBody());
//        assertEquals(200, response.getStatusCodeValue());
//    }

    @Test
    public void testHistoryUserError(){
        ResponseEntity<List<UserOrder>> response =
                orderController.getOrdersForUser("errorUser");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}

