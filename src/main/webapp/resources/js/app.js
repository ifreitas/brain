/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Israel Freitas -- ( gmail => israel.araujo.freitas)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
var Log = {
  elem: false,
  info: function(text){
	  if (!this.elem) this.elem = document.getElementById('log');
	  this.elem.innerHTML       = text;
	  this.elem.style.left      = (550 - this.elem.offsetWidth / 2) + 'px';
  },
  error: function(text){
	  if (!this.elem) this.elem = document.getElementById('log');
	  this.elem.innerHTML       = text;
	  this.elem.style.left      = (550 - this.elem.offsetWidth / 2) + 'px';  
  }
};

const ObjectManager = {
	lastClicked: null,
	getLastClicked: function(){ return this.lastClicked; },
	setLastClicked: function(lastClicked){ this.lastClicked = lastClicked; }
}


var st = null
function initTree(json){
    st = new $jit.ST({
   		injectInto: 'infovis',
   		background:false,
   		orientation: 'top',
   	    duration: 800,
        transition: $jit.Trans.Quart.easeInOut,
        levelsToShow: 5,
        levelDistance: 50,
        
        Node: {
            height: 20,
            width: 80,
            type: 'rectangle',
            overridable: true,
            CanvasStyles:{
            	shadowColor: 'black',
                shadowBlur: 10,
                shadowOffsetY: 10
            }
        },
        
        Edge: {
            type: 'arrow',
            color: '#A7B6FF',
            overridable: true,
            CanvasStyles:{
            	shadowColor: 'black',
                shadowBlur: 10,
                shadowOffsetY: 10
            }
        },
        
        onBeforeCompute: function(node){
        	if(node) Log.info("loading " + node.name);
        },
        
        onAfterCompute: function(){
            Log.info("done");
        },
        
        onCreateLabel: function(label, node){
        	
        	Ext.EventManager.on(label, 'contextmenu', teste);
        	function teste(e,t){
        		window.getSelection().removeAllRanges();
        		ObjectManager.setLastClicked(node);
        		
        		e.stopEvent();
        		contextMenu.showAt(e.getXY());
        	}
        	
            label.id          = node.id;            
            label.innerHTML   = node.name;
            
            var style         = label.style;
            style.width       = 80 + 'px';
            style.height      = 20 + 'px';
            style.color       = 'white';
            style.textAlign   = 'center';
            style.fontWeight  = 'bold';
            style.fontFamily  = 'tahoma,arial,verdana,sans-serif';
            style.fontSize    = '11px';
            style.overflow    = "hidden";
            style.cursor      = 'pointer';
        },
        
        Events: {  
            enable: true,
            
            onClick: function(node, eventInfo, e){
            	if(node){
            		ObjectManager.setLastClicked(node);
            		st.onClick(node.id);
            	}
            }
        },
        
        onBeforePlotNode: function(node){
            //add some color to the nodes in the path between the
            //root node and the selected node.
            if (node.selected) {
                node.data.$color = '#23A4FF';
            }
            else {
                delete node.data.$color;
                //if the node belongs to the last plotted level
                if(!node.anySubnode("exist")) {
                    var count = 0;
                    node.eachSubnode(function(n) { count++; });
                    //assign a node color based on how many children it has
                    node.data.$color = ['#6D89D5', '#476DD5', '#133CAC', '#2B4281', '#062270', '#090974'][count];                    
                }
            }
        },
        
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = '#23A4FF';
                adj.data.$lineWidth = 3;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
    
    st.add = function(newKnowledge){
    	lastClikedNode = ObjectManager.lastClicked;
		st.addSubtree(
				{ id : lastClikedNode.id, children : [ newKnowledge ] }, 
				'replot',
				{ hideLabels : false, duration : 700 }
		);
		if (st.clickedNode.id != lastClikedNode.id) {
			st.onClick(lastClikedNode.id, { duration : 800 });
		}
		Log.info("Knowledege named '" + newKnowledge.name + "' added successfully.")
    };
    
    st.remove = function(knowledge){
    	lastClickedNode = ObjectManager.lastClicked;
    	st.removeSubtree(
				lastClickedNode.id,
				true,
				'animate',
				{
					duration : 500,
					hideLabels : false,
					onComplete : function() {
						Log.info("Knowledge '"+knowledge.name+"' deleted successfully.");
					}
				})
    };
    
    st.rename = function(knowledge, newName){
    	try{
    		ObjectManager.lastClicked.name = newName;
    		document.getElementById(knowledge.id).innerHTML = newName;
    		Log.info("Knowledege '"+knowledge.name+"' renamed to '" + newName + "' successfully.");
    	}
    	catch(e){
    		Log.error("Unable to rename Knowledege '"+knowledge.name+"'. Cause: "+e);
    	}
    }
    
    try{
    	if(json.id == null) throw "Invalid server data."
    	st.loadJSON(json);
    	st.compute();
    	st.geom.translate(new $jit.Complex(0, -300), "current");//optional: make a translation of the tree
    	st.onClick(st.root);
    }
    catch(e){
    	alert("Unable to load the tree. Cause: " + e)
    }
    
}

Ext.application({
    name: 'Brain',
    launch: function() {
        Ext.create('Ext.container.Viewport', {
            layout: 'border',
            items: [
                {
			        region: 'north',
			        html: '<h1>Brain</h1>',
			        border: true,
			        margins: '5 5 5 5'
			    }, {
			        region: 'west',
			        title: 'Help',
			        width: 300,
			        margins: '0 5 0 5'
			        // could use a TreePanel or AccordionLayout for navigational items
			    }, {
			        region: 'south',
			        contentEl: 'log',
			        height: 30,
			        minHeight: 30,
			        margins: '5 5 5 5'
			    }, {
			        region: 'east',
			        width: 300,
			        margins: '0 5 0 5',
			        items:[
				        Ext.create('Ext.tab.Panel', {
					    	region: 'center',
					    	border:false,
					        items: [
				                {
				                	title: 'Properties',
				                	disabled: true,
			                		tabConfig: {
						                tooltip: 'The properties of the selected element.'
						            }
						        }, 
						        {
						            title: 'Chat',
						            tabConfig: {
						                tooltip: 'A chat to test the knowledge base.'
						            }
						        },
						        {
						            title: 'Utilities',
						            disabled: true
						        }
					        ]
					    })
				    ]
			    },
			    Ext.create('Ext.tab.Panel', {
			    	region: 'center',
			        items: [
		                {
		                	title: 'Knowledge Base',
		                	contentEl: 'theTree'
				        }, 
				        {
				            title: 'Bots',
				            disabled: true,
				            tabConfig: {
				                tooltip: 'AIML Bots configurations set'
				            }
				        }
			        ],
			        tbar: [
							{
								text:'Apply',
								tooltip:'Apply the new Knowledge Base to the bot.',
								handler: function(){
									applyKnowledge();
								}
							}
						]
			    })
            ]
        });
    }
});


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//Ext.require(['Ext.data.*', 'Ext.grid.*']);
//
//Ext.define('Information', {
//    extend: 'Ext.data.Model',
//    fields: [{
//        name: 'id',
//        type: 'string',
//        useNull: true
//    }, 'name', 'knowledgeId'],
//    validations: [{
//        type: 'length',
//        field: 'name',
//        min: 1
//    }]
//});
//
//Ext.onReady(function(){
//
//    var store = Ext.create('Ext.data.Store', {
//        autoLoad: true,
//        autoSync: false,
//        model: 'Information',
//        proxy: {
//            type: 'rest',
//            url: 'rest/knowledges/9:0/informations',
//            reader: {
//                type: 'json',
//                root: 'data'
//            },
//            writer: {
//                type: 'json'
//            }
//        },
//        listeners: {
//            write: function(store, operation){
//                var record = operation.getRecords()[0],
//                    name = Ext.String.capitalize(operation.action),
//                    verb;
//                    
//                    
//                if (name == 'Destroy') {
//                    record = operation.records[0];
//                    verb = 'Destroyed';
//                } else {
//                    verb = name + 'd';
//                }
//                //Ext.example.msg(name, Ext.String.format("{0} user: {1}", verb, record.getId()));
//                
//            }
//        }
//    });
//    
//    var rowEditing = Ext.create('Ext.grid.plugin.RowEditing');
//    
//    var grid = Ext.create('Ext.grid.Panel', {
//        renderTo: document.body,
//        plugins: [rowEditing],
//        width: 400,
//        height: 300,
//        frame: true,
//        title: 'Informations',
//        store: store,
//        iconCls: 'icon-user',
//        columns: [{
//            text: 'ID',
//            width: 40,
//            sortable: true,
//            dataIndex: 'id'
//        }, {
//            text: 'Name',
//            flex: 1,
//            sortable: true,
//            dataIndex: 'name',
//            field: {
//                xtype: 'textfield'
//            }
//        }, {
//            header: 'knowledgeid',
//            width: 80,
//            sortable: true,
//            dataIndex: 'knowledgeId',
//            field: {
//                xtype: 'textfield'
//            }
//        }, ],
//        dockedItems: [{
//            xtype: 'toolbar',
//            items: [{
//                text: 'Add',
//                iconCls: 'icon-add',
//                handler: function(){
//                    // empty record
//                    store.insert(0, new Person());
//                    rowEditing.startEdit(0, 0);
//                }
//            }, '-', {
//                itemId: 'delete',
//                text: 'Delete',
//                iconCls: 'icon-delete',
//                disabled: true,
//                handler: function(){
//                    var selection = grid.getView().getSelectionModel().getSelection()[0];
//                    if (selection) {
//                        store.remove(selection);
//                        store.sync();
//                    }
//                }
//            }]
//        }]
//    });
//    grid.getSelectionModel().on('selectionchange', function(selModel, selections){
//        grid.down('#delete').setDisabled(selections.length === 0);
//    });
//});

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////





/**
 * TODO: Usar este recordField
 * Usage: Ext.create("Brain.form.textfield", {record: panel.getForm().getRecord()})
 */
Ext.define('Brain.form.textfield', {
	extend : 'Ext.form.field.Text',
	alias : "recordField",
	listeners : {
		'blur' : function(e, eOpts) {
			this.record.set(this.name, this.value)
		}
	}
});

function KnowledgeExtWrapper(){
	
	var store = null;
	defineProxy();
	defineModel();
	defineStore();
	
	this.create = function(){
		var record = Ext.create('Brain.model.Knowledge', {name:'', parentId:ObjectManager.getLastClicked().id})
		var form = prepareForm(record, {
			success: function(rec, op){
				store.add(rec.data);
				st.add({id:rec.data.id, name:rec.data.name, data:{}, children:[]});
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var panel = basicWindow('Create a Knowledge', [ form ])
		panel.show();
		return panel;
	}
	
	this.update  = function(){
		var record = store.findRecord('id',ObjectManager.lastClicked.id)
		var form = prepareForm(record, {
			success: function(rec, op) {
				record.commit();
				st.rename(ObjectManager.lastClicked, rec.data.name); 
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var panel = basicWindow('Update the Knowledge', [ form ])
		panel.show();
		return panel;
	}
	
	this.destroy = function(){
		if(ObjectManager.lastClicked.getParents().length == 0){
			Ext.MessageBox.alert("","Unable to delete the tree's root knowledge")
		}
		else{
			Ext.MessageBox.confirm('Confirm to delete the knowledge?', 'If yes, all its informations and nested knowledges will be removed too. Are you sure?', function(answer){
				if(answer == 'yes'){
					var record = store.findRecord('id',ObjectManager.lastClicked.id)
					record.destroy({
						success: function(rec, op){
							st.remove(ObjectManager.lastClicked)
							ObjectManager.setLastClicked(null)
						},
						failure: function(rec, op){ alert(op.getError()); }
					});
				}
			});
		};
	}
	
	function prepareForm(record, callbacks){
		var theForm = Ext.create('Ext.form.Panel', {
			border:false, layout:'form', bodyPadding: 5,
			model: 'Brain.model.Knowledge',
			items:[
			       {
			    	   xtype: 'textfield', fieldLabel: 'Name', name: 'name',
			    	   allowBlank: false, minLength: 2, maxLength: 40,
			    	   listeners:{
			    		   'blur' : function( e, eOpts ){
			    			   theForm.getForm().getRecord().set(this.name, this.value)
			    		   }
			    	   }
			       }
		       ],
		       bbar: [
		              {
					   xtype: 'button', text: 'Save', 
					   formBind : true,
					   handler: function(){theForm.getForm().getRecord().save({
						    success: function(rec, op) { 
						    	theForm.up().close(); 
						    	if (callbacks != null) callbacks.success(rec, op); 
						    },
						    failure: function(rec, op) { if (callbacks != null) callbacks.failure(rec, op); }
						});},
					   scope: this
					}, '-',
					{
					   xtype: 'button', text: 'Cancel',
					   handler: function() { theForm.up().close(); },
					   scope: this
					}
				]
		});
		theForm.getForm().loadRecord(record);
		return theForm;
	}
	
	function basicWindow(title, items){
		return Ext.create('Ext.window.Window', {
			title:       title,
			modal:       true,
			closable:    true,
			resizable:	 false,
			height:      200,
			width:       400,
			layout:      'fit',
			items:       items,
			listeners:{
				'show':function(window){
					window.items.first().getForm().getFields().first().focus();
				}
			}
		});
	}
	
	function defineProxy(){
		this.proxy = {
	        type:     'rest',
	        url :     'rest/knowledges',
	        model:    'Brain.model.Knowledge',
	        format:   'json',
	        appendId: true
	    };
	}
	
	function defineModel(){
		Ext.define('Brain.model.Knowledge', {
		    extend: 'Ext.data.Model',
		    idProperty: 'id',
		    fields:['id', 'name', 'parentId'],
		    proxy: this.proxy
		});
	}
		
	function defineStore(){
		store = Ext.create('Ext.data.Store', {
		     model: 'Brain.model.Knowledge',
		     proxy: this.proxy,
		     autoLoad: true,
		     autoSync: false 
		 });
	}
}

var knowledgeExtWrapper = new KnowledgeExtWrapper();



function InformationExtWrapper(){
	var store = null;
	defineModel();
	initStore();
	this.grid = initGrid(); //temp implementation
	this.teachingExtWrapper = new TeachingExtWrapper();
	var me = this
	
	function create(){
		var record = Ext.create('Brain.model.Information', {name:'', knowledgeId:ObjectManager.getLastClicked().id})
		var form = prepareForm(record, {
			success: function(rec, op){
				store.add(rec.data)
				Log.info("Information named '" + rec.data.name + "' created successfully.");
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var p = basicWindow('Create a Information', [ form ]);
		p.show();
		return p;
	}
	
	function update(){
		
		if(me.grid.getSelectionModel().getLastSelected() == null) return false;
		
		var selectedItem = me.grid.getSelectionModel().getLastSelected().data
		var record = store.findRecord('id', selectedItem.id)
		var form = prepareForm(record, {
			success: function(rec, op) {
				record.commit();
				Log.info("Information renamed to '" + rec.data.name + "' successfully."); 
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var panel = basicWindow('Update the Information', [ form ])
		panel.show();
		return panel;
	}
	
	function destroy(){
		var selectedItem = me.grid.getView().getSelectionModel().getSelection()[0];
		if(selectedItem == null) return false;
		Ext.MessageBox.confirm('Confirm to delete the Information?', 'If yes, all its teachings will be removed too. Are you sure?', function(answer){
			if(answer == 'yes'){
				store.remove(selectedItem);
				store.sync();
			}
		});
	}
	
	
	function prepareForm(record, callbacks){
		var theForm = Ext.create('Ext.form.Panel', {
			border:false, layout:'form', bodyPadding: 5,
			model: 'Brain.model.Information',
			items:[
			       {
			    	   xtype: 'textfield', fieldLabel: 'Name', name: 'name',
			    	   allowBlank: false, minLength: 2, maxLength: 40,
			    	   listeners:{
			    		   'blur' : function( e, eOpts ){
			    			   theForm.getForm().getRecord().set(this.name, this.value)
			    		   }
			    	   }
			       }
		       ],
		       bbar: [
		              {
					   xtype: 'button', text: 'Save', 
					   formBind : true,
					   handler: function(){theForm.getForm().getRecord().save({
						    success: function(rec, op) { 
						    	theForm.up().close(); 
						    	if (callbacks != null) callbacks.success(rec, op); 
						    },
						    failure: function(rec, op) { if (callbacks != null) callbacks.failure(rec, op); }
						});},
					   scope: this
					}, '-',
					{
					   xtype: 'button', text: 'Cancel',
					   handler: function() { theForm.up().close(); },
					   scope: this
					}
				]
		});
		theForm.getForm().loadRecord(record);
		return theForm;
	}
	
	function initGrid(){
		return Ext.create('Ext.grid.Panel', {
		   region: 'north',
		   margins: '5 5 5 5',
		   height: 205,
		   title: 'Informations',
		   store: store,
		   tbar: [
				{
					  text: 'Create',
					  handler:function(){ create(); }
				}, '-',
				{
					  text: 'Update',
					  handler:function(){ update(); }
				}, '-',
				{
					  text: 'Delete',
					  handler:function(){ destroy(); }
				}
			],
		    columns: [{ text: 'Name',  dataIndex: 'name', width:'100%'}],
		    listeners:{
		    	select:function( theGrid, record, index, eOpts ){
		    		me.teachingExtWrapper.setInformation(record.data)
		    	},
				deselect:function( record, index, eOpts ){
					me.teachingExtWrapper.setInformation(null)
				},
				itemdblclick: function( record, item, index, e, eOpts ){
					me.update();
				}
		    }
		});
	}
	
	function basicWindow(title, items){
		return Ext.create('Ext.window.Window', {
			title:       title,
			modal:       true,
			closable:    true,
			resizable:	 false,
			height:      200,
			width:       400,
			layout:      'fit',
			items:       items,
			listeners:{
				'show':function(window){
					window.items.first().getForm().getFields().first().focus();
				}
			}
		});
	}
	
	function defineModel(){
		Ext.define( 'Brain.model.Information', {
		    extend: 'Ext.data.Model',
		    fields:['id', 'name', 'knowledgeId'],
		    proxy: {
				type: 'rest',
				url : 'rest/knowledges/'+ObjectManager.lastClicked.id+'/informations',
				appendId: true
		    }
		});
	}
		
	function initStore(){
		store = Ext.create('Ext.data.Store', {
			model: 'Brain.model.Information',
			autoLoad: true,
			autoSync: false,
			listeners:{
				'remove':function(store, record, index, isMove, eOpts){
					me.teachingExtWrapper.setInformation(null)
					Log.info("Information '"+record.data.name+"' deleted successfully.");
				}
			}
		 });
	}
}


/**
 * 
 * @returns
 */
function TeachingExtWrapper(){

	var information = null;
	initProxy('rest/knowledges/'+ObjectManager.lastClicked.id+'/informations/noId/teachings');
	defineModel();
	var store = initStore();
	var grid = this.grid = initGrid();
	
	
	this.setInformation = function(newInformation){
		information = newInformation;
		
		if(newInformation == null){
			grid.setDisabled(true);
			this.grid.getSelectionModel().clearSelections();
			store.loadData([]);
		}
		else{
			grid.setDisabled(false);
			this.grid.getSelectionModel().clearSelections();
			this.grid.setTitle("Teachings of " + information.name);
			changeProxyUrlTo('rest/knowledges/'+ObjectManager.lastClicked.id+'/informations/'+information.id+'/teachings')
			store.reload();
		}
	}
	
	function changeProxyUrlTo(url){
		initProxy(url);
		defineModel();
		store.proxy.url = url;
	}
	
	function create(){
		var record = Ext.create('Brain.model.Teaching', {informationId:information.id, whenTheUserSays:'', respondingTo:'', memorize:'', say:''})
		var form = prepareForm(record, {
			success: function(rec, op){
				store.add(rec.data)
				record.commit();
				Log.info("Teaching created successfully.");
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var p = basicWindow('Create a Teaching', [ form ])
		p.show();
		return p;
	}
	
	function update(){
		if(grid.getSelectionModel().getLastSelected() == null) return false;
		var selectedItem = grid.getSelectionModel().getLastSelected().data
		var record = store.findRecord('id', selectedItem.id)
		var form = prepareForm(record, {
			success: function(rec, op) {
				record.commit();
				Log.info("Teaching updated successfully."); 
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var panel = basicWindow('Update the Teaching', [ form ])
		panel.show();
		return panel;
	}
	
	function destroy(){
		var selectedItem = grid.getView().getSelectionModel().getSelection()[0];
		if(selectedItem == null) return false;
		Ext.MessageBox.confirm('Confirm to delete the theaching?', 'Are you sure?', function(answer){
			if(answer == 'yes'){
				store.remove(selectedItem);
				store.sync();
			}
		});
	}
	
	function initGrid(){
		return Ext.create('Ext.grid.Panel', {
			disabled:true, 
			region: 'center',
			layout: 'fit',
			margins: '0 5 5 5',
			title: 'Teachings',
			store: store,
			tbar: [
		          {
		        	  text: 'Create',
					  handler:function(){ create(); }
		          }, '-',
		          {
		        	  text: 'Update',
					  handler:function(){ update(); }
		          }, '-',
		          {
		        	  text: 'Delete',
					  handler:function(){ destroy(); }
		          }
		          ],
		   columns: [
                    { text: 'When the user says',  dataIndex: 'whenTheUserSays', width:280},
                    { text: 'Responding to',  dataIndex: 'respondingTo', width:200 },
                    { text: 'Memorize',  dataIndex: 'memorize', width:100},
                    { text: 'Say',  dataIndex: 'say', width:280 },
           ],
           listeners:{
        	   itemdblclick:function( record, index, eOpts ){
        		   update();
        	   }
           },
           height: 200
	   });
	}
	
	function prepareForm(record, callbacks){
		var theForm = Ext.create('Ext.form.Panel', {
			border:false, layout:'form', bodyPadding: 5,
			model: 'Brain.model.Teaching',
			items:[
			       {
			    	   xtype: 'textareafield', fieldLabel: 'When the user says', name: 'whenTheUserSays',
			    	   allowBlank: false, minLength: 1, maxLength: 200,
			    	   listeners:{
			    		   'blur' : function( e, eOpts ){
			    			   theForm.getForm().getRecord().set(this.name, this.value)
			    		   }
			    	   }
			       },
			       {
			    	   xtype: 'textfield', fieldLabel: 'Responding to', name: 'respondingTo',
			    	   allowBlank: true, minLength: 1, maxLength: 40,
			    	   listeners:{
			    		   'blur' : function( e, eOpts ){
			    			   theForm.getForm().getRecord().set(this.name, this.value)
			    		   }
			    	   }
			       },
			       {
			    	   xtype: 'textareafield', fieldLabel: 'Memorize', name: 'memorize',
			    	   allowBlank: true, minLength: 3, maxLength: 200,
			    	   listeners:{
			    		   'blur' : function( e, eOpts ){
			    			   theForm.getForm().getRecord().set(this.name, this.value)
			    		   }
			    	   }
			       },
			       {
			    	   xtype: 'textareafield', fieldLabel: 'Then say', name: 'say',
			    	   allowBlank: false, minLength: 1, maxLength: 200,
			    	   listeners:{
			    		   'blur' : function( e, eOpts ){
			    			   theForm.getForm().getRecord().set(this.name, this.value)
			    		   }
			    	   }
			       }
		       ],
		       bbar: [
		              {
					   xtype: 'button', text: 'Save', 
					   formBind : true,
					   handler: function(){
						   theForm.getForm().getRecord().save({
							   success: function(rec, op) { 
								   theForm.up().close(); 
								   if (callbacks != null) callbacks.success(rec, op); 
							   },
							   failure: function(rec, op) { if (callbacks != null) callbacks.failure(rec, op); }
						   });
					   },
					   scope: this
					}, '-',
					{
					   xtype: 'button', text: 'Cancel',
					   handler: function() { theForm.up().close(); },
					   scope: this
					}
				]
		});
		theForm.getForm().loadRecord(record);
		return theForm;
	}
	
	function basicWindow(title, items){
		return Ext.create('Ext.window.Window', {
			title:       title,
			modal:       true,
			closable:    true,
			resizable:	 false,
			height:      365,
			width:       500,
			layout:      'fit',
			items:       items,
			listeners:{
				'show':function(window){
					window.items.first().getForm().getFields().first().focus();
				}
			}
		});
	}
	
	function initProxy(url){
		this.proxy = {
	        type:     'rest',
	        url :     url,
	        model:    'Brain.model.Teaching',
	        format:   'json',
	        appendId: true
	    };
	}
	
	function defineModel(){
		Ext.define('Brain.model.Teaching', {
		    extend: 'Ext.data.Model',
		    fields:['id', 'informationId', 'whenTheUserSays', 'respondingTo', 'memorize', 'say'],
		    proxy: this.proxy
		});
	}
		
	function initStore(){
		return Ext.create('Ext.data.Store', {
		     model: 'Brain.model.Teaching',
		     proxy: this.proxy,
		     autoLoad: false,
		     autoSync: false,
			listeners:{
				'remove':function(store, record, index, isMove, eOpts){
					Log.info("Teaching deleted successfully.")
				}
			}
		 });
	}
}


function InformationAndTeachingWindow() {
	var informationExtWrapper = new InformationExtWrapper();
	var infoGrid = informationExtWrapper.grid;
	var teachingGrid = informationExtWrapper.teachingExtWrapper.grid;
	Ext.create('Ext.window.Window', {
		title:       'Informations & Teachings',
		closeAction: 'hide',
		modal:       true,
		closable:    true,
		resizable:	 false,
		height:      600,
		width:       900,
		layout:      'border',
		items:[	infoGrid, teachingGrid]
	}).show();
}

var contextMenu = Ext.create('Ext.menu.Menu', {
	items:[
	       { text : 'Add a Knowledge',       handler : function(item){knowledgeExtWrapper.create();} }, 
		   { text : 'Rename this Knowledge', handler : function(item){knowledgeExtWrapper.update();} }, 
		   { text : 'Delete this Knowledge', handler : function(item){knowledgeExtWrapper.destroy();} }, 
		   '-', 
		   { text : 'Topics & Teachings',    handler : function(item){new InformationAndTeachingWindow()}}
   ]
});

