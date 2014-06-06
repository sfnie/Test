/**
 * author : qsyan
*/
(function ($) {
	
    $.widget("ui.combobox", $.ui.selectRange, {
        options : {
            url : null,
            data : null,
            lazyLoad : true,
            selectBoxMinHeight : 130,
            selectBoxMaxHeight : 260,
            order : '',
            delayHide : 600
        },
        destroy : function (){
        	var element = this.element;
        	element.removeClass('l-text-field');
        	this.selectBox.unbind(this.eventNamespace);
        	this.selectBox.remove();
        	this._super();
        },
        _create : function () {
            this._super();
            var that = this,
            select = this.element,
            container = this.container;
            var order = select.attr('order');
            if(order){
            	this.options.order = order;
            }
            select.addClass('l-text-field').attr('readonly',true);
            var selectBox = this.selectBox = $('<div class="selectBox" '+
                'style="position: absolute;"/>');
            selectBox.width(container.width()).data('combobox',this);
            $('body').append(this.selectBox);
            
            selectBox.bind('mouseenter' + this.eventNamespace, function (e){
            	 that.mouseenterSelextBox = true;
            }).bind('mouseleave' + this.eventNamespace, function (e){
                that.mouseenterSelextBox = false;
                if(that.options.delayHide === -1){
                	return;
                }
                if(!e.relatedTarget){
                	 that._delay(function (){
                         if(!this.mouseenterSelextBox && !this.mouseenterComponent){
                             this.hideSelectBox();
                         }
                     },that.options.delayHide);
                }
                var relatedTarget = $(e.relatedTarget),
                    comboContainer = relatedTarget.closest('.comboContainer');
                if(comboContainer.length == 0 || comboContainer.get(0) !== that.container){
                    that._delay(function (){
                        if(!this.mouseenterSelextBox && !this.mouseenterComponent){
                            this.hideSelectBox();
                        }
                    },that.options.delayHide);
                }
            });
            this.container = container;
            this.showEdit();
        },
        _init : function (){
            this.cascade = [];
            this.selectBoxLoaded = false;
            this._renderCombobox();
        },
        addCascade : function (component,columnName){
            if(!component || !columnName){
                return ;
            }
            if(typeof component === 'string'){
                var targetInst = $(component);
                if(!targetInst.length){
                    targetInst = $('#'+component+',input[baseType=\''+component+'\']');
                }
                if(targetInst.length){
                    component = targetInst;
                }
            }
            if(component instanceof $){
                component = component.data('combobox');
            }
            if(component){
                var that = this;
                component.bind('onchange',function (){
                    that.options.data = null;
                    that.selectBoxLoaded = false;
                    that.setValue(null);
                });
                this.cascade.push({
                    inst : component, 
                    name : columnName
                });
            }
        },
        _mouseleaveHandler : function (e){
            if(!this.editModel){
                return;
            }
            if(this.options.delayHide === -1){
            	return;
            }
            this._delay(function (){
                if(!this.mouseenterComponent && !this.mouseenterSelextBox){
                	if(!e.relatedTarget){
                		if(!this.mouseenterSelextBox){
                           this.hideSelectBox();
                           this.mouseenterComponent = false;
                        }
                	}
                    var relatedTarget = $(e.relatedTarget);
                    if(this.selectBox.is(':visible') && relatedTarget.closest('.selectBox').length == 0 &&
                    		relatedTarget.closest('.comboContainer').length == 0){
                              this.hideSelectBox();
                              this.mouseenterComponent = false;
                    } 
                }
            },this.options.delayHide);
        },
        showView : function (){
            this._super();
            if(this.selectBox){
                this.selectBox.hide();
            }
            this.element.css('cursor','');
        },
        showEdit : function (){
            this._super();
            if(!this.readonly){
                this.element.css('cursor','pointer')
                .attr('readonly',true);
            }
        },
        _onSwitchButtonClick : function (){
            if(this.selectBox.is(':visible')){
                this.hideSelectBox();
            }else{
                if(!this.selectBoxLoaded){
                    this._renderCombobox(true);
                }else{
                    this.showSelectBox();
                }
            }
            return false;
        },
        showSelectBox : function (){
            var offset = this.container.offset();
            this.selectBox.css({
                'top': offset.top + this.container.height() ,
                'left':  offset.left
            });
            if(this.selectBoxHeight){
                var top = this.container.offset().top + this.container.height() + this.selectBoxHeight;

                
                
                if(top >= ($(window).height() + ( $.browser.msie ? document.documentElement.scrollTop : $('body').scrollTop() ))){
                	this.selectBox.css({top : this.container.offset().top - this.selectBoxHeight});
                }    
            }
            this.container.css({
                'z-index' : '999'
            });
            this.selectBox.bind('scroll' + this.eventNamespace,function (e){
                var resizable = $(this).data('resizable');
                if(resizable){
                    resizable.handles.se.css('bottom',-$(this).scrollTop());
                }
            });
            $(document).bind('click' + this.eventNamespace,function (e){
                 if($(e.target).closest('.selectBox').length === 0){
                	 $('.selectBox:visible').data('combobox').hideSelectBox();
                 }
                 e.stopImmediatePropagation();
            });
            this.selectBox.show();
        },
        hideSelectBox : function (){
            this.selectBox.hide();
            this.container.css({
                'z-index' : '0'
            });
            $(document).unbind('click' + this.eventNamespace);
            this.selectBox.unbind('scroll' + this.eventNamespace);
            this.mouseenterSelextBox = false;
        },
        _renderCombobox : function (load){
            var data = this.options.data,that = this;
            if(data === undefined || data == null){
                if((load) || (!this.options.lazyLoad)){
                    var url = this.options.url;
                    if(!url){
                        if(this.options.baseType){
                            url = ssfwConfig.comboboxUrl + "?name=" + this.element.attr('baseType');
                        }
                    }
                    this.showSelectBox();
                    if(url){
                        this.selectBox.html('<img style="width:16px;width:16px;" src="' +ssfwConfig.contextPath+'/'+
                            'resources/js/ssfw/widgets/base/images/throbber.gif"/> 努力加载中..');
                        var param = '';
                        for(var i = 0; i < this.cascade.length;i++){
                            var targetInst = this.cascade[i];
                            if(targetInst.inst && targetInst.inst.hidden.val()){
                            	param += ('&cascol=' + targetInst.name + '&casval=' + targetInst.inst.hidden.val());
                            }
                        }
                        if(!this.cascade.length){
                        	param += ('cascol=&casval=');
                        }
                        param += ('&order=' + this.options.order) ;
                        $.ajax({
                            url : url,
                            dataType : 'json',
                            data : param ,
                            success : function (data){
                                if(!data || !data.length){
                                    data = [];	
                                }
                                that.options.data = data;
                                that.selectBox.html('');
                                that._renderCombobox();
                                that.selectBoxLoaded = true;
                                that.showSelectBox();
                            }
                        });
                        return;
                    }
                    this.selectBox.css({
                        'height' : this.options.selectBoxMinHeight
                    });
                }
            }else{
                that._createItems(data);
                that.selectBoxLoaded = true;
            }
        },
        _setOption: function(key,value) {
            if(key === 'data'){
                if(!value || !value.length){
                    value = [];
                }
                this.options[key] = value;
                this.setValue();
            }else{
                this.options[key] = value;
            }
        },
        _createItems : function (data){
            if(data){
                var resize = this.selectBox.data('resizable');
                if(resize){
                    resize.destroy();
                }
                this.selectBox.empty();
    		    
                var that = this;
                this.options.data = data;
                if(data.length === 0){
                    this.selectBox.css({
                        'height':this.options.selectBoxMinHeight
                    });
                }else{
                	$('<div/>').addClass('combobox-menuitem').attr('id','temp' + this.uuid).appendTo(this.selectBox);
                	var temp = $('#temp' + this.uuid),
                	    defaultLineHeight = temp.css('lineHeight').toUpperCase(),
                	    pxIndex = defaultLineHeight.indexOf('PX');
                	defaultLineHeight = pxIndex !== -1 ? 
                			  defaultLineHeight.substring(0,pxIndex) : defaultLineHeight; 
                	this.selectBox.remove('#temp' + this.uuid);
                    var i = 0,height = (data.length * defaultLineHeight / 2);
                    if(data.length > 0){  // && (!that.element.metadata() || that.element.metadata().required !== true)
                        data.unshift({
                            id : '',
                            label : '请选择'
                        });
                    }
                    for(; i < data.length; i++){
                        var item = $('<div/>').addClass('combobox-menuitem')
                        .text(data[i].label).attr('item-id',data[i].id);
                        this._on(item, {
                            mouseenter: function(e) {
                                $(e.currentTarget).css('background-color','#D6E9F8');
                            },
                            mouseleave: function(e) {
                                if(this.currentSelected && this.currentSelected.length > 0 
                                          && this.currentSelected[0] === e.target){
                                     $(e.currentTarget).css('background-color','#D6E9F8');
                                }else{
                                    $(e.currentTarget).css('background-color','');
                                }
                            }
                        });
                        this.selectBox.append(item);
                    }
                    if(height > this.options.selectBoxMaxHeight){
                        height = this.options.selectBoxMaxHeight;
                    }else if(height < this.options.selectBoxMinHeight){
                        height = this.options.selectBoxMinHeight;
                    }
                    
                    this.selectBox.css({
                        'height' : height
                    });
                    this.count = i;
                    $('.combobox-menuitem',this.selectBox).click(function (e){
                    	e.stopPropagation();
                    	var menuitem = $(this),
                    	    data = {text : menuitem.text() , value : menuitem.attr('item-id')};
                        if(that._trigger("beforeselect",e,data) === false){
                        	return;
                        }
                        that.setValue(menuitem.attr('item-id'),menuitem.text());
                        handlers = that.eventMgr['onchange'];
                        if(handlers && handlers.length){
                            for(var i = 0; i < handlers.length;i++){
                                if(!handlers[i].call(that)){
                                    break;
                                }
                            }
                        }
                        if(!that.currentSelected){
                            that.currentSelected = menuitem;
                        }
                        if(that.currentSelected && that.currentSelected !== menuitem){
                            that.currentSelected.css('background-color','');
                            that.currentSelected = menuitem;
                        }
                        that.hideSelectBox();
                        that._trigger("afterselect",e,data);
                        that.element.trigger('keyup',e);
                        that.mouseenterComponent = false;
                    });
                }
                if(height > this.options.selectBoxMinHeight){
                	this.selectBox.resizable({
                        minWidth : that.container.width(),
                        minHeight : that.options.selectBoxMinHeight,
                        handles:"se,e"
                    });
                	if(this.selectBox.data('resizable')){
                        this.selectBox.data('resizable').handles.se.css('bottom','1px');
                    }
                }
                this.selectBoxHeight = height;
            }
        },
        getText : function (){
            var value = this.hidden.val();
            if(value){
                if(this.options.data){
                    for(var i = 0; i < this.options.data.length;i++){
                        var entity = this.options.data[i];
                        if(entity.id === value){
                            return entity.label;
                        }
                    }
                }
                return this._super();
            }
            return '';
        }
    });
})(jQuery);
