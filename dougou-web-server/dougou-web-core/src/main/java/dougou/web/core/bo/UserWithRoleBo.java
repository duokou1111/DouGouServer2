package dougou.web.core.bo;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class UserWithRoleBo {
    private String username;
    private String password;
    private String nickName;
    private List<UserRoleBo> userRoles;

}
