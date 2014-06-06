var selectedRoleOfGroupSM = new Ext.grid.CheckboxSelectionModel();
var selectedRoleOfGroupStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectRole/listSelectedOfGroup.ssm',
			storeId : 'selectedRoleOfGroupStore',
			root : 'roles',
			idProperty : 'role',
			fields : ['role']
		});
var selectedRoleOfGroupGrid = new Ext.grid.GridPanel({
	id : 'selectedRoleOfGroupGrid',
	title : '已选择角色列表',
	columnWidth : 0.5,
	border : false,
	store : selectedRoleOfGroupStore,
	cm : new Ext.grid.ColumnModel([selectedRoleOfGroupSM, {
				id : 'selectedRoleOfGroupName',
				header : "角色名称",
				width : 120,
				sortable : true,
				dataIndex : 'role'
			}]),
	sm : selectedRoleOfGroupSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '取消选择',
		iconCls : 'remove',
		handler : function() {
			// 获取待取消的角色
			var roles = new Array();
			var selections = selectedRoleOfGroupGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				roles[roles.length] = selections[i].get('role');
			}
			// 删除选中角色与当前用户组对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectRole/cancelOfGroup.ssm',
				method : 'post',
				params : {
					'groupWid' : current_selected_group,
					'roles' : Ext.util.JSON.encode(roles)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadRoleOfGroup();
					} else {
						Ext.Msg.alert('消息', '删除失败！');
					}

				},
				failure : function(response, options) {
					Ext.Msg.alert('消息', '删除失败！');
				}
			});
		}
	}],

	iconCls : 'icon-grid'
});

// ////////////////////////////////////////////////////////////////////////
var unSelectedRoleOfGroupSM = new Ext.grid.CheckboxSelectionModel();
var unSelectedRoleOfGroupStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectRole/listUnSelectedOfGroup.ssm',
			storeId : 'unSelectedRoleOfGroupStore',
			root : 'roles',
			idProperty : 'role',
			fields : ['role']
		});
var unSelectedRoleOfGroupGrid = new Ext.grid.GridPanel({
	id : 'unSelectedRoleOfGroupGrid',
	title : '未选择角色列表',
	columnWidth : 0.5,
	border : false,
	store : unSelectedRoleOfGroupStore,
	cm : new Ext.grid.ColumnModel([unSelectedRoleOfGroupSM, {
				id : 'unSelectedRoleOfGroupName',
				header : "角色名称",
				width : 120,
				sortable : true,
				dataIndex : 'role'
			}]),
	sm : unSelectedRoleOfGroupSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '添加选择',
		iconCls : 'add',
		handler : function() {
			// 获取待取消的角色
			var roles = new Array();
			var selections = unSelectedRoleOfGroupGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				roles[roles.length] = selections[i].get('role');
			}
			// 删除选中角色与当前用户组对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectRole/addOfGroup.ssm',
				method : 'post',
				params : {
					'groupWid' : current_selected_group,
					'roles' : Ext.util.JSON.encode(roles)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadRoleOfGroup();
					} else {
						Ext.Msg.alert('消息', '添加失败！');
					}

				},
				failure : function(response, options) {
					Ext.Msg.alert('消息', '添加失败！');
				}
			});
		}
	}],

	iconCls : 'icon-grid'
});
// //////////////////////////////////////////////////////////////////////

var selectRoleOfGroupPanel = new Ext.Panel({
			layout : 'column',
			items : [unSelectedRoleOfGroupGrid, selectedRoleOfGroupGrid]
		});

// 选择用户组对话框
var selectRoleOfGroupDlg = new Ext.Window({
			width : 680,
			height : 420,
			title : '用户组选择',
			closable : true,
			resizable : true,
			layout : 'fit',
			closeAction : 'hide',
			modal : true,
			buttonAlign : 'right',
			bodyStyle : 'padding:5px;',
			items : selectRoleOfGroupPanel,
			buttons : [{
						text : '确定',
						scope : this,
						handler : function() {
							selectRoleOfGroupDlg.hide({});
						}
					}],
			listeners : {
				'show' : function() {
					reloadRoleOfGroup();
				}
			}
		});

function reloadRoleOfGroup() {
	selectedRoleOfGroupStore.reload({
				params : {
					groupWid : current_selected_group
				}
			});

	unSelectedRoleOfGroupStore.reload({
				params : {
					groupWid : current_selected_group
				}
			});
}

// ///////////////////////////////////////////////////////////////////////////
var selectedRoleOfUserSM = new Ext.grid.CheckboxSelectionModel();
var selectedRoleOfUserStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectRole/listSelectedOfUser.ssm',
			storeId : 'selectedRoleOfUserStore',
			root : 'roles',
			idProperty : 'role',
			fields : ['role']
		});
var selectedRoleOfUserGrid = new Ext.grid.GridPanel({
	id : 'selectedRoleOfUserGrid',
	title : '已选择角色列表',
	columnWidth : 0.5,
	border : false,
	store : selectedRoleOfUserStore,
	cm : new Ext.grid.ColumnModel([selectedRoleOfUserSM, {
				id : 'selectedRoleOfUserName',
				header : "角色名称",
				width : 120,
				sortable : true,
				dataIndex : 'role'
			}]),
	sm : selectedRoleOfUserSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '取消选择',
		iconCls : 'remove',
		handler : function() {
			// 获取待取消的角色
			var roles = new Array();
			var selections = selectedRoleOfUserGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				roles[roles.length] = selections[i].get('role');
			}
			// 删除选中角色与当前用户对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectRole/cancelOfUser.ssm',
				method : 'post',
				params : {
					'userWid' : current_selected_user,
					'roles' : Ext.util.JSON.encode(roles)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadRoleOfUser();
					} else {
						Ext.Msg.alert('消息', '添加失败！');
					}

				},
				failure : function(response, options) {
					Ext.Msg.alert('消息', '添加失败！');
				}
			});
		}
	}],

	iconCls : 'icon-grid'
});

// ////////////////////////////////////////////////////////////////////////
var unSelectedRoleOfUserSM = new Ext.grid.CheckboxSelectionModel();
var unSelectedRoleOfUserStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectRole/listUnSelectedOfUser.ssm',
			storeId : 'unSelectedRoleOfUserStore',
			root : 'roles',
			idProperty : 'role',
			fields : ['role']
		});
var unSelectedRoleOfUserGrid = new Ext.grid.GridPanel({
	id : 'unSelectedRoleOfUserGrid',
	title : '未选择角色列表',
	columnWidth : 0.5,
	border : false,
	store : unSelectedRoleOfUserStore,
	cm : new Ext.grid.ColumnModel([unSelectedRoleOfUserSM, {
				id : 'unSelectedRoleOfUserName',
				header : "角色名称",
				width : 120,
				sortable : true,
				dataIndex : 'role'
			}]),
	sm : unSelectedRoleOfUserSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '添加选择',
		iconCls : 'add',
		handler : function() {
			// 获取待选择的角色
			var roles = new Array();
			var selections = unSelectedRoleOfUserGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				roles[roles.length] = selections[i].get('role');
			}
			// 添加选中角色与当前用户对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectRole/addOfUser.ssm',
				method : 'post',
				params : {
					'userWid' : current_selected_user,
					'roles' : Ext.util.JSON.encode(roles)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadRoleOfUser();
					} else {
						Ext.Msg.alert('消息', '添加失败！');
					}

				},
				failure : function(response, options) {
					Ext.Msg.alert('消息', '添加失败！');
				}
			});
		}
	}],

	iconCls : 'icon-grid'
});
// //////////////////////////////////////////////////////////////////////

var selectRoleOfUserPanel = new Ext.Panel({
			layout : 'column',
			items : [unSelectedRoleOfUserGrid, selectedRoleOfUserGrid]
		});

// 选择用户组对话框
var selectRoleOfUserDlg = new Ext.Window({
			width : 680,
			height : 420,
			title : '用户组选择',
			closable : true,
			resizable : true,
			layout : 'fit',
			closeAction : 'hide',
			modal : true,
			buttonAlign : 'right',
			bodyStyle : 'padding:5px;',
			items : selectRoleOfUserPanel,
			buttons : [{
						text : '确定',
						scope : this,
						handler : function() {
							selectRoleOfUserDlg.hide({});
						}
					}],
			listeners : {
				'show' : function() {
					reloadRoleOfUser();
				}
			}
		});

function reloadRoleOfUser() {
	selectedRoleOfUserStore.reload({
				params : {
					userWid : current_selected_user
				}
			});

	unSelectedRoleOfUserStore.reload({
				params : {
					userWid : current_selected_user
				}
			});
}
