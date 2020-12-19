package dougou.web.core.ibo;

import lombok.Data;

@Data
public class AddUserIbo {
    private String username;
    private String password;
    private String nickName;
    private Integer roleId;
}
