package com.blllf.blogease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blllf.blogease.mapper.ArticleMapper;
import com.blllf.blogease.mapper.CategoryMapper;
import com.blllf.blogease.pojo.Category;
import com.blllf.blogease.pojo.dto.CategoryArticleCount;
import com.blllf.blogease.service.CategoryService;
import com.blllf.blogease.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void add(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Map<String , Object> map = ThreadLocalUtil.get();
        Integer uId = (Integer) map.get("id");
        category.setCreateUser(uId);
        categoryMapper.add(category);
    }

    @Override
    public List<Category> selectAll() {
        Map<String , Object> map = ThreadLocalUtil.get();
        Integer uId = (Integer) map.get("id");
        return categoryMapper.selectAll(uId);
    }

    @Override
    public List<Category> selectAll2() {
        return categoryMapper.selectAll2();
    }

    @Override
    public Category selectOneById(Integer id) {
        Category c = categoryMapper.selectOneById(id);
        return c;
    }

    @Override
    public void update(Category category) {
        categoryMapper.update(category);
    }

    @Override
    public void delete(Integer id) {
        categoryMapper.delete(id);
    }


    //前端热门分类
    @Override
    public List<Category> select5Category() {
        //统计每种分类的数据总数
        List<CategoryArticleCount> categoryArticleCount = articleMapper.selectArticleByCategory();

        HashMap<Integer, Integer> hm = new HashMap<>();

        for (CategoryArticleCount cac : categoryArticleCount) {
            //System.out.println(cac);
            Integer totalCount = cac.getTotalCount();
            Integer id = cac.getId();
            hm.put(id , totalCount);
        }

        Set<Map.Entry<Integer, Integer>> entries = hm.entrySet();
        //存方键值对到一个链表中
        ArrayList<Map.Entry<Integer, Integer>> list = new ArrayList<>(entries);
        //按照值进行排序要前5的数据
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        ArrayList<Integer> list1 = new ArrayList<>();
        int flag = 0;
        for (Map.Entry<Integer, Integer> entry : list) {
            list1.add(entry.getKey());
            flag++;
            if (flag == 10){
                break;
            }
        }
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.in("id", list1);
        return categoryMapper.selectList(wrapper);
    }
}
