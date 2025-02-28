let LayeredSearchHandler = (function () {

    LayeredSearchHandler.prototype.layers = {1: []};
    LayeredSearchHandler.prototype.searchId = "";

    function LayeredSearchHandler() {
    }

    LayeredSearchHandler.prototype.init = function () {
        this.addNewLayer(1);
        this.searchId = generateUUID().toString().replaceAll("-", "");
    }

    LayeredSearchHandler.prototype.setLayerIsLoading = function (depth, isLoading) {
        let $layer = $('.layered-search-builder-container .layer-container[data-depth="' + depth + '"]');
        if (isLoading) $layer.find('.load').removeClass('hidden');
        else $layer.find('.load').addClass('hidden');
    }

    LayeredSearchHandler.prototype.addNewLayerAtEnd = function () {
        const depth = Object.keys(this.layers).length + 1;
        this.addNewLayer(depth);
    }

    LayeredSearchHandler.prototype.addNewLayer = function (depth) {
        let $template = $('.layered-search-builder-container .layer-template').clone();
        $template.find('.layer-container').attr('data-depth', depth);
        $template.find('.depth-label').html(depth);
        $('.layered-search-builder-container .layers-container').append($template.html());
        this.layers[depth] = [];
    }

    LayeredSearchHandler.prototype.addNewSlot = function ($btn) {
        const type = $btn.data('type');
        const depth = parseInt($btn.closest('.layer-container').data('depth'));
        const $slot = $btn.closest('.empty-slot');
        $slot.get(0).style.maxWidth = '100px';

        // Clone the template, set it up with ids and whatnot and add it to UI and our layers dictionary
        const $htmlTemplate = $('.layered-search-builder-container .slot-templates .template-' + type).clone();
        $htmlTemplate.attr('data-id', generateUUID());
        $btn.closest('.layer').prepend($htmlTemplate);
        this.layers[depth].push($htmlTemplate);
        $btn.closest('.choose-layer-popup').toggle(50);
        this.markLayersAsDirty(depth);
    }

    LayeredSearchHandler.prototype.applyLayerSearch = async function (depth) {
        if (!this.layers[depth]) return;
        let applicableDepths = Object.keys(this.layers).filter(d => d <= depth);
        let applicableLayers = [];

        applicableDepths.forEach((d) => {
            this.setLayerIsLoading(d, true);
            let slots = this.layers[d];

            // Every slot has information about a queryable filter for our database. We pass
            // this information onto our backend and let it apply it, see how it goes.
            let slotDtos = [];
            for (let i = 0; i < slots.length; i++) {
                let $slot = slots[i];
                const value = $slot.find('.slot-value').val();
                const type = $slot.data('type');
                slotDtos.push({type: type, value: value});
            }
            let layer = {
                depth: d,
                count: -1,
                slots: slotDtos
            }
            applicableLayers.push(layer);
        })

        $.ajax({
            url: "/api/search/layered",
            type: "POST",
            data: JSON.stringify({
                searchId: this.searchId,
                layers: JSON.stringify(applicableLayers),
            }),
            contentType: "application/json",
            success: (response) => {
                this.updateLayerResults(JSON.parse(response));
            },
            error: function (xhr, status, error) {
                showMessageModal("Searched Layer Error", xhr.responseText);
            }
        }).always(() => {
            applicableDepths.forEach((d) => this.setLayerIsLoading(d, false));
        });
    }

    LayeredSearchHandler.prototype.markLayersAsDirty = function (depth) {
        let applicableDepths = Object.keys(this.layers).filter(d => d >= depth);
        applicableDepths.forEach((d) => {
            let $layer = $('.layers-container .layer-container[data-depth="' + d + '"]');
            let $metadata = $layer.find('.layer-metadata-container');
            $metadata.find('.document-hits').html('?');
            $metadata.find('.page-hits').html('?');
            $metadata.find('.apply-layer-btn').removeClass('applied');
        });
    }

    LayeredSearchHandler.prototype.updateLayerResults = function (layerResults) {
        for (let i = 0; i < layerResults.length; i++) {
            let curLayer = layerResults[i];
            let $uiLayer = $('.layers-container .layer-container[data-depth="' + curLayer.depth + '"]');
            let $metadata = $uiLayer.find('.layer-metadata-container');
            $metadata.find('.document-hits').html(curLayer.documentHits);
            $metadata.find('.page-hits').html(curLayer.pageHits);
            $metadata.find('.apply-layer-btn').addClass('applied');
        }
    }

    return LayeredSearchHandler;
}());

function getNewLayeredSearchHandler() {
    return new LayeredSearchHandler();
}

/**
 * Triggers when a new layer with a certain type is being created.
 */
$('body').on('click', '.layered-search-builder-container .choose-layer-popup a', function () {
    window.layeredSearchHandler.addNewSlot($(this));
})

/**
 * Triggers when we apply a layer filter.
 */
$('body').on('click', '.layered-search-builder-container .apply-layer-btn', async function () {
    const depth = $(this).closest('.layer-container').data('depth');
    window.layeredSearchHandler.applyLayerSearch(parseInt(depth));
})

/**
 * Triggers when we change any slot of a layer.
 */
$('body').on('change', '.layered-search-builder-container .layer .slot input', async function () {
    const depth = $(this).closest('.layer-container').data('depth');
    window.layeredSearchHandler.markLayersAsDirty(parseInt(depth));
})

/**
 * Triggers when we want to add a new layer.
 */
$('body').on('click', '.layered-search-builder-container .add-new-layer-btn', async function () {
    window.layeredSearchHandler.addNewLayerAtEnd();
})

$(document).ready(function () {
    window.layeredSearchHandler = getNewLayeredSearchHandler();
    window.layeredSearchHandler.init();
})