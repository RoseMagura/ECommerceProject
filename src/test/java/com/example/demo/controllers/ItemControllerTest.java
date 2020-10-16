package com.example.demo.controllers;

import com.example.demo.SareetaApplication;
import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.apache.coyote.Response;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = SareetaApplication.class)
public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }


    @Test
    public void getAllItemsSuccessful(){
        ResponseEntity<List<Item>> items = itemController.getItems();
        assertNotNull(items);
        assertEquals(200, items.getStatusCodeValue());
    }

    @Test
    public void getItemByIdSuccessful(){
        when(itemRepo.findById(1L)).thenReturn(
                java.util.Optional.of(new Item("Round Widget", 2.99, "A widget that is round")));
        ResponseEntity<Item> item = itemController.getItemById(1L);
        assertNotNull(item);
        assertEquals(200, item.getStatusCodeValue());
        assertEquals("Round Widget", Objects.requireNonNull(item.getBody()).getName());
        assertEquals(BigDecimal.valueOf(2.99), item.getBody().getPrice());
        assertEquals("A widget that is round", item.getBody().getDescription());
    }

    public void getItemByIdError(){
        when(itemRepo.findById(1L)).thenReturn(
                java.util.Optional.of(new Item("Round Widget", 2.99, "A widget that is round")));
        ResponseEntity<Item> item = itemController.getItemById(100L);
        assertNotNull(item);
        assertEquals(404, item.getStatusCodeValue());
    }

    @Test
    public void getItemByNameSuccessful(){
        List<Item> items = Collections.singletonList(new Item("Round Widget", 2.99, "A widget that is round"));
        when(itemRepo.findByName("Round Widget")).thenReturn(items);
        ResponseEntity<List<Item>> item = itemController.getItemsByName("Round Widget");
        assertNotNull(item);
        assertEquals(200, item.getStatusCodeValue());
        assertEquals("Round Widget", Objects.requireNonNull(item.getBody()).get(0).getName());
        assertEquals(BigDecimal.valueOf(2.99), item.getBody().get(0).getPrice());
        assertEquals("A widget that is round", item.getBody().get(0).getDescription());
    }

    @Test
    public void getItemByNameError(){
        List<Item> items = Collections.singletonList(new Item("Round Widget", 2.99, "round"));
        when(itemRepo.findByName("Round Widget")).thenReturn(items);
        ResponseEntity<List<Item>> item = itemController.getItemsByName("Round Wdget");
        assertNotNull(item);
        assertEquals(404, item.getStatusCodeValue());
    }



}
