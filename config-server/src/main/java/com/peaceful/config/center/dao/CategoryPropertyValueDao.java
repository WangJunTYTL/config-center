package com.peaceful.config.center.dao;


import com.peaceful.config.center.domain.CategoryPropertyValue;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Jun on 2017/4/15.
 */
public interface CategoryPropertyValueDao {


    @Insert("insert into category_property_value(`key`,`category_id`,`property_id`,`value`,`snapshot`,`add_time`) values (#{key},#{categoryId},#{propertyId},#{value},#{snapshot},now())" +
            " ON DUPLICATE KEY UPDATE `value` = #{value},`snapshot`= #{snapshot},`mod_time` = now()")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue);

    @Select("select * from category_property_value where category_id = #{categoryId} and `property_id` = #{propertyId}")
    CategoryPropertyValue selectByCategoryIdAndPropertyId(@Param("categoryId") long categoryId, @Param("propertyId") long propertyId);

    @Select("select * from category_property_value where category_id = #{categoryId} and `property_id` = #{propertyId} and key = #{key}")
    CategoryPropertyValue selectByKeyAndPropertyId(@Param("categoryId") long categoryId, @Param("key") String key, @Param("propertyId") long propertyId);

    @Select("select * from category_property_value where category_id = #{categoryId}")
    List<CategoryPropertyValue> selectValueList(@Param("categoryId") long categoryId);

    @Delete("delete from category_property_value where `property_id` = #{propertyId}")
    int deleteByPropertyId(@Param("propertyId") long properId);


}
