package com.klasdq.demo.web.controller;

import com.klasdq.demo.common.utils.BeanMapper;
import com.klasdq.demo.domain.bean.TestUserBean;
import com.klasdq.demo.persistence.mybatis.*;
import com.klasdq.demo.persistence.mybatis.enums.SQLConnector;
import com.klasdq.demo.persistence.mybatis.enums.SQLOperator;
import com.klasdq.demo.persistence.mybatis.enums.SortDirection;
import com.klasdq.demo.persistence.qo.TestUserQo;
import com.klasdq.demo.service.TestUserService;
import com.klasdq.demo.web.vo.TestUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Api(tags = "测试用户接口",description = "增加删除修改查询")
@RestController
public class TestUserController {
    @Autowired
    private TestUserService testUserService;

    @ApiOperation(value = "保存用户信息",notes = "传入用户消息")
    @RequestMapping(value = "save",method = RequestMethod.POST)
    public int testSave(@ApiParam(name = "vo",value = "用户对象") @RequestBody TestUserVo vo){
        return testUserService.save(BeanMapper.copyProperties(vo,TestUserBean.class));
    }

    @ApiOperation(value = "批量保存用户信息",notes = "批量保存用户信息")
    @RequestMapping(value = "save-all",method = RequestMethod.POST)
    public int testSaveAll(@ApiParam(name = "vos",value = "用户对象List") @RequestBody List<TestUserVo> vos){
        List<TestUserBean> beans = BeanMapper.copyList(vos,TestUserBean.class);
        System.out.println("------------testSaveAll start--------------");
        beans.stream().forEach(bean->{
            System.out.println(bean);
        });
        System.out.println("------------testSaveAll end--------------");
        return testUserService.saveAll(beans);
    }

    @ApiOperation(value = "获取用户信息",notes = "根据id获取用户信息")
    @RequestMapping(value = "get",method = RequestMethod.POST)
    public TestUserVo testGet(@ApiParam(name = "id",value = "人员id") long id){
        return BeanMapper.mapper(testUserService.get(id),TestUserVo.class);
    }

    @ApiOperation(value = "查找用户",notes = "根据条件查找用户")
    @RequestMapping(value = "find",method = RequestMethod.POST)
    public TestUserVo testFind(@ApiParam(name = "qo",value = "查询条件")@RequestBody TestUserQo qo){
        return BeanMapper.mapper(testUserService.find(qo),TestUserVo.class);
    }

    @ApiOperation(value = "查看是否存在用户",notes = "根据条件查找用户是否存在")
    @RequestMapping(value = "has-content",method = RequestMethod.POST)
    public boolean testHas(@ApiParam(name = "qo",value = "查询条件")@RequestBody TestUserQo qo){
        return testUserService.has(qo);
    }

    @ApiOperation(value = "查找用户",notes = "根据条件查找用户")
    @RequestMapping(value = "query-list",method = RequestMethod.POST)
    public List<TestUserVo> queryList(@ApiParam(name = "qo",value = "查询条件")@RequestBody TestUserQo qo){
        return testUserService.getList(qo,TestUserVo.class);
    }

    @ApiOperation(value = "用户分页",notes = "根据条件分页获取用户")
    @RequestMapping(value = "page",method = RequestMethod.POST)
    public Page<TestUserVo> testPage(@ApiParam(name = "pageParam",value = "分页条件")@RequestBody PageParam<TestUserQo> pageParam){
//        Page<TestUserBean> page = testUserService.getPage(pageParam);
//        Page<TestUserVo> voPage = page.map(TestUserVo.class);

        TestUserQo testUserQo = pageParam.getCondition();
        //如果查询对象中没有排序规则，那就默认以创建时间排序
        if (testUserQo!=null){
            if (!testUserQo.hasSort()){
                testUserQo.addOrder("create_dt", SortDirection.DESC);
            }
        }
        Page<TestUserVo> voPage = testUserService.getPage(pageParam,TestUserVo.class);
        return voPage;
    }

    @ApiOperation(value = "修改用户信息",notes = "动态修改用户信息，只修改传入的信息")
    @RequestMapping(value = "dynamic-update",method = RequestMethod.POST)
    public int testDynamicUpdate(@ApiParam(name = "vo",value = "用户信息") @RequestBody TestUserVo vo){
        return  testUserService.dynamicUpdate(BeanMapper.copyProperties(vo,TestUserBean.class));
    }

    @ApiOperation(value = "删除用户",notes = "根据id删除用户")
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public int testDelete(@ApiParam(name = "id",value = "用户id")long id){
        return testUserService.delete(id);
    }

    @ApiOperation(value = "批量删除用户",notes = "传入id集合批量删除用户")
    @RequestMapping(value = "delete-ids",method = RequestMethod.POST)
    public int testDeleteIds(@ApiParam(name = "ids",value = "id集合") @RequestBody Collection<Long> ids){
        ids.stream().forEach(id->{
            System.out.println(id);
        });
        return testUserService.deleteByIds(ids);
    }

    @GetMapping("page2")
    public Page<TestUserVo> testUserVoPage(){
        TestUserQo qo = new TestUserQo();

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new SearchCondition("age", SQLOperator.GREATER,40));
        conditions.add(new SearchCondition("username",SQLOperator.LEFT_LIKE,"张"));
        SearchConditionGroup searchConditionGroup = new SearchConditionGroup(SQLConnector.AND,SQLConnector.OR,conditions);
        List<SearchConditionGroup> groups = new ArrayList<>();
        groups.add(searchConditionGroup);

        qo.setSearchConditionGroups(groups);
        qo.addOrder("create_dt",SortDirection.DESC);

        PageParam<TestUserQo> param = new PageParam<>(2,2,qo);

        return testUserService.getPage(param,TestUserVo.class);
    }
}
