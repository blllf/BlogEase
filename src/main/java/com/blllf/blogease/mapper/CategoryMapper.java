package com.blllf.blogease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blllf.blogease.pojo.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    //新增分类
    @Insert("insert into category(category_name, category_alias, create_user, create_time, update_time , category_pic) VALUES " +
            "(#{categoryName} , #{categoryAlias} ,#{createUser} ,#{createTime} , #{updateTime} , #{categoryPic})")
    void add(Category category);


    @Select("select * from category where create_user = #{uId}")
    List<Category> selectAll(Integer uId);

    //查询所有用户的分类信息
    @Select("select * from category")
    List<Category> selectAll2();

    @Select("select * from category where id = #{id}")
    Category selectOneById(Integer id);

    @Update("update category set category_name = #{categoryName} ,category_alias = #{categoryAlias} , category_pic = #{categoryPic}, update_time = now()" +
            "where id = #{id}")
    void update(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Integer id);

    //根据文章Id查询分类的名称
    @Select("select category.* from category INNER JOIN article ON category.id = article.category_id where article.id = #{articleId} ;")
    Category selectCategoryByArticleId(Integer articleId);
}
