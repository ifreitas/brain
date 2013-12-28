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
					},
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
	
	
	initProxy();
	defineModel();
	var store = initStore();
	var grid = this.grid = initGrid(); //temp implementation
	
	function create(){
		var record = Ext.create('Brain.model.Information', {name:'', knowledgeId:ObjectManager.getLastClicked().id})
		var form = prepareForm(record, {
			success: function(rec, op){
				store.add(rec.data)
				record.commit();
				Log.info("Information named '" + rec.data.name + "' added successfully.");
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var p = basicWindow('Create a Information', [ form ])
		p.show();
		return p;
	}
	
	function update(){
		var selectedItem = grid.getSelectionModel().getLastSelected().data
		var record = store.findRecord('id', selectedItem.id)
		var form = prepareForm(record, {
			success: function(rec, op) {
				record.commit();
				Log.info("Information renamed to '" + rec.data.name + "' successfully."); 
			},
			failure: function(rec, op) { alert(op.getError()); }
		});
		var panel = basicWindow('Update the Knowledge', [ form ])
		panel.show();
		return panel;
	}
	
	function destroy(){
		Ext.MessageBox.confirm('Confirm to delete the Information?', 'If yes, all its teachings will be removed too. Are you sure?', function(answer){
			if(answer == 'yes'){
				var selectedItem = grid.getSelectionModel().getLastSelected().data
				var record = store.findRecord('id',selectedItem.id)
				record.destroy({
					success: function(rec, op){
						record.commit();
						Log.info("Information '"+selectedItem.name+"' deleted successfully.");
					},
					failure: function(rec, op){ alert(op.getError()); }
				});
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
					},
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
				},
				{
					  text: 'Update',
					  handler:function(){ update(); }
				},
				{
					  text: 'Delete',
					  handler:function(){ destroy(); }
				}
			],
		    columns: [{ text: 'Name',  dataIndex: 'name', width:'100%'}],
		    listeners:{
		    	select:function( theGrid, record, index, eOpts ){
//		    		teachingExtWrapper.panel.getSelectionModel().clearSelections();
//		    		teachingExtWrapper.panel.setDisabled(false)
//		    		teachingExtWrapper.panel.setTitle("Teachings of " + record.data.name)
//		    		teachingExtWrapper.panel.store.loadData([]);// TODO: search a better way to clear the grid.
//					teachingLoadMask.show();
//		 		    loadTeachings();
		    	},
				deselect:function( record, index, eOpts ){
//					teachingExtWrapper.panel.setDisabled(true)
				},
				itemdblclick: function( record, item, index, e, eOpts ){
					update();
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
	
	function initProxy(){
		this.proxy = {
	        type:     'rest',
	        url :     'rest/knowledges/'+ObjectManager.lastClicked.id+'/informations',
	        model:    'Brain.model.Information',
	        format:   'json',
	        appendId: true
	    };
	}
	
	function defineModel(){
		Ext.define('Brain.model.Information', {
		    extend: 'Ext.data.Model',
		    fields:['id', 'name', 'knowledgeId'],
		    proxy: this.proxy
		});
	}
		
	function initStore(){
		return Ext.create('Ext.data.Store', {
		     model: 'Brain.model.Information',
		     proxy: this.proxy,
		     autoLoad: true,
		     autoSync: false, 
		     sorters: [{
		    	 property: 'name',
		    	 direction: 'ASC'
		     }]
		 });
	}
}


//var teachingExtWrapper = {
//	panel: Ext.create('Ext.grid.Panel', {
//		disabled : true,
//		   region: 'center',
//		   layout: 'fit',
//		   margins: '0 5 5 5',
//		   title: 'Teachings',
//		   store: Ext.create('Ext.data.Store', {
//			   config:{
//				   sortOnLoad:true,
//			   },
//			   sorters: [{
//			         property: 'name',
//			         direction: 'ASC'
//			     }],
//			    storeId:'teachingStore',
//			    fields:['id', 'informationId', 'whenTheUserSays', 'respondingTo', 'memorize', 'say'],
//			    data:[]
//			}),
//		   tbar: [
//		          {
//		        	  text: 'Create',
//					  handler:function(){
//						  teachingExtWrapper.resetFormPanel();
//						  teachingExtWrapper.formPanel.show();
//					  }
//		          },
//		          {
//		        	  text: 'Update',
//					  handler:function(){
//						  if(teachingExtWrapper.panel.getSelectionModel().getLastSelected() != null){
//							  teachingExtWrapper.prepareUpdateFormPanel();
//							  teachingExtWrapper.formPanel.show();
//						  }
//					  }
//		          },
//		          {
//		        	  text: 'Delete',
//					  handler:function(){
//						  if(teachingExtWrapper.panel.getSelectionModel().getLastSelected() != null){
//							  teachingExtWrapper.deleteFormPanel.show();
//						  }
//					  }
//		          }
//		          ],
//		   columns: [
//                    { text: 'When the user says',  dataIndex: 'whenTheUserSays', width:280},
//                    { text: 'Responding to',  dataIndex: 'respondingTo', width:200 },
//                    { text: 'Memorize',  dataIndex: 'memorize', width:100},
//                    { text: 'Say',  dataIndex: 'say', width:280 },
//           ],
//           listeners:{
//        	   select:function( theGrid, record, index, eOpts ){
//        	   },
//        	   deselect:function( record, index, eOpts ){
//        	   },
//        	   itemdblclick:function( record, index, eOpts ){
//        		   teachingExtWrapper.prepareUpdateFormPanel();
//        		   teachingExtWrapper.formPanel.show();
//        	   }
//           },
//           height: 200
//	   }),
//	   
//	   formPanel: Ext.create('Ext.window.Window', {
//			title:       'Create the Teaching',
//			closeAction: 'hide',
//			modal:       true,
//			closable:    true,
//			resizable:	 false,
//			height:      410,
//			width:       500,
//			layout:      'fit',
//			items:       {
//				xtype:     'panel',
//				border:    false,
//				contentEl: 'teachingForm'
//			},
//			listeners: {
//				'beforehide':function(window){
//					teachingExtWrapper.resetFormPanel();
//				},
//				'beforeshow':function(window){
//					document.getElementById("teachingInformationId").value=informationExtWrapper.panel.getSelectionModel().getLastSelected().data.id;
//				}
//			}
//		}),
//		
//		resetFormPanel : function(){
//		   document.getElementById("teachingId").value='';
//		   document.getElementById("teachingInformationId").value='';
//		   document.getElementById("whenTheUserSaysInput").value='';
//		   document.getElementById("whenTheUserSaysInput").value='';
//		   document.getElementById("respondingToInput").value='';
//		   document.getElementById("memorizeInput").value='';
//		   document.getElementById("sayInput").value='';
//		   document.getElementById("teachingFormStatus").innerHTML='';
//	   },
//	   
//	   prepareUpdateFormPanel :  function(){
//		   selectedItem = teachingExtWrapper.panel.getSelectionModel().getLastSelected().data
//		   document.getElementById("teachingId").value            = selectedItem.id;
//		   document.getElementById("teachingInformationId").value = selectedItem.teachingInformationId;
//		   document.getElementById("whenTheUserSaysInput").value  = selectedItem.whenTheUserSays;
//		   document.getElementById("respondingToInput").value     = selectedItem.respondingTo;
//		   document.getElementById("memorizeInput").value         = selectedItem.memorize;
//		   document.getElementById("sayInput").value              = selectedItem.say;
//		   document.getElementById("teachingFormStatus").innerHTML='';
//	   },
//		
//		deleteFormPanel: Ext.create('Ext.window.Window', {
//			title:       'Delete the Teaching',
//			closeAction: 'hide',
//			modal:       true,
//			closable:    true,
//			resizable:	 false,
//			height:      200,
//			width:       400,
//			layout:      'fit',
//			items:       {
//				xtype:     'panel',
//				border:    false,
//				contentEl: 'deleteTeachingForm'
//			},
//			listeners: {
//				'beforehide':function(window){
//					document.getElementById("whatTeachingToDelete").value='';
//					document.getElementById("deleteTeachingFormStatus").innerHTML='';
//				},
//				'beforeshow':function(window){
//					document.getElementById("whatTeachingToDelete").value=teachingExtWrapper.panel.getSelectionModel().getLastSelected().data.id;
//				}
//			}
//		})
//}
//
//var informationAndTeachingWindow = Ext.create('Ext.window.Window', {
//	title:       'Informations & Teachings',
//	closeAction: 'hide',
//	modal:       true,
//	closable:    true,
//	resizable:	 false,
//	height:      600,
//	width:       900,
//	layout:      'border',
//	items:[	informationExtWrapper.panel, teachingExtWrapper.panel],
//	listeners: {
//		'show':function(window){
//			informationExtWrapper.panel.store.loadData([]);// TODO: search a better way to clear the grid.
//			informationLoadMask.show();
// 		    loadInformations();
//		}
//	}
//});
//
//var informationLoadMask = new Ext.LoadMask(informationExtWrapper.panel, {msg:"Loading Informations. Please wait..."});
//function afterReceiveInformation(data){
//	informationExtWrapper.panel.store.loadData(eval(data))
//	informationLoadMask.hide();
//}
//
//var teachingLoadMask = new Ext.LoadMask(teachingExtWrapper.panel, {msg:"Loading Teachings. Please wait..."});
//function afterReceiveTeachings(data){
//	teachingExtWrapper.panel.store.loadData(eval(data))
//	teachingLoadMask.hide();
//}

function InformationAndTeachingWindow() {
	var informationExtWrapper = new InformationExtWrapper();
	var infoGrid = informationExtWrapper.grid
	Ext.create('Ext.window.Window', {
		title:       'Informations & Teachings',
		closeAction: 'hide',
		modal:       true,
		closable:    true,
		resizable:	 false,
		height:      600,
		width:       900,
		layout:      'border',
		items:[	infoGrid]
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

