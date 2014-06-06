var selectedGroupOfRoleSM = new Ext.grid.CheckboxSelectionModel();
var selectedGroupOfRoleStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectGroup/listSelectedOfRole.ssm',
			storeId : 'selectedGroupOfRoleStore',
			root : 'groups',
			idProperty : 'wid',
			fields : ['wid', 'name']
		});
var selectedGroupOfRoleGrid = new Ext.grid.GridPanel({
	id : 'selectedGroupOfRoleGrid',
	title : '已选择用户组列表',
	columnWidth : 0.5,
	border : false,
	store : selectedGroupOfRoleStore,
	cm : new Ext.grid.ColumnModel([selectedGroupOfRoleSM, {
				id : 'selectedGroupOfRoleName',
				header : "用户组名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : selectedGroupOfRoleSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '取消选择',
		iconCls : 'remove',
		handler : function() {
			// 获取待取消的用户组
			var groupWids = new Array();
			var selections = selectedGroupOfRoleGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				groupWids[groupWids.length] = selections[i].get('wid');
			}
			// 删除选中用户组与当前角色对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectGroup/cancelOfRole.ssm',
				method : 'post',
				params : {
					'role' : current_selected_role,
					'groupWids' : Ext.util.JSON.encode(groupWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadGroupOfRole();
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
var unSelectedGroupOfRoleSM = new Ext.grid.CheckboxSelectionModel();
var unSelectedGroupOfRoleStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectGroup/listUnSelectedOfRole.ssm',
			storeId : 'unSelectedGroupOfRoleStore',
			root : 'groups',
			idProperty : 'wid',
			fields : ['wid', 'name']
		});
var unSelectedGroupOfRoleGrid = new Ext.grid.GridPanel({
	id : 'unSelectedGroupOfRoleGrid',
	title : '未选择用户组列表',
	columnWidth : 0.5,
	border : false,
	store : unSelectedGroupOfRoleStore,
	cm : new Ext.grid.ColumnModel([unSelectedGroupOfRoleSM, {
				id : 'unSelectedGroupOfRoleName',
				header : "用户组名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : unSelectedGroupOfRoleSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '添加选择',
		iconCls : 'add',
		handler : function() {
			// 获取待添加的用户组
			var groupWids = new Array();
			var selections = unSelectedGroupOfRoleGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				groupWids[groupWids.length] = selections[i].get('wid');
			}
			// 添加选中用户组与当前角色对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectGroup/addOfRole.ssm',
				method : 'post',
				params : {
					'role' : current_selected_role,
					'groupWids' : Ext.util.JSON.encode(groupWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadGroupOfRole();
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

var selectGroupOfRolePanel = new Ext.Panel({
			layout : 'column',
			items : [unSelectedGroupOfRoleGrid, selectedGroupOfRoleGrid]
		});

// 选择用户组对话框
var selectGroupOfRoleDlg = new Ext.Window({
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
			items : selectGroupOfRolePanel,
			buttons : [{
						text : '确定',
						scope : this,
						handler : function() {
							selectGroupOfRoleDlg.hide({});
						}
					}],
			listeners : {
				'show' : function() {
					reloadGroupOfRole();
				}
			}
		});

// 重新加载角色的组信息
function reloadGroupOfRole() {
	selectedGroupOfRoleStore.reload({
				params : {
					role : current_selected_role
				}
			});

	unSelectedGroupOfRoleStore.reload({
				params : {
					role : current_selected_role
				}
			});
}

// ///////////////////////////////////////////////////////////////////////////
var selectedGroupOfUserSM = new Ext.grid.CheckboxSelectionModel();
var selectedGroupOfUserStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectGroup/listSelectedOfUser.ssm',
			storeId : 'selectedGroupOfUserStore',
			root : 'groups',
			idProperty : 'wid',
			fields : ['wid', 'name']
		});
var selectedGroupOfUserGrid = new Ext.grid.GridPanel({
	id : 'selectedGroupOfUserGrid',
	title : '已选择用户组列表',
	columnWidth : 0.5,
	border : false,
	store : selectedGroupOfUserStore,
	cm : new Ext.grid.ColumnModel([selectedGroupOfUserSM, {
				id : 'selectedGroupOfUserName',
				header : "用户组名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : selectedGroupOfUserSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '取消选择',
		iconCls : 'remove',
		handler : function() {
			// 获取待取消的用户组
			var groupWids = new Array();
			var selections = selectedGroupOfUserGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				groupWids[groupWids.length] = selections[i].get('wid');
			}
			// 删除选中用户组与当前用户对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectGroup/cancelOfUser.ssm',
				method : 'post',
				params : {
					'userWid' : current_selected_user,
					'groupWids' : Ext.util.JSON.encode(groupWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadGroupOfUser();
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
var unSelectedGroupOfUserSM = new Ext.grid.CheckboxSelectionModel();
var unSelectedGroupOfUserStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/ugr/select/selectGroup/listUnSelectedOfUser.ssm',
			storeId : 'unSelectedGroupOfUserStore',
			root : 'groups',
			idProperty : 'wid',
			fields : ['wid', 'name']
		});
var unSelectedGroupOfUserGrid = new Ext.grid.GridPanel({
	id : 'unSelectedGroupOfUserGrid',
	title : '未选择用户组列表',
	columnWidth : 0.5,
	border : false,
	store : unSelectedGroupOfUserStore,
	cm : new Ext.grid.ColumnModel([unSelectedGroupOfUserSM, {
				id : 'unSelectedGroupOfUserName',
				header : "用户组名称",
				width : 120,
				sortable : true,
				dataIndex : 'name'
			}]),
	sm : unSelectedGroupOfUserSM,
	bodyStyle : 'height:400',

	viewConfig : {
		forceFit : false
	},

	tbar : [{
		text : '添加选择',
		iconCls : 'add',
		handler : function() {
			// 获取待添加的用户组
			var groupWids = new Array();
			var selections = unSelectedGroupOfUserGrid.getSelectionModel()
					.getSelections();
			for (var i = 0; i < selections.length; i++) {
				groupWids[groupWids.length] = selections[i].get('wid');
			}
			// 添加选中用户组与当前用户对应关系
			Ext.Ajax.request({
				url : ctx + '/ugr/select/selectGroup/addOfUser.ssm',
				method : 'post',
				params : {
					'userWid' : current_selected_user,
					'groupWids' : Ext.util.JSON.encode(groupWids)
				},
				success : function(response, options) {
					var isSuc = Ext.util.JSON.decode(response.responseText).success;
					if (isSuc) {
						reloadGroupOfUser();
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

var selectGroupOfUserPanel = new Ext.Panel({
			layout : 'column',
			items : [unSelectedGroupOfUserGrid, selectedGroupOfUserGrid]
		});

// 选择用户组对话框
var selectGroupOfUserDlg = new Ext.Window({
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
			items : selectGroupOfUserPanel,
			buttons : [{
						text : '确定',
						scope : this,
						handler : function() {
							selectGroupOfUserDlg.hide({});
						}
					}],
			listeners : {
				'show' : function() {
					reloadGroupOfUser();
				}
			}
		});

function reloadGroupOfUser() {
	selectedGroupOfUserStore.reload({
				params : {
					userWid : current_selected_user
				}
			});

	unSelectedGroupOfUserStore.reload({
				params : {
					userWid : current_selected_user
				}
			});
}
