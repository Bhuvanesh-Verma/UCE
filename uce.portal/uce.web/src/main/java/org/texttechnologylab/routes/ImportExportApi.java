package org.texttechnologylab.routes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.texttechnologylab.Importer;
import org.texttechnologylab.exceptions.DatabaseOperationException;
import org.texttechnologylab.exceptions.ExceptionUtils;
import org.texttechnologylab.services.PostgresqlDataInterface_Impl;
import spark.Route;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ImportExportApi {

    private PostgresqlDataInterface_Impl db;
    private ApplicationContext serviceContext;

    private static final Logger logger = LogManager.getLogger(PostgresqlDataInterface_Impl.class);

    public ImportExportApi(ApplicationContext serviceContext) {
        this.serviceContext = serviceContext;
        this.db = serviceContext.getBean(PostgresqlDataInterface_Impl.class);
    }

    public Route uploadUIMA = ((request, response) -> {
        try {
            // First, we need to know which corpus this document should be added to.
            var corpusId = ExceptionUtils.tryCatchLog(
                    () -> Long.parseLong(new String(request.raw().getPart("corpusId").getInputStream().readAllBytes(), StandardCharsets.UTF_8)),
                    (ex) -> logger.error("Error getting the corpusId this document should be added to. Aborting.", ex));
            if (corpusId == null)
                return "Parameter corpusId didn't exist. Without it, the document cannot be uploaded.";

            var corpus = ExceptionUtils.tryCatchLog(
                    () -> db.getCorpusById(corpusId),
                    (ex) -> logger.error("Couldn't fetch corpus when uploading new document to corpusId " + corpusId, ex));
            if (corpus == null)
                return "Corpus with id " + corpusId + " wasn't found in the database; can't upload document.";

            var importer = new Importer(this.serviceContext);
            try (var input = request.raw().getPart("file").getInputStream()) {
                // Import the doc in the background
                var importFuture = CompletableFuture.runAsync(() -> {
                    try {
                        importer.storeUploadedXMIToCorpusAsync(input, corpus);
                    } catch (DatabaseOperationException e) {
                        throw new RuntimeException(e);
                    }
                });
                importFuture.get();
                response.status(200);
                return "File uploaded successfully!";
            }
        } catch (Exception e) {
            response.status(500);
            return "Error uploading a file: " + e.getMessage();
        }
    });

}
