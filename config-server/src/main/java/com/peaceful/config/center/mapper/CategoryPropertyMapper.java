package com.peaceful.config.center.mapper;


import com.peaceful.config.center.domain.CategoryProperty;
import com.peaceful.config.center.domain.Property;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Jun on 2017/4/15.
 */
public interface CategoryPropertyMapper {


    @Insert("insert into category_property(`name`,`description`,`type`,`option`,`add_time`,`default_value`) values (#{name},#{description},#{type},#{option},now(),#{defaultValue})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insertProperty(Property property);

    @Insert("insert into category_property_map (`category_id`,`property_id`,`add_time`) values (#{categoryId},#{propertyId},now())")
    long insertCategoryProperty(CategoryProperty categoryProperty);

    @Select("select * from category_property_map where category_id = #{categoryId}")
    List<CategoryProperty> selectCategoryPropertyList(long categoryId);

    @Select("select * from category_property where id = #{id}")
    Property getPropertyById(long id);

    @Select("select * from category_property where name = #{name}")
    Property getPropertyByName(String name);

    @Update("update category_property set description = #{description},mod_time = now() where id = #{id}")
    int updatePropertyDescription(@Param("id") long id, @Param("description") String description);

    @Update("update category_property set default_value = #{defaultValue},mod_time = now() where name = #{name}")
    int updatePropertyDefaultValueByName(@Param("name") String name, @Param("defaultValue") String defaultValue);

    @Delete("delete from category_property where id = #{id}")
    void deleteById(@Param("id") long propertyId);

    @Delete("delete from category_property_map where property_id = #{id}")
    void deleteCategoryPropertyMap(@Param("id") long propertyId);
}
