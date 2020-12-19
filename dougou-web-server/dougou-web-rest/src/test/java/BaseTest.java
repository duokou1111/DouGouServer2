import dougou.web.rest.WebServer7071;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebServer7071.class})
public class BaseTest {

    @Before
    public void init() {
        System.out.println("开始测试-----------------\n\n");
    }

    @After
    public void after() {
        System.out.println("\n\n测试结束-----------------");
    }

}
