/**
 * author : qsyan
*/
(function ($) {
	
    $.widget("ui.photo", {
        options : {
            uploadUrl : null,
            downloadUrl : null,
            handler : 'defaultHandler',
            imgUrl : 'image/photoNotfound.gif',
            imgHeight : '150px',
            imgWidth : '120px',
            uploadFolder : 'upload',
            contextPath : '',
            uploadButtonVal : '上传',
            uploadButtonClassName : 'submitLong',
            success : $.noop,
            error : $.noop
        },
        _create : function () {
            if(this.options.downloadUrl === null){
                this.options.downloadUrl = ssfwConfig.photoDownloadUrl;
                this.options.uploadUrl = ssfwConfig.photoUploadUrl;
            }
            this.options.contextPath = ssfwConfig.contextPath;
            var options = this.options,
            element = $(this.element);
            var id = "photoContainer" + (++jQuery.uuid);
            var container = $('<div/>').css({
                'width':'180px',
                'height':'190px',
                'margin-left':'10px'
            }).attr('id',id);	
            var photoContainer = $('<div align="center"/>');
            var buttonContainer = this.buttonContainer = $('<div align="center"/>').css({
                'height' : '25px',
                'margin-top' : '5px'
            });
            element.wrap(container).hide();
            container = $("#" + id);
            container.append(photoContainer);
            container.append(buttonContainer);
            var uploadButton = $('<input type="button"/>').val(options.uploadButtonVal);
            if(options.uploadButtonClassName){
                uploadButton.addClass(options.uploadButtonClassName);
            }else{
                uploadButton.css({
                    'background-color':'white',
                    'border':'1px solid #CDCDCD',
                    'height':'20px',
                    'width':'70px'
                });
            }
            uploadButton.css({
                'position':'relative'
            });
			
            uploadButton.appendTo(buttonContainer);	
            var handler = element.attr('handler');
            if(!handler){
                handler = this.options.handler;
            }
            //TODO container 多实例id重复、width、height固定。    
            $('<img style="*height:'+options.imgHeight+';*width:'+options.imgWidth+';" id=\'fileUploadPhoto\'/>').attr(
                'src', options.downloadUrl + (options.downloadUrl.indexOf('?') !== -1 ?  "&" : "?") + "handler=" + handler + "&value="+element.val()+"&bh=" + element.attr('bh'))
            .attr('width',options.imgWidth)
            .attr('height',options.imgHeight).appendTo(photoContainer);

            var elementName = element.attr('name');
            elementName = 'imgFile'; //TODO 上传默认名字固定。
            //element.removeAttr('name');
            var fileField = $('<input type=\'file\'/ name=\'imgFile\'/>').css({
                'position':'relative',
                'opacity' : 0,
                'top':'-25px',
                'height':'28px'
            }).attr('name',elementName).appendTo(buttonContainer);	    
            if($.browser.msie){
                fileField.css('left','-100');
            }else{
                fileField.css('left','-90');
            }
            var frame = $('<iframe id=\'tempFileUploadFrame\' name=\'tempFileUploadFrame\' src="about:blank"/>')
            .css('position','absolute').css('left','-700px');
			
            frame.appendTo('body');

    
            fileField.change(function (){
                var inst = $(this).parent().prev().prev().data('photo');
                var form = $('<form id="tempFileUploadForm" method="post" enctype="multipart/form-data"/>')
                .css('position','absolute').css('left','-700px').attr('action',options.uploadUrl)
                .attr('target','tempFileUploadFrame');
                $('<input name=\'bh\' type=\'hidden\'/>').val(element.attr('bh')).appendTo(form);
                $('<input name=\'value\' type=\'hidden\'/>').val(element.attr('value')).appendTo(form);
                var handler = element.attr('handler');
                if(!handler){
                    handler = inst.options.handler;
                }
                $('<input name=\'handler\' type=\'hidden\'/>').val(handler).appendTo(form);
                $(this).appendTo(form);
                form.appendTo('body');
                form.submit();
	    		
                var that = $(this);
                frame.load(function (){
                    var frame = document.getElementById('tempFileUploadFrame');
                    try{
                        eval("var data = " + frame.contentWindow.document.body.innerHTML);
                        if(data && data.success === true){
                            var photo = $('#fileUploadPhoto');
                            var handler = inst.element.attr('handler');
                            if(!handler){
                                handler = inst.options.handler;
                            }
                            photo.attr('src',inst.options.downloadUrl + (options.downloadUrl.indexOf('?') !== -1 ?  "&" : "?")+ "handler=" + handler + "&bh=" + 
                                inst.element.attr('bh') + "&value="+data.value+"&t=" + new Date().getTime());
                            inst.element.attr('value',data.value);
                            options.success.call(that,data,options);
	    					
                        }else{
                            options.error.call(that,data);
                        }
                    }catch(e){
                        options.error.call(that);
                    }    		
                    buttonContainer.append(that);
                    $('#tempFileUploadForm').remove();
	    			
                });
            });
        },
        showEdit : function() {
        	if(!this.element.prop('readonly')){
        		 this.buttonContainer.show();
        	}
        },
        showView : function() {
            this.buttonContainer.hide();
        }
    
    });
})(jQuery);
