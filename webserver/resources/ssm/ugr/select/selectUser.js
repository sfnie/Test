var selectedUserOfRoleSM = new Ext.grid.CheckboxSelectionModel();
var selectedUserOfRoleStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + 'ugr/select/selectUser/listSelectedOfRole.ssm',
			storeId : 'selectedUserOfRoleStore',
			root : 'users',
			idProperty : 'wid',
			fields : ['wid', 'name', 'email']
		});
var selectedUserOfRoleGrid = new Ext.grid.GridPanel({
	id : 'selectedUserOfRoleGrid',
	title : '已选择用户列表',
	columnWidth : 0.5,
	border : false,
	autoScroll : true,
	store : selectedUserOfRoleStore,
	cm : new Ext.grid.ColumnModel([selectedUserOfRoleSM, {
				id : 'selectedUserOfRoleName',
				header : "用户名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : selectedUserOfRoleSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '取消选择',
		iconCls : 'remove',
		handler : function() {
			// 获取待取消的用户
			var userWids = new Array();
			var selections = selectedUserOfRoleGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				userWids[userWids.length] = selections[i].get('wid');
			}
			// 删除选中用户与当前角色对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectUser/cancelOfRole.ssm',
				method : 'post',
				params : {
					'role' : current_selected_role,
					'userWids' : Ext.util.JSON.encode(userWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadUserOfRole();
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
var unSelectedUserOfRoleSM = new Ext.grid.CheckboxSelectionModel();
var unSelectedUserOfRoleStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectUser/listUnSelectedOfRole.ssm',
			storeId : 'unSelectedUserOfRoleStore',
			root : 'users',
			idProperty : 'wid',
			fields : ['wid', 'name', 'email']
		});
var unSelectedUserOfRoleGrid = new Ext.grid.GridPanel({
	id : 'unSelectedUserOfRoleGrid',
	title : '未选择用户列表',
	columnWidth : 0.5,
	border : false,
	autoScroll : true,
	store : unSelectedUserOfRoleStore,
	cm : new Ext.grid.ColumnModel([unSelectedUserOfRoleSM, {
				id : 'unSelectedUserOfRoleName',
				header : "用户名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : unSelectedUserOfRoleSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '添加选择',
		iconCls : 'add',
		handler : function() {
			// 获取待添加的用户
			var userWids = new Array();
			var selections = unSelectedUserOfRoleGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				userWids[userWids.length] = selections[i].get('wid');
			}
			// 添加选中用户与当前角色对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectUser/addOfRole.ssm',
				method : 'post',
				params : {
					'role' : current_selected_role,
					'userWids' : Ext.util.JSON.encode(userWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadUserOfRole();
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

var selectUserOfRolePanel = new Ext.Panel({
			layout : 'column',
			items : [unSelectedUserOfRoleGrid, selectedUserOfRoleGrid]
		});

// 选择用户对话框
var selectUserOfRoleDlg = new Ext.Window({
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
			items : selectUserOfRolePanel,
			buttons : [{
						text : '确定',
						scope : this,
						handler : function() {
							selectUserOfRoleDlg.hide({});
						}
					}],
			listeners : {
				'show' : function() {
					reloadUserOfRole();
				}
			}
		});

function reloadUserOfRole() {
	selectedUserOfRoleStore.reload({
				params : {
					role : current_selected_role
				}
			});

	unSelectedUserOfRoleStore.reload({
				params : {
					role : current_selected_role
				}
			});
}

// ///////////////////////////////////////////////////////////////////////////
var selectedUserOfGroupSM = new Ext.grid.CheckboxSelectionModel();
var selectedUserOfGroupStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectUser/listSelectedOfGroup.ssm',
			storeId : 'selectedUserOfGroupStore',
			root : 'users',
			idProperty : 'wid',
			fields : ['wid', 'name', 'email']
		});
var selectedUserOfGroupGrid = new Ext.grid.GridPanel({
	id : 'selectedUserOfGroupGrid',
	title : '已选择用户列表',
	columnWidth : 0.5,
	border : false,
	autoScroll : true,
	store : selectedUserOfGroupStore,
	cm : new Ext.grid.ColumnModel([selectedUserOfGroupSM, {
				id : 'selectedUserOfGroupName',
				header : "用户名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : selectedUserOfGroupSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '取消选择',
		iconCls : 'remove',
		handler : function() {
			// 获取待取消的用户
			var userWids = new Array();
			var selections = selectedUserOfGroupGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				userWids[userWids.length] = selections[i].get('wid');
			}
			// 删除选中用户与当前用户组对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectUser/cancelOfGroup.ssm',
				method : 'post',
				params : {
					'groupWid' : current_selected_group,
					'userWids' : Ext.util.JSON.encode(userWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadUserOfGroup();
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
var unSelectedUserOfGroupSM = new Ext.grid.CheckboxSelectionModel();
var unSelectedUserOfGroupStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectUser/listUnSelectedOfGroup.ssm',
			storeId : 'unSelectedUserOfGroupStore',
			root : 'users',
			idProperty : 'wid',
			fields : ['wid', 'name', 'email']
		});
var unSelectedUserOfGroupGrid = new Ext.grid.GridPanel({
	id : 'unSelectedUserOfGroupGrid',
	title : '未选择用户列表',
	columnWidth : 0.5,
	border : false,
	autoScroll : true,
	store : unSelectedUserOfGroupStore,
	cm : new Ext.grid.ColumnModel([unSelectedUserOfGroupSM, {
				id : 'unSelectedUserOfGroupName',
				header : "用户名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : unSelectedUserOfGroupSM,
	bodyStyle : 'height:400',
	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '添加选择',
		iconCls : 'add',
		handler : function() {
			// 获取待添加的用户
			var userWids = new Array();
			var selections = unSelectedUserOfGroupGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				userWids[userWids.length] = selections[i].get('wid');
			}
			// 添加选中用户与当前角色对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectUser/addOfGroup.ssm',
				method : 'post',
				params : {
					'groupWid' : current_selected_group,
					'userWids' : Ext.util.JSON.encode(userWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadUserOfGroup();
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

var selectUserOfGroupPanel = new Ext.Panel({
			layout : 'column',
			items : [unSelectedUserOfGroupGrid, selectedUserOfGroupGrid]
		});

// 选择用户对话框
var selectUserOfGroupDlg = new Ext.Window({
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
			items : selectUserOfGroupPanel,
			buttons : [{
						text : '确定',
						scope : this,
						handler : function() {
							selectUserOfGroupDlg.hide({});
						}
					}],
			listeners : {
				'show' : function() {
					reloadUserOfGroup();
				}
			}
		});

function reloadUserOfGroup() {
	selectedUserOfGroupStore.reload({
				params : {
					groupWid : current_selected_group
				}
			});

	unSelectedUserOfGroupStore.reload({
				params : {
					groupWid : current_selected_group
				}
			});
}
