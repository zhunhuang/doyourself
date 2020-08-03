package demo.server;

import com.nolan.learn.rpc.RpcFrameWork;
import demo.sdk.HelloService;

import java.io.IOException;

/**
 * description:
 *
 * @author zhunhuang, 2020/8/3
 */
public class Server {

    public static void main(String[] args) throws IOException {
        // server端，实现服务，暴露服务
        HelloService helloService = new HelloServiceImpl();

        // 暴露服务
        RpcFrameWork.expose(helloService, 9501);
    }

    public static class HelloServiceImpl implements HelloService {

        @Override
        public String sayHello(String name) {
            System.out.println("收到客户端调用请求，请求参数： " + name);
            return "你好，" + name + ", 我是RPC的服务端的返回结果";
        }
    }
}
