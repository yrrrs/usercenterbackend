package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.ErrorCode;
import com.example.exception.BussinessException;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.contant.UserContant.USER_LOGIN_STATE;

/**
* @author 27362
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-10-27 00:28:56
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private UserMapper userMapper;

    private static final String WATER = "yang" ;


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        if (userAccount.length() < 4){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        if (userPassword.length() < 6 || checkPassword.length() < 6){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"密码或校验密码过短");
        }
        // 账户不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");
        }
        // 密码与校验密码相同
        if (!userPassword.equals(checkPassword)){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        // 2.加密

        String hidePassword = DigestUtils.md5DigestAsHex((WATER + userPassword).getBytes());
        // 3.加入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(hidePassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BussinessException(ErrorCode.SYSTEM_ERROR,"存入数据库失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if (userAccount.length() < 4){
            return null;
        }
        if (userPassword.length() < 6){
            return null;
        }
        // 账户不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return null;
        }
        // 2.加密
        String hidePassword = DigestUtils.md5DigestAsHex((WATER + userPassword).getBytes());

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",hidePassword);
        User user1 = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user1 == null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3.用户脱敏
        User safetyUser = getSafetyUser(user1);

        // 4.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);



        return safetyUser;
    }

    /**
     * 用户脱敏
     */
    @Override
    public User getSafetyUser(User user1){
        if (user1 == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user1.getId());
        safetyUser.setUsername(user1.getUsername());
        safetyUser.setUserAccount(user1.getUserAccount());
        safetyUser.setAvatarUrl(user1.getAvatarUrl());
        safetyUser.setGender(user1.getGender());
        safetyUser.setPhone(user1.getPhone());
        safetyUser.setEmail(user1.getEmail());
        safetyUser.setUserRole(user1.getUserRole());
        safetyUser.setUserStatus(user1.getUserStatus());
        safetyUser.setCreateTime(user1.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




