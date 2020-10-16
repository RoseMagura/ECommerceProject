package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.apache.coyote.Response;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
//        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
//        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @BeforeClass
    public void addItems() {
        itemRepo.save(new Item("Round Widget", 2.99, "A widget that is round"));
        itemRepo.save(new Item("Square Widget", 1.99, "A widget that is square"));
    }

    @Test
    public void getAllItemsSuccessful(){
        ResponseEntity<List<Item>> items = itemController.getItems();
        assertNotNull(items);
        assertEquals(200, items.getStatusCodeValue());
    }

    @Test
    public void getItemByIdSuccessful(){
        System.out.println(itemRepo.findAll());
        ResponseEntity<Item> item = itemController.getItemById(0L);
        assertNotNull(item);
        assertEquals(200, item.getStatusCodeValue());
        System.out.println(item.getBody());
    }

    public void getItemByIdError(){}

    public void getItemByNameSuccessful(){}

    public void getItemByNameError(){}



}
