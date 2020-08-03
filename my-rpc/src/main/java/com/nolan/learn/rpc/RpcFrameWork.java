package com.nolan.learn.rpc;

import com.sun.deploy.util.ReflectionUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * description: RPC 框架 API
 *
 * @author zhunhuang, 2020/8/3
 */
public class RpcFrameWork {

    public static void expose(final Object service, int port) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(port);
        new Thread(() -> {
            while (true) {
                try {
                    try (Socket accept = serverSocket.accept()) {
                        try (ObjectInputStream inputStream = new ObjectInputStream(accept.getInputStream())) {
                            String methodName = (String) inputStream.readObject();
                            Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                            Object[] arguments = (Object[]) inputStream.readObject();
                            try (ObjectOutputStream outputStream = new ObjectOutputStream(accept.getOutputStream())) {
                                try {
                                    Object invokeResult = ReflectionUtil.invoke(service,methodName,parameterTypes,arguments);
                                    outputStream.writeObject(invokeResult);
                                } catch (Exception e) {
                                    outputStream.writeObject(e);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static <T> T refer(final Class<T> service, final String hostName, final int port) {
        return (T) Proxy.newProxyInstance(
                service.getClassLoader(), new Class[]{service}, (proxy, method, args) -> {
                    try (Socket socket = new Socket(hostName, port)) {
                        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());) {
                            outputStream.writeObject(method.getName());
                            outputStream.writeObject(method.getParameterTypes());
                            outputStream.writeObject(args);
                            outputStream.flush();
                            try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                                Object result = inputStream.readObject();
                                if (result instanceof Throwable) {
                                    throw (Throwable) result;
                                }
                                return result;
                            }
                        }
                    }
                }
        );
    }


}
