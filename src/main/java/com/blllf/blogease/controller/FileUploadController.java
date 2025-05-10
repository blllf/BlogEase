package com.blllf.blogease.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.mapper.UserPicturesMapper;
import com.blllf.blogease.pojo.Result;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.pojo.UserPictures;
import com.blllf.blogease.util.AliOssUtil;
import com.blllf.blogease.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class FileUploadController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPicturesMapper userPicturesMapper;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        //保证文件名唯一
        String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //把文件存储到本地磁盘中
        // file.transferTo(new File("C:\\Users\\lovinyq\\Desktop\\file\\" + filename));
        String url = AliOssUtil.upload(filename, file.getInputStream());
        return Result.success(url);
    }

    //类别封面上传
    @PostMapping("/uploadCategoryPic")
    public Result<String> uploadCategoryPic(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        //保证文件名唯一
        String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //上传都阿里云OSS中的位置
        filename = "categoryPic/" + filename;
        String url = AliOssUtil.upload(filename, file.getInputStream());
        return Result.success(url);
    }


    //照片墙上传 单张图片
    @PostMapping("/upload1")
    public Result<String> upload1(MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();
        //保证文件名唯一
        String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //上传都阿里云OSS中的位置
        //如果想要文件存方的位置更加清晰 可以用时间分割开来
        filename = "pictureswall/" + filename;
        String url = AliOssUtil.upload(filename, file.getInputStream());

        return Result.success(url);
    }

    //照片墙上传 多张图片
    @PostMapping("/upload2")
    public Result<List<String>> upload2(MultipartFile[] files) throws IOException {
        ArrayList<String> picturelist = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return Result.error("没有选择文件");
            }
        }
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                //保证文件名唯一
                String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
                filename = "pictureswall/" + filename;
                String url = AliOssUtil.upload(filename, file.getInputStream());
                picturelist.add(url);
            }
        }
        return Result.success(picturelist);
    }

    /**
     * 从本地获取图片的url
     * */
    @GetMapping("/getUrl")
    public Result<List<String>> getUrlFromServer(){
        String prePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\picture\\";
        File dir = new File(prePath);
        File[] files = dir.listFiles();
        ArrayList<String> fileNames  = new ArrayList<>();
        if (files != null){
            for (File file : files) {
                if (!file.isDirectory()){
                    fileNames.add(prePath + file.getName());
                }
            }
        }
        return Result.success(fileNames);
    }

    /**
     * 数据导出为EXCEL表
     *
     * */
    @GetMapping("/fileExport")
    public void exportData(String fileName, HttpServletResponse response) {
        try {
            this.setExcelResponseProp(response, fileName);
            List<User> users = userMapper.selectList(null);
            //List<Student> list = CommonUtil.buildDemoExcel(Student.class);
            //使用 EasyExcel 库创建了一个 ExcelWriter 对象，指定了输出流为 HttpServletResponse 的输出流，这意呈现了响应的输出流。
            EasyExcel.write(response.getOutputStream())
                    .head(User.class)            //.head(ExcelFourDto.class) 设置 Excel 表格的表头信息
                    .excelType(ExcelTypeEnum.XLSX)  //.excelType(ExcelTypeEnum.XLSX) 指定了导出的 Excel 文件类型为 XLSX 格式
                    .sheet(fileName)                //.sheet(fileName) 设置了 Excel 的表单名为 fileName
                    .doWrite(users);             //.doWrite(list) 将数据写入 Excel 文件
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void setExcelResponseProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }


    /**
     *
     * 添加 url
     * */
    @PostMapping("/picture/add")
    public Result addPicture(@URL String userPicture){
        UserPictures userPictures = new UserPictures();

        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        User user = userMapper.selectById(uid);

        userPictures.setCreateUser(uid);
        userPictures.setUserPicture(userPicture);
        userPictures.setNickname(user.getNickname());

        userPicturesMapper.addPicUrl(userPictures);
        return Result.success();
    }

    /**
     *
     * 查询所有的照片根据用户ID
     * */

    @GetMapping("/picture/findAll")
    public Result<List<UserPictures>> findAll(@RequestParam Integer uid){
        List<UserPictures> pictures = userPicturesMapper.findAll(uid);
        return Result.success(pictures);

    }

    /**
     *
     * 删除照片根据ID
     * */
    @DeleteMapping("picture/deleteById")
    public Result deletePictureById(@NotNull Integer id){
        userPicturesMapper.deletePicById(id);
        return Result.success();
    }

}
