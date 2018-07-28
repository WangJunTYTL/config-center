package com.peaceful.config.center.dao;


import com.peaceful.config.center.domain.CategoryPropertyValue;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by wang on 2017/4/15.
 */
public interface CategoryPropertyValueDao {


    @Insert("insert into category_property_value(`category_id`,`property_id`,`value`,`snapshot`,`add_time`) values (#{categoryId},#{propertyId},#{value},#{snapshot},now())" +
            " ON DUPLICATE KEY UPDATE `value` = #{value},`snapshot`= #{snapshot},`mod_time` = now()")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue);

    @Select("select * from category_property_value where category_id = #{categoryId}")
    List<CategoryPropertyValue> selectValueList( @Param("categoryId") long categoryId);


}
