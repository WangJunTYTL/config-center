package com.peaceful.config.center.web;

import com.peaceful.config.center.domain.Category;
import com.peaceful.config.center.domain.Property;
import com.peaceful.config.center.service.CategoryService;
import com.peaceful.config.center.util.WebResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Jun on 2018/8/19.
 */
@RestController
@RequestMapping("cs")
public class PublishServerController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("get-categoryId")
    public Object getCategoryId(String cname) {
        Category category = categoryService.getCategoryByName(cname);
        if (category == null) {
            return WebResponse.JSON(500, cname + " is not exist");
        }
        return WebResponse.JsonData(200, "OK", category.getId());
    }


    @RequestMapping("get")
    public Object getValue(HttpServletRequest request) {
        String categoryIdStr = request.getParameter("c");
        long categoryId = Long.parseLong(categoryIdStr);
        String key = request.getParameter("k");
        Property property = categoryService.getPropertyByName(categoryId, key);
        if (property == null) {
            return WebResponse.JSON(500, key + " is not exist!");
        }
        String value = categoryService.getCategoryPropertyValue(categoryId, property.getId());
        return WebResponse.JsonData(200, "OK", value);
    }

    @RequestMapping("put")
    public Object putValue(HttpServletRequest request) {
        String categoryIdStr = request.getParameter("c");
        long categoryId = Long.parseLong(categoryIdStr);
        String key = request.getParameter("k");
        String value = request.getParameter("v");
        Property property = categoryService.getPropertyByName(categoryId, key);
        if (property == null) {
            return WebResponse.JSON(500, key + " is not exist!");
        }
        categoryService.updateCategoryPropertyValue(categoryId, property.getId(), value);
        return WebResponse.JSON(200, "OK");
    }
}
