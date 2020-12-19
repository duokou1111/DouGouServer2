import dougou.web.rest.controller.UserController;
import dougou.web.rest.dto.BaseResponse;
import dougou.web.rest.enums.ResponseCodeEnum;
import dougou.web.rest.vo.AddUserVo;
import dougou.web.rest.vo.RetriveUserVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserControllerTest extends BaseTest {
    @Autowired
    UserController userController;
    @Test
    public void RetriveUser(){
        RetriveUserVo retriveUserVo = new RetriveUserVo();
        retriveUserVo.setUsername("test");
        BaseResponse response = userController.retriveUser(retriveUserVo);
        System.out.println("response.toString() = " + response.toString());
        Assert.assertTrue(response.getCode().equals(ResponseCodeEnum.RESPONSE_CODE_SUCCESS.getCode()));
        retriveUserVo.setUsername("test1");
        response = userController.retriveUser(retriveUserVo);
        System.out.println("response.toString() = " + response.toString());
        Assert.assertTrue(response.getCode().equals(ResponseCodeEnum.RESPONSE_CODE_NULL_FAILURE.getCode()));
    }
    @Test
    public void Resgister(){
        AddUserVo addUserVo = new AddUserVo();
        addUserVo.setUsername("test1232");
        addUserVo.setNickName("test12");
        addUserVo.setPassword("test12");
        addUserVo.setNickName("test123");
        BaseResponse<Boolean> res = userController.registerUser(addUserVo);
        Assert.assertFalse(res.getData());
        addUserVo.setUsername("test"+System.currentTimeMillis());
        Assert.assertTrue(userController.registerUser(addUserVo).getData());
    }
}
