/**
 * author : qsyan
 */
(function($) {
	$.widget('ui.attachment', {
		options : {
			allowFileType : [], //['jpg','bmp']
			size : 5,
			success : $.noop,
			error : $.noop,
			remove : $.noop,
			afterRemove : $.noop,
			handler : 'attachmentHandler',
			attachInfo : null,
			serviceId : null,
			oneKeyDownload : false
		},
		destroy : function (){
			//TODO 销毁
		},
		getFileInfos : function (){
			return this.attchments;
		},
		getValue : function (){
			return this.element.val();
		},
		_upload : function (file){
			// 验证文件类型
			if(!file && !that.uploading) return ;
			var fileValue = file.value;
			if(!fileValue || fileValue.indexOf('.') == -1) return;
			var index = fileValue.indexOf('.'),
			    extName = fileValue.substring(index + 1).toUpperCase(),
			    that = this;
			for(var i = 0,length = this.options.allowFileType.length; i < length; i++){
				var type = this.options.allowFileType[i];
				if(type.toUpperCase() === extName){
					break;
				}
			}
			if(this.options.allowFileType.length && i === length){
				alert('不能上传文件类型' + extName + '。');
				return;
			}
			var frame = $('#tempFileUploadFrame');
			if(!this.frame && !frame.length){
				this.frame = $('<iframe id=\'tempFileUploadFrame\' name=\'tempFileUploadFrame\' src="about:blank"/>')
				.css({left : '-700px', position : 'absolute'}).appendTo('body');
			}else{
				this.frame = frame;
			}
			// 上传文件
			var tempFileUploadForm = $('<form/>',{
                    enctype : 'multipart/form-data',
                    id : 'tempFileUploadForm',
                    method : 'post',
                    action : ssfwConfig.contextPath + '/attach/uploadAttachment.widgets',
                    target : 'tempFileUploadFrame'
               }).css({ position : 'absolute', left : -1700});
            $(file).appendTo(tempFileUploadForm);
            $('<input/>',{type : 'hidden',name : 'handler',value : that.options.handler}).appendTo(tempFileUploadForm);
            $('<input/>',{type : 'hidden',name : 'serviceId',value : that.options.serviceId}).appendTo(tempFileUploadForm);
            if(that.options.attachInfo){
               $('<input/>',{type : 'hidden',name : 'attachinfo',value : that.options.attachInfo}).appendTo(tempFileUploadForm);
            }
   			var loadTip = $.ui.util.loadingTip('上传中...');
			$('.ui-attchment:first',that.container).before(loadTip);
			that.frame.bind('load',function (e){
				    try{
				    	eval('var resultData = ' + this.contentWindow.document.body.innerHTML);
				    	if(resultData.success == true){
		                	if(that.options.size === 1){
		                	   that.element.val(resultData.attachmentId);
		                	   $('.ui-attchment',that.container).remove();
		                	}
		                	that._createAttachmentByJsonObject(resultData);
		                	var val = that.element.val();
		                	if(val){
		                	    val += ',';
		                	}
		                	that.element.val(val + resultData.attachmentId);
		                	that._trigger('success',e,resultData);
		            	 }else{
		            	    that._trigger('error',e);
		            	 }
				    }catch(ex){
				    	that._trigger('error',e);
				    }
            	    loadTip.remove();
            	    $(this).unbind('load').attr('src','about:blank');
            	    that.uploading = false;
             });
             tempFileUploadForm.appendTo('body');
             that.uploading = true;
             tempFileUploadForm.submit();
 	         this._createInputFile();
		},
		_createInputFile : function (){
			var that = this;
			$('<input>',{type : 'file',size : 28,name : 'attachment'})
		    .addClass('ui-attchment-button-file')
		    .css({opacity : 0,left : 0,top : 0})  
		    .appendTo(this.buttonContainer)
		    .change(function (){
				var alreadyUploaded = $('.ui-attchment',that.container).length;
				if(alreadyUploaded < that.options.size){
					that._upload(this);
				}else{
					alert('最多只能上传 ' + that.options.size + ' 个文件。');
				}
			 });
		},
		_createAttachmentByJsonObject : function (attachment){
			var attchmentDom = $('<div/>').addClass('ui-attchment'),
			    donwloadLink = $('<a/>',{ target : '_blank' ,href : ssfwConfig.contextPath + '/attach/downloadAttachment.widgets?attachmentId=' + attachment.attachmentId,title : '下载'}).text(attachment.originName).css({cursor : 'pointer'}),
			    name = $('<div/>').css({float : 'left'}).append(donwloadLink),
			    remove = $('<div/>',{title : '删除'}).html('<a>X</a>').addClass('ui-attchment-button-remove').data('attachmentId',attachment.attachmentId).css('display',this.editModel == true ? 'block' : 'none'),
			    clear = $('<div/>').addClass('ui-clear'),
			    buttonContainer = $('.ui-attchment-button-container',this.container);
				attchmentDom.append(name).append(remove).append(clear),
				that = this;
			if(buttonContainer.length){
				buttonContainer.before(attchmentDom);
			}else{
				this.container.append(attchmentDom);
			}
			remove.click(function (e){
				var attachmentId = $(this).data('attachmentId'),remove = this;
				if(that._trigger('remove',e,attachmentId) === false){
					return false;
				}
				var loadTip = $.ui.util.loadingTip('删除中...');
				$('.ui-attchment:first',that.container).before(loadTip);
				$.ajax({
					url : ssfwConfig.contextPath + '/attach/deleteAttachment.widgets?attachmentId=' + attachmentId,
					dataType : 'json',
					success : function (data){
						that._trigger('afterRemove',e,data);
						loadTip.remove();
						if(data.success == true){
							$(remove).closest('.ui-attchment').remove();
						}else{
							alert('删除失败');
						}
					}
				});
			    return false;
			});
		},
		_create : function() {
			var element = this.element,
			    container = this.container = $('<div/>').addClass('ui-attchmentContainer'),
				attchment = $('<div/>').addClass('ui-attchment'),
				buttonContainer = this.buttonContainer = $('<div/>').addClass('ui-attchment-button-container'),
				that = this,
				customHandler = element.attr('handler'),
				customAttachInfo = element.attr('attachInfo'),
				serviceId = element.attr('serviceId');
			if(customHandler){
				this.options.handler = customHandler;
			}
			if(customAttachInfo){
				this.options.attachInfo = customAttachInfo;
			}
			if(serviceId){
				this.options.serviceId = serviceId;
			}
			var loadTip = $.ui.util.loadingTip('加载中...').appendTo(container);
			element.hide().parent().append(container);
			// 获取附件列表信息
			$.ajax({
				url : ssfwConfig.contextPath + '/attach/listAttachment.widgets',
				data : {handler : this.options.handler, serviceId : this.options.serviceId},
				dataType : 'json',
				cache : false,
				success : function (data){
					if (data.attachments) {
						for ( var i = 0; i < data.attachments.length; i++) {
							var attchmentData = data.attachments[i];
							that._createAttachmentByJsonObject(attchmentData);
						}
					}
					loadTip.remove();
				}
			});
			$('<input/>',{type : 'button',value : '文件上传'})
				.addClass('ui-attchment-button')
				.addClass('submitLong')
				.appendTo(buttonContainer);
			this._createInputFile();
			buttonContainer.appendTo(container);
			if(this.options.oneKeyDownload){
				$('<a/>').text('一键下载')
				.addClass('ui-attchment-button')
				.addClass('submitLong')
				.css('left',80)
				.click(function (){
					window.open(ssfwConfig.contextPath + '/attach/oneKeyDownload.widgets?serviceId='+ that.options.serviceId);
				})
				.appendTo(buttonContainer);
			}
		},
		showEdit : function() {
			if(!this.element.prop('readonly')){
				$('.ui-attchment-button-container',this.container).show();
		        $('.ui-attchment-button-remove',this.container).show();
		        this.editModel = true;
			}
		},
		showView : function() {
		   $('.ui-attchment-button-container',this.container).hide();
		   $('.ui-attchment-button-remove',this.container).hide();
		   this.editModel = false;
		}
	});
})(jQuery);
