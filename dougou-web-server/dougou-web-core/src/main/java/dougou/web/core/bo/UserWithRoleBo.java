package dougou.web.core.bo;

import lombok.Data;

import java.util.List;
@Data
public class UserWithRoleBo {
    private String username;
    private String password;
    private String nickName;
    private List<UserRoleBo> userRoles;

}
