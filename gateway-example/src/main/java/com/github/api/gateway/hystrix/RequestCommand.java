package com.github.api.gateway.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

/**
 * hystrix服务熔断
 */
public class RequestCommand extends HystrixCommand<String> {
    protected RequestCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("request")));
    }


    @Override
    protected String run() throws Exception {
        if(Math.random() > 0.9) {
            throw new RuntimeException("错误");
        }
        return "Sucess";
    }

    @Override
    protected String getFallback() {
        return "Execute Except";
    }

    public static void main(String[] args) {
        while (true) {
            String ret = new RequestCommand().execute();
            System.out.println(ret);
        }
    }
}
