package com.alirizakaygusuz.gymcrm.security.self;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class SelfServiceAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Advisor advisor;
    private final List<String> targetBeans = new ArrayList<>();

    public SelfServiceAnnotationBeanPostProcessor(SelfServiceAuthenticationInterceptor interceptor) {
        var pointcut = new StaticMethodMatcherPointcut() {
            private final AnnotationMethodMatcher matcher = new AnnotationMethodMatcher(SelfService.class);

            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return matcher.matches(method, targetClass);
            }
        };
        this.advisor = new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SelfService.class)) {
                targetBeans.add(beanName);
                break;
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (targetBeans.contains(beanName)) {
            var proxyFactory = new ProxyFactory(bean);
            proxyFactory.setProxyTargetClass(true);  // CGLIB proxy
            proxyFactory.addAdvisor(advisor);
            return proxyFactory.getProxy();
        }
        return bean;
    }
}