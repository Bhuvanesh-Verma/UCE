<div class="wiki-page container">

    <!-- breadcrumbs -->
    <div class="mb-3">
        <#include "*/wiki/components/breadcrumbs.ftl">
    </div>

    <!-- metadata header -->
    <div>
        <#include "*/wiki/components/metadata.ftl">
    </div>

    <hr class="mt-2 mb-4"/>

    <!-- list the lemmas that form this named entity -->
    <div class="mt-3 mb-3 lemma-tree">
        <h6 class="w-100 text-center color-prime font-weight-bold">
            ${vm.getCoveredText()}
            <span class="text ml-1 small-font text">(${vm.getWikiModel().getType()})</span>
        </h6>
        <div class="tree">
            <#list vm.getLemmas() as lemma>
                <div class="mb-0 mr-1 ml-1 position-relative pt-1">
                    <label data-wid="${lemma.getWikiId()}" data-wcovered="${lemma.getValue()}"
                           class="mb-0 open-wiki-page add-wiki-logo text">
                        ${lemma.getValue()}
                    </label>
                </div>
            </#list>
        </div>
    </div>

    <!-- the document this is from -->
    <div class="mt-4 mb-3 w-100 p-0 m-0 justify-content-center flexed align-items-start">
        <div class="document-card w-100">
            <#assign document = vm.getDocument()>
            <#assign searchId = "">
            <#include '*/search/components/documentCardContent.ftl' >
        </div>
    </div>

    <!-- kwic view -->
    <div class="mt-3">
        <#include "*/wiki/components/kwic.ftl">
    </div>

    <!-- similar documents as in the sense of where this ne also exists -->
    <#if (vm.getSimilarDocuments()?has_content) && (vm.getSimilarDocuments()?size > 0)>
        <div class="mt-4">
            <#assign similarDocuments = vm.getSimilarDocuments()>
            <h6 class="text-center">${languageResource.get("foundInDocuments")}</h6>
            <div class="similar-documents">
                <div class="row m-0 p-0">
                    <#list similarDocuments as document>
                        <div class="col-md-4 m-0 p-2">
                            <div class="item">
                                <div class="p-2">
                                    <h6 class="color-prime mb-0 text-center flexed align-items-center justify-content-center">
                                        ${document.getDocumentTitle()}
                                    </h6>
                                </div>
                                <hr class="mb-1 mt-1"/>
                                <div class="p-1">
                                    <p class="font-italic mb-0 small-font text block-text normal-line-height">
                                        ${document.getFullTextSnippet(30)}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </#if>
</div>
