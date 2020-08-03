package demo.client;

import com.nolan.learn.rpc.RpcFrameWork;
import demo.sdk.HelloService;

/**
 * description:
 *
 * @author zhunhuang, 2020/8/3
 */
public class Client {

    public static void main(String[] args) {
        HelloService remoteHelloService = RpcFrameWork.refer(HelloService.class,"127.0.0.1",9501);

        String result = remoteHelloService.sayHello("nolan");
        System.out.println("调用server端返回结果：" + result);
    }
}
