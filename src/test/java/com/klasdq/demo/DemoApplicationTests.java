package com.klasdq.demo;

import com.klasdq.demo.domain.bean.TestUserBean;
import com.klasdq.demo.persistence.dao.TestUserDao;
import com.klasdq.demo.persistence.mybatis.SearchCondition;
import com.klasdq.demo.persistence.mybatis.SearchConditionGroup;
import com.klasdq.demo.persistence.mybatis.enums.SQLConnector;
import com.klasdq.demo.persistence.mybatis.enums.SQLOperator;
import com.klasdq.demo.persistence.qo.TestUserQo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

}
