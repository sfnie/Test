/*
 * 缓存维护页面
 */
Ext.onReady(function() {

	var tabPanel = new Ext.TabPanel({
		activeTab : 0,
		defaults : {
			autoScroll : true
		},
		items : [selectRangeCacheElementGrid,bizobjCacheElementGrid,loggerConfigCacheElementGrid,securityCacheElementGrid,allCacheElementGrid]
	});

	var viewport = new Ext.Panel({
				layout : 'fit',
				items : tabPanel,
				renderTo:'managerContainer',
				height: 500
			});

});
