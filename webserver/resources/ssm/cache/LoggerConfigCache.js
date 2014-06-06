var loggerConfigCacheElementSM = new Ext.grid.CheckboxSelectionModel();

var loggerConfigCacheElementStore = new Ext.data.JsonStore({
			autoLoad : true,
			url : ctx + '/cache/findCacheElementKeys.ssm',
			baseParams : {
				cacheName : 'loggerConfigCache'
			},
			root : 'elementKeys',
			idProperty : 'elementKey',
			fields : ['elementKey', 'elementKeyStr']
		});

/**
 * 缓存元素表格
 */
var loggerConfigCacheElementGrid = new Ext.grid.GridPanel({
	id : 'loggerConfigCacheElementGrid',
	title : '日志配置元素列表',
	region : 'center',
	store : loggerConfigCacheElementStore,
	cm : new Ext.grid.ColumnModel([loggerConfigCacheElementSM, {
				header : "元素名称",
				width : 160,
				sortable : true,
				dataIndex : 'elementKeyStr'
			}]),

	sm : loggerConfigCacheElementSM,

	viewConfig : {
		forceFit : true
	},

	listeners : {
		rowdblclick : function(g, rowIndex, e) {

		}
	},

	tbar : [{
				id : 'loggerConfigCache_element_textfield',
				xtype : 'textfield',
				width : 200
			}, {
				text : '查询',
				iconCls : 'search',
				handler : function() {
					var filterStr = Ext.getCmp('loggerConfigCache_element_textfield')
							.getValue();
					loggerConfigCacheElementStore.reload({
								params : {
									cacheName : 'loggerConfigCache',
									filterStr : filterStr
								}
							});
				}
			}, '-', {
				text : '清除',
				iconCls : 'remove',
				handler : function() {
					var elementKeys = new Array();
					var selRecords = loggerConfigCacheElementGrid.getSelectionModel()
							.getSelections();
					for (var i = 0; i < selRecords.length; i++) {
						var elementKey = selRecords[i].get("elementKey");
						elementKeys[elementKeys.length] = elementKey;
					}

					Ext.MessageBox.confirm('提示', '确认删除选中缓存项么？',
							function(btnId) {
								if ('yes' == btnId) {
									Ext.Ajax.request({
										url : ctx
												+ '/cache/clearCacheElementKeys.ssm',
										method : 'post',
										params : {
											'cacheName' : 'loggerConfigCache',
											'elementKeys' : Ext.util.JSON
													.encode(elementKeys)
										},
										success : function(response, options) {
											var isSuc = Ext.util.JSON
													.decode(response.responseText).success;
											if (isSuc) {
												var filterStr = Ext
														.getCmp('loggerConfigCache_element_textfield')
														.getValue();
												loggerConfigCacheElementStore.reload(
														{
															params : {
																cacheName : 'loggerConfigCache',
																filterStr : filterStr
															}
														});
											} else {
												Ext.Msg.alert('消息', '删除失败！');
											}

										},
										failure : function(response, options) {
											Ext.Msg.alert('消息', '删除失败！');
										}
									});
								}
							});
				}
			}],

	iconCls : 'icon-grid'
});