
//Ext.define('SelectorStatistics', {
//	extend : 'Ext.data.Model',
//	fields : [
//		{
//			name : 'timesSeenInfinity',
//			type : 'int'
//		}, {
//			name : 'timesSeenLatestQuarter',
//			type : 'int'
//		}, {
//			name : 'timesSeenLatestWeek',
//			type : 'int'
//		}, {
//			name : 'timesSeenLatestDay',
//			type : 'int'
//		}
//	]
//});
//
//var selectorStore = Ext.create('Ext.data.Store', {
//	model : 'Selector',
//	proxy : {
//		type : 'ajax',
//		url : '/graphql.json',
//		reader : {
//			type : 'json',
//			rootProperty : 'users'
//		}
//	},
//	autoLoad : true
//});
//Ext.require('Ext.grid.Grid');
//



Ext.define('DocumentListRootQuery', {
	extend : 'Ext.data.Model',
	fields : [{
			name : 'data',
			model : 'Selector'
		}
	]
});

Ext.define('SelectorStatistics', {
	extend : 'Ext.data.Model',
	fields : [
		{
			name : 'timesSeenInfinity',
			type : 'int'
		}, {
			name : 'timesSeenLatestQuarter',
			type : 'int'
		}, {
			name : 'timesSeenLatestWeek',
			type : 'int'
		}, {
			name : 'timesSeenLatestDay',
			type : 'int'
		}
	]
});

Ext.define('Selector', {
	extend : 'Ext.data.Model',
	fields : [
		{
			name : 'headline',
			type : 'string'
		}, {
			name : 'type',
			type : 'string'
		}, {
			name : 'statistics',
			model : 'SelectorStatistics'
		}
	]
});

Ext.define('SelectorQuery', {
	extend : 'Ext.data.Model',
	fields : [
		{
			name : 'timeOfEvent',
			type : 'date'
		}, {
			name : 'uid',
			type : 'string'
		}, {
			name : 'headline',
			type : 'string'
		}, {
			name : 'type',
			type : 'string'
		}
	]
});

Ext.define('Document', {
	extend : 'Ext.data.Model',
	fields : [
		{
			name : 'timeOfEvent',
			type : 'date'
		}, {
			name : 'uid',
			type : 'string'
		}, {
			name : 'headline',
			type : 'string'
		}, {
			name : 'type',
			type : 'string'
		}
	]
});

var documentStore = Ext.create('Ext.data.Store', {
    model: 'Document',
    proxy: {
    	type: 'ajax',
    	url: 'http://localhost:8110/graphql?query=%7B%20Localname(uid%3A%20%22E414266463DA22065CDCEF6B7A6AB48B%22)%7B%0A%20%20indexables%7B%0A%20%20%20%20send_Email%7B%0A%20%20%20%20%20%20uid%20headline%20timeOfEvent%20type%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D%20%20%0A%7D'
    },
    reader: {
    	type: 'json',
    	rootProperty: 'data.data.Localname.indexables.send_Email'
    }
});

documentStore.load({
	callback: function(){
		console.log(documentStore.first());
	}
});

Ext.application({
    name : 'GraphQl Example',

    launch : function() {
    	Ext.create('Ext.grid.Grid', {
      title: 'Document List',
  
      store: documentStore,
  
      columns: [
          { text: 'Time of Event',  dataIndex: 'timeOfEvent', width: 200 },
          { text: 'Unique ID', dataIndex: 'uid', width: 120 },
          { text: 'Summary', dataIndex: 'headline', width: 400 },
          { text: 'Document type', dataIndex: 'type', width: 120 }
      ],  
      height: 400,
      layout: 'fit',
      fullscreen: true
  });        
    }
});
