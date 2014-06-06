var ugrGroupSM = new Ext.grid.CheckboxSelectionModel();

var ugrGroupStore = new Ext.data.JsonStore({
			autoLoad : true,
			url : ctx + '/ugr/ugrGroups/list.ssm',
			storeId : 'ugrGroupStore',
			root : 'ugrGroups',
			idProperty : 'wid',
			fields : ['wid', 'name']
		});

var ugrGroupGrid = new Ext.grid.GridPanel({
	id : 'ugrGroupGrid',
	title : '用户组列表',
	columnWidth : 1 / 3,
	border : false,
	store : ugrGroupStore,
	cm : new Ext.grid.ColumnModel([ugrGroupSM, {
				id : 'ugrGroupname',
				header : "用户组名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}, {
				header : "操作",
				width : 120,
				renderer : function(value, cellmeta, record, rowIndex,
						columnIndex, store) {
					var link = '<a href="#" onclick="showRoleOfGroup()">角色</a>&nbsp;&nbsp;';
					link += '&nbsp;&nbsp;';
					link += '<a href="#" onclick="showUserOfGroup()">用户</a>';
					return link;
				}
			}]),
	sm : ugrGroupSM,
	bodyStyle : 'height:800',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
				text : '新增用户组',
				iconCls : 'add',
				handler : function() {
					// 设置表单字段值
					var data = {
						ugrGroup : '',
						memo : ''
					};

					ugrGroupDlg.show({});
					ugrGroupForm.getForm().setValues(data);
				}
			}, {
				text : '删除用户组',
				iconCls : 'remove',
				handler : function() {
					Ext.MessageBox.confirm('请确认', '确认删除用户组？', function(btn) {
								if (btn == 'yes') {
									// 获取选中的用户组列表
									var ugrGroups = new Array();
									var selections = ugrGroupGrid
											.getSelectionModel()
											.getSelections();
									for (var i = 0; i < selections.length; i++) {
										var r = selections[i];
										ugrGroups[ugrGroups.length] = r
												.get('wid');
									}

									Ext.Ajax.request({
												url : ctx + '/ugr/ugrGroups/delete.ssm',
												method : 'post',
												params : {
													'ugrGroups' : Ext.util.JSON
															.encode(ugrGroups)
												},
												success : function(response,
														options) {
													ugrGroupStore.reload({

													});
												},
												failure : function(response,
														options) {
													alert('删除失败！');
												}
											});
								}
							});
				}
			}],

	iconCls : 'icon-grid'
});

// 用户组编辑表单
var ugrGroupForm = new Ext.FormPanel({
			baseCls : 'x-plain',
			labelWidth : 75,
			labelSeparator : ':',
			defaultType : 'textfield',
			items : [{
						fieldLabel : '用户组名称',
						name : 'ugrGroup',
						allowBlank : false,
						width : 540
					}, {
						fieldLabel : '用户组描述',
						xtype : 'htmleditor',
						name : 'memo',
						allowBlank : false,
						width : 540,
						height : 300
					}]
		});

// 模块编辑对话框
var ugrGroupDlg = new Ext.Window({
			width : 680,
			height : 420,
			title : '用户组编辑',
			closable : true,
			resizable : true,
			layout : 'fit',
			closeAction : 'hide',
			modal : true,
			buttonAlign : 'center',
			bodyStyle : 'padding:5px;',
			items : ugrGroupForm,
			buttons : [{
						text : '保存',
						scope : this,
						handler : saveUgrGroup
					}, {
						text : '取消',
						scope : this,
						handler : cancelUgrGroup
					}],
			listeners : {
				render : function() {
				}
			}
		});

function saveUgrGroup() {
	var url = ctx + '/ugr/ugrGroups/add.ssm';

	ugrGroupForm.getForm().submit({
				url : url,
				method : 'post',
				waitMsg : '正在提交...',
				timeout : 60000,// 60秒超时
				success : function(form, action) {
					var isSuc = action.result.success;
					if (isSuc) {
						ugrGroupDlg.hide({});
						ugrGroupStore.reload({});
					} else {
						Ext.Msg.alert('消息', '保存失败!');
					}

				},
				failure : function(form, action) {
					Ext.Msg.alert('消息', '保存失败!');
				}
			});

}

function cancelUgrGroup() {
	ugrGroupDlg.hide({});
}

function showRoleOfGroup() {
	var selections = ugrGroupGrid.getSelectionModel().getSelections();
	current_selected_group = selections[0].get('wid');
	selectRoleOfGroupDlg.show({});
}

function showUserOfGroup() {
	var selections = ugrGroupGrid.getSelectionModel().getSelections();
	current_selected_group = selections[0].get('wid');
	selectUserOfGroupDlg.show({});
}
