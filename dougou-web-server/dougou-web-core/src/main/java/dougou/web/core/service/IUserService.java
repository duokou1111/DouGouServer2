package dougou.web.core.service;

import dougou.web.core.bo.UserRoleBo;
import dougou.web.core.bo.UserWithRoleBo;
import dougou.web.core.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import dougou.web.core.ibo.AddUserIbo;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zihan.wu
 * @since 2020-12-19
 */
@Component
public interface IUserService extends IService<User> {
    UserWithRoleBo getUserWithRoleByUsername(String username);
    Boolean addUser(AddUserIbo addUserIbo);
}
