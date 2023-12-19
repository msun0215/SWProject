package com.example.BoardDBRestAPIBySpring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Component
public class ThymeleafUtil {
    private static TemplateEngine templateEngine;

    @Autowired
    public ThymeleafUtil(TemplateEngine templateEngine){
        ThymeleafUtil.templateEngine=templateEngine;
    }

    public static String processTemplate(String templateName, Model model){
        Context context=new Context();
        model.asMap().forEach(context::setVariable);
        return templateEngine.process(templateName,context);
    }
}
