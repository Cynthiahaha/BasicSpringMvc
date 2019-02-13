package com.study.util;

import com.study.anotation.AutoWire;
import com.study.anotation.RequestMapping;
import com.study.anotation.controller;
import com.study.anotation.service;
import com.study.controller.cuiController;
import com.study.service.OmsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private List<String> classNames = new ArrayList<>();
    private Map<String, Object> beans = new HashMap<>();
    private Map<String, Object> requestMap = new HashMap<>();

    @Override//tomcat 启动的时候 实例化map,ioc
    public void init(ServletConfig config) throws ServletException {

        basePackageScan("com/study");
        doInstance();
        doAutoWire();
        doMappingHandler();

    }

    public void basePackageScan(String classPath) {
        String projectPath = this.getClass().getClassLoader().getResource("").getPath();
        if (!classPath.contains(projectPath)) {
            classPath = projectPath + classPath;
        }
        File file = new File(classPath);
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()) {
                basePackageScan(file1.getPath());
            } else {
                String base = file1.getPath().replace(projectPath, "");
                String name = base.replace("/", ".");
                classNames.add(name.replace(".class", ""));
            }
        }
    }

    public void doInstance() {
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(controller.class) || (clazz.isAnnotationPresent(service.class))) {
                    String BeanName = clazz.getName();
                    Object instance = clazz.newInstance();
                    beans.put(BeanName, instance);
                } else continue;

            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

    }

    public void doAutoWire() {

        for (Map.Entry<String, Object> stringObjectEntry : beans.entrySet()) {
            Object instance = stringObjectEntry.getValue();
            Class<?> clazz = instance.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(AutoWire.class)) {
                    AutoWire auto = field.getAnnotation(AutoWire.class);
                    Object bean = beans.get(auto.value());
                    field.setAccessible(true);
                    try {
                        field.set(instance, bean);
                        System.out.println(field.equals(bean));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    //地址和方法的映射
    public void doMappingHandler() {
        for (Map.Entry<String, Object> stringObjectEntry : beans.entrySet()) {
            Class clazz = stringObjectEntry.getValue().getClass();
            if (clazz.isAnnotationPresent(controller.class)) {
                controller controller = (controller) clazz.getAnnotation(controller.class);
                String path1 = controller.value();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        String path2 = path1 + requestMapping.value();
                        requestMap.put(path2, method);
                    }
                }
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String request = uri.replace(req.getContextPath(), "");
        Method method = (Method) requestMap.get(request);


        try {
            method.invoke(beans.get("com.study.controller.cuiController"), resp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}
