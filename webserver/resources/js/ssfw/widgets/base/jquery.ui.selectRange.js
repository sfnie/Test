/**
 * author : qsyan
 */
(function($) {

	$.widget("ui.selectRange", $.ui.textButton, {
		options : {
			url : null,
			baseType : null,
			initTextByValue : true,
			dialogHeigth : null,
			dialogWidth : null,
            beforeselect : $.noop,
            afterselect : $.noop
		},
		_create : function() {
			this._super();
			var element = this.element, options = this.options, name = element
				.attr('name'), value = element.val();
			if (element.attr('baseType')) {
				options.baseType = element.attr('baseType');
			}
			this.element.removeAttr('name').removeAttr('value');
			this.hidden = $('<input/>').attr('name', name).attr('type','hidden').val(value);
			if(this.options.initTextByValue === true && value){
				 this.setValue(value);
			}
			this.container.append(this.hidden);
		},
	    setText : function(text){
	    	this.element.val(text);
	    },
	    setValue : function (value,text){
	        if(value){
	            this.hidden.val(value);
	            if(text === undefined){
	                text = this.getText();
	            }
	            this.setText(text);	
	        }else{
	            this.hidden.val('');
	            this.setText('');
	        }
	    },
	    getValue : function (){
	        return this.hidden.val();
	    },
	    getText : function (){
	    	var text = this.element.val();
	    	if(text){
	    		return text;
	    	}else{
	    		var value = this.getValue();
	   	        if(value){
	   	        	// TODO 配置了url 并且还没初始化时，尝试加载 url 中的数据，并保存在data中。
	   	            var baseType = this.options.baseType;
	   	            var text = '';
	   	            if(value && baseType){
	   	                $.fn.selectRange.getTextFromServer.call(this,'keys=1&baseTypes=' 
	   	                    	+ baseType + '&values=' + this.hidden.val(),function (result){
	   	                	var data = result.selectRange;
	   	                    if(data && data.length){
	   	                        text = data[0].label;
	   	                    }
	   	                },false);
	   	            }
	   	            return text;
	   	        }
	   	        return '';	
	    	}
	     },
		destroy : function (){
			//TODO 如果组件有初始值时，设置初始值。
//			var originalVlaue = this.hidden.val();
//			if(originalVlaue){
//				this.setText(originalVlaue);
//			}
			$('input[type=hidden]',this.container).remove();
			this._super();
				
		}
	});
	$.fn.selectRange.getTextFromServer = function(param, callback, async) {
		var that = this;
		$.ajax({
			url : ssfwConfig.selectrangeUrl,
			type : 'post',
			dataType : 'json',
			async : async === false ? false : true, //TODO 改用异步，提高性能。
			data : param,
			success : function(data) {
				callback.call(that, data);
			}
		});
	};
})(jQuery);
