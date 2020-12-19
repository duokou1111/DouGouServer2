package dougou.web.core.mapper;

import dougou.web.core.bo.UserWithRoleBo;
import dougou.web.core.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zihan.wu
 * @since 2020-12-19
 */
public interface UserMapper extends BaseMapper<User> {
    UserWithRoleBo getUserWithRoleByUsername(String username);
}
