package com.sw.shop.service;

import com.sw.shop.domain.item.Book;
import com.sw.shop.domain.item.Item;
import com.sw.shop.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @Test
    public void 상품_등록() throws Exception {
        //given
        Item item = new Book();

        //when
        Long itemId = itemService.save(item);

        //then
        Assertions.assertThat(itemRepository.finaOne(itemId)).isEqualTo(item);
    }
}