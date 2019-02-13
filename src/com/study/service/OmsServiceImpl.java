package com.study.service;

import com.study.anotation.service;

@service
public class OmsServiceImpl implements OmsService {
    Integer cout=0;

    @Override
    public String BuildOrder(String orderID) {
        String newOrder=orderID+cout;
        cout++;
        return newOrder;
    }
}
