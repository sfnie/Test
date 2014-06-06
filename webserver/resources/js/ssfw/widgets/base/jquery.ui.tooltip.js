/**
 * author : qsyan 
 */
(function($) {

    $.widget("ui.tooltip", $.ui.tooltip, {
        _create : function() {
            this._on({
                mouseover : "open"
            });
            this.tooltips = {};
        }
    });
})(jQuery);
