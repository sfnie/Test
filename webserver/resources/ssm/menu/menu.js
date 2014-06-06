/**
 * 菜单表格
 */
var menuSM = new Ext.grid.CheckboxSelectionModel();

var menuStore = new Ext.data.JsonStore({
    autoLoad : true,
    url : ctx + '/menu/list.ssm',
    root : 'menus',
    idProperty : 'wid',
    fields : ['wid', 'name', 'memo','role']
});

var menuGrid = new Ext.grid.GridPanel({
    id : 'menuGrid',
    title : '菜单列表',
    region : 'center',
    border : false,
    store : menuStore,
    cm : new Ext.grid.ColumnModel([menuSM, {
        header : "菜单名称",
        width : 160,
        sortable : true,
        dataIndex : 'name'
    }, {
        header : "所属角色",
        width : 60,
        sortable : true,
        dataIndex : 'role'
    },{
        header : "操作",
        width : 120,
        renderer : function(value, cellmeta, record, rowIndex,
            columnIndex, store) {
            var link = '<a href="#" onclick="editMenu()">修改</a>';
            link += '&nbsp;&nbsp;';
            link += '<a href="#" onclick="loadMenuItems()">菜单项</a>';
            return link;
        }
    }]),

    sm : menuSM,

    viewConfig : {
        forceFit : false
    },

    listeners : {
        rowdblclick : function(g, rowIndex, e) {
            var r = menuGrid.getStore().getAt(rowIndex);
            curent_menu_wid = r.get('wid');

        // dmStore.reload({
        // params : {
        // curent_menu_wid : curent_menu_wid
        // }
        // });

        }
    },

    tbar : [{
        text : '从模块生成菜单',
        iconCls : 'add',
        handler : function() {
            addMenu();
        }
    }, {
        text : '删除菜单',
        iconCls : 'remove',
        handler : function() {
            removeMenu();
        }
    }],

    iconCls : 'icon-grid'
});
             
var unSelectedRoleStore = new Ext.data.JsonStore({
    autoLoad : true,
    url : ctx + '/menu/listUnSelectedRoles.ssm',
    storeId : 'unSelectedRoleStore',
    root : 'roles',
    idProperty : 'role',
    fields : ['role']
});
// define a template to use for the detail view
var menuTplMarkup = [
'菜单名称:{name}<br/>',
'所属角色: {role}<br/>'
];
var menuTpl = new Ext.Template(menuTplMarkup);//创建一个模版

 
menuGrid.getSelectionModel().on('rowselect', function(sm, rowIdx, r) {
    var detailPanel = Ext.getCmp('descPanel');
    menuTpl.overwrite(detailPanel.body, r.data);
});

// 菜单编辑表单
var menuForm = new Ext.FormPanel({
    baseCls : 'x-plain',
    labelWidth : 75,
    labelSeparator : ':',
    defaultType : 'textfield',
    items : [{
        fieldLabel : '菜单名称',
        name : 'name',
        allowBlank : false,
        width : 540
    }, {
        fieldLabel : '所属角色',
        xtype : 'combo',
        id : 'cbmUnSelectedRole',
        name:'role',
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

    },{
        fieldLabel : '菜单描述',
        xtype : 'htmleditor',
        name : 'memo',
        allowBlank : false,
        width : 540,
        height : 300
    }, {
        xtype : 'hidden',
        fieldLabel : 'wid',
        name : 'wid',
        allowBlank : false
    }]
});

// 菜单编辑对话框
var menuDlg = new Ext.Window({
    width : 680,
    height : 450,
    title : '菜单编辑',
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
    items : menuForm,
    buttons : [{
        text : '保存',
        scope : this,
        handler : saveMenu
    }, {
        text : '取消',
        scope : this,
        handler : cancelMenu
    }],
    listeners : {
        render : function() {
        }
    }
});

// 取消菜单编辑
function cancelMenu() {
    menuDlg.hide({});
}

// 增加菜单
function addMenu() {
    // 设置表单字段值
    var data = {
        wid : null,
        path : '',
        memo : ''
    };

    menuDlg.show({});
    menuForm.getForm().setValues(data);
}

// 表单中加载菜单信息
function loadMenu(wid) {
    menuForm.getForm().load({
        url : ctx + '/menu/load.ssm',
        waitMsg : '正在载入...',
        params : {
            'wid' : wid
        },
        success : function(form, action) {
        },
        failure : function(form, action) {
        }
    });
}

// 编辑菜单
function editMenu() {
    var selections = menuGrid.getSelectionModel().getSelections();
    current_selected_menu_wid = selections[0].get('wid');

    menuDlg.show({});
    loadMenu(current_selected_menu_wid);
}

// 删除菜单
function removeMenu() {
    var functions = new Array();

    var selections = menuGrid.getSelectionModel().getSelections();
    for (var i = 0; i < selections.length; i++) {
        var r = selections[i];
        functions[functions.length] = r.get('wid');
    }

    Ext.Ajax.request({
        url : ctx + '/menu/remove.ssm',
        method : 'post',
        params : {
            'menuWids' : Ext.util.JSON.encode(functions)
        },
        success : function(response, options) {
            var isSuc = Ext.util.JSON.decode(response.responseText).success;
            if (isSuc) {
                menuStore.reload({});
            } else {
                Ext.Msg.alert('消息', '删除失败！');
            }

        },
        failure : function(response, options) {
            Ext.Msg.alert('消息', '删除失败！');
        }
    });

}

function saveMenu() {
    var url = ctx + '/menu/';

    var menuId = menuForm.getForm().getValues().wid;
    if (menuId) {// 如果是编辑菜单
        url += 'edit.ssm';
    } else {// 如果是新增菜单
        url += 'add.ssm';
    }

    menuForm.getForm().submit({
        url : url,
        method : 'POST',
        waitMsg : '正在提交...',
        timeout : 60000,// 60秒超时
        success : function(form, action) {
            var isSuc = action.result.success;
            if (isSuc) {
                menuDlg.hide({});
                menuStore.reload({});
            } else {
                Ext.Msg.alert('消息', '保存失败..');
            }

        },
        failure : function(form, action) {
            Ext.Msg.alert('消息', '保存失败........');
        }
    });

}

/**
 * 加载菜单项
 */
function loadMenuItems(){
    var selections = menuGrid.getSelectionModel().getSelections();
    current_edit_menuitem_menu_wid = selections[0].get('wid');
    // 菜单项重新加载
    menuItemTreePanel.getRootNode().reload({});
}
