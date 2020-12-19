package dougou.web.rest.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetUserInfoDto {
    private String Username;
    private String password;
    private String nickName;
    private List<Integer> roleIds;
}
