package com.klasdq.demo.service.impl;

import com.klasdq.demo.domain.bean.TestUserBean;
import com.klasdq.demo.persistence.dao.TestUserDao;
import com.klasdq.demo.service.TestUserService;
import org.springframework.stereotype.Service;

@Service
public class TestUserServiceImpl extends BaseServiceImpl<TestUserBean, TestUserDao> implements TestUserService {
}
