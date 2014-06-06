var ugrUserSM = new Ext.grid.CheckboxSelectionModel();

var ugrUserStore = new Ext.data.JsonStore({
			autoLoad : true,
			url : ctx + '/ugr/ugrUsers/list.ssm',
			storeId : 'ugrUserStore',
			root : 'ugrUsers',
			idProperty : 'wid',
			fields : ['wid', 'name']
		});

var ugrUserGrid = new Ext.grid.GridPanel({
	id : 'ugrUserGrid',
	title : '用户列表',
	columnWidth : 1 / 3,
	border : false,
	store : ugrUserStore,
	cm : new Ext.grid.ColumnModel([ugrUserSM, {
				id : 'ugrUsername',
				header : "用户名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}, {
				header : "操作",
				width : 120,
				renderer : function(value, cellmeta, record, rowIndex,
						columnIndex, store) {
					var link = '<a href="#" onclick="showRoleOfUser()">角色</a>&nbsp;&nbsp;';
					link += '&nbsp;&nbsp;';
					link += '<a href="#" onclick="showGroupOfUser()">用户组</a>';
					
					return link;
				}
			}]),
	sm : ugrUserSM,
	bodyStyle : 'height:800',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
				text : '新增用户',
				iconCls : 'add',
				handler : function() {
					// 设置表单字段值
					var data = {
						ugrUser : '',
						memo : ''
					};

					ugrUserDlg.show({});
					ugrUserForm.getForm().setValues(data);
				}
			}, {
				text : '删除用户',
				iconCls : 'remove',
				handler : function() {
					Ext.MessageBox.confirm('请确认', '确认删除用户？', function(btn) {
								if (btn == 'yes') {
									// 获取选中的用户组列表
									var ugrUsers = new Array();
									var selections = ugrUserGrid
											.getSelectionModel()
											.getSelections();
									for (var i = 0; i < selections.length; i++) {
										var r = selections[i];
										ugrUsers[ugrUsers.length] = r
												.get('wid');
									}

									Ext.Ajax.request({
												url : ctx + '/ugr/ugrUsers/delete.ssm',
												method : 'post',
												params : {
													'ugrUsers' : Ext.util.JSON
															.encode(ugrUsers)
												},
												success : function(response,
														options) {
													ugrUserStore.reload({

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
			}, '-', {
				xtype : 'textfield',
				width : 120

			}, {
				text : '查找',
				handler : function() {
				}
			}],

	iconCls : 'icon-grid'
});

// 用户组编辑表单
var ugrUserForm = new Ext.FormPanel({
			baseCls : 'x-plain',
			labelWidth : 75,
			labelSeparator : ':',
			defaultType : 'textfield',
			items : [{
						fieldLabel : '用户名称',
						name : 'ugrUser',
						allowBlank : false,
						width : 540
					}, {
						fieldLabel : '用户描述',
						xtype : 'htmleditor',
						name : 'memo',
						allowBlank : false,
						width : 540,
						height : 300
					}]
		});

// 模块编辑对话框
var ugrUserDlg = new Ext.Window({
			width : 680,
			height : 420,
			title : '用户编辑',
			closable : true,
			resizable : true,
			layout : 'fit',
			closeAction : 'hide',
			modal : true,
			buttonAlign : 'center',
			bodyStyle : 'padding:5px;',
			items : ugrUserForm,
			buttons : [{
						text : '保存',
						scope : this,
						handler : saveUgrUser
					}, {
						text : '取消',
						scope : this,
						handler : cancelUgrUser
					}],
			listeners : {
				render : function() {
				}
			}
		});

function saveUgrUser() {
	var url = ctx + '/ugr/ugrUsers/add.ssm';

	ugrUserForm.getForm().submit({
				url : url,
				method : 'post',
				waitMsg : '正在提交...',
				timeout : 60000,// 60秒超时
				success : function(form, action) {
					var isSuc = action.result.success;
					if (isSuc) {
						ugrUserDlg.hide({});
						ugrUserStore.reload({});
					} else {
						Ext.Msg.alert('消息', '保存失败!');
					}

				},
				failure : function(form, action) {
					Ext.Msg.alert('消息', '保存失败!');
				}
			});

}

function cancelUgrUser() {
	ugrUserDlg.hide({});
}

function showGroupOfUser() {
	var selections = ugrUserGrid.getSelectionModel().getSelections();
	current_selected_user = selections[0].get('wid');
	selectGroupOfUserDlg.show({});
}

function showRoleOfUser() {
	var selections = ugrUserGrid.getSelectionModel().getSelections();
	current_selected_user = selections[0].get('wid');
	selectRoleOfUserDlg.show({});
}
