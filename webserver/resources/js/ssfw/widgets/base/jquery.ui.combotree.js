/**
 * author : qsyan
*/
(function ($) {
	
    $.widget("ui.combotree", $.ui.combobox, {
        options : {
        	selectBoxMaxHeight : 200,
            onlySelectLeaf : false
        },
        _setOption : function (key,value){
            this._superApply([key,value]);
            if(key === 'onlySelectLeaf'){
                this.options[key] = value;
            } 
        },
        _renderCombobox : function (load) {
            var that = this;
            if((load && !this.selectBoxLoaded) || 
                (!this.selectBoxLoaded && !this.options.lazyLoad)){
                var url = this.options.url;
                if(!url){
                    url = ssfwConfig.combotreeUrl + "?name=" + this.element.attr('baseType') + 
                       "&cascol=&casval=&order="+this.options.order+"&pcol=";
                }
                that.selectBox.jstree({ "json_data" : {ajax : {
                            url : url,cache : true,
                            data : function (n){
                                return n !== -1 ? 
                                {
                                   pval : n.data('id')
                                } : 
                                {
                                   pval : ''    
                                };
                            }
                        }
                    },
                    "themes" : {
                        "theme" : "classic"
                    },
                    "plugins" : [ "themes", "json_data", "ui" ]
                }).bind('select_node.jstree',function (e,data){
                	var obj = {text : data.rslt.obj.data("label"), value : data.rslt.obj.data("id")};
                    if(that._trigger('beforeselect',e,obj) === false){
                        return ;
                    }
                    if(that.options.onlySelectLeaf && !data.inst.is_leaf()){
                        return ;
                    }
                    that.setValue(data.rslt.obj.data("id"),data.rslt.obj.data("label"));
                    that.hideSelectBox(); 
                    that._trigger('afterselect',e,obj);
                    that.element.trigger('keyup',e);
                }).bind('loaded.jstree',function (e,data){
                    that.selectBox.resizable({
                        minWidth : that.container.width(),
                        minHeight : that.options.selectBoxMinHeight,
                        handles:"se,e"
                    });
                    var clearButton = $('<div/>').html('请选择').css({cursor : 'pointer','marginLeft' : 5,'line-height' : '15px'});
                    clearButton.bind('click' + that.eventNamespace, function (e){
                    	that.setValue(null);
                    	that.hideSelectBox(); 
                    	that.element.trigger('keyup',e);
                    });
                    $('ul:eq(0)',that.selectBox).before(clearButton);
                    that.selectBoxHeight = that.options.selectBoxMaxHeight ? that.options.selectBoxMaxHeight : 200;
                    that.selectBox.height(that.selectBoxHeight);
                    that.showSelectBox();
                    that.selectBoxLoaded = true;

                }).delegate("a", "click", function (event, data) {
                    event.preventDefault(); 
                });
                this.count = true;
            }
        },
        destroy : function (){
        	//TODO jstree 销毁
        	this._super();
        }
    });
})(jQuery);
