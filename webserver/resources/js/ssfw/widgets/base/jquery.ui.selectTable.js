/**
 * author : qsyan
 */
(function($) {
	
	$.widget("ui.selectTable", $.ui.selectRange, {
		options : {
			url : null,
			pageSize : 10,
			pageNo : 1,  // TODO 改变时刷新数据。
			title : '请选择',
			columns : null
		},
		_create : function() {
			this._super();
			if (!this.options.url && this.options.baseType) {
				this.options.url = ssfwConfig.combotableUrl + "?name="
					    + this.options.baseType;
			}
		},	
		destroy : function (){
			var dialog = $('#' + this.dialogId).data('dialog');
			if(dialog){
				dialog.destroy();
			}
			if(this.scrollPanel){
				this.scrollPanel.parent().remove();
			}
			this._super();
		},
		refreshAndRender : function (pageNo){
			if(pageNo === undefined){
				pageNo = this.options.pageNo;
			}
			var that = this,table = this.table,name = this.input.val();
			$.ajax({
				url : that.options.url,
				type : 'post',
				dataType : 'json',
				data : ('pageCount=' + that.options.pageSize + '&currentPage=' 
						+ pageNo + '&condition=' + (name === undefined ? '' : encodeURI(name))),
				success : function(data) {
					while (table.rows.length > 1) {
						table.deleteRow(1);
					}
					$(table).show();
					if (data && data.totalCount > 0) {
						for ( var i = 0; i < data.data.length; i++) {
							 if (i >= that.options.pageSize) {
								break;
							 }
							(function (){
								var entity = data.data[i];
								if(entity){
									var cell = $(table.insertRow(table.rows.length));
									if (i % 2 == 0) {
										cell.css('backgroundColor', '#F6FBFF');
									} else {
										cell.css('backgroundColor', 'white');
									}
									cell.addClass('t_con').click(function() {
										if(that.currentActive){
											that.currentActive.css('backgroundColor',
													that.currentActive.data('metadata').oldBackgroundColor);
										}
										$(this).data('metadata', {entity : entity, 
											   oldBackgroundColor : $(this).css('backgroundColor')}).css('backgroundColor','#CEDCEF');
										that.currentActive = $(this);
									}).dblclick(function (e){
										e.preventDefault();
									    if(that._trigger("beforeselect",e,entity) === false){
					                        return;
					                    }
                                        that.setValue(entity.value,entity.text);
										$('#' + that.dialogId).dialog('close');
										that._trigger("afterselect",e,entity);
									}).mousedown(function (e){
										e.preventDefault();
									});
									var index = 0;
									for ( var prop in that.columnCache) {
                                        var column = cell[0].insertCell(index);
									    var val = entity[prop];
									    if(!val){
                                            val = " "; 
									    }
                                        column.innerHTML = val;
									    index++;
									}
								}
							})();
						}
						var totalPage = data.totalCount % that.options.pageSize == 0 ? 
								data.totalCount / that.options.pageSize : data.totalCount / that.options.pageSize + 1 ;
						totalPage = parseInt(totalPage);
						that.pageContainer.html('<span>共<i>'+data.totalCount+'</i>条记录</span><span>当前是第<i>'+pageNo+'</i>页</span> <span>共<i>'+totalPage+'</i>页</span>');
	                    $('<a/>').text('首页').click(function (e){
	                    	 e.preventDefault();
	                    	 that.refreshAndRender(1);	
	                    	 return false;
						}).appendTo(that.pageContainer).css('cursor','pointer');
	                    
	                    $('<a/>').text('上一页').click(function (e){
	                    	 e.preventDefault();
	                    	 that.refreshAndRender(pageNo <=  1 ? 1 : --pageNo);	
	                    	 return false;
						}).appendTo(that.pageContainer).css('cursor','pointer');
	                    
	                    $('<a/>').text('下一页').click(function (e){
	                    	 e.preventDefault();
	                    	 that.refreshAndRender(pageNo >= totalPage ? totalPage : ++pageNo);	
	                    	 return false;
						}).appendTo(that.pageContainer).css('cursor','pointer');
	                    
                          $('<a/>').text('末页').click(function (e){
                        	 e.preventDefault();
					         that.refreshAndRender(totalPage);	
		                     return false;
						}).appendTo(that.pageContainer).css('cursor','pointer');
						that.pageContainer.addClass('page_nav').css({'bottom':0,'right':1});
						that.input.focus();
						$('a',that.pageContainer).mousedown(function (e){
                       	    e.preventDefault();
						});
					}
				}
			});
		},
		_onSwitchButtonClick : function() {
			var that = this,
			    instance = $('#' + this.dialogId).data('dialog'),
			    columns = this.options.columns;
			if(instance){
			    instance.open();
			    return;
			}
			if (!columns) {
                //TODO 没配置column。
			    return;
			}
			var table = this.table = document.createElement('TABLE'),
			    defualtColumnWidth = 200,
                            heander = table.insertRow(0),
                            columnTotalWidth = 0,
                            uniqueId = this.uuid;
			table.className = 'table_con';
			table.cellPadding = '0';
			table.cellSpacing = '0';
			table.style.textAlign = 'center';
			heander.className = 'tableTr';
			this.columnCache = {};
			for(var i = 0; i < columns.length; i++){
				var col = columns[i],
                                    td = heander.insertCell(i);
				td.className = 'tableTr';
                                td.innerHTML = col.header;
				if(col.width){
			            td.style.width = col.width + 'px';
				    columnTotalWidth += parseInt(col.width);
				}else{
				    columnTotalWidth += defaultColumnWidth;
			            td.style.width = defualtColumnWidth + 'px';
				}
				this.columnCache[col.name] = i;
			}
			table.style.display = 'none';
			table.style.width = "100%";
			this.dialogId = this.widgetName +'_' + uniqueId;
			var tableDialog = this.tableDialog = $('<div/>').attr('id', this.dialogId),
                            dialogMinWidth = this.dialogMinWidth = 440,
                            dialogMinHeight = this.dialogMinHeight = 155 + (34 * this.options.pageSize),
                            dialogWidth = columnTotalWidth + 40 < dialogMinWidth ? dialogMinWidth : columnTotalWidth + 40,
                            scrollPanel = this.scrollPanel = $('<div/>').css({'height' : (34 * this.options.pageSize) + 10 
                                ,'overflowY':'auto','marginTop':'5','position':'relative'}).append(table).append(table);
			$('body').append(scrollPanel);
			$(scrollPanel).wrap(tableDialog);
			var dialogContent = $('#' + this.dialogId).dialog({
				autoOpen : true,
				modal : true,
				title : this.options.title,
				width : dialogWidth,
                minWidth : dialogWidth,
				height :  dialogMinHeight,  
                minHeight : dialogMinHeight,
				buttons : {
					"选择" : function(e) {
						if(that.currentActive){
							var metadata = that.currentActive.data('metadata');
			                if(that._trigger("beforeselect",e,metadata.entity) === false){
		                        return;
		                    }
							that.setText(metadata.entity.text); 
							that.hidden.val(metadata.entity.value);
							this.selected = that.currentActive;
							$(this).dialog("close");
							that._trigger("afterselect",e,metadata.entity);
						}
					},
					'取消' : function() {
						if(that.currentActive){
	                        if(that.currentActive != this.selected){
	                        	that.currentActive.css('backgroundColor',function (){
	                        		return $(this).data('metadata').oldBackgroundColor;
	                        	});
	                        	if(this.selected){
		                        	this.selected.css('backgroundColor','#CEDCEF'); 
		                        	that.currentActive = this.selected;
	                        	}
							}
						}
						$(this).dialog("close");
					}
				}
			});
			dialogContent.css('overflowX','hidden');
			var searchTable = document.createElement('TABLE'),
			    row = searchTable.insertRow(0),
			    buttonColumn = row.insertCell(0),
			    inputColumn = row.insertCell(0),
			    labelColumn = row.insertCell(0),
			    input = this.input = $('<input/>'),
			    inputWidth = 200;
			$(labelColumn).html('搜索  : ');
			$(inputColumn).append(input).css('width',inputWidth + 10).keyup(function (e){
				if(e.keyCode === $.ui.keyCode.ENTER){
					if(input.val()){
						that.refreshAndRender(1);
					}
				}else{
					var inst = input.data('inputText');
					if(inst.getText() == ""){
						$('.ui-inputText-x-trigger',inst.container).hide();
						inst.element.focus();
						return;
					}
					var deleteButton = $('.ui-inputText-x-trigger',inst.container);
					if(!deleteButton.length){
						deleteButton = $('<span/>').text('x').addClass('ui-inputText-x-trigger').click(function (){
		 				     inst.setText('');
						     $(this).hide();
						     that.refreshAndRender(1);
						});
						var elem = inst.element;
						elem.before(deleteButton).width(elem.width() - 7);
					}
					deleteButton.show();
				}
			});
			input.inputText({width : inputWidth});
			var button = $('<button/>');
			button.text('搜索');
			button.css('display','inline');
			button.click($.proxy(function (e){
			      this.refreshAndRender();
			},this));
                        $(buttonColumn).append(button).css('width',100);
			button.button();
                        this.searchContainer = $('<div/>').append(searchTable).css('position','relative');
			$(scrollPanel).before(this.searchContainer);
			this.pageContainer = $('<div/>').css('position','absolute');
			$(scrollPanel).after(this.pageContainer);
			this.refreshAndRender(1);
		}
	});
})(jQuery);
