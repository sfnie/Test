/*
 * 菜单维护页面
 */
Ext.onReady(function() {

			var westPanel = new Ext.Panel({
						region : 'west',
						layout : 'border',
						width : 400,
						border : false,
						split : true,
						items : [menuGrid, descPanel]
					});
					
			var centerPanel = new Ext.Panel({
						region : 'center',
						layout : 'border',
						border : false,
						items : [menuItemTreePanel]
					});
                                    
                         var eastPanel = new Ext.Panel({
						region : 'east',
						layout : 'border',
                                                width : 400,
						border : false,
                                                split : true,
						items : [moduleTreePanel]
					});
                                    

			var viewport = new Ext.Panel({
						layout : 'border',
						items : [westPanel, centerPanel,eastPanel],
						renderTo:'managerContainer',
						height: 500
					});
					

		});
