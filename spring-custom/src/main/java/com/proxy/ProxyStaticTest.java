package com.proxy;

/*
其实每个模式名称就表明了该模式的作⽤，代理模式就是多⼀个代理类出来，替原对象进⾏⼀些操作。
代理⼜分为动态代理和静态代理。
静态代理的重点：
  需要为源类⼿动编写⼀个代理类
  代理类和源类实现同⼀接⼝。
  代理对象持有源对象的引⽤。
静态代理的缺点：
  会产⽣⼤量的代理类。
代理模式的应⽤场景：
如果已有的⽅法在使⽤的时候需要对原有的⽅法进⾏改进，此时有两种办法：
1. 修改原有的⽅法来适应。这样违反了“对扩展开放，对修改关闭”的原则。
2. 就是采⽤⼀个代理类调⽤原有的⽅法，且对产⽣的结果进⾏控制。这种⽅法就是代理模式。
使⽤代理模式，可以将功能划分的更加清晰，有助于后期维护！
 */
public class ProxyStaticTest {
    public static void main(String[] args) {
        Sourceable source = new Proxy();
        source.method();
    }
}

interface Sourceable {
    public void method();
}

class Source implements Sourceable {

    @Override
    public void method() {
        System.out.println("the original method!");
    }
}

class Proxy implements Sourceable {
    // 持有源对象的引⽤
    private Source source;

    public Proxy() {
        super();
        this.source = new Source();
    }

    @Override
    public void method() {
        before();
        source.method();
        atfer();
    }

    private void atfer() {
        System.out.println("after proxy!");
    }

    private void before() {
        System.out.println("before proxy!");
    }
}