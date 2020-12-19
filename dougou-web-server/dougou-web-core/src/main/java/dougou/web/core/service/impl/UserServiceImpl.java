package dougou.web.core.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dougou.web.core.Exception.BussinessException;
import dougou.web.core.bo.UserWithRoleBo;
import dougou.web.core.entity.User;
import dougou.web.core.entity.UserRole;
import dougou.web.core.ibo.AddUserIbo;
import dougou.web.core.mapper.UserMapper;
import dougou.web.core.service.IUserRoleService;
import dougou.web.core.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zihan.wu
 * @since 2020-12-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    UserMapper userMapper;
    @Autowired
    IUserRoleService UserRoleServiceImpl;
    @Override
    public UserWithRoleBo getUserWithRoleByUsername(String username) {
        return userMapper.getUserWithRoleByUsername(username);
    }

    @Override
    @Transactional(rollbackFor ={Exception.class} )
    public Boolean addUser(AddUserIbo addUserIbo) {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getUsername,addUserIbo.getUsername());
        if(Objects.nonNull(getOne(wrapper))){
            return false;
        }
        User user = new User();
        user.setNickName(addUserIbo.getNickName());
        user.setPassword(addUserIbo.getPassword());
        user.setUsername(addUserIbo.getUsername());
        Boolean res = save(user);
        if(res){
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(addUserIbo.getRoleId());
            if(UserRoleServiceImpl.save(userRole)){
                return true;
            }else{
                throw new BussinessException("用户创建失败");
            }
        }
        return res;
    }
}
