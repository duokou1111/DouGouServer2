package dougou.web.rest.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RetriveUserVo {
    @Length(min = 4, max = 20, message = "用户名长度不正确")
    private String username;
}
