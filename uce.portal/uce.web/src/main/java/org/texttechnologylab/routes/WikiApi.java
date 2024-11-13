package org.texttechnologylab.routes;

import com.google.gson.Gson;
import freemarker.template.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.texttechnologylab.*;
import org.texttechnologylab.exceptions.ExceptionUtils;
import org.texttechnologylab.models.viewModels.wiki.AnnotationWikiPageViewModel;
import org.texttechnologylab.services.PostgresqlDataInterface_Impl;
import org.texttechnologylab.services.WikiService;
import spark.ModelAndView;
import spark.Route;

import java.util.HashMap;

public class WikiApi {

    private static final Logger logger = LogManager.getLogger();
    private ApplicationContext context = null;
    private PostgresqlDataInterface_Impl db = null;
    private Configuration freemakerConfig = Configuration.getDefaultConfiguration();
    private WikiService wikiService = null;

    public WikiApi(ApplicationContext serviceContext, Configuration freemakerConfig) {
        this.freemakerConfig = freemakerConfig;
        this.context = serviceContext;
        this.wikiService = serviceContext.getBean(WikiService.class);
        this.db = serviceContext.getBean(PostgresqlDataInterface_Impl.class);
    }

    public Route getAnnotationPage = ((request, response) -> {
        var model = new HashMap<String, Object>();

        try {
            var languageResources = LanguageResources.fromRequest(request);

            var wid = ExceptionUtils.tryCatchLog(() -> request.queryParams("wid"),
                    (ex) -> logger.error("The WikiView couldn't be generated - id missing.", ex));
            var coveredText = ExceptionUtils.tryCatchLog(() -> request.queryParams("covered"),
                    (ex) -> logger.error("The WikiView couldn't be generated - covered text missing.", ex));

            if (wid == null || !wid.contains("-") || coveredText == null || coveredText.isEmpty()) {
                model.put("information", languageResources.get("missingParameterError"));
                return new CustomFreeMarkerEngine(this.freemakerConfig).render(new ModelAndView(model, "defaultError.ftl"));
            }

            // Determine the type. A wikiID always has the following format: <type>-<model_id>
            var splited = wid.split("-");
            var type = splited[0];
            var id = Long.parseLong(splited[1]);

            if(type.equals("NE")){
                // We generate a NER annotation view
                var xd = "";
            } else if(type.equals("TP") || type.equals("TD")){
                // TP = TopicPage TD = TopicDocument
                var viewModel = wikiService.buildTopicAnnotationWikiPageViewModel(id, type, coveredText);
                model.put("vm", viewModel);
                return new CustomFreeMarkerEngine(this.freemakerConfig).render(new ModelAndView(model, "/wiki/pages/topicAnnotationPage.ftl"));
            } else{
                // The type part of the wikiId was unknown. Throw an error.
                model.put("information", languageResources.get("missingParameterError"));
                return new CustomFreeMarkerEngine(this.freemakerConfig).render(new ModelAndView(model, "defaultError.ftl"));
            }

        } catch (Exception ex) {
            logger.error("Error getting a wiki page for an annotation - best refer to the last logged API call " +
                    "with id=" + request.attribute("id") + " to this endpoint for URI parameters.", ex);
            return new CustomFreeMarkerEngine(this.freemakerConfig).render(new ModelAndView(null, "defaultError.ftl"));
        }

        return new CustomFreeMarkerEngine(this.freemakerConfig).render(new ModelAndView(model, "/wiki/pages/annotationPage.ftl"));
    });

}
