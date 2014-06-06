var securityCacheElementSM = new Ext.grid.CheckboxSelectionModel();

var securityCacheElementStore = new Ext.data.JsonStore({
			autoLoad : true,
			url : ctx + '/cache/findCacheElementKeys.ssm',
			baseParams : {
				cacheName : 'securityCache'
			},
			root : 'elementKeys',
			idProperty : 'elementKey',
			fields : ['elementKey', 'elementKeyStr']
		});

/**
 * 缓存元素表格
 */
var securityCacheElementGrid = new Ext.grid.GridPanel({
	id : 'securityCacheElementGrid',
	title : '安全元数据元素列表',
	region : 'center',
	store : securityCacheElementStore,
	cm : new Ext.grid.ColumnModel([securityCacheElementSM, {
				header : "元素名称",
				width : 160,
				sortable : true,
				dataIndex : 'elementKeyStr'
			}]),

	sm : securityCacheElementSM,

	viewConfig : {
		forceFit : true
	},

	listeners : {
		rowdblclick : function(g, rowIndex, e) {

		}
	},

	tbar : [{
				id : 'securityCache_element_textfield',
				xtype : 'textfield',
				width : 200
			}, {
				text : '查询',
				iconCls : 'search',
				handler : function() {
					var filterStr = Ext.getCmp('securityCache_element_textfield')
							.getValue();
					securityCacheElementStore.reload({
								params : {
									cacheName : 'securityCache',
									filterStr : filterStr
								}
							});
				}
			}, '-', {
				text : '清除',
				iconCls : 'remove',
				handler : function() {
					var elementKeys = new Array();
					var selRecords = securityCacheElementGrid.getSelectionModel()
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
											'cacheName' : 'securityCache',
											'elementKeys' : Ext.util.JSON
													.encode(elementKeys)
										},
										success : function(response, options) {
											var isSuc = Ext.util.JSON
													.decode(response.responseText).success;
											if (isSuc) {
												var filterStr = Ext
														.getCmp('securityCache_element_textfield')
														.getValue();
												securityCacheElementStore.reload(
														{
															params : {
																cacheName : 'securityCache',
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