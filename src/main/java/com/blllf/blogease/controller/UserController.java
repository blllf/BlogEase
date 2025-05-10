package com.blllf.blogease.controller;


import com.blllf.blogease.mapper.UserMapper;
import com.blllf.blogease.pojo.dto.CategoryArticleCount;
import com.blllf.blogease.pojo.PageBean;
import com.blllf.blogease.pojo.Result;
import com.blllf.blogease.pojo.User;
import com.blllf.blogease.service.FocusRedisService;
import com.blllf.blogease.service.UserService;
import com.blllf.blogease.util.Captcha;
import com.blllf.blogease.util.JwtUtil;
import com.blllf.blogease.util.ThreadLocalUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private static String verifyCode ;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FocusRedisService focusRedisService;


    @PostMapping("/register")
    //@GetMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$")String username , @Pattern(regexp = "^\\S{5,16}$")String password ,
                           @Pattern(regexp = "^\\S{5,16}$") String rePassword){
        User u = userService.findByUsername(username);
        if (password.equals(rePassword)){
            if (u == null){
                //数据库没有相同的用户
                userService.register(username , password);
                return Result.success();
            }else {
                return Result.error("用户名已被占用");
            }
        }else {
            return Result.error("再次输入密码为空");
        }

        /*if(u == null){
            //数据库没有相同的用户
            userService.register(username , password);
            return Result.success();
        }else {
            return Result.error("用户名已被占用");
        }*/
    }

    //@RequestMapping("/login")
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$")String username , @Pattern(regexp = "^\\S{5,16}$")String password ,
                                String rememberMe , HttpServletResponse response){
        User loginUser = userService.findByUsername(username);
        if (loginUser==null){
            return Result.error("用户名错误");
        }

        boolean flag = Boolean.parseBoolean(rememberMe);
        String token = null;
        String token2 = null;

        /*if (Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            //登录成功
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("id" , loginUser.getId());
            claims.put("username" , loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            //把token 存到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,12, TimeUnit.HOURS);
            return Result.success(token);
        }*/

        if (password.equals(loginUser.getPassword())){
            //登录成功
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("id" , loginUser.getId());
            claims.put("username" , loginUser.getUsername());
            token = JwtUtil.genToken(claims);
            //把token 存到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,12, TimeUnit.HOURS);
            if (flag){
                // 将这个token与用户ID关联起来存储在数据库或缓存中
                token2 = token + "1";
                operations.set(token2, String.valueOf(loginUser.getId()) , 7 , TimeUnit.DAYS);
                Cookie cookie = new Cookie("rememberMe", token2);
                //cookie.setHttpOnly(true); // 防止JS访问
                //cookie.setSecure(true);   // 只能通过HTTPS发送
                cookie.setMaxAge(60 * 60 * 24 * 7); // 一周有效
                cookie.setPath("/");      // 根据需要设置路径
                response.addCookie(cookie);
            }else {
                //删除cookie
                Cookie cookie = new Cookie("rememberMe", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                //删除redis中对应的数据
                if (token2 != null && !token2.isEmpty()){
                    stringRedisTemplate.delete(token2);
                }

            }
            return Result.success(token);
        }

        return Result.error("密码错误");
    }
    /*
    后端创建Cookie:
    生成唯一标识符：后端生成一个唯一的、长期有效的标识符（如UUID）。
    存储标识符：将这个标识符与用户的ID关联起来存储在数据库或缓存中。
    设置Cookie：将这个标识符作为Cookie的值发送给客户端，设置为长期有效（例如一周）。
    前端处理逻辑
    检查Cookie：前端在页面加载时检查是否存在rememberMe Cookie。
    自动填充用户名：如果存在rememberMe Cookie，前端可以使用这个标识符向后端请求用户的用户名，并自动填充到登录表单中。
    自动登录：如果需要实现自动登录，前端可以使用这个标识符向后端验证用户身份，并获取JWT令牌。
    */
    @PostMapping("/validateRememberMe")// ResponseEntity<?> Result<Map<String, String>>
    public Result<Map<String, String>> validateRememberMe(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String rememberMeToken = payload.get("rememberMe") + "1";
        if (rememberMeToken == null || rememberMeToken.isEmpty()) {
            return null;
        }
        // 从数据库或缓存中查找对应的用户信息
        String id = stringRedisTemplate.opsForValue().get(rememberMeToken);
        if (id == null) {
            return Result.success(new HashMap<>());
        }
        User user = userMapper.selectById(id);
        // 返回用户名
        Map<String, String> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("password", user.getPassword());
        return Result.success(result);
    }

    //查询出所有用户
    @GetMapping("/selectAllusers")
    public Result<List<User>> selectAll(){
        List<User> users = userService.selectAll();
        return Result.success(users);
    }

    @GetMapping("/userInfo")
    public Result<User> userInfo(){
        //@RequestHeader(name = "Authorization") String token
        //每次都要获取一次令牌，从令牌中获得用户的信息，可以用ThreadLocal对象存储用户信息并获取
        Map<String , Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        /*Map<String, Object> map = JwtUtil.parseToken(token);
        String username = (String) map.get("username");*/
        User u = userService.findByUsername(username);
        return Result.success(u);
    }

    @GetMapping("/peopleInfo")
    public Result<User> peopleInfoById(@RequestParam int id){
        User user = userService.findPeopleInfoById(id);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        Boolean flag = null;
        try {
            flag = userService.update(user);
        } catch (Exception e) {
            return Result.error("邮箱不能为空");
        }
        if (flag){
            return Result.success();
        }
        return Result.error("邮箱已存在");
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePassword")
    public Result updatePassword(@RequestBody Map<String , String> map , @RequestHeader("Authorization") String token){
        String oldPwd = map.get("old_pwd");
        String newPwd = map.get("new_pwd");
        String rePwd = map.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }

        //原密码是否正确
        Map<String , Object> userMap = ThreadLocalUtil.get();
        String username = (String) userMap.get("username");
        User u = userService.findByUsername(username);
        /*if (!u.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写错误");
        }*/
        if (!u.getPassword().equals(oldPwd)){
            return Result.error("原密码填写错误");
        }

        //newPwd与rePwd是否一致
        if (!newPwd.equals(rePwd)){
            return Result.error("两次填写的密码不一致");
        }
        userService.updatePassword(newPwd);
        //删除redis对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }


    //发送邮件
    @GetMapping("/sendEmail")
    public Result<String> sendEmail(@RequestParam @Email String email){

        User user = userService.selectByEmail(email);
        if (user != null){
            //email = "1729121348@qq.com";
            //生成验证码
            verifyCode = Captcha.genCode();

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("854234687@qq.com");
            message.setTo(email);
            message.setSubject("类贴吧 官方邮件 ");
            message.setSentDate(new Date());
            message.setText("你正在修改密码，验证码为:  " + verifyCode);

            javaMailSender.send(message);

            return Result.success(verifyCode);
        }

        return Result.error("不存在该邮箱");

    }

    /*
    * @RequestBody Map<String , String> map
    * @RequestParam String code , @RequestParam String email
    * */

    //找回密码
    @PatchMapping("/findPwd")
    public Result retrievePassword(@RequestBody Map<String , String> map){
        String email = map.get("email");
        String code = map.get("code");
        String newPwd = map.get("new_pwd");
        String rePwd = map.get("re_pwd");

        if (!StringUtils.hasLength(code) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }

        if (code.equals(verifyCode)){
            User user = userService.selectByEmail(email);
            //newPwd与rePwd是否一致
            if (!newPwd.equals(rePwd)){
                return Result.error("两次填写的密码不一致");
            }
        }

        userService.findPassword(newPwd , email);

        return Result.success();
    }

    //查找单个用户
    @GetMapping("/getOne")
    public Result<User> findOne(Integer id){
        User user = userMapper.selectById(id);
        return Result.success(user);
    }

    // 管理员行为
    //查询数据库中所有用户的值
   // @GetMapping("/getEveryOne")
    /*public Result<List<User>> selectAllPeople(){
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");
        if (uid == 1){
            List<User> users = userMapper.selectList(null);
            return Result.success(users);
        }
        return Result.error("该用户不是管理员");
    }*/
    @GetMapping("/getEveryOne")
    //查询改进
    public Result<PageBean<User>> selectUsersAdmin(Integer pageNum , Integer pageSize ,
                                                   @RequestParam(required = false) String username,
                                                   @RequestParam(required = false) String email,
                                                   @RequestParam(required = false) String nickname){

        PageBean<User> users = userService.selectAllByAdmin(pageNum, pageSize, username, email, nickname);

        return Result.success(users);
    }



    //删除用户
    @DeleteMapping("/deleteUser")
    public Result deleteUser(Integer id){
        boolean flag = userService.deleteUser(id);
        return flag ? Result.success() : Result.error("删除失败");
    }

    //管理员 修改用户信息 用户名：保持唯一不修改 邮箱不能重复
    //通过用户Id值修改
    @PostMapping("/updateUserAdmin")
    public Result updateUser(@RequestBody User user){
        //System.out.println("user：" + user);
        Boolean flag = userService.updateUserByAdmin(user);
        return flag?Result.success():Result.error("邮箱已存在");
    }

    //添加用户
    @PostMapping("/addUserAdmin")
    public Result addUser(@RequestBody  User user){

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "username");
        violations.addAll(validator.validateProperty(user, "password"));

        if (!violations.isEmpty()){
            for (ConstraintViolation<User> violation : violations) {
                System.out.println("------");
                return Result.error(violation.getMessage());
            }

        }else {
            //验证通过
            //1. 查询该用户名（账号）在数据库是否唯一
            //Map<String, Object> map1 = new HashMap<>();
            //Map<String, Object> map2 = new HashMap<>();
            /*map1.put("username", user.getUsername());
            map1.put("email",user.getEmail());
            List<User> users = userMapper.selectByMap(map1);*/
            //验证数据库是否有用户名同名的或者是同邮箱的
            List<User> users = userMapper.selectByEmail2(user.getEmail(), user.getUsername());
            System.out.println("hahahahahahha" + users);
            //List<User> user2 = userMapper.selectByMap(map2);
            if (!users.isEmpty()){
                return Result.error("该用户名或邮箱已存在");
            }else {
                userMapper.addUserAdmin(user);
                return Result.success("添加成功");
            }
        }
        return null;
    }


    @GetMapping("/findUA")
    public Result<ArrayList<CategoryArticleCount>> findUA(){
        List<CategoryArticleCount> cacs = userMapper.findUsernameAndArticles();
        HashMap<String, Integer> hm = new HashMap<>();
        for (CategoryArticleCount cac : cacs) {
            String username = cac.getUsername();
            Integer totalCount = cac.getTotalCount();
            hm.put(username , totalCount);
        }

        Set<Map.Entry<String, Integer>> entries = hm.entrySet();
        //存方键值对到一个链表中
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(entries);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<Map.Entry<String, Integer>> top10 = list.subList(0, Math.min(list.size(), 10));
        ArrayList<CategoryArticleCount> list1 = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : top10) {
            CategoryArticleCount cac = new CategoryArticleCount();
            cac.setUsername(entry.getKey());
            cac.setTotalCount(entry.getValue());
            list1.add(cac);
        }
        return Result.success(list1);
    }

    //查询用户名点赞排名
    @GetMapping("/findRank")
    public Result<List<CategoryArticleCount>> findRank(){
        List<CategoryArticleCount> rank = userService.findRank();
        return Result.success(rank);
    }

    @GetMapping("/updateEmailOrNick")
    public Result updateEmailOrNick(@RequestParam(required = false) @Email String email,
                                    @RequestParam(required = false) String nickname,
                                    @RequestParam(required = false) String username){
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer uid = (Integer) map.get("id");

        if (nickname != null && nickname != ""){
            userMapper.updateNickname(nickname , uid);
            return Result.success();
        }

        if (email != null && email != ""){
            User user = userMapper.selectByEmail3(email, username);
            if (user == null){
                userMapper.updateEmail(email , uid);
                return Result.success();
            }else {
                return Result.error("邮箱被占用");
            }
        }
        return Result.error("操作失败");
    }

    @GetMapping("/follow")
    public Result follow(@RequestParam Integer followerId , @RequestParam Integer followingId){
        String str = userService.followUser(followerId, followingId);
        if (str.equals("yes")){
            //更新redis计数器
            focusRedisService.incrementFollowerCount(followingId);
            focusRedisService.incrementFollowingCount(followerId);
            //关注缓存关系
            focusRedisService.addFollowing(followerId , followingId);
            focusRedisService.addFollower(followingId, followerId);
            return Result.success(str);
        }
        return Result.error(str);
    }

    @GetMapping("/unfollow")
    public Result unfollow(@RequestParam Integer followerId , @RequestParam Integer followingId){
        String str = userService.unfollowUser(followerId, followingId);
        if (str.equals("yes")){
            //2. 更新计数器
            focusRedisService.decrementFollowerCount(followingId);
            focusRedisService.decrementFollowingCount(followerId);
            //3.删除对应关系
            focusRedisService.deleteFollow(followerId , followingId);
            return Result.success(str);
        }
        return Result.error(str);
    }

    @GetMapping("/existsFollow")
    public Result existsFollow(@RequestParam Integer followerId , @RequestParam Integer followingId){
        Boolean b = focusRedisService.existsFollow(followerId, followingId);
        if (b)
            return Result.success();
        else
            return Result.success("null");
    }

    @GetMapping("/getFollowerCount")
    public Result getFollowerCount(@RequestParam Integer userId){
        int followerCount = focusRedisService.getFollowerCount(userId);
        return Result.success(followerCount);
    }

    @GetMapping("/findUserAttention")
    public Result<List<User>> findUserAttention(@RequestParam Integer userId){
        List<User> users = focusRedisService.getUserAttentions(userId);
        return Result.success(users);
    }








}
