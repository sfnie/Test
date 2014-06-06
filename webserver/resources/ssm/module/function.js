// 模块页面表格

// 当前模块路径MID
var curent_modulepath_mid;

var functionSM = new Ext.grid.CheckboxSelectionModel();

var functionStore = new Ext.data.JsonStore({
			autoLoad : false,
			url : ctx + '/moduleRelPath/list.ssm',
			root : 'modulepaths',
			idProperty : 'id',
			fields : ['id', 'path', 'memo', 'module_id', 'indexed']
		});

var functionGrid = new Ext.grid.GridPanel({
			id : 'functionGrid',
			title : '功能路径',
			region : 'center',
			border : false,
			store : functionStore,
			cm : new Ext.grid.ColumnModel([functionSM, {
						header : "功能路径",
						width : 500,
						sortable : true,
						dataIndex : 'path'
					}, {
						header : "操作",
						width : 120,
						renderer : function(value, cellmeta, record, rowIndex,
								columnIndex, store) {
							var link = '';

							if (record.get('indexed') == 0) {
								link = '<a href="#" ><font color="red">默认</font></a>';
							} else {
								link = '<a href="#" onclick="setAsDefault()">设定为默认</a>';
							}

							// alert(link);

							return link;
						}
					}, {
						header : "描述",
						width : 150,
						sortable : true,
						dataIndex : 'memo'
					}]),
			sm : functionSM,

			viewConfig : {
				forceFit : false
			},

			listeners : {
				rowdblclick : function(g, rowIndex, e) {
					var r = functionGrid.getStore().getAt(rowIndex);
					curent_modulepath_mid = r.get('id');

					dmStore.reload({
								params : {
									modulepath_mid : curent_modulepath_mid
								}
							});

				}
			},

			tbar : [{
						text : '增加路径',
						iconCls : 'add',
						handler : function() {
							addFunction();
						}
					}, {
						text : '删除路径',
						iconCls : 'remove',
						handler : function() {
							removeFunction();
						}
					}],

			iconCls : 'icon-grid'
		});

// 功能编辑表单
var functionEditForm = new Ext.FormPanel({
			baseCls : 'x-plain',
			labelWidth : 75,
			labelSeparator : ':',
			defaultType : 'textfield',
			items : [{
						fieldLabel : '路径',
						name : 'path',
						allowBlank : false,
						width : 540
					}, {
						fieldLabel : '描述',
						xtype : 'htmleditor',
						name : 'memo',
						allowBlank : false,
						width : 540,
						height : 300
					}, {
						xtype : 'hidden',
						fieldLabel : '次序',
						name : 'indexed',
						allowBlank : false
					}, {
						xtype : 'hidden',
						fieldLabel : 'id',
						name : 'id',
						allowBlank : false
					}, {
						xtype : 'hidden',
						fieldLabel : 'module_id',
						name : 'module_id',
						allowBlank : false
					}]
		});

// 功能编辑对话框
var functionEditDlg = new Ext.Window({
			width : 680,
			height : 450,
			title : '功能路径编辑',
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
			items : functionEditForm,
			buttons : [{
						text : '保存',
						scope : this,
						handler : saveFunction
					}, {
						text : '取消',
						scope : this,
						handler : cancelFunction
					}],
			listeners : {
				render : function() {
				}
			}
		});

// 取消模块编辑
function cancelFunction() {
	functionEditDlg.hide({});
}

// 增加模块
function addFunction() {
	if (!current_module_node) {
		Ext.Msg.alert('消息', '请先选择模块！');
		return;
	}
	// 设置表单字段值
	var data = {
		id : null,
		path : '',
		memo : '',
		module_id : current_module_node.id,
		indexed : 1
	};

	functionEditDlg.show({});
	functionEditForm.getForm().setValues(data);
}

// 表单中加载模块信息
function loadFunction(id) {
	functionEditForm.getForm().load({
				url : ctx + '/moduleRelPath/load.ssm',
				waitMsg : '正在载入...',
				params : {
					'id' : id
				},
				success : function(form, action) {
				},
				failure : function(form, action) {
				}
			});
}

// 编辑模块
function editFunction() {
	functionEditDlg.show({});
	loadFunction(current_module_node.id);
}

// 删除模块
function removeFunction() {
	var functions = new Array();

	var selections = functionGrid.getSelectionModel().getSelections();
	for (var i = 0; i < selections.length; i++) {
		var r = selections[i];
		functions[functions.length] = r.get('id');
	}

	Ext.Ajax.request({
				url : ctx + '/moduleRelPath/remove.ssm',
				method : 'post',
				params : {
					'idsStr' : Ext.util.JSON.encode(functions)
				},
				success : function(response, options) {
					functionStore.reload({
								params : {
									module_id : current_module_node.id
								}
							});
				},
				failure : function(response, options) {
					alert('删除失败！');
				}
			});

}

// 设置模块默认路径
function setAsDefault() {

	var selections = functionGrid.getSelectionModel().getSelections();
	var pathWid = selections[0].get('id');

	Ext.Ajax.request({
				url : ctx + '/moduleRelPath/setAsDefault.ssm',
				method : 'post',
				params : {
					'moduleWid' : current_module_node.id,
					'pathWid' : pathWid
				},
				success : function(response, options) {
					functionStore.reload({
								params : {
									module_id : current_module_node.id
								}
							});
				},
				failure : function(response, options) {
					alert('设置失败！');
				}
			});

}

function saveFunction() {
	var url = ctx + '/moduleRelPath/';

	var parentNode = null;

	var nodeId = functionEditForm.getForm().getValues().id;
	if (nodeId) {// 如果是编辑模块
		url += 'edit.ssm';
	} else {// 如果是新增模块
		url += 'add.ssm';
	}

	functionEditForm.getForm().submit({
				url : url,
				method : 'POST',
				waitMsg : '正在提交...',
				timeout : 60000,// 60秒超时
				success : function(form, action) {
					var isSuc = action.result.success;
					if (isSuc) {
						functionEditDlg.hide({});
						functionStore.reload({
									params : {
										module_id : current_module_node.id
									}
								});
					} else {
						Ext.Msg.alert('消息', '保存失败..');
					}

				},
				failure : function(form, action) {
					Ext.Msg.alert('消息', '保存失败........');
				}
			});

}
