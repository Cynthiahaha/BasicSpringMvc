package com.study.controller;

import com.study.anotation.AutoWire;
import com.study.anotation.RequestMapping;
import com.study.anotation.RequestParam;
import com.study.anotation.controller;
import com.study.service.OmsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

@controller("/main")
public class cuiController {

    @AutoWire("com.study.service.OmsServiceImpl")
    private OmsService OmsService;


    @RequestMapping("/index")
    public void getIndex(HttpServletResponse response){
        String index="index";
        String OrderId;
        OrderId=OmsService.BuildOrder(index);
        try {
            PrintWriter printWriter=response.getWriter();
            printWriter.write(OrderId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
