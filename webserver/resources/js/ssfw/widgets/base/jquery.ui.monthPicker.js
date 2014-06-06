/**
 * author : qsyan 
 */
(function($) {
    $.widget("ui.monthPicker",$.ui.textButton,{
    	options : {
     	   separator : '', // 年月分隔符  
     	   title : '选择年月'
        },
        _create : function() {
        	this._super();
        	var separator = this.element.attr('separator');
        	if(separator){
        		this.options.separator = separator;
        	}
        	var title = this.element.attr('title');
        	if(title){
        		this.options.title = title;
        	}
        },
        _onSwitchButtonClick : function (){
        	if(this.monthPickerContainer){
        		var dialogInstance = this.monthPickerContainer.data('dialog');
        		if(dialogInstance.isOpen()){
        			dialogInstance.close();
            		return false;
        		}
        	}
        	var defaultDate = new Date(),
        	    customDate = this.element.val(),
        	    that = this,
        	    format = 'yyyy-MM',
        	    monthDisplay = {1 : '一',2 : '二',3 : '三',4 : '四',5 : '五',6 : '六',7 : '七',8 : '八',9 : '九',10 : '十',11 : '十一',12 : '十二'};
        	if(customDate){
        		var array = customDate.split(this.options.separator);
        		defaultDate = new Date(array[0],parseInt(array[1] - 1));
        	}
        	var month = this.currentSelectMonth = defaultDate.getMonth() + 1,
        	    year  = this.currentSelectYear = defaultDate.getFullYear(),
        	    displayYear = year - 5,
        	    html = '<table border="0" cellspacing="0" style="width: 170px; height: 150px;"><tbody>',
        	    monthIndex = 0;
        	this.startYear = displayYear;
        	for(var i = 0 ; i < 6; i++){
        		monthIndex++;
        		html += '<tr><td class="x-date-mp-month month_'+monthIndex+'"><a href="#">'+ monthDisplay[monthIndex] +'月</a></td>';
        		monthIndex++;
        		html += '<td class="monthBorder x-date-mp-month month_'+monthIndex+'"><a href="#">'+ monthDisplay[monthIndex] +'月</a></td>';
        		if(i == 0){
            		html += '<td class="month_prev_page"><img title="上一页" src="'+ssfwConfig.contextPath+'/resources/js/ssfw/widgets/base/images/page-prev.gif"/></td><td class="month_next_page"><img title="下一页" src="'+ssfwConfig.contextPath+'/resources/js/ssfw/widgets/base/images/page-next.gif"/></td></tr>'
        		}else{
        			displayYear++;
            		html += '<td class="x-date-mp-year" d="year_'+ displayYear +'"><a href="#">' + displayYear + '</a></td>';
          			displayYear++;
            		html += '<td class="x-date-mp-year" d="year_'+ displayYear +'"><a href="#">' + displayYear + '</a></td></tr>';
        		}
        	}
        	var offset = this.element.offset();
        	html +=  '</tbody></table>';
        	var buttons = $('<div>').width(180).css('margin-top',5).html('<button type="button" class="x-date-mp-ok">确定</button>&nbsp;<button type="button" class="x-date-mp-cancel">取消</button>&nbsp;<button type="button" class="x-data-mp-clear">清空</button>');
        	var monthPickerContainer = this.monthPickerContainer = $('<div class="monthPickerContainer">').css('overflow','hidden');
        	monthPickerContainer.html(html).append(buttons).dialog({resizable : false,draggable : false,position : [offset.left - $(document).scrollLeft(),(offset.top 
        	          + this.element.height()) - $(document).scrollTop()],title : this.options.title,width : $.browser.msie&&($.browser.version == "7.0") ? 210 : 190}).find('button').button();
        	$('td[d=year_'+year+']',monthPickerContainer).addClass('mouth_select');
        	$('.month_' + month,monthPickerContainer).addClass('mouth_select');
        	$('td',monthPickerContainer).each(function (){
        		if($(this).hasClass('x-date-mp-month') || $(this).hasClass('x-date-mp-year')){
        			var isYear = false;
        			if($(this).attr('d') && $(this).attr('d').indexOf('year') != -1){
            			isYear = true;
            			$(this).click(function (e){
            				if($(this).hasClass('mouth_select')){
            					$(this).removeClass('mouth_select');
            				}
                		    var number = $(this).attr('d').substring($(this).attr('d').indexOf('_') + 1 ,$(this).attr('d').length);
                		    $('td[d=year_'+that.currentSelectYear+']',monthPickerContainer).removeClass('mouth_select');
            				$('td[d=year_'+number+']',monthPickerContainer).addClass('mouth_select');
            				that.currentSelectYear = number;
            				return false;
            			});
            		}else{
            			$(this).click(function (e){
            				if($(this).hasClass('mouth_select')){
            					$(this).removeClass('mouth_select');
            				}
                		    var number = this.className.substring(this.className.indexOf('_') + 1 ,this.className.length);
            				$('.month_' + that.currentSelectMonth,monthPickerContainer).removeClass('mouth_select');
            				$('.month_' + number,monthPickerContainer).addClass('mouth_select');
            				that.currentSelectMonth = number;
            				return false;
            			});
            		}
        		}
        	});
        	$('.x-date-mp-ok',monthPickerContainer).click(function (){
        		  var displayMonth = parseInt(that.currentSelectMonth / 10) > 0 ? that.currentSelectMonth : "0" + that. currentSelectMonth;
        		  that.element.val(that.currentSelectYear + that.options.separator + displayMonth);
        		  that.monthPickerContainer.dialog("close");
        	});
            $('.x-date-mp-cancel',monthPickerContainer).click(function (){
        		 that.monthPickerContainer.dialog("close");
        	});
            $('.x-data-mp-clear',monthPickerContainer).click(function (){
	              that.element.val('');
	              that.currentSelectMonth = null;
	              that.currentSelectYear = null;
	              that.monthPickerContainer.dialog("close");
            });
            $('.month_prev_page',monthPickerContainer).click(function (e){
            	e.stopImmediatePropagation();
            	$('td[d=year_'+that.currentSelectYear+']',monthPickerContainer).removeClass('mouth_select');
                $('.x-date-mp-year').each(function (){
                	var first = $(this).first(),
                	    nextYear = parseInt(first.text()) - 10;
                	first.text(nextYear);
                	$(this).attr('d','year_' + nextYear);
                	
                });
            	that.startYear -= 10
        		$('td[d=year_'+that.currentSelectYear+']',monthPickerContainer).addClass('mouth_select');

            });
            $('.month_next_page',monthPickerContainer).click(function (e){
            	e.stopImmediatePropagation();
            	$('td[d=year_'+that.currentSelectYear+']',monthPickerContainer).removeClass('mouth_select');
                $('.x-date-mp-year').each(function (){
                	var first = $(this).first(),
                	    nextYear = parseInt(first.text()) + 10;
                	first.text(nextYear);
                	$(this).attr('d','year_' + nextYear);
                
                });
            	that.startYear += 10
        		$('td[d=year_'+that.currentSelectYear+']',monthPickerContainer).addClass('mouth_select');

            });
        }
    });
})(jQuery);