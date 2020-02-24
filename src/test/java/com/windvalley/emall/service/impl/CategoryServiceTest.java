package com.windvalley.emall.service.impl;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.service.ICategoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class CategoryServiceTest {
    @Autowired
    private ICategoryService categoryService;

    @Test
    public void selectCategoryChildById() {
        ServerResponse serverResponse = categoryService.selectCategoryChildById(100005);
        if (serverResponse.isSuccess()){
            List<Integer> list = (ArrayList) serverResponse.getData();
            Assert.assertEquals(6, list.size());
        }
    }
}
