var current_module_node = null;
// 模块树
var moduleTreePanel = new Ext.tree.TreePanel({
			region : 'center',
			title : "功能模块",
			rootVisible : false,// 是否显示根节点
			border : false,
			height : 300,
			autoScroll : true,
			loader : new Ext.tree.TreeLoader({
						url : ctx + '/module/list.ssm',
						listeners : {
							'beforeload' : function(treeLoader, node) {
								Ext.apply(treeLoader.baseParams, {
											parent_id : node.id
										});
							}
						}
					}),
			viewConfig : {
				forceFit : false
			},
			root : new Ext.tree.AsyncTreeNode({
						id : "000000",
						text : "根节点",// 节点名称
						expanded : true,// 展开
						leaf : false
					}),
			listeners : {
				'contextmenu' : function(node, e) {
					current_module_node = node;
					moduleMenu.showAt(e.getPoint());
				},
				'click' : function(node, e) {
					current_module_node = node;
					var module_id = node.id;
					clickModule(module_id);
				}
			},
			tbar : [{
						xtype : 'textfield',
						width : 200

					}, {
						text : '查找',
						handler : function() {
						}
					}, '-', {
						text : '增加顶层模块',
						handler : function() {
							addTopModule();
						}
					}, '-', {
						text : '应用',
						handler : function() {
							applyModule();
						}
					}]

		});

// 模块编辑表单
var moduleEditForm = new Ext.FormPanel({
			baseCls : 'x-plain',
			labelWidth : 75,
			labelSeparator : ':',
			defaultType : 'textfield',
			items : [{
						fieldLabel : '模块名称',
						name : 'cname',
						allowBlank : false,
						width : 540
					}, {
						fieldLabel : '模块描述',
						xtype : 'htmleditor',
						name : 'memo',
						allowBlank : false,
						width : 540,
						height : 300
					}, {
						xtype : 'hidden',
						fieldLabel : 'id',
						name : 'id',
						allowBlank : false
					}, {
						xtype : 'hidden',
						fieldLabel : 'parent_id',
						name : 'parent_id',
						allowBlank : false
					}]
		});

// 模块编辑对话框
var moduleEditDlg = new Ext.Window({
			width : 680,
			height : 420,
			title : '**模块编辑',
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
			items : moduleEditForm,
			buttons : [{
						text : '保存',
						scope : this,
						handler : saveModule
					}, {
						text : '取消',
						scope : this,
						handler : cancelModule
					}],
			listeners : {
				render : function() {
				}
			}
		});

function loadcallback() {
	moduleTreePanel.root.expand();
}

// 取消模块编辑
function cancelModule() {
	moduleEditDlg.hide({});
}

// 表单中加载模块信息
function loadModule(module_id) {
	moduleEditForm.getForm().load({
				url : ctx + '/module/load.ssm',
				waitMsg : '正在载入...',
				params : {
					'module_id' : module_id
				},
				success : function(form, action) {
				},
				failure : function(form, action) {
				}
			});
}

// 增加模块
function addModule() {
	// alert(current_module_node.id);
	// 设置表单字段值
	var data = {
		cname : '',
		memo : '',
		id : null,
		parent_id : current_module_node.id
	};

	moduleEditDlg.show({});

	moduleEditForm.getForm().setValues(data);

}

// 增加顶层模块
function addTopModule() {
	// 设置表单字段值
	var data = {
		cname : '',
		memo : '',
		id : null,
		parent_id : null
	};

	moduleEditDlg.show({});

	current_module_node = null;

	moduleEditForm.getForm().setValues(data);
}

// 编辑模块
function editModule() {
	moduleEditDlg.show({});
	loadModule(current_module_node.id);
}

// 删除模块
function removeModule() {
	var id = current_module_node.id;
	Ext.Ajax.request({
				url : ctx + '/module/remove.ssm',
				method : 'post',
				params : {
					'id' : id
				},
				success : function(response, options) {
					var rev = Ext.util.JSON.decode(response.responseText);
					if (rev.success) {
						moduleTreePanel.getNodeById(id).parentNode
								.removeChild(moduleTreePanel.getNodeById(id));
					} else {
						alert('删除失败！');
					}
				},
				failure : function(response, options) {
					alert('删除失败！');
				}
			});

}

function saveModule() {
	var url = ctx + '/module/';

	var parentNode = null;

	var nodeId = moduleEditForm.getForm().getValues().id;
	if (nodeId) {// 如果是编辑模块
		url += 'edit.ssm';

		var node = moduleTreePanel.getNodeById(nodeId);
		parentNode = node.parentNode;

	} else {// 如果是新增模块
		url += 'add.ssm';

		var parent_id = moduleEditForm.getForm().getValues().parent_id;
		if (parent_id) {// 增加非顶级模块
			parentNode = moduleTreePanel.getNodeById(parent_id);
		} else {// 增加最顶级模块
			parentNode = moduleTreePanel.root;
		}
	}

	moduleEditForm.getForm().submit({
				url : url,
				method : 'POST',
				waitMsg : '正在提交...',
				timeout : 60000,// 60秒超时
				success : function(form, action) {
					var isSuc = action.result.success;
					if (isSuc) {
						moduleEditDlg.hide({});

						moduleTreePanel.getLoader().load(parentNode,
								loadcallback);

					} else {
						Ext.Msg.alert('消息', '保存失败..');
					}

				},
				failure : function(form, action) {
					Ext.Msg.alert('消息', '保存失败..');
				}
			});

}

var moduleMenu = new Ext.menu.Menu({
			items : [{
						text : '增加顶层模块',
						iconCls : '',
						handler : function() {
							addTopModule();
						}
					}, '-', {

						text : "增加模块",
						iconCls : 'add',// 右键名称前的小图片
						handler : function() {
							addModule();
						}

					}, {

						text : "删除模块",
						iconCls : 'remove',
						handler : function() {
							removeModule();
						}

					}, {

						text : "编辑模块",
						iconCls : 'edit',
						handler : function() {
							editModule();
						}

					}]
		});

/**
 * 选中某模块 刷新角色列表； 刷新功能路径列表；
 */
function clickModule(module_id) {
	var roleStroe = roleGrid.getStore();
	roleStroe.reload({
				params : {
					module_id : module_id
				}
			});

	unSelectedRoleStore.reload({
				params : {
					module_id : module_id
				}
			});

	functionStore.reload({
				params : {
					module_id : module_id
				}
			});
}

// 应用模块
function applyModule() {
	Ext.Ajax.request({
				url : ctx + '/module/apply.ssm',
				method : 'post',
				params : {},
				success : function(response, options) {
					var rev = Ext.util.JSON.decode(response.responseText);
					if (rev.success) {
						alert('应用完成！');
					} else {
						alert('应用失败！');
					}
				},
				failure : function(response, options) {
					alert('应用失败！');
				}
			});
}