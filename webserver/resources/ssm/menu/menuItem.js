// define a template to use for the detail view
var menuItemTplMarkup = [
'菜单项名称:{name}<br/>',
'图标路径: {iconPath}<br/>',
'功能路径:{path}<br/>',
'模块名称:{moduleName}<br/>',
'菜单项描述: {memo}<br/>'
];
var menuItemTpl = new Ext.Template(menuItemTplMarkup);//创建一个模版

var menuItemTreePanel = new Ext.tree.TreePanel({
    region: 'center',
    title: "菜单项编辑",
    rootVisible: false, // 是否显示根节点
    border: false,
    height: 320,
    enableDD: true,
    autoScroll: true,
    loader: new Ext.tree.TreeLoader({
        url: ctx + '/menuItem/list.ssm',
        listeners: {
            'beforeload': function(treeLoader, node) {
                if (!current_edit_menuitem_menu_wid) {
                    return false;
                }
                Ext.apply(treeLoader.baseParams, {
                    menuWid: current_edit_menuitem_menu_wid,
                    menuItemWid: node.id == '000000'
                    ? null
                    : node.id
                });
            }
        }
    }),
    viewConfig: {
        forceFit: false
    },
    root: new Ext.tree.AsyncTreeNode({
        id: "000000",
        text: "根节点", // 节点名称
        expanded: false, // 展开
        leaf: false
    }),
    listeners: {
        'contextmenu': function(node, e) {
            current_edit_menuitem_wid = node.id;
            menuItemMenu.showAt(e.getPoint());
        },
        'click': function(node, e) {
            var detailPanel = Ext.getCmp('descPanel');
            
            current_edit_menuitem_wid = node.id;
            
            //获取数据 v
            Ext.Ajax.request({
                url: ctx + '/menuItem/load.ssm',
                method : 'post',
                params : {
                    'menuItemWid' : current_edit_menuitem_wid
                },
                success : function(response, options) {
                    var rev = Ext.util.JSON.decode(response.responseText);
                    if (rev.success) {
                        var data = rev.data;
                        menuItemTpl.overwrite(detailPanel.body, data);
                    }
                    else {
                        alert('获取菜单描述失败！');
                    }
                },
                failure : function(response, options) {
                    alert('获取菜单描述失败！');
                }
            });
        //detailPanel.html(v);
     
           
        },
        'beforenodedrop': function(e) {
            handleDDNode(e);
        }
    },
    tbar: [{
        text: '增加顶级菜单项',
        iconCls: 'add',
        handler: function() {
            addTopMenuItem();
        }
    }, 
//    {
    //        text: '删除菜单项',
    //        iconCls: 'remove',
    //        handler: function() {
    //            removeMenuItem();
    //        }
    //    }, {
    //        text: "编辑菜单项",
    //        iconCls: 'edit',
    //        handler: function() {
    //            editMenuItem();
    //        }
    //
    //    },
    '-', {
        text: "上移",
        iconCls: 'up',
        handler: function() {
            upMenuItem();
        }
    }, {
        text: "下移",
        iconCls: 'down',
        handler: function() {
            downMenuItem();
        }
    }, {
        text: "排序",
        iconCls: 'sort',
        handler: function() {
            sortMenuItem();
        }
    }, '-', {
        text: "顶级排序",
        iconCls: 'sort',
        handler: function() {
            topSortMenuItem();
        }
    }]


});
 

//处理节点拖曳
function handleDDNode(e) {
    //定位拖曳至所属菜单项
    var dropNode = e.dropNode;
    var nn = new Ext.tree.AsyncTreeNode();
    nn.loader = menuItemTreePanel.getLoader();
    e.dropNode = nn;
    var menuItemWid = e.target.id;
    if('append' != e.point){
        menuItemWid = e.target.parentNode.id;
    }
    Ext.Ajax.request({
        url : ctx + '/menuItem/dd.ssm',
        method : 'post',
        params : {
            'moduleWid' : dropNode.id,
            'menuItemWid': menuItemWid == '000000'?null:menuItemWid,
            'menuWid':current_edit_menuitem_menu_wid
        },
        success : function(response, options) {
            var rev = Ext.util.JSON.decode(response.responseText);
            if (rev.success) {
                var data = rev.data;
                nn.setText(data.name);
                nn.setId(data.wid);
            }
            else {
                alert('创建菜单项失败！');
            }
        },
        failure : function(response, options) {
            alert('创建菜单项失败！');
        }
    });
    
}

// 菜单项编辑表单
var menuItemForm = new Ext.FormPanel({
    baseCls: 'x-plain',
    labelWidth: 75,
    labelSeparator: ':',
    defaultType: 'textfield',
    items: [{
        fieldLabel: '菜单项名称',
        name: 'name',
        allowBlank: false,
        width: 540
    }, {
        fieldLabel: '图标路径',
        name: 'iconPath',
        allowBlank: true,
        width: 540
    }, {
        fieldLabel: '功能路径',
        name: 'path',
        allowBlank: true,
        width: 540
    }, {
        xtype: 'hidden',
        fieldLabel: '功能模块',
        name: 'moduleWid',
        allowBlank: true,
        readOnly: true,
        width: 540
    }, {
        fieldLabel: '模块名称',
        name: 'moduleName',
        allowBlank: true,
        readOnly: true,
        width: 540
    }, {
        fieldLabel: '菜单项描述',
        xtype: 'htmleditor',
        name: 'memo',
        allowBlank: false,
        width: 540,
        height: 300
    }, {
        xtype: 'hidden',
        fieldLabel: 'wid',
        name: 'wid',
        allowBlank: false
    }, {
        xtype: 'hidden',
        fieldLabel: 'parent_wid',
        name: 'parent_wid',
        allowBlank: false
    }]
});

// 菜单项编辑对话框
var menuItemWindow = new Ext.Window({
    width: 680,
    height: 500,
    title: '菜单项编辑',
    plain: true,
    closable: true,
    resizable: true,
    layout: 'fit',
    closeAction: 'hide',
    modal: true,
    buttonAlign: 'center',
    hideMode: 'offsets',
    constrainHeader: true,
    bodyStyle: 'padding:5px;',
    items: menuItemForm,
    buttons: [{
        text: '保存',
        scope: this,
        handler: saveMenuItem
    }, {
        text: '取消',
        scope: this,
        handler: cancelMenuItem
    }],
    listeners: {
        render: function() {
        }
    }
});

// 取消菜单项编辑
function cancelMenuItem() {
    menuItemWindow.hide({});
}

// 表单中加载菜单项信息
function loadMenuItem(module_id) {
    // 清空表单字段值
    var data = {
        wid: null,
        name: '',
        iconPath: '',
        path: '',
        moduleWid: '',
        moduleName: '',
        memo: '',
        parent_wid: ''
    };
    menuItemForm.getForm().setValues(data);
    
    menuItemForm.getForm().load({
        url: ctx + '/menuItem/load.ssm',
        waitMsg: '正在载入...',
        params: {
            'menuItemWid': current_edit_menuitem_wid
        },
        success: function(form, action) {
        },
        failure: function(form, action) {
        }
    });
}

// 增加菜单项
function addMenuItem() {
    // 设置表单字段值
    var data = {
        wid: null,
        name: '',
        iconPath: '',
        path: '',
        moduleWid: '',
        moduleName: '',
        memo: '',
        parent_wid: current_edit_menuitem_wid
    };
    menuItemWindow.show({});
    menuItemForm.getForm().setValues(data);

}

// 增加顶级菜单项
function addTopMenuItem() {
    current_edit_menuitem_wid=null;
    // 设置表单字段值
    var data = {
        wid: null,
        name: '',
        iconPath: '',
        path: '',
        moduleWid: '',
        moduleName: '',
        memo: '',
        parent_wid: current_edit_menuitem_wid
    };
    
    menuItemWindow.show({});
    menuItemForm.getForm().setValues(data);

}

/**
 * 保存菜单项
 */
function saveMenuItem() {
    var url = ctx + '/menuItem/';

    var parentNode = null;

    var wid = menuItemForm.getForm().getValues().wid;
    if (wid) {// 如果是编辑菜单项
        url += 'edit.ssm';

    } else {// 如果是新增菜单项
        url += 'add.ssm';

    }

    menuItemForm.getForm().submit({
        url: url,
        method: 'POST',
        waitMsg: '正在提交...',
        timeout: 60000, // 60秒超时
        params: {
            menuWid: current_edit_menuitem_menu_wid
        },
        success: function(form, action) {
            var isSuc = action.result.success;
            if (isSuc) {
                menuItemWindow.hide({});            
             
                var currentNode ;
                if(current_edit_menuitem_wid==null)
                {
                    currentNode=menuItemTreePanel.getRootNode();
                }
                else
                {
                    currentNode = menuItemTreePanel
                    .getNodeById(current_edit_menuitem_wid);
                    if(wid){
                        currentNode = currentNode.parentNode;
                    }
                }
                menuItemTreePanel.getLoader().load(currentNode, function() {
                    currentNode.expand();
                });           
            } else {
                Ext.Msg.alert('消息', '保存失败..');
            }

        },
        failure: function(form, action) {
            Ext.Msg.alert('消息', '保存失败..');
        }
    });
}

 

// 编辑菜单项
function editMenuItem() {
    menuItemWindow.show({});
    loadMenuItem(current_edit_menuitem_wid);
}

// 删除菜单项
function removeMenuItem() {
    var currentNode = menuItemTreePanel.getNodeById(current_edit_menuitem_wid);
    var parentNode = currentNode.parentNode;

    Ext.Ajax.request({
        url: ctx + '/menuItem/remove.ssm',
        method: 'post',
        params: {
            'menuItemWid': current_edit_menuitem_wid
        },
        success: function(response, options) {
            var rev = Ext.util.JSON.decode(response.responseText);
            if (rev.success) {
                menuItemTreePanel.getLoader().load(parentNode,
                    function() {
                        parentNode.expand();
                    });
            } else {
                alert('删除失败！');
            }
        },
        failure: function(response, options) {
            alert('删除失败！');
        }
    });

}

// 上移菜单项
function upMenuItem() {
    if (!current_edit_menuitem_wid) {
        alert("请先选择菜单项！");
        return;
    }

    var currentNode = menuItemTreePanel.getNodeById(current_edit_menuitem_wid);
    var parentNode = currentNode.parentNode;

    Ext.Ajax.request({
        url: ctx + '/menuItem/up.ssm',
        method: 'post',
        params: {
            'menuItemWid': current_edit_menuitem_wid
        },
        success: function(response, options) {
            var rev = Ext.util.JSON.decode(response.responseText);
            if (rev.success) {
                menuItemTreePanel.getLoader().load(parentNode,
                    function() {
                        parentNode.expand();
                    });
            } else {
                alert('上移失败！');
            }
        },
        failure: function(response, options) {
            alert('上移失败！');
        }
    });
}

// 下移菜单项
function downMenuItem() {
    if (!current_edit_menuitem_wid) {
        alert("请先选择菜单项！");
        return;
    }

    var currentNode = menuItemTreePanel.getNodeById(current_edit_menuitem_wid);
    var parentNode = currentNode.parentNode;

    Ext.Ajax.request({
        url: ctx + '/menuItem/down.ssm',
        method: 'post',
        params: {
            'menuItemWid': current_edit_menuitem_wid
        },
        success: function(response, options) {
            var rev = Ext.util.JSON.decode(response.responseText);
            if (rev.success) {
                menuItemTreePanel.getLoader().load(parentNode,
                    function() {
                        parentNode.expand();
                    });
            } else {
                alert('下移失败！');
            }
        },
        failure: function(response, options) {
            alert('下移失败！');
        }
    });
}

// 排序菜单项
function sortMenuItem() {
    if (!current_edit_menuitem_wid) {
        alert("请先选择菜单项！");
        return;
    }

    var currentNode = menuItemTreePanel.getNodeById(current_edit_menuitem_wid);
    var parentNode = currentNode.parentNode;

    Ext.Ajax.request({
        url: ctx + '/menuItem/sort.ssm',
        method: 'post',
        params: {
            'menuItemWid': current_edit_menuitem_wid
        },
        success: function(response, options) {
            var rev = Ext.util.JSON.decode(response.responseText);
            if (rev.success) {
                menuItemTreePanel.getLoader().load(parentNode,
                    function() {
                        parentNode.expand();
                    });
            } else {
                alert('排序失败！');
            }
        },
        failure: function(response, options) {
            alert('排序失败！');
        }
    });
}

// 排序顶级菜单项
function topSortMenuItem() {
    if (!current_edit_menuitem_menu_wid) {
        alert("请先选择需要排序的菜单！");
        return;
    }

    var parentNode = menuItemTreePanel.root;

    Ext.Ajax.request({
        url: ctx + '/menuItem/topSort.ssm',
        method: 'post',
        params: {
            menuWid: current_edit_menuitem_menu_wid
        },
        success: function(response, options) {
            var rev = Ext.util.JSON.decode(response.responseText);
            if (rev.success) {
                menuItemTreePanel.getLoader().load(parentNode,
                    function() {
                        parentNode.expand();
                    });
            } else {
                alert('排序失败！');
            }
        },
        failure: function(response, options) {
            alert('排序失败！');
        }
    });
}

var menuItemMenu = new Ext.menu.Menu({
    items: [{
        text: "增加菜单项",
        iconCls: 'add', // 右键名称前的小图片
        handler: function() {
            addMenuItem();
        }

    }, {
        text: "删除菜单项",
        iconCls: 'remove',
        handler: function() {
            removeMenuItem();
        }

    }, {
        text: "编辑菜单项",
        iconCls: 'edit',
        handler: function() {
            editMenuItem();
        }

    }, '-', {
        text: "上移",
        iconCls: 'up',
        handler: function() {
            upMenuItem();
        }
    }, {
        text: "下移",
        iconCls: 'down',
        handler: function() {
            downMenuItem();
        }
    }, {
        text: "排序",
        iconCls: 'sort',
        handler: function() {
            sortMenuItem();
        }
    }, '-', {
        text: "顶级排序",
        iconCls: 'sort',
        handler: function() {
            topSortMenuItem();
        }
    }]
});