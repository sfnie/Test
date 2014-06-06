var roleSM = new Ext.grid.CheckboxSelectionModel();

var unSelectedRoleStore = new Ext.data.JsonStore({
			autoLoad : true,
			url : ctx + '/moduleRelRole/listUnSelected.ssm',
			storeId : 'unSelectedRoleStore',
			root : 'roles',
			idProperty : 'role',
			fields : ['role']
		});

var roleGrid = new Ext.grid.GridPanel({
			id : 'roleGrid',
			title : '角色列表',
			region : 'south',
			border : false,
			height : 250,
			collapsible : true,
			store : new Ext.data.JsonStore({
						autoDestroy : true,
						url : ctx + '/moduleRelRole/listSelected.ssm',
						storeId : 'selectedRoleStore',
						root : 'roles',
						idProperty : 'role',
						fields : ['role']
					}),
			cm : new Ext.grid.ColumnModel([roleSM, {
						id : 'rolename',
						header : "角色名称",
						width : 250,
						sortable : true,
						dataIndex : 'role'
					}]),
			sm : roleSM,

			viewConfig : {
				forceFit : false
			},

			tbar : [{
						xtype : 'combo',
						id : 'cbmUnSelectedRole',
						width : 200,
						store : unSelectedRoleStore,
						displayField : 'role',
						valueField : 'role',
						mode : 'local',
						forceSelection : false,
						blankText : '选择角色',
						emptyText : '选择角色',
						editable : false,
						triggerAction : 'all',
						allowBlank : true

					}, {
						text : '添加角色',
						iconCls : 'add',
						handler : function() {
							var role = Ext.getCmp('cbmUnSelectedRole')
									.getValue();
							if (role) {
								var module_id = current_module_node.id;
								Ext.Ajax.request({
											url : ctx + '/moduleRelRole/add.ssm',
											method : 'post',
											params : {
												'module_id' : module_id,
												'role' : role
											},
											success : function(response,
													options) {
												var rev = Ext.util.JSON
														.decode(response.responseText);
												if (rev.success) {
													clickModule(module_id);
													Ext.getCmp('cbmUnSelectedRole').setValue(null);
												} else {
													alert('添加失败！');
												}
											},
											failure : function(response,
													options) {
												alert('添加失败！');
											}
										});
							}
						}
					}, {
						text : '删除角色',
						iconCls : 'remove',
						handler:function(){
							var module_id = current_module_node.id;
							
							var roles = new Array();
							
							var selections = roleGrid.getSelectionModel().getSelections();
							for(var i = 0;i < selections.length;i++){
								var r = selections[i];
								roles[roles.length] = r.get('role');
							}
							
							Ext.Ajax.request({
											url : ctx + '/moduleRelRole/remove.ssm',
											method : 'post',
											params : {
												'module_id' : module_id,
												'role' : Ext.util.JSON.encode(roles)
											},
											success : function(response,
													options) {
												var rev = Ext.util.JSON
														.decode(response.responseText);
												if (rev.success) {
													clickModule(module_id);
													Ext.getCmp('cbmUnSelectedRole').setValue(null);
												} else {
													alert('添加失败！');
												}
											},
											failure : function(response,
													options) {
												alert('添加失败！');
											}
										});
						}
					}],

			iconCls : 'icon-grid'
		});
