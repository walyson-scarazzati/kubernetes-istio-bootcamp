package com.course.devops.blue.api;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/html")
@Tag(name = "DevOps-Blue custom API", description = "DevOps-Blue custom API")
public class DevopsBlueHtml {

  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;

  @Value("${devops.blue.html.hardcoded:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlConfigmapHardcoded;

  @Value("${devops.blue.html.color.background:#ffffff}")
  private String htmlConfigmapBgColor;

  @Value("${devops.blue.html.color.text:#000000}")
  private String htmlConfigmapTextColor;

  @Value("${devops.blue.html.text.one:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlConfigmapTextOne;

  @Value("${devops.blue.html.text.two:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlConfigmapTextTwo;

  @Value("${devops.blue.html.text.three:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlConfigmapTextThree;

  @Value("${devops.blue.html.text.four:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlConfigmapTextFour;

  @Value("${devops.blue.html.text.five:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlConfigmapTextFive;

  @Value("${devops.blue.html.text.six:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlConfigmapTextSix;

  @Value("${devops.blue.html.sealed-secret.config-file:Default text (k8s Configmap / Secret not loaded)}")
  private String htmlSealedSecretConfigFile;

  @GetMapping(value = "/configmap-secret", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> fromConfigmapSecret()
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
    var dataModel = new HashMap<String, Object>();
    var out = new StringWriter();

    dataModel.put("htmlConfigmapHardcoded", htmlConfigmapHardcoded);
    dataModel.put("htmlConfigmapBgColor", htmlConfigmapBgColor);
    dataModel.put("htmlConfigmapTextColor", htmlConfigmapTextColor);
    dataModel.put("htmlConfigmapTextOne", htmlConfigmapTextOne);
    dataModel.put("htmlConfigmapTextTwo", htmlConfigmapTextTwo);
    dataModel.put("htmlConfigmapTextThree", htmlConfigmapTextThree);
    dataModel.put("htmlConfigmapTextFour", htmlConfigmapTextFour);
    dataModel.put("htmlConfigmapTextFive", htmlConfigmapTextFive);
    dataModel.put("htmlConfigmapTextSix", htmlConfigmapTextSix);

    var template = freeMarkerConfigurer.getConfiguration().getTemplate("html-configmap-secret.ftlh");

    template.process(dataModel, out);

    return ResponseEntity.ok().body(out.toString());
  }

  @GetMapping(value = "/sealed-secret", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> fromSealedSecret()
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
    var dataModel = new HashMap<String, Object>();
    var out = new StringWriter();

    dataModel.put("htmlSealedSecretConfigFile", htmlSealedSecretConfigFile);

    var template = freeMarkerConfigurer.getConfiguration().getTemplate("html-sealed-secret.ftlh");

    template.process(dataModel, out);

    return ResponseEntity.ok().body(out.toString());
  }

}