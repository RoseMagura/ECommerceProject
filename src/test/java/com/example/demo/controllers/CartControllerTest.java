package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
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
        when(itemRepo.findById(1L)).thenReturn(
                java.util.Optional.of(new Item("Round Widget", 2.99, "A widget that is round")));
    }
    @Test
    public void testAddToCartSuccess(){
        initRepo();
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(1);
        r.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNotNull(c);
        assertEquals("test", c.getUser().getUsername());
        assertEquals("Round Widget", c.getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(5.98), c.getTotal());
    }

    @Test
    public void testAddToCartUserError(){
        initRepo();
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("errorUser");
        r.setItemId(1);
        r.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartItemError(){
        initRepo();
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(100);
        r.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartSuccess(){
        initRepo();
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(1);
        r.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNotNull(c);
        assertEquals("test", c.getUser().getUsername());
        assertTrue( c.getItems().isEmpty());
        assertThat(new BigDecimal("0.00"), Matchers.comparesEqualTo(c.getTotal()));
    }

    @Test
    public void testRemoveFromCartUserError(){
        initRepo();
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("errorUser");
        r.setItemId(1);
        r.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartItemError(){
        initRepo();
        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(100);
        r.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
