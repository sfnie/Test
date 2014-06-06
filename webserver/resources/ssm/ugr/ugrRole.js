var ugrRoleSM = new Ext.grid.CheckboxSelectionModel();

var ugrRoleStore = new Ext.data.JsonStore({
			autoLoad : true,
			url : ctx + '/ugr/ugrRoles/list.ssm',
			storeId : 'ugrRoleStore',
			root : 'ugrRoles',
			idProperty : 'ugrRole',
			fields : ['ugrRole']
		});

var ugrRoleGrid = new Ext.grid.GridPanel({
	id : 'ugrRoleGrid',
	title : '角色列表',
	columnWidth : 1 / 3,
	border : false,
	store : ugrRoleStore,
	cm : new Ext.grid.ColumnModel([ugrRoleSM, {
				id : 'ugrRolename',
				header : "角色名称",
				width : 120,
				sortable : true,
				dataIndex : 'ugrRole'
			}, {
				header : "操作",
				width : 120,
				renderer : function(value, cellmeta, record, rowIndex,
						columnIndex, store) {
					var link = '<a href="#" onclick="showGroupOfRole()">用户组</a>';
					link += '&nbsp;&nbsp;&nbsp;&nbsp;';
					link += '<a href="#" onclick="showUserOfRole()">用户</a>';
					return link;
				}
			}]),
	sm : ugrRoleSM,
	bodyStyle : 'height:800',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
				text : '新增角色',
				iconCls : 'add',
				handler : function() {
					// 设置表单字段值
					var data = {
						ugrRole : '',
						memo : ''
					};

					ugrRoleDlg.show({});
					ugrRoleForm.getForm().setValues(data);
				}
			}, {
				text : '删除角色',
				iconCls : 'remove',
				handler : function() {
					Ext.MessageBox.confirm('请确认', '确认删除角色？', function(btn) {
								if (btn == 'yes') {
									// 获取选中的角色列表
									var ugrRoles = new Array();
									var selections = ugrRoleGrid
											.getSelectionModel()
											.getSelections();
									for (var i = 0; i < selections.length; i++) {
										var r = selections[i];
										ugrRoles[ugrRoles.length] = r
												.get('ugrRole');
									}

									Ext.Ajax.request({
												url : ctx + '/ugr/ugrRoles/delete.ssm',
												method : 'post',
												params : {
													'ugrRoles' : Ext.util.JSON
															.encode(ugrRoles)
												},
												success : function(response,
														options) {
													ugrRoleStore.reload({

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

// 角色编辑表单
var ugrRoleForm = new Ext.FormPanel({
			baseCls : 'x-plain',
			labelWidth : 75,
			labelSeparator : ':',
			defaultType : 'textfield',
			items : [{
						fieldLabel : '角色名称',
						name : 'ugrRole',
						allowBlank : false,
						width : 540
					}, {
						fieldLabel : '角色描述',
						xtype : 'htmleditor',
						name : 'memo',
						allowBlank : false,
						width : 540,
						height : 300
					}]
		});

// 模块编辑对话框
var ugrRoleDlg = new Ext.Window({
			width : 680,
			height : 420,
			title : '角色编辑',
			plain : true,
			closable : true,
			resizable : true,
			layout : 'fit',
			closeAction : 'hide',
			modal : true,
			buttonAlign : 'center',
			hideMode : 'offsets',
			constrainHeader : true,
			bodyStyle : 'padding:5px;',
			items : ugrRoleForm,
			buttons : [{
						text : '保存',
						scope : this,
						handler : saveUgrRole
					}, {
						text : '取消',
						scope : this,
						handler : cancelUgrRole
					}],
			listeners : {
				render : function() {
				}
			}
		});

function saveUgrRole() {
	var url = ctx + '/ugr/ugrRoles/add.ssm';

	ugrRoleForm.getForm().submit({
				url : url,
				method : 'post',
				waitMsg : '正在提交...',
				timeout : 60000,// 60秒超时
				success : function(form, action) {
					var isSuc = action.result.success;
					if (isSuc) {
						ugrRoleDlg.hide({});
						ugrRoleStore.reload({});
					} else {
						Ext.Msg.alert('消息', '保存失败!');
					}

				},
				failure : function(form, action) {
					Ext.Msg.alert('消息', '保存失败!');
				}
			});

}

function cancelUgrRole() {
	ugrRoleDlg.hide({});
}

function showGroupOfRole() {
	var selections = ugrRoleGrid.getSelectionModel().getSelections();
	current_selected_role = selections[0].get('ugrRole');
	selectGroupOfRoleDlg.show({});
}

function showUserOfRole() {
	var selections = ugrRoleGrid.getSelectionModel().getSelections();
	current_selected_role = selections[0].get('ugrRole');
	selectUserOfRoleDlg.show({});
}
