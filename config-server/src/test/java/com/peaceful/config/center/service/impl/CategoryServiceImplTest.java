package com.peaceful.config.center.service.impl;

import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.CategoryPropertyValue;
import com.peaceful.config.center.domain.Property;
import com.peaceful.config.center.service.CategoryService;
import com.peaceful.config.center.dao.enumhandler.PropertyType;
import com.peaceful.config.center.service.CategoryException;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Jun on 2018/7/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @org.junit.Test
    public void getCategoryPropertyList() {
    }

    @org.junit.Test
    public void getPropertyById() {
    }

    @org.junit.Test
    public void getCategoryPropertyValue() {
    }

    @org.junit.Test
    public void getSingleCategoryPropertyValue() {
    }

    @org.junit.Test
    public void insertCategory() {
        /*Category category = new Category();
        category.setName("root");
        category.setParentCategory(null);
        category.setDescription("root node");

        categoryService.insertCategory(category);*/

        /*Category root = categoryService.getCategoryByName("root");
        Category category = new Category();
        category.setName("测试02");
        category.setParentCategory(root);
        category.setDescription("test 01 node");*/

        Category root = categoryService.getCategoryByName("测试02");
        for (int i = 0; i < 100; i++) {
            Category category = new Category();
            category.setName("c2n" + i);
            category.setParentCategory(root);
            category.setDescription("c 01 node");

            categoryService.insertCategory(category);
        }

    }

    @org.junit.Test
    public void getCategoryByName() {
    }

    @org.junit.Test
    public void getCategoryById() {
    }

    @org.junit.Test
    public void deleteCategory() {
        for (int i=10;i<200;i++) {
            try {
                categoryService.deleteCategory("c1n" + i);
            }catch (CategoryException e){
                // ignore
            }
        }
    }

    @org.junit.Test
    public void deleteProperty() {
        for (int i=10;i<200;i++) {
            try {
                categoryService.deleteProperty(4,"foo"+i);
            }catch (CategoryException e){
                // ignore
            }
        }
    }

    @org.junit.Test
    public void insertProperty() {
        for (int i=0;i<100;i++) {
            Property property = new Property();
            property.setDefaultValue("1"+i);
            property.setDescription("测试属性描述");
            property.setName("foo"+i);
            property.setType(PropertyType.INPUT.getCode());
//            categoryService.insertProperty("c1",property);
        }


        CategoryPropertyValue categoryPropertyValue = new CategoryPropertyValue();
        categoryPropertyValue.setValue("bar");
        Category c1 = categoryService.getCategoryByName("c1");
        categoryPropertyValue.setCategoryId(c1.getId());
//        categoryService.insertCategoryPropertyValue();
    }

    @org.junit.Test
    public void insertCategoryPropertyValue() {

    }

    @org.junit.Test
    public void updateDescriptionByName() {
    }

    @org.junit.Test
    public void updateDefaultValueByName() {
    }
}
