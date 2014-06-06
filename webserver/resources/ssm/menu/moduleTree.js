var widlist;
var i = 0;
// 模块树
var moduleTreePanel = new Ext.tree.TreePanel({
	region : 'center',
	title : "功能模块",
	rootVisible : false, // 是否显示根节点
	border : false,
	autoScroll : true,
	enableDD : true,
	loader : new Ext.tree.TreeLoader({
		url : ctx + '/moduleExcludesMenu/list.ssm',
		listeners : {
			'beforeload' : function(treeLoader, node) {
				Ext.apply(treeLoader.baseParams, {
					parent_id : node.id,
					menuId : current_edit_menuitem_menu_wid
				});
			}
		}
	}),
	viewConfig : {
		forceFit : false
	},
	root : new Ext.tree.AsyncTreeNode({
		id : "000000",
		text : "根节点", // 节点名称
		expanded : true, // 展开
		leaf : false
	}),
	listeners : {
		'contextmenu' : function(node, e) {
		},
		'click' : function(node, e) {
			current_module_node = node;
			var module_id = node.id;
		}
	}
	/**,
	tbar : [ {
		id : 'tb_search',
		xtype : 'textfield',
		width : 200

	}
	
	, {
		text : '查找',
		handler : function() {
			i=0;
			widlist=null;
			var tb_search = document.getElementById('tb_search');
			if(tb_search.value=='')
				{tb_search.focus();return;}
			currentNode=moduleTreePanel.getRootNode();
			moduleTreePanel.getLoader().load(currentNode, function() {
                   currentNode.expand();
               });  
			
			searchStore.setBaseParam('name', tb_search.value);
			searchStore.load();
			searchDlg.show({});
			// moduleTreePanel.expandAll();
		}
	}
	 ]**/

});

var searchStore = new Ext.data.JsonStore({
	autoLoad : false,
	url : ctx + '/moduleExcludesMenu/searchlist.ssm',
	idProperty : 'id',
	fields : [ 'id', 'name', 'memo', 'parent_wid', 'path', 'wids' ]
});

var searchGrid = new Ext.grid.GridPanel({
	id : 'searchGrid',
	title : '搜索结果',
	region : 'center',
	border : false,
	store : searchStore,
	cm : new Ext.grid.ColumnModel([ {
		header : "功能名称",
		width : 160,
		sortable : true,
		dataIndex : 'name'
	}, {
		header : "所在路径",
		width : 160,
		sortable : true,
		dataIndex : 'path'
	}, {
		hidden:true,
		header : "所在路径",
		width : 160,
		sortable : true,
		dataIndex : 'wids'
	} ]),

	viewConfig : {
		forceFit : false
	},

	listeners : {
		rowdblclick : function(g, rowIndex, e) {
			var r = menuGrid.getStore().getAt(rowIndex);
			curent_menu_wid = r.get('id');

			// dmStore.reload({
			// params : {
			// curent_menu_wid : curent_menu_wid
			// }
			// });

		}
	},
	sm : new Ext.grid.RowSelectionModel({
		singleSelect : true
	}),
	iconCls : 'icon-grid'
});

var selectedwids;

searchGrid.getSelectionModel().on('rowselect', function(sm, rowIdx, r) {

	selectedwids = r.data.wids;
	searchDlg.hide();
	var module_id = r.data.id;

});

function test() {
	if (i < widlist.length) {
		i++;
		current_module_node = moduleTreePanel.getNodeById(widlist[i]);
		current_module_node.expand(false, false, test);
		current_module_node.select();
	}
};
// 菜单编辑对话框
var searchDlg = new Ext.Window({
	width : 680,
	height : 450,
	title : '搜索结果',
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
	items : searchGrid,

	listeners : {
		render : function() {
		},
		hide : function() {

			widlist = selectedwids.split('-->');

			var current_module_node = moduleTreePanel.getNodeById(widlist[i]);

			current_module_node.expand(false, false, test);
		}
	}
});

// searchDlg.on('close',function{
// //上面的window是extjs的变量名，下面的window是jsp的。
// alert(1);
// });
