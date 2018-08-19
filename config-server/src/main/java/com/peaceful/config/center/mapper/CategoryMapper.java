package com.peaceful.config.center.mapper;


import com.peaceful.config.center.domain.Category;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Jun on 2017/4/15.
 */
public interface CategoryMapper {

    @Insert("insert into category(`name`,`parent_category_id`,`description`,`add_time`) values( #{category.name},#{category.parentCategory.id},#{category.description},now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCategory(@Param("category") Category category);

    @Select("select * from category where `name` = #{name}")
    Category getCategoryByName(String name);

    @Select("select * from category where `id` = #{id}")
    Category getCategoryById(long id);

    @Select("select * from category where `parent_category_id` = #{id}")
    List<Category> getCategoryByParentId(long id);

    @Delete("delete from category where `name` = #{name}")
    int deleteByName(@Param("name")String name);

}
