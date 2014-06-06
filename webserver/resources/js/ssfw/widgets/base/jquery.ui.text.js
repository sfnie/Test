/**
 * author : qsyan
*/
(function ($) {
	
    $.widget("ui.inputText", {
        options : {
            width : 130,
            readonly : false,
            emptyText : null,
            label : null,
            containerStyle : null,
            hintImage : null,
            hintTitle : null
        },
        _setOption: function(key,value) {
            if(key === 'containerStyle' && value){
            	if(value.substring(value.length - 1) !== ';'){
            		value += ';';
            	}
            	var oldCssText = this.container[0].style.cssText;
            	this.container[0].style.cssText = oldCssText += value;
            }else{
                this.options[key] = value;
            }
        },
        _create : function () {
            var element = this.element,that = this,
                priorWidth = element.attr('width'),
                elementStyle = element.attr('style'),
                originalProperty = this.originalProperty = { value : element.val(), 
            	readonly : element.prop('readonly')},
                hintImage = element.attr('hintImage'),
                hintTitle = element.attr('hintTitle');
             if(this.options.emptyText){
            	element.val(this.options.emptyText);
            }
            if(elementStyle !== undefined && elementStyle.indexOf('width') !== -1){
                priorWidth = element.css('width');
            }
            if(priorWidth){
                var pxIndex = priorWidth.indexOf('px');
                if(pxIndex != -1){
                   priorWidth = priorWidth.substring(0,pxIndex);   
                }
                originalProperty.width = priorWidth;
                this.options.width = priorWidth;
            }
            element.addClass('l-text-field');
            var container = this.container = $('<div class="comboContainer"/>');
            var containerStyle = element.attr('containerStyle');
            if(containerStyle){
            	this.options.containerStyle = containerStyle;
            }
            if(this.options.containerStyle){
            	container[0].style.cssText = this.options.containerStyle;
            }
            if(hintImage){
            	this.options.hintImage = hintImage;
            }
            if(hintTitle){
            	this.options.hintTitle = hintTitle;
            }
            if(this.options.hintImage){
            	var defaultHintImageWidth = 16;
            	var img = $('<img/>',{
            		 src : this.options.hintImage
            	}).css({'width' : defaultHintImageWidth,'height' : 16});
            	this.options.width - defaultHintImageWidth;
            	if(this.options.hintTitle){
            		img.attr('title',this.options.hintTitle);
            		img.tooltip();
            	}
            }
            if(this.options.width){
                element.css({
                    width : this.options.width - 3 + 'px'
                    });
                container.css({
                    width : this.options.width
                    });
            }
            container.css({
                display:'block',
                width : this.options.width + 'px'
            });
            element.wrap(container);
            this.container = $(element.parent()).bind('mouseleave' + this.eventNamespace,function (e){
            	 that.mouseenterComponent = false;
                 that._mouseleaveHandler(e);
            }).bind('mouseenter' + this.eventNamespace,function (e){
            	 that.mouseenterComponent = true;
                 that._mouseenterHandler(e);
            });
            if(img){
            	this.container.after(img).css('float','left');
            }
            this.readonly = element.attr('readonly') === undefined ? this.options.readonly 
            		: element.prop('readonly');
            this.editModel = true;
            this.eventMgr = {};
        },
        showView : function (){
            this.element.removeClass('l-text-field')
            .addClass('solided').attr('readonly',true)
            .parent().css('border','none');
            this.element.css('width',this.options.width - 3);
            this.editModel = false;
        },
        showEdit : function (){
            if(!this.readonly){
                this.element.removeClass('solided')
                .addClass('l-text-field')
                .attr('readonly',false)
                .parent().css('border','1px solid #AECAF0');
                this.editModel = true;
            }else{
                this.showView();
            }
        },
        setText : function (text){
            this.element.val(text);
        },
        getText : function (){
            return this.element.val();
        },
        bind : function (name,fn){
            var handlers = this.eventMgr[name];
            if(!handlers){
                this.eventMgr[name] = handlers = [];
            }
            handlers.push(fn);
        },
        _mouseleaveHandler : $.noop,
        _mouseenterHandler : $.noop,
        destroy : function () {
        	if(this.editModel){
        		this.showView();
        	}
        	var element = this.element;
        	this.container.unbind(this.eventNamespace);
        	element.unbind(this.eventNamespace)
        	    .removeClass('l-text-field').removeClass('solided').unwrap()
        	    .removeData(this.widgetFullName)
        	    .val('');
        	var originalProperty = this.originalProperty;
        	if(originalProperty.width){
        		element.width(originalProperty.width);
        	}
        	if(originalProperty.readonly === true){
        		element.prop('readonly',true);
        	}else{
        		element.prop('readonly',false);
        	}
        }
    });
})(jQuery);
