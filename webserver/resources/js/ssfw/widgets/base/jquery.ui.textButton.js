/**
 * author : qsyan 
 */
(function($) {

	$.widget("ui.textButton", $.ui.inputText, {
		options : {
			triggerClassName : 'l-trigger',
			click : $.noop
		},
		_create : function() {
			this._super();
			var that = this;
			var switchButton = this.switchButton = $('<div>'
					+ '<div class="l-trigger-icon"></div></div>');
			switchButton.addClass(this.options.triggerClassName);
			this.element.parent().append(switchButton);
//			this.element.css({
//                width : this.options.width - 20 + 'px'
//            });
			(function (){
				function invoke(e){
					if (that._trigger('click', e) === false) {
						return false;
					}
		            if(!that.editModel){
		                return false;
		            }
					return that._onSwitchButtonClick(e);
				}
				$(switchButton).bind('click' + that.eventNamespace,function (e){
					return invoke(e);
				});
				that.element.bind('click' + that.eventNamespace,function (e){
					return invoke(e);
				});
			})();
		},
		showView : function() {
			this._super();
			$('.' + this.options.triggerClassName, this.container).hide();
		},
		showEdit : function() {
			this._super();
			if (!this.readonly) {
				$('.' + this.options.triggerClassName, this.container).show();
			}
		},
		_onSwitchButtonClick : $.noop,
		destroy : function (){
			$("." + this.options.triggerClassName,this.container).remove();
			this._super();
		}
	});
})(jQuery);
