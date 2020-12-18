package dougou.web.rest.controller;

import dougou.web.core.service.IUserService;
import dougou.web.rest.dto.SimpleResponse;
import dougou.web.rest.vo.UserLoginVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;


@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    IUserService userServiceImpl;
    @PostMapping
    public SimpleResponse<Boolean> login(@RequestBody @Validated UserLoginVo userLoginVo){
        userServiceImpl.getById()
        return new SimpleResponse<Boolean>(true);
    }
}
