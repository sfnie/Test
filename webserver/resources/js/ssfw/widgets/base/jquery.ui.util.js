(function (){
	$.ui.util = {
		loadingTip : function (message){
		   var tip = $('<div/>').addClass('loading_container').html('<img style="width:16px;width:16px;" src="'+ssfwConfig.contextPath+'/'+
               'resources/js/ssfw/widgets/base/images/throbber.gif"/><span class="loading_message"> ' + message + '</span>');
		   return tip;
		},
		upload : function (callback){
			//TODO  photo 、 attachment 上传
		}
	}
})();