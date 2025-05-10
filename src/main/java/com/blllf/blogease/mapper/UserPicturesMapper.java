package com.blllf.blogease.mapper;

import com.blllf.blogease.pojo.UserPictures;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserPicturesMapper {

    //插入上传图片的数据到数据库中
    @Insert("insert into user_pictures(create_user, nickname, user_picture, create_time) " +
            "values (#{createUser} , #{nickname} , #{userPicture} , now())")
    void addPicUrl(UserPictures userPictures);

    @Select("select * from user_pictures where create_user = #{createUser}")
    List<UserPictures> findAll(Integer createUser);


    @Delete("delete from user_pictures where id = #{id}")
    void deletePicById(Integer id);


}
