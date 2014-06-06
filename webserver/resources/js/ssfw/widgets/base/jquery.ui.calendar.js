/**
 * author : qsyan 
 */
(function($) {
    var dayIdPrefix = 'ssfw_calendar_day_';
    
    $.widget("ui.calendar", {
    	options : {
    		stepSize : 30,
    		timeRanges : [],
    		onReservationsClick : $.noop,
	        onTimeRangeClick : $.noop,
	        url : null,
	        nextPageId : null,
	        prevPageId : null,
	        displayCurrentTime : true
    	},
        _create : function() {
     	    var table = $('<table>').addClass('ssfw_calendar_table').addClass('table')
     	        .addClass('clum-table').attr('cellspacing',0).attr('border',0),
     	        firstRow = $('<tr>').addClass('table-title'),
     	        dayIdPrefix = 'ssfw_calendar_day_',
     	        dayMapper = {0 : '日',1 : '一',2 : '二',3 : '三',4 : '四',5 : '五',6 : '六'},
		   	    cache = this.cache = {},
     	        timeRanges = this.options.timeRanges,
     	        that = this;
     	    for(var i = -1; i < 7; i++){
     	    	var cell = $('<th>');
     	    	if(i > -1){
     	    	  cell.attr('id',dayIdPrefix + i)
     	    	  .css({width : 100})
     	    	  .html('<div class="ssfw_calendar_week">'+ 
     	    	  dayMapper[i] +'</div><div class="ssfw_calendar_date"></div>');
     	    	}else{
     	    		cell.css({width : 120});
     	    	}
     	    	firstRow.append(cell);
     	    }
			// 调试
			table.attr('border',1);
     	    table.append(firstRow);
		   	for(var i = 0; i < timeRanges.length; i++){
		   		var range = timeRanges[i];
		   		var tr = $('<tr>');
		   		    firstCell = $('<td>').html(range.startTime + " - " + range.endTime)
		   		    .appendTo(tr).attr('id','ssfw_calendar_hour_' + this.getHour(range.startTime) );
		   		firstCell.data('timeRangeNumber',{start : this.getTotalMinute(range.startTime),end : this.getTotalMinute(range.endTime)});
		   		for(var j = 0; j < 7; j++){
		   			$('<td>').data('cellInfo',{day : i,timeRange : range})
		   			         .click(function (e){
		   			        	 var cell = $(this);
		   			        	 var info = cell.data('cellInfo');
		   			        	 var th = cell.closest('table').find('tr:first').find('th');
		   			        	 info.day = {day : info.day,dateObj : $.data(th[cell.index()],'date')};
		   				         that._trigger("onTimeRangeClick",e,info);
		   			         })
		   			         .appendTo(tr);
		   		}
		   		table.append(tr);
		   	}
		  	$('tr',table).find('td:first').each(function (){
		   		  var cell = $(this);
		   		  cache[cell.attr('id').substring('ssfw_calendar_hour_'.length)] = cell;
		   	});
     	    this.element.append(table);
//        	var loadTip = $.ui.util.loadingTip('加载中...');
//        	this.element.append(loadTip);
     	    
        	var that = this;
		/**
        	$.ajax({
				url : that.options.url,
				dataType : 'json',
				success : function (data){
					that.calendar = data;
					that.init();
				}
			});**/
				
         	this.calendar = { 
		        	   reservations  : [{day : 0,timeRanges : [{startTime : '8:30',endTime : '9:00',areaInfo : {displayInfo : 'XXXXXXXX已预约'}}]},
		        	                    {day : 1,timeRanges : [{startTime : '8:40',endTime : '11:00',areaInfo : {displayInfo : '计算机学院已预约'}}]},
		        	                    {day : 5,timeRanges : [{startTime : '8:30',endTime : '11:50',areaInfo : {}}]},
		        	     	            {day : 6,timeRanges : [{startTime : '8:00',endTime : '12:00',areaInfo : {}}]}],
		        	   startTime : '2013-05-26',
       	           noReservations : [{day : 2,timeRanges : [{startTime : '9:20',endTime : '11:50',areaInfo : {'111':'222'}}]}]
	        };
            this.init();
          	if(this.options.nextPageId){
          		var nextPage = $('#' + this.options.nextPageId);
          		if(nextPage.length){
          			nextPage.click(function (){
					/**
          				$.ajax({
            				url : that.options.url,
            				data : {'startTime' :that.calendar.startTime , 'direction' : 'forward'},
            				dataType : 'json',
            				success : function (data){
            					that.calendar = data;
            					that.init();
            				}
            			});**/
						
          	          	that.calendar = { 
         		        	   reservations  : [{day : 0,timeRanges : [{startTime : '9:30',endTime : '9:10',areaInfo : {'111':'222'}}]},
         		        	     	            {day : 6,timeRanges : [{startTime : '8:50',endTime : '11:00',areaInfo : {}}]}],
         		        	   startTime : '2013-04-21',
                	           noReservations : [{day : 3,timeRanges : [{startTime : '9:20',endTime : '11:50',areaInfo : {'111':'222'}}]}]
         	        	   };
          	          	that.init();
          			});
          		}
          	}
        	if(this.options.prevPageId){
          		var prevPage = $('#' + this.options.prevPageId);
          		if(prevPage.length){
          			prevPage.click(function (){
				     /**
          			 	$.ajax({
            				url : that.options.url,
            				data : {'startTime' :that.calendar.startTime , 'direction' : 'prev'},
            				dataType : 'json',
            				success : function (data){
            					that.calendar = data;
            					that.init();
            				}
            			});*/
						
         		         	that.calendar = { 
		        	   reservations  : [{day : 0,timeRanges : [{startTime : '8:30',endTime : '9:00',areaInfo : {displayInfo : 'XXXXXXXX已预约'}}]},
		        	                    {day : 1,timeRanges : [{startTime : '8:40',endTime : '11:00',areaInfo : {displayInfo : '计算机学院已预约'}}]},
		        	                    {day : 5,timeRanges : [{startTime : '8:30',endTime : '11:50',areaInfo : {}}]},
		        	     	            {day : 6,timeRanges : [{startTime : '8:00',endTime : '12:00',areaInfo : {}}]}],
		        	   startTime : '2013-05-26',
       	           noReservations : [{day : 2,timeRanges : [{startTime : '9:20',endTime : '11:50',areaInfo : {'111':'222'}}]}]
	        };
            that.init();
          			});
          		}
          	}
    
        },
		displayCurrentTime : function (){
		    if(this.options.displayCurrentTime){
        		var currentDate  = new Date(),
        		    week = currentDate.getDay(),
        		    th = $('#' + dayIdPrefix + week),
        		    startTime = currentDate.getHours() + ":" + currentDate.getMinutes(),
        		    currentDate = new Date(currentDate.getTime() + (1000 * 60 * 5)),
        		     endTime = currentDate.getHours() + ":" +  (currentDate.getMinutes() < 10 ? "0"  + currentDate.getMinutes() : currentDate.getMinutes()),
        		    timeRange = {startTime : startTime ,endTime : endTime};
        		if(!this.isOutOfTimeRange(timeRange)){
	                this.addTimeRangeToCalendar(th,timeRange,'ssfw_calendar_currentTime');
 		        }
        	}
		},
        init : function (){
        	var table = this.table,
        	    calendar = this.calendar;
		   	    stepSize = this.options.stepSize,
		   	    that = this,
		   	    timeRanges = this.options.timeRanges,
		   	    array = calendar.startTime.split('-'),
		        date = new Date(array[0],parseInt(array[1] - 1),array[2]),
		        dayMillisecond = 1000 * 24 * 60 * 60,
				currentDate = new Date();
				
		    $('.ssfw_calendar_timeRange').remove();
		   	$('th',table).each(function (){
		   		var th = $(this),
			    id = th.attr('id') || '',
			    idPrefixIndex = id.indexOf(dayIdPrefix);
				if(idPrefixIndex >= 0){
				  	th.find('.ssfw_calendar_date').html(date.getMonth() + 1 + "月" + date.getDate() + "日");
					th.data('date',date);
					date = new Date(date.getTime() +  dayMillisecond);
				    var month = date.getMonth() ;
					var da = date.getDate();
					if(currentDate.getMonth() === month && currentDate.getDate() === da){
					    that.displayCurrentTime();
					}
				} 
		   	 });
	        
	        function process (th,day,tiemRanges,className){
	        	  for(var i = 0; i < tiemRanges.length; i++){
	    	    	  var dayDisplayInfo = tiemRanges[i];
	    	    	  if(dayDisplayInfo.day === day){
	    	    		  var timeRanges = dayDisplayInfo.timeRanges || [];
	    	    		  for(var j = 0; j < timeRanges.length; j++){
	    	    			    var timeRange = timeRanges[j];
	    	    		        if(!this.isOutOfTimeRange(timeRange)){
	    		                    this.addTimeRangeToCalendar(th,timeRange,className);
	    	    		        }
	    		    	  }
	    	    		  break;
	    	    	  }
	    	      }
	        }
            $('th',table).each(function (){
    			var th = $(this),
    			    id = th.attr('id') || '',
    			    idPrefixIndex = id.indexOf(dayIdPrefix);
    			if(idPrefixIndex >= 0){
    			      var day = parseInt(id.substring(dayIdPrefix.length));
    		          var calendar = that.calendar;
    		          if(calendar.reservations){
    		        	  process.call(that,th, day, calendar.reservations, "ssfw_calendar_reservations");
    		          }
    		  	      if(calendar.noReservations){
    		  	    	  process.call(that,th, day, calendar.noReservations, "ssfw_calendar_noreservations");
    		  	      }
    			 }
    		});	
        },
        addTimeRangeToCalendar : function (th,timeRange,className){
		    var startHour = this.getHour(timeRange.startTime),
		        startMinute = this.getMinute(timeRange.startTime),
		        endHour = this.getHour(timeRange.endTime),
		        endMinute = this.getMinute(timeRange.endTime),
		        stepSize = this.options.stepSize,
		        totalTime = ((endHour - startHour) * 60 / stepSize) + (endMinute - startMinute) / stepSize; 
	    	    cache = this.cache,
	    	    that = this,
	    	    areaInfo = timeRange.areaInfo || {};
		    var hourCell = cache[startHour];
		    if(!hourCell){
			   return;
		    }
		    totalTime -= that.getStepSize(timeRange);
		    var offset = hourCell.offset(),
		        top = offset.top + hourCell.outerHeight() * ((that.getTotalMinute(timeRange.startTime) 
		    		 - hourCell.data('timeRangeNumber').start) / stepSize),
		        left = th.offset().left + ($.browser.msie ? 0 : 1);
		    $('<div>').addClass('ssfw_calendar_timeRange').addClass(className).height(
				       hourCell.outerHeight() * totalTime - ( $.browser.msie ? 0 : 1 ))
		               .width(th.outerWidth() - ( $.browser.msie ? 0 : 1 ))
		               .data('info',timeRange || {})
		               .css({overflow : 'hidden','z-index' : (className.indexOf('currentTime') != -1 ? 999 : 0),'word-break':'break-all',
		               position : 'absolute',left : left,top : top + ($.browser.msie ? 1 : 1),})
		               .html( (totalTime >= 0.5 && areaInfo.displayInfo) ? areaInfo.displayInfo : '').click(function (e){
		            	    that._trigger("onReservationsClick",e,$(this).data('info'));
		               }).mouseenter(function (e){
		            	   that._trigger("onReservationsMouseenter",e,$(this).data('info'));
		               }).mouseleave(function (e){
		            	   that._trigger("onReservationsMouseleave",e,$(this).data('info'));
		               }).appendTo('body');
        },
        isOutOfTimeRange : function (timeRange){
        	var startTime = this.options.startTime,
        	    startMinute = this.getTotalMinute(timeRange.startTime);
	   		if(this.getTotalMinute(startTime) > startMinute){
   			   return true;
   		    }
	   		var endTime = this.options.endTime,
	   		    endMinute = this.getTotalMinute(timeRange.endTime);
	   		if(this.getTotalMinute(endTime) < endMinute){
	   			return true;
	   		}
	   		function isInTimeRange(minute){
	   	        var configTimeRange = this.options.timeRanges;
	            for(var i = 0; i < configTimeRange.length; i++){
	            	var configStartTimeMinute = this.getTotalMinute(configTimeRange[i].startTime);
	            	var configEndTimeMinute = this.getTotalMinute(configTimeRange[i].endTime);
	            	if(configStartTimeMinute <= minute && configEndTimeMinute >= minute){
	            		break;
	            	}
	            	var next = configTimeRange[i];
	            	if(next){
	            		var nextStartTime = this.getTotalMinute(next.startTime);
	            		if(configEndTimeMinute <= minute && nextStartTime >= minute){
	            			if((configEndTimeMinute + this.options.stepSize) == nextStartTime){
	            				break;
	            			}
	            		}
	            	}
	            }
	            if(i == configTimeRange.length){
	            	return true;
	            }
	            return false;
	   		}
	   		if(isInTimeRange.call(this, startMinute) || isInTimeRange.call(this, endMinute)){
	   			return true;
	   		}
            return false;
        },
        getStepSize : function (timeRange){
        	var startMinute = this.getTotalMinute(timeRange.startTime),
        	    endMinute = this.getTotalMinute(timeRange.endTime),
        	    result = 0;
        	while(startMinute < endMinute){
        		for(var i = 0;i < this.options.timeRanges.length ;i++){
        			var configTimeRange = this.options.timeRanges[i];
        			var configStartMinute = this.getTotalMinute(configTimeRange.startTime);
        			var configEndMinute = this.getTotalMinute(configTimeRange.endTime);
        			if(configStartMinute <= startMinute && configEndMinute >= startMinute){
        				break;
        			}
        		}
        		if(i == this.options.timeRanges.length){
        			result++;
        		}
        		startMinute += this.options.stepSize;
        	}
        	return result;
        },
        getTotalMinute : function (time){
        	var timeArray = time.split(':');
        	return (parseInt(timeArray[0]) * 60) + parseInt(timeArray[1]);
        },
        getHour : function (time){
        	return parseInt(time.substring(0,time.indexOf(':')));
        },
        getMinute : function (time){
        	return parseInt(time.substring(time.indexOf(':') + 1 ,time.length));
        }
    });
})(jQuery);