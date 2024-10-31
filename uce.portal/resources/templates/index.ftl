<!DOCTYPE html>
<html lang="${languageResource.getDefaultLanguage()}">
<head>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
          crossorigin="anonymous">
    <link
            href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"
            rel="stylesheet">
    <style>
        <#include "css/site.css">
        <#include "css/simple-loader.css">
        <#include "css/search-redesign.css">
        <#include "*/css/corpus-universe.css">
    </style>
    <script src="https://kit.fontawesome.com/b0888ca2eb.js"
            crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script
            src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js"
            integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
            crossorigin="anonymous"></script>
    <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js"
            integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
            crossorigin="anonymous"></script>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>

    <!-- For corpus universe three.js -->
    <script type="importmap">
        {
          "imports": {
            "three": "https://unpkg.com/three@v0.161.0/build/three.module.js",
            "three/addons/": "https://unpkg.com/three@v0.161.0/examples/jsm/"
          }
        }
    </script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.4.0/gsap.min.js"></script>
    <script src="https://requirejs.org/docs/release/2.3.5/minified/require.js"></script>
    <!--<script src="https://unpkg.com/@tweenjs/tween.js@^20.0.0/dist/tween.umd.js"></script>-->
    <!-- For corpus universe three.js -->

    <title>${title}</title>
</head>

<body>

<div class="site-container">

    <nav class="position-relative">

        <div class="container-fluid flexed align-items-center justify-content-around">
            <img class="mb-0 logo" src="${logo}">

            <div class="flexed align-items-center nav-container">
                <div class="flexed align-items-center nav-buttons">
                    <a class="btn text" data-id="team">${languageResource.get("team")}</a>
                    <a class="btn text selected-nav-btn" data-id="search">${languageResource.get("search")}</a>
                    <a class="btn text" data-id="contact">${languageResource.get("contact")}</a>
                </div>
                <select class="form-control bg-light rounded-0 color-prime border-right-0 large-font switch-language-select">
                    <option data-lang="de-DE">Deutsch</option>
                    <option data-lang="en-EN">Englisch</option>
                </select>
            </div>
        </div>
    </nav>

    <div class="sr-query-builder-include">
    </div>

    <div class="corpusUniverse-content-container">

        <div class="view pt-5" data-id="search">

            <div class="flexed align-items-stretch search-header container p-0">
                <div class="flexed align-items-center h-100 position-relative" style="z-index: 2">
                    <a class="btn btn-light rounded-0 open-corpus-inspector-btn" data-trigger="hover"
                       data-toggle="popover" data-placement="top"
                       data-content="${languageResource.get("openCorpus")}">
                        <i class="fas fa-globe xlarge-font mr-2 ml-2 text-dark"></i>
                    </a>
                    <select class="form-control" id="corpus-select" aria-label="Default select example">
                        <#list corpora as corpusVm>
                            <option data-id="${corpusVm.getCorpus().getId()}"
                                    data-hasbiofid="${corpusVm.getCorpusConfig().getAnnotations().getTaxon().isBiofidOnthologyAnnotated()?c}"
                                    data-hasembeddings="${corpusVm.getCorpusConfig().getOther().isEnableEmbeddings()?c}"
                                    data-hastopicdist="${corpusVm.getCorpusConfig().getOther().isAvailableOnFrankfurtUniversityCollection()?c}"
                                    data-hasragbot="${corpusVm.getCorpusConfig().getOther().isEnableRAGBot()?c}"
                                    data-sparqlalive="${isSparqlAlive?c}"
                                    data-hassr="${corpusVm.getCorpusConfig().getAnnotations().isSrLink()?c}">${corpusVm.getCorpus().getName()}</option>
                        </#list>
                    </select>
                    <button class="btn open-sr-builder-btn" data-trigger="hover" data-toggle="popover"
                            data-placement="top"
                            data-content="${languageResource.get("openSrBuilder")}">
                        <i class="fas fa-project-diagram mr-1 ml-1"></i>
                    </button>
                </div>

                <!-- Search bar and menu -->
                <div class="w-100 position-relative">
                    <input type="text" class="search-input form-control large-font w-100"
                           placeholder="${languageResource.get("searchPlaceholder")}"/>
                    <div class="search-menu-div">
                        <div class="backdrop"></div>

                        <div style="z-index: 2; position:relative;">
                            <div class="search-history-div">
                            </div>
                            <div class="search-settings-div flexed align-items-center justify-content-around">
                                <!-- The data-ids are corresponding to the SearchLayer enum. Change them with care!! -->
                                <i class="text w-auto fab fa-searchengin mr-2 large-font"></i>
                                <div class="option" data-type="radio">
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" checked
                                               name="searchLayerRadioOptions"
                                               id="inlineRadio1" value="FULLTEXT">
                                        <label class="form-check-label color-prime small-font"
                                               for="inlineRadio1">Fulltext</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="searchLayerRadioOptions"
                                               id="inlineRadio2" value="NAMED_ENTITIES">
                                        <label class="form-check-label color-secondary small-font" for="inlineRadio2">Named-Entities</label>
                                    </div>
                                </div>
                                <div class="option">
                                    <label class="mb-0 w-100 color-gold small-font">Embedding</label>
                                    <input type="checkbox" data-id="EMBEDDINGS"/>
                                </div>
                                <div class="option" data-trigger="hover"
                                     data-toggle="popover" data-placement="top" data-html="true"
                                     data-content="${languageResource.get("kwicWarning")}">
                                    <label class="mb-0 w-100 color-dark small-font">KWIC</label>
                                    <input type="checkbox" data-id="KWIC"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <button class="btn btn-primary search-btn">
                    <i class="fas fa-search ml-1 mr-1"></i>
                </button>
            </div>

            <div class="position-relative">
                <#include "*/search/components/loader.ftl">
                <div class="search-result-container container-fluid position-relative">
                    <#include "*/landing-page.ftl" />

                </div>
            </div>

        </div>

        <div class="view display-none" data-id="team">
            ${languageResource.get("team")}
        </div>

        <div class="view display-none" data-id="contact">
            ${languageResource.get("contact")}
        </div>
    </div>

    <div class="corpus-inspector-include display-none">
    </div>

    <div class="ragbot-chat-include">
        <#include "*/ragbot/chatwindow.ftl"/>
    </div>

</div>
</body>

<footer>
    <div class="container p-3 text-light h-100 text-center flexed align-items-center justify-content-center">
        <h5 class="text-center m-0">Footer</h5>
    </div>
</footer>

<script type="module">
    <#include "js/corpusUniverse.js">
</script>

<script>
    <#include "js/site.js">
    <#include "js/language.js">
    <#include "js/search.js">
    <#include "js/keywordInContext.js">
</script>

</html>