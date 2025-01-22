package com.yuramoroz.spring_crm_system;

import com.yuramoroz.spring_crm_system.config.AppConfig;
import com.yuramoroz.spring_crm_system.service.impl.TrainingServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 */
public class TrainingApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    }
}
