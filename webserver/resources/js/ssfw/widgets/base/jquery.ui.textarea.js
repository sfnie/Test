/**
 * author : qsyan
*/
(function ($) {
	
    $.widget("ui.textarea", $.ui.inputText, {
        options : {
            width : null,
            height: null
        },
        _create : function () {
            this._super();
            var width = this.options.width,
                height = this.options.height,
                pxIndex = -1;
            if(this.element.attr('width')){
                var customWidth = this.element.attr('width');
                pxIndex = customWidth.indexOf('px');
                if(pxIndex != -1){
                    customWidth = customWidth.substring(0,pxIndex);
                }
                width = customWidth;
            }
            if(this.element.attr('height')){
                var customHeight = this.element.attr('height');
                pxIndex = customHeight.indexOf('px');
                if(pxIndex != -1){
                    customHeight = customHeight.substring(0,pxIndex);
                }
                height = customHeight;
            }
            this.container.css({
                width : width ? width : '100%',
                height: height ? height : '100%'
            });
            var textarea = this.textarea = this.element.appendTo(this.container);
            textarea.css({
                outline:'none',
                width : width ? width - 4 : '99.5%',
                height: height ? height - 4 : '99.5%'
            });
            textarea.css('border','none');
            textarea.css('resize','none');
            textarea.attr('class',this.element.attr('class')).data('textarea',this);
            textarea.prop('readonly',this.element.prop('readonly'));
        },
        getText : function (){
            return this.textarea.val();
        },
        showView : function (){
            this._super();
            this.textarea.prop('readonly',true);
        },
        showEdit : function (){
            this._super();
            if(!this.readonly){
                     this.textarea.prop('readonly',false);
             }
        }
    });
})(jQuery);
