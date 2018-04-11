package com.shiyanlou.spring.SpringAuto;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.shiyanlou.spring.services.CustomerService;

public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext context = 
        new ClassPathXmlApplicationContext(new String[] {"SpringFiltering.xml"});

        CustomerService cust = (CustomerService)context.getBean("customerService");
        System.out.println(cust);

    }
}