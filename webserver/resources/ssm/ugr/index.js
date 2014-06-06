/*
 * 功能模块页面
 */
Ext.onReady(function() {

			var panel = new Ext.Panel({
						layout : 'column',
						items : [ugrRoleGrid, ugrGroupGrid, ugrUserGrid]
					});

			var viewport = new Ext.Panel({
						layout : 'fit',
						items : panel,
				        renderTo:'managerContainer',
				        height: 500
					});

		});
