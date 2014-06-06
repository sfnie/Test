/*
 * 功能模块页面
 */
Ext.onReady(function() {

			var westPanel = new Ext.Panel({
						region : 'west',
						layout : 'border',
						width : 380,
						border : false,
						split : true,
						items : [moduleTreePanel, roleGrid]
					});
					
			var centerPanel = new Ext.Panel({
						region : 'center',
						layout : 'border',
						border : false,
						items : [functionGrid]
					});

			var viewport = new Ext.Panel({
						layout : 'border',
						items : [westPanel, centerPanel],
				        renderTo:'managerContainer',
				        height: 500
					});

		});
