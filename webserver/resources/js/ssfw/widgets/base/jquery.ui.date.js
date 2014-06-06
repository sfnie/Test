/**
 * author : qsyan 
 */
(function($) {

    $.datepicker.regional['zh-CN'] = {
        closeText: '关闭',
        prevText: '&#x3C;上月',
        nextText: '下月&#x3E;',
        currentText: '今天',
        monthNames: ['一月','二月','三月','四月','五月','六月',
        '七月','八月','九月','十月','十一月','十二月'],
        monthNamesShort: ['一月','二月','三月','四月','五月','六月',
        '七月','八月','九月','十月','十一月','十二月'],
        dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
        dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
        dayNamesMin: ['日','一','二','三','四','五','六'],
        weekHeader: '周',
        dateFormat: 'yy-mm-dd',
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: true
    };
	
    $.datepicker.setDefaults($.datepicker.regional['zh-CN']);
		
    $.widget("ui.date", $.ui.textButton, {
    	options : {
    	   delayHide : 600  
        },
        _create : function() {
            this._super();
            this._renderCombobox();
        },
        _renderCombobox : function() {
            this.show = false;
            var that = this, element = this.element;
            element.datepicker({
            	changeMonth: true,
                changeYear: true,
                showOn : '',
                dateFormat : 'yy-mm-dd',
                onClose : function() {
                    that.show = false;
                },
                showAnim : "",
                beforeShow : function() {
                    this.show = true;
                },
                onSelect : function(dateText, inst) {
                    that.hideDate();
                    that.element.attr('value', dateText);
                    that.element.trigger('keyup');
                }
            });
            this.element.datepicker( $.datepicker.regional[ "zh-CN" ] );
            this.element.datepicker('widget').mouseleave(function(e) {
            	that.mouseenterComponent = false;
            	that._delay(function (){
            		if (element.attr('id') !== e.relatedTarget.id && that.mouseenterComponent !== true) {
                        this.hideDate();
                    }
            	},that.options.delayHide)
            }).mouseenter(function (e){
            	that.mouseenterComponent = true;
            });
            this.element.click(function(e) {
                e.preventDefault();
            });
            element.attr('readonly', true);
        },
        showEdit : function() {
            if(!this.readonly){
                this._super();
            }
            this.element.attr('readonly', true);
        },
        _mouseleaveHandler : function(e) {
            if(this.editModel && this.show){
                this._delay(function (){
                    if (!this.mouseenterComponent && 
                        (!e.relatedTarget || $(e.relatedTarget).closest('.ui-datepicker').length === 0)) {
                        this.hideDate();
                        this.mouseenterComponent = false;
                    }
                },this.options.delayHide);
            }
        },
        hideDate : function() {
            this.element.datepicker('hide');
            this.show = false;
        },
        showDate : function() {
            this.element.datepicker('show');
            this.show = true;
        },
        _onSwitchButtonClick : function() {
            if (this.editModel) {
                if (this.show) {
                    this.hideDate();
                } else {
                    this.showDate();
                }
            }
        }
    });
})(jQuery);
